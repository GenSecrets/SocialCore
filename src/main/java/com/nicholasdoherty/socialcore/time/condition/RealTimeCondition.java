package com.nicholasdoherty.socialcore.time.condition;

import java.util.Date;
import java.util.Map;

/**
 * Created by john on 8/6/14.
 */
public class RealTimeCondition extends TimeCondition {
    long endTime;

    public RealTimeCondition(Runnable run, boolean done,long endTime) {
        super(done,run);
        this.endTime = endTime;
    }



    @Override
    public boolean check() {
        long curTime = new Date().getTime();
        if (curTime > endTime) {
            onDone();
            return true;
        }
        return false;
    }

    @Override
    public long tickLength() {
        long diff = endTime - new Date().getTime();
        diff = diff /1000;
        return diff/20;
    }

    @Override
    public long ticksLeft() {
        return tickLength();
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = baseSerialize();
        map.put("end-time",endTime);
        return map;
    }

    public RealTimeCondition deserialize(Map<String, Object> map) {
        long end = new Long(map.get("end-time")+"");
        boolean done = (boolean) map.get("done");
        return new RealTimeCondition(null,done, end);
    }

}
