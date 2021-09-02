package com.nicholasdoherty.socialcore.utils.time.condition;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;


/**
 * Created by john on 8/6/14.
 */
public abstract class TimeCondition implements ConfigurationSerializable {
    private boolean done = false;
    private Runnable run;

    protected TimeCondition(boolean done, Runnable run) {
        this.done = done;
        this.run = run;
    }


    public boolean isDone() {
        return done;
    }
    private void run() {
        if (run != null) {
            run.run();
        }
    }

    public Runnable getRun() {
        return run;
    }

    /**
     *
     * @return If it's done.
     */
    public abstract boolean check();
    public void onDone() {
        done = true;
        run();
    }
    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy-HH:mm:ss");

    public static TimeCondition fromString(String line, Runnable run) {
        dateFormatter.setTimeZone(TimeZone.getDefault());
        line = line.toLowerCase();
        if (line == null)
            return null;
        if (line.contains(":")) {
            long time = 0;
            try {
                Date date = dateFormatter.parse(line);
                time = date.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return new RealTimeCondition(run,false,time);
        }else {
            long totalTicks = 0;
            totalTicks += getTicks(line,"d");
            totalTicks += getTicks(line,"h");
            totalTicks += getTicks(line,"m");
            totalTicks += getTicks(line,"s");
            totalTicks += getTicks(line,"t");
            return new TickCondition(run,false,totalTicks);
        }
    }

    public void setRun(Runnable run) {
        this.run = run;
    }
    protected Map<String, Object> baseSerialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("done",done);
        return map;
    }
    public static TimeCondition fromString(String line) {
        return fromString(line,null);
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
    public abstract long tickLength();
    public abstract long ticksLeft();
}
