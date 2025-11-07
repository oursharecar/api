package com.github.oursharecar.models

import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Serializable
data class UserResource(
    override val id: ID<UserResource>? = null,

    val name: String,

    val picture: String? = null,
    val locale: String? = null,

    /** ユーザー設定（preferences） */
    val preferences: PreferencesResource = PreferencesResource(),
) : Identifiable<UserResource> {
    @Serializable
    data class PreferencesResource(
        val unitSystem: String = "metric",
        val timezone: String = "Asia/Tokyo",
        val language: String = "ja-JP",
        val theme: String? = null
    )

    override fun withId(id: ID<UserResource>) = this.copy(id = id)
}