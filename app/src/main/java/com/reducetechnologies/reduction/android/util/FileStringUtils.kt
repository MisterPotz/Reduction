package com.reducetechnologies.reduction.android.util

object FileStringUtils {
    fun pathAsPicassoImage(path: String) : String {
        if (path.startsWith("file")) {
            return path
        }
        return "file:///android_asset/$path"
    }
}