package com.brightkey.nickfl.helpers

import java.util.*

enum class Periods {
    All,
    Year2020,
    Year2019,
    Year2018
}

class PeriodManager {

    private var periodStart: Date = veryOldDate()
    private var periodEnd: Date = veryNewDate()
    var period: Periods = Periods.All

    fun setCurrentPeriod(period: Periods) {
        this.period = period
    }
    private fun veryOldDate(): Date {
        return dateYear(1970, true)
    }
    private fun veryNewDate(): Date {
        val cal = Calendar.getInstance()
        return cal.time
    }
    private fun dateYear(year: Int, start: Boolean): Date {
        val cal = Calendar.getInstance()
        val month: Int = if (start) 0 else 11
        val day: Int = if (start) 1 else 31
        cal.set(year, month, day)
        return cal.time
    }
//    private fun periodDateFrom(timeStamp: String?): Date {
//        if (timeStamp != null)
//            return DateFormatters.dateFromString(timeStamp)
//        return Date()
//    }

//    fun updatePeriodFullFrom(start: String?, end: String?) {
//        periodStart = periodDateFrom(start)
//        periodEnd = periodDateFrom(end)
//    }

    fun updatePeriodForYear(year: Int) {
        periodStart = dateYear(year, true)
        periodEnd = dateYear(year, false)
    }

    fun updatePeriodForToday() {
        periodStart = veryOldDate()
        periodEnd = veryNewDate()
    }

//    fun updatePeriodFull(start: Date?, end: Date?) {
//        if (start != null)
//            periodStart = start
//        if (end != null)
//            periodEnd = end
//    }

//    fun updatePeriodStart(start: Date?) {
//        if (start != null)
//            periodStart = start
//    }

//    fun updatePeriodEnd(end: Date?) {
//        if (end != null)
//            periodEnd = end
//    }

    fun isDateInPeriod(date: Date?): Boolean {
        if (date == null || date.before(periodStart) || date.after(periodEnd))
            return false
        return true
    }

    companion object {
        val shared = PeriodManager()
    }
}