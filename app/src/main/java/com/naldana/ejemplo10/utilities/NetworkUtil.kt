package com.naldana.ejemplo10.utilities

import android.net.Uri
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.*

class NetworkUtil {

    fun buildUrl(url: String): URL {
        val uri = Uri.parse(url).buildUpon().build()
        val url = try {
            URL(uri.toString())
        } catch (e: MalformedURLException) {
            URL("")
        }
        return url
    }

    @Throws(IOException::class)
    fun getResponseFromHttpUrl(url: URL): String {
        val apiKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpYXQiOjE1NTQ4MjAyODMsImV4cCI6MTYxNzAyODI4M30.MDqFZP6iMjpYhDfQwMfkaee3He9tbBAYbksd-gLGhxY"
        val urlConnection = url.openConnection() as HttpURLConnection
        urlConnection.setRequestProperty("Authorization", "Bearer $apiKey")
        try {
            val `in` = urlConnection.inputStream

            val scanner = Scanner(`in`)
            scanner.useDelimiter("\\A")

            val hasInput = scanner.hasNext()
            return if (hasInput) {
                scanner.next()
            } else {
                ""
            }
        } finally {
            urlConnection.disconnect()
        }
    }
}