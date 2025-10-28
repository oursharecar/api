package com.github.oursharecar.server.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.config.*

fun Application.configureSecurity() {
    val jwtSettings = JwtSettings.load(environment.config)
    if (jwtSettings == null) {
        environment.log.warn("JWT authentication disabled: missing security.jwt configuration")
        return
    }

    install(Authentication) {
        jwt {
            realm = jwtSettings.realm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtSettings.secret))
                    .withAudience(jwtSettings.audience)
                    .withIssuer(jwtSettings.issuer)
                    .build()
            )
            validate { credential ->
                if (credential.payload.audience.contains(jwtSettings.audience)) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }
}

private data class JwtSettings(
    val issuer: String,
    val audience: String,
    val realm: String,
    val secret: String
) {
    companion object {
        fun load(config: ApplicationConfig): JwtSettings? {
            val jwtConfig = config.config("security.jwt")
            val issuer = jwtConfig.propertyOrNull("issuer")?.getString()
            val audience = jwtConfig.propertyOrNull("audience")?.getString()
            val realm = jwtConfig.propertyOrNull("realm")?.getString()
            val secret = jwtConfig.propertyOrNull("secret")?.getString()

            return if (issuer != null && audience != null && realm != null && secret != null) {
                JwtSettings(
                    issuer = issuer,
                    audience = audience,
                    realm = realm,
                    secret = secret
                )
            } else {
                null
            }
        }
    }
}
