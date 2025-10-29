package com.github.oursharecar.server.config

import com.auth0.jwk.JwkProvider
import io.ktor.server.config.*

data class Auth0Settings(
    val issuer: String,
    val audience: String
) {
    companion object {
        fun load(config: ApplicationConfig): Auth0Settings {
            val auth0Config = config.config("security.auth0")
            return Auth0Settings(
                issuer = auth0Config.property("issuer").getString(),
                audience = auth0Config.property("audience").getString()
            )
        }
    }
}

fun Auth0Settings.toJwkProvider(): JwkProvider =
    com.auth0.jwk.JwkProviderBuilder(this.issuer).build()
