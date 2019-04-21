package com.brightkey.nickfl.helpers

import java.util.*

object PeriodManager {

    private var periodStart: Date = Date()
    private var periodEnd: Date = Date()

    private fun periodDateFrom(timeStamp: String?): Date {
        if (timeStamp != null)
            return DateFormatters.dateFromString(timeStamp)
        return Date()
    }

    fun updatePeriodFullFrom(start: String?, end: String?) {
        periodStart = periodDateFrom(start)
        periodEnd = periodDateFrom(end)
    }

    fun updatePeriodFull(start: Date?, end: Date?) {
        if (start != null)
            periodStart = start
        if (end != null)
            periodEnd = end
    }

    fun updatePeriodStart(start: Date?) {
        if (start != null)
            periodStart = start
    }

    fun updatePeriodEnd(end: Date?) {
        if (end != null)
            periodEnd = end
    }

    fun isDateInPeriod(date: Date?): Boolean {
        if (date == null || date.before(periodStart) || date.after(periodEnd))
            return false
        return true
    }
}