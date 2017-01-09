package com.justinwells.mytravelproject.Misc;

import android.util.Log;
import java.util.Calendar;
import java.util.Date;

import static com.google.android.gms.plus.PlusOneDummyView.TAG;

/**
 * Created by justinwells on 12/22/16.
 */

public class CurrentDate {

    public static boolean isAfterToday (String date) {
        //makes sure entered date is after current date
        String todaysDate = getCurrentDate();

        int currentYear = Integer.parseInt(todaysDate.substring(0,4));
        int currentMonth = Integer.parseInt(todaysDate.substring(5,7));
        int currentDay = Integer.parseInt(todaysDate.substring(8));

        int enteredYear = Integer.parseInt(date.substring(0,4));

        int enteredMonth = Integer.parseInt(date.substring(5,7));
        if (!monthMakesSense(enteredMonth)) {
            return false;
        }

        int enteredDay = Integer.parseInt(date.substring(8));
        if (!dayMakesSense(enteredDay, enteredMonth)) {
            return false;
        }

        Log.d(TAG, "isAfterToday: " + currentYear + "-" + currentMonth + "-" + currentDay);
        Log.d(TAG, "isAfterToday: " + enteredYear + "-" + enteredMonth + "-" + enteredDay);

        if (enteredYear < currentYear || enteredMonth < currentMonth || enteredDay < currentDay) {
            return false;
        }

        return true;
    }

    public static String idealFlightDate () {
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH) + 3;
        if (month > 12) {
            month-=12;
        }

        String date = cal.get(Calendar.YEAR)+"-"+month+"-"+cal.get(Calendar.DATE);
        Log.d(TAG, "idealFlightDate: " + date);

        return date;
    }

    public static String returnDate () {
        String date = idealFlightDate();
        String [] seperated = date.split("-");
        int day = Integer.parseInt(seperated[2]);
        int month = Integer.parseInt(seperated[1]);
        int year = Integer.parseInt(seperated[0]);

        day += 7;

        if (day > getDaysInMonth(month)) {
            day -= getDaysInMonth(month);
            month++;
            if (month > 12) {
                month = 1;
                year++;
            }
        }

        String nextWeek = year + "-" + month + "-" + day;
        Log.d(TAG, "returnDate: " + nextWeek);

        return nextWeek;


    }

    public static boolean isValidDate (String date) {
        //makes sure entered date is valid
        if (date.length() != 10) {
            return false;
        }

        if (isValidFormat(date)) {
            return isAfterToday(date);
        }

        return false;
    }

    private static boolean isValidFormat (String date) {
        //makes sure entered date follows yyyy-mm-dd format
        if (!Character.isDigit(date.charAt(0))) {
            Log.d(TAG, "isValidFormat: problem");
            return false;
        }
        if (!Character.isDigit(date.charAt(1))) {
            Log.d(TAG, "isValidFormat: problem");
            return false;
        }
        if (!Character.isDigit(date.charAt(2))) {
            Log.d(TAG, "isValidFormat: problem");
            return false;
        }
        if (!Character.isDigit(date.charAt(3))) {
            Log.d(TAG, "isValidFormat: problem");
            return false;
        }
        if (date.charAt(4)!= '-') {
            Log.d(TAG, "isValidFormat: problem-");
            return false;
        }
        if (!Character.isDigit(date.charAt(5))) {
            Log.d(TAG, "isValidFormat: problem");
            return false;
        }
        if (!Character.isDigit(date.charAt(6))) {
            Log.d(TAG, "isValidFormat: problem");
            return false;
        }
        if (date.charAt(7) != '-') {
            Log.d(TAG, "isValidFormat: problem-");
            return false;
        }
        if (!Character.isDigit(date.charAt(8))) {
            Log.d(TAG, "isValidFormat: problem");
            return false;
        }
        if (!Character.isDigit(date.charAt(9))) {
            Log.d(TAG, "isValidFormat: problem");
            return false;
        }

        return true;
    }

    public static boolean dayMakesSense (int day, int month) {
        if (day < 1 || day > 31) {
            return false;
        }

        if (month == 9 || month == 4 || month == 6 || month == 11) {
            if (day > 30) {
                return false;
            }
        } else {
            if (day > 31) {
                return false;
            }
        }
        return true;
    }

    public static boolean monthMakesSense (int month) {
        if (month < 1 || month > 12) {
            return false;
        }

        return true;
    }

    public static String getCurrentDate () {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.YEAR)+"-"+(cal.get(Calendar.MONTH)+1)+"-"+cal.get(Calendar.DATE);
    }

    public static int lengthOfTrip (String departureDate, String returnDate) {
        int dayLeaving = Integer.parseInt(departureDate.substring(8));
        int dayReturning = Integer.parseInt(returnDate.substring(8));

        if (dayLeaving <= dayReturning) {
            return dayReturning - dayLeaving;
        }

        return 0;

    }

    public static int getDaysInMonth (int month) {

        if (month == 9 || month == 4 || month == 6 || month == 11) {
            return 30;
        }

        return 31;
    }



}
