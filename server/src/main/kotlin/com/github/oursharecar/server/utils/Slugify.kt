package com.github.oursharecar.server.utils

import com.github.slugify.Slugify

object GlobalSlugify {
    private val slugify by lazy {
        Slugify.builder().build()
    }

    fun slugify(input: String): String {
        return slugify.slugify(input)
    }
}