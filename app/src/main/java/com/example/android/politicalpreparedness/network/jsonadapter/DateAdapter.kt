package com.example.android.politicalpreparedness.network.jsonadapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.text.SimpleDateFormat
import java.util.*

class DateAdapter {
    val dateFormat =  "yyyy-MM-dd"

    @FromJson
    fun stringToDate(electionDay: String): Date {
        return SimpleDateFormat(dateFormat).parse(electionDay)
    }

    @ToJson
    fun dateToString(date: Date): String {
        val dateFormatter = SimpleDateFormat(dateFormat, Locale.getDefault())
        return dateFormatter.format(date)
    }
}