package com.example.exhibition

import org.json.JSONArray
import org.json.JSONObject


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