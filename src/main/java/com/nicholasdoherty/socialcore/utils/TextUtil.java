package com.nicholasdoherty.socialcore.utils;

import com.nicholasdoherty.socialcore.courts.objects.Citizen;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author amy
 * @since 7/26/18.
 */
@SuppressWarnings("unused")
public final class TextUtil {
    private static final DateFormat dateFormat;
    private static final String dateFormatString = "MM/dd/yy-HH:mm";
    
    static {
        dateFormat = new SimpleDateFormat(dateFormatString);
        dateFormat.setTimeZone(TimeZone.getDefault());
    }
    
    private TextUtil() {
    }
    
    public static List<String> citizenNames(final Iterable<? extends Citizen> citizens) {
        final List<String> names = new ArrayList<>();
        for(final Citizen citizen : citizens) {
            names.add(citizen.getName());
        }
        return names;
    }
    
    public static String fancyList(final List<String> list) {
        if(list.isEmpty()) {
            return "none";
        }
        if(list.size() == 1) {
            return list.get(0);
        }
        if(list.size() == 2) {
            return list.get(0) + " and " + list.get(1);
        }
        final StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < list.size(); i++) {
            if(i <= list.size() - 3) {
                stringBuilder.append(list.get(i)).append(", ");
            } else if(i == list.size() - 2) {
                stringBuilder.append(", and ").append(list.get(i));
            } else {
                stringBuilder.append(list.get(i));
            }
        }
        return stringBuilder.toString();
    }
    
    public static String formatDouble(final double in, final int places) {
        if(places == 0) {
            return "" + (int) Math.round(in);
        }
        final double modifier = places * 10;
        return "" + (double) Math.round(in * modifier) / modifier;
    }
    
    public static String numberWithLeadingZeros(final int number, final int digits) {
        if(digits == 1) {
            return number + "";
        }
        final StringBuilder formatted = new StringBuilder(number + "");
        while(formatted.length() < digits) {
            formatted.insert(0, '0');
        }
        return formatted.toString();
    }
    
    public static String formatDate(final long time) {
        return dateFormat.format(time);
    }
    
    public static long dateFromString(String in) {
        in = in.trim();
        try {
            return dateFormat.parse(in).getTime();
        } catch(final ParseException e) {
            return -1;
        }
    }
    
    public static String getServerTimeZoneDisplayName() {
        //noinspection UseOfObsoleteDateTimeApi
        final TimeZone timeZone = TimeZone.getDefault();
        final boolean daylight = timeZone.inDaylightTime(new Date());
        final Locale locale = Locale.getDefault();
        return timeZone.getDisplayName(daylight, TimeZone.LONG, locale);
    }
    
    public static String dateFormat() {
        return dateFormatString;
    }
}
