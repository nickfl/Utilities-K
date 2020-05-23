package com.brightkey.nickfl.myutilities.helpers

import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object DateFormatters {
    fun dateStringFromCalendar(cal: Calendar): String {
        val dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM)
        return dateFormat.format(cal.time)
    }

    fun dateStringFromDate(date: Date): String {
        val cal = Calendar.getInstance()
        cal.time = date
        val dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM)
        return dateFormat.format(cal.time)
    }

    fun monthStringFromCalendar(cal: Calendar): String {
        val dateFormat = SimpleDateFormat("MMM, yyyy", Locale.US)
        return dateFormat.format(cal.time)
    }

    fun dateFromString(dateString: String): Date {
        val dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM)
        var convertedDate = Date()
        try {
            convertedDate = dateFormat.parse(dateString)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return convertedDate
    }
}
