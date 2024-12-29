package com.example.exhibition

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream

fun loadJSONFromAsset(context: Context, fileName: String): String? {
    return try {
        val inputStream: InputStream = context.assets.open(fileName)
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        String(buffer, Charsets.UTF_8)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun getVenueLocation(venues: JSONArray, venueID: Int): String? {
    for (i in 0 until venues.length()){
        val venue = venues.getJSONObject(i)
        if (venue.getInt("venue_id") == venueID) {
            return venue.getString("name")
        }
    }
    return "-"
}

fun JSONArray.toMutableList(): MutableList<JSONObject> {
    val list = mutableListOf<JSONObject>()
    for (i in 0 until this.length()) {
        val jsonObject = this.getJSONObject(i)
        list.add(jsonObject)
    }
    return list
}