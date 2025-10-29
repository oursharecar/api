package com.github.oursharecar.server.config

import com.auth0.jwk.JwkProvider
import com.auth0.jwk.JwkProviderBuilder
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.config.ApplicationConfig

data class JwtSettings(
    val issuer: String?,
    val audience: String,
    val realm: String,
    val secret: String?
) {
    companion object {
        fun load(config: ApplicationConfig): JwtSettings {
            val jwtConfig = config.config("security.jwt")
            val issuer = jwtConfig.propertyOrNull("issuer")?.getString()
            val audience =
                jwtConfig.propertyOrNull("audience")?.getString() ?: throw IllegalStateException("Missing JWT audience")
            val realm =
                jwtConfig.propertyOrNull("realm")?.getString() ?: throw IllegalStateException("Missing JWT realm")
            val secret = jwtConfig.propertyOrNull("secret")?.getString()

            return JwtSettings(
                issuer = issuer,
                audience = audience,
                realm = realm,
                secret = secret
            )
        }
    }
}

fun buildJwkProvider(jwtSettings: JwtSettings): JwkProvider {
    return runCatching {
        JwkProviderBuilder(jwtSettings.issuer).build()
    }.onFailure {
        JWT.require(Algorithm.HMAC256(jwtSettings.secret)).build()
    }.getOrThrow()
}