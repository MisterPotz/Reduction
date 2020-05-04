package com.reducetechnologies.reduction.android.util

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class FileStringUtilsTest {

    @Test
    fun formatPathAsAsset() {
        val picture = "encyclopedia_pictures/reductor.jpg"
        val path = FileStringUtils.formatPathAsAsset(picture)
        assertEquals("file:///assets/encyclopedia_pictures/reductor.jpg", path)
    }
}