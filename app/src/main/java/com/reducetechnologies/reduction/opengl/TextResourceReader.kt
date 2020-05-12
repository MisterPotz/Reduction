package com.reducetechnologies.reduction.opengl

import android.content.Context
import android.content.res.Resources
import androidx.annotation.RawRes
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.StringBuilder

object TextResourceReader {
    fun readTextFileFromResource(context: Context, @RawRes resourceId: Int) : String {
        val body = StringBuilder()
        try {
            // Raw stream
            val inputStream: InputStream = context.resources.openRawResource(resourceId)
            // Bridge between bytes and characters
            val inputStreamReader = InputStreamReader(inputStream)
            // For comfortability
            val bufferedReader = BufferedReader(inputStreamReader)
            var nextLine: String? = null

            while (bufferedReader.let {
                    nextLine = it.readLine()
                    nextLine != null
                }) {
                body.append(nextLine)
                body.append('\n')
            }
        } catch (e: IOException) {
            throw RuntimeException(
                "Could not open resource: $resourceId", e
            )
        } catch (nfe : Resources.NotFoundException) {
            throw RuntimeException("Resource not found: " + resourceId, nfe);
        }
        return body.toString()
    }
}