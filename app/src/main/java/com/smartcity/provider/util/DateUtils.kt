package com.smartcity.provider.util

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*


class DateUtils {

    companion object{

        private val TAG: String = "AppDebug"

        // dates from server look like this: "2019-07-23T03:28:01.406944Z"
        fun convertServerStringDateToLong(sd: String): Long{
            var stringDate = sd.removeRange(sd.indexOf("T") until sd.length)
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            try {
                val time = sdf.parse(stringDate).time
                return time
            } catch (e: Exception) {
                throw Exception(e)
            }
        }

        fun convertDatePickerStringDateToLong(stringDate: String): Long{
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            try {
                val c = Calendar.getInstance()
                c.time = sdf.parse(stringDate)
                c.add(Calendar.DATE, 1) // number of days to add
                val time = sdf.parse(sdf.format(c.time)).time

                return time
            } catch (e: Exception) {
                throw Exception(e)
            }
        }

        fun convertLongToStringDate(longDate: Long): String{
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            try {
                val date = sdf.format(Date(longDate))
                return date
            } catch (e: Exception) {
                throw Exception(e)
            }
        }

        fun convertLongToStringDateTime(longDate: Long): String{
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH)
            try {
                val date = sdf.format(Date(longDate))
                return date
            } catch (e: Exception) {
                throw Exception(e)
            }
        }

        fun convertStringToStringDate(date: String): String{
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.ENGLISH)
            try {
                val date = sdf.format(sdf.parse(date)).replace("T"," ")
                return date
            } catch (e: Exception) {
                throw Exception(e)
            }
        }

        fun convertStringToStringDateSimpleFormat(date: String): String{
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.ENGLISH)
            val test=parser.parse(date)
            val formatter  = SimpleDateFormat("d MMM", Locale.ENGLISH)
            val result= formatter.format(test)
            try {

                return result
            } catch (e: Exception) {
                throw Exception(e)
            }
        }

        fun convertStringToStringDateSimpleFormatSecond(date: String): String{
            val parser = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            val test=parser.parse(date)
            val formatter  = SimpleDateFormat("d MMM", Locale.ENGLISH)
            val result= formatter.format(test)
            try {

                return result
            } catch (e: Exception) {
                throw Exception(e)
            }
        }
    }


}