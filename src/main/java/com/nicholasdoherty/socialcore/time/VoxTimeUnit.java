package com.nicholasdoherty.socialcore.time;

/**
 * Created by john on 8/6/14.
 */
public enum VoxTimeUnit {
    YEAR(20L*60L*60L*24L*365L),MONTH(20L*60L*60L*24L*30L),DAY(20L*60L*60L*24L),HOUR(20L*60L*60L),MINUTE(20L*60L),SECOND(20L),TICK(1L);
    long ticks;

    VoxTimeUnit(long ticks) {
        this.ticks = ticks;
    }

    public long getTicks() {
        return ticks;
    }
    public static VoxTimeUnit byName(String in) {
        in = in.toLowerCase();
        if (in.length() == 0)
            return TICK;
        char first = in.charAt(0);
        if (first == 'y') {
            return YEAR;
        }
        if (first == 'm') {
            return MONTH;
        }if (first == 'd') {
            return DAY;
        }if (first == 'h') {
            return HOUR;
        }if (first == 'm') {
            return MINUTE;
        }if (first == 's') {
            return SECOND;
        }
        return TICK;
    }
    public static long getTicks(String in) {
        long totalTicks = 0;
        totalTicks += getTicks(in,"d");
        totalTicks += getTicks(in,"h");
        totalTicks += getTicks(in,"m");
        totalTicks += getTicks(in,"s");
        totalTicks += getTicks(in,"t");
        return totalTicks;
    }
    private static Long getTicks(String in, String type) {
        if (!in.contains(type))
            return Long.valueOf(0);
        String before = in.split(type)[0].trim();
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = before.length()-1; i >= 0; i--) {
            char character = before.charAt(i);
            if (Character.isDigit(character) || character == '.') {
                stringBuffer.insert(0,character);
            }else {
                break;
            }
        }
        double asNumber = Double.valueOf(stringBuffer.toString());
        if (type.equalsIgnoreCase("d")) {
            return Math.round(asNumber*20*60*60*24);
        }
        if (type.equalsIgnoreCase("h")) {
            return Math.round(asNumber*20*60*60);
        }
        if (type.equalsIgnoreCase("m")) {
            return Math.round(asNumber*20*60);
        }
        if (type.equalsIgnoreCase("s")) {
            return Math.round(asNumber*20);
        }
        return new Long(Math.round(asNumber));
    }
    private static long MILLISECONDS_IN_TICK = 50;
    public long toMillis(long unit) {
        long ticks = unit * this.getTicks();
        return ticks*MILLISECONDS_IN_TICK;
    }
    public long fromMillis(long millis) {
        long ticks = Math.round(millis / MILLISECONDS_IN_TICK);
        return ticks / this.getTicks();
    }
}
