package com.github.oursharecar.server.plugins

import com.github.oursharecar.server.config.Auth0Settings
import com.github.oursharecar.server.config.JwtSettings
import com.github.oursharecar.server.config.buildJwkProvider
import com.github.oursharecar.server.config.toJwkProvider
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun Application.configureSecurity() {
    val jwtSettings = try {
        JwtSettings.load(environment.config)
    } catch (e: IllegalStateException) {
        throw IllegalStateException("Failed to load JWT settings: ${e.message}")
    }

    install(Authentication) {
        jwt("jwt-groups-claims") {
            realm = jwtSettings.realm

            runCatching {
                // Auth0 configuration first
                val auth0Settings = Auth0Settings.load(this@configureSecurity.environment.config)
                verifier(auth0Settings.toJwkProvider(), auth0Settings.issuer)
            }.recover {
                // Fallback to custom JWT configuration
                val jwtSettings = JwtSettings.load(this@configureSecurity.environment.config)
                verifier(buildJwkProvider(jwtSettings))
            }.getOrThrow()

            validate { credential ->
                val sub: String? = credential.payload.getClaim("sub").asString() ?: return@validate null
                if (sub.isNullOrEmpty()) return@validate null

                val groups: List<String>? = credential.payload.getClaim("groups").asList(String::class.java)
                if (groups.isNullOrEmpty()) return@validate null

                if (credential.payload.audience.contains(jwtSettings.audience)) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }
}