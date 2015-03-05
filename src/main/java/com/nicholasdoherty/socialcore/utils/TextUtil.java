package com.nicholasdoherty.socialcore.utils;

import com.nicholasdoherty.socialcore.courts.objects.Citizen;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by john on 1/6/15.
 */
public class TextUtil {
    private static DateFormat dateFormat;
    private static String dateFormatString = "MM/dd/yy-HH:mm";
    static {
        dateFormat= new SimpleDateFormat(dateFormatString);
        dateFormat.setTimeZone(TimeZone.getDefault());

    }
    public static List<String> citizenNames(Iterable<? extends Citizen> citizens) {
        List<String> names = new ArrayList<>();
        for (Citizen citizen : citizens) {
            names.add(citizen.getName());
        }
        return names;
    }
    public static String fancyList(List<String> list) {
        if (list.size() == 0)
            return "none";
        if (list.size() == 1) {
            return list.get(0);
        }
        if (list.size() == 2) {
            return list.get(0) + " and " + list.get(1);
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i <= list.size()-3) {
                stringBuilder.append(list.get(i) + ", ");
            }else if (i == list.size() - 2) {
                stringBuilder.append(", and " + list.get(i));
            }else {
                stringBuilder.append(list.get(i));
            }
        }
        return stringBuilder.toString();
    }
    public static String formatDouble(double in, int places) {
        if (places == 0) {
            return ""+((int) Math.round(in));
        }
        double modifier = places * 10;
        return "" + (double)Math.round(in * modifier) / modifier;
    }
    public static String numberWithLeadingZeros(int number, int digits) {
        if (digits == 1)
            return number+"";
        String formatted = number+"";
        while (formatted.length() < digits) {
            formatted = "0" + formatted;
        }
        return formatted;
    }
    public static String formatDate(long time) {
        return dateFormat.format(time);
    }
    public static long dateFromString(String in) {
        in = in.trim();
        try {
            return dateFormat.parse(in).getTime();
        } catch (ParseException e) {
            return -1;
        }
    }
    public static String getServerTimeZoneDisplayName()
    {
        final TimeZone timeZone = TimeZone.getDefault();
        final boolean daylight = timeZone.inDaylightTime(new Date());
        final Locale locale = Locale.getDefault();
        return timeZone.getDisplayName(daylight, TimeZone.LONG, locale);
    }
    public static String dateFormat() {
        return dateFormatString;
    }
}
