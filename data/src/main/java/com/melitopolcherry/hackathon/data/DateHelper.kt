package com.melitopolcherry.hackathon.data

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.text.SpannableString
import android.text.format.DateUtils
import android.text.style.StyleSpan
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object DateHelper {

    @SuppressLint("ConstantLocale")
    val promoCodeDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    @SuppressLint("ConstantLocale")
    val notificationsDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)

    @SuppressLint("ConstantLocale")
    val venueDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    @SuppressLint("ConstantLocale")
    val dateWithTimeFormatter = SimpleDateFormat("MMMM, d, hh:mm aa", Locale.US)

    @SuppressLint("ConstantLocale")
    val smallEventDateFormatter = SimpleDateFormat("d, hh:mm aa", Locale.US)

    @SuppressLint("ConstantLocale")
    var backendDateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)

    fun getStartOfDay(date: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        return calendar.time
    }

    fun getDatesStartAndEndDateInRange(range: Int): Pair<String, String> {
        val calendar = Calendar.getInstance()
        calendar.time = getStartOfDay(calendar.time)
        val dateStart = backendDateFormatter.format(calendar.time)
        calendar.add(Calendar.DATE, range)
        val dateEnd = backendDateFormatter.format(calendar.time)
        return Pair(dateStart, dateEnd)
    }

    fun checkPromoCodeDate(date: String): Boolean {
        val endDate = promoCodeDateFormat.parse(date)
        val today = Calendar.getInstance().time
        return endDate != null && endDate.after(today)
    }

    fun getExpirationLeftDays(endDate: String): String {
        val date = venueDateFormat.parse(endDate)
        val millionSeconds = date?.time!! - Calendar.getInstance().timeInMillis
        val days = TimeUnit.MILLISECONDS.toDays(millionSeconds)
        return when {
            days > 1L -> {
                "$days days left"
            }
            days == 1L -> {
                "$days day left"
            }
            days < 1L -> {
                "${TimeUnit.MILLISECONDS.toHours(millionSeconds)} hours left"
            }
            else -> {
                "wrong date"
            }
        }
    }

    fun isEventStartInHours(eventDate: String, hours: Long): Boolean {
        val calendar = Calendar.getInstance().time

        val endDate: Date = backendDateFormatter.parse(eventDate)!!

        var different = endDate.time - calendar.time
        val secondsInMilli: Long = 1000
        val minutesInMilli = secondsInMilli * 60
        val hoursInMilli = minutesInMilli * 60
        val daysInMilli = hoursInMilli * 24

        val elapsedDays = different / daysInMilli
        different %= daysInMilli

        val elapsedHours = different / hoursInMilli
        different %= hoursInMilli

        val elapsedMinutes = different / minutesInMilli
        different %= minutesInMilli

        return if (elapsedDays > 0L) {
            false
        } else {
            if (elapsedHours == hours && elapsedHours >= 0 && elapsedMinutes == 0L) {
                true
            } else {
                elapsedHours in 0 until hours && elapsedMinutes >= 0L
            }
        }
    }

    fun eventDateToMillis(eventDate: String): Long {
        val calendar = Calendar.getInstance().time
        val endDate: Date = backendDateFormatter.parse(eventDate)!!
        return endDate.time - calendar.time
    }

    @SuppressLint("SimpleDateFormat")
    fun formatWeatherDate(date: String): String {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

        val weatherDate = format.parse(date)
        val cal = Calendar.getInstance()
        cal.time = weatherDate
        val hour = cal.get(Calendar.HOUR_OF_DAY)

        if (hour == 0) {
            return "0 am"
        }

        return if (hour < 12) "$hour am"
        else "$hour pm"
    }

    @SuppressLint("SimpleDateFormat")
    fun dateToLong(date: String): Long {
        val dateInLong = backendDateFormatter.parse(date)
        return dateInLong?.time!!
    }

    @SuppressLint("SetTextI18n")
    fun dateForHomeEvent(eventDate: String?): SpannableString {
        if (eventDate != null) {
            val startDate = backendDateFormatter.parse(eventDate)!!
            val nowTime = Calendar.getInstance()
            val cal = Calendar.getInstance()
            cal.time = startDate
            val date = cal.get(Calendar.DATE)

            when {
                DateUtils.isToday(startDate.time) -> {
                    val formattedDate = smallEventDateFormatter.format(startDate)
                    val span = SpannableString("Today, $formattedDate")
                    span.setSpan(StyleSpan(Typeface.BOLD), 0, 5, 0)
                    return span
                }
                date - nowTime.get(Calendar.DATE) == 1 -> {
                    val formattedDate = smallEventDateFormatter.format(startDate)
                    val span = SpannableString("Tomorrow, $formattedDate")
                    span.setSpan(StyleSpan(Typeface.BOLD), 0, 9, 0)
                    return span
                }
                else -> {
                    return SpannableString(dateWithTimeFormatter.format(startDate))
                }
            }
        } else {
            return SpannableString("unable date")
        }
    }

    fun dateForEvent(eventDate: String?): String {
        return if (eventDate != null) {
            var startDate = Date()

            try {
                startDate = backendDateFormatter.parse(eventDate)!!
            } catch (e: java.text.ParseException) {
                e.printStackTrace()
            }
            dateWithTimeFormatter.format(startDate)
        } else {
            "unable date"
        }
    }

    fun dateForNotifications(eventDate: String?): String {
        return if (eventDate != null) {
            var startDate = Date()

            try {
                startDate = notificationsDateFormat.parse(eventDate)!!
            } catch (e: java.text.ParseException) {
                e.printStackTrace()
            }
            dateWithTimeFormatter.format(startDate)
        } else {
            "unable date"
        }
    }

    fun dateForStadium(eventDate: String?): String {
        return if (eventDate != null) {
            var startDate = Date()

            try {
                startDate = backendDateFormatter.parse(eventDate)!!
            } catch (e: java.text.ParseException) {
                e.printStackTrace()
            }
            val dateWithTimeFormatter = SimpleDateFormat("yyyyMMddHHmm", Locale.US)
            dateWithTimeFormatter.format(startDate)
        } else {
            "unable date"
        }
    }

    fun shortDate(eventDate: String?): String {

        return if (eventDate != null) {
            var startDate = Date()

            try {
                startDate = backendDateFormatter.parse(eventDate)!!
            } catch (e: java.text.ParseException) {
                e.printStackTrace()
            }
            SimpleDateFormat("MMMM, d, hh:mm aa", Locale.US).format(startDate)
        } else {
            "unable date"
        }
    }

    fun dateForTicket(eventDate: String?): String {
        return if (eventDate != null) {
            var startDate = Date()

            try {
                startDate = backendDateFormatter.parse(eventDate)!!
            } catch (e: java.text.ParseException) {
                e.printStackTrace()
            }
            dateWithTimeFormatter.format(startDate)
        } else {
            "unable date"
        }
    }
}