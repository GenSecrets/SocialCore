package com.nicholasdoherty.socialcore.utils.time.condition;

import com.nicholasdoherty.socialcore.utils.time.Clock;

import java.util.Map;

/**
 * Created by john on 8/6/14.
 */
public class TickCondition extends TimeCondition{
    private boolean started;
    private long length;
    private long endTime;
    private long ticksLeft = -1;

    protected TickCondition(Runnable run,boolean done, long length) {
        super(done,run);
        this.length = length;
    }

    public TickCondition(long length,boolean done, long ticksLeft) {
        super(done,null);
        this.length = length;
        this.ticksLeft = ticksLeft;
    }

    public void start() {
        started = true;

        endTime = Clock.getTime() + length;
    }
    @Override
    public boolean check() {
        if (this.isDone())
            return true;
        if (!started) {
            return false;
        }
        long currentTime = Clock.getTime();
        if (currentTime
                >= endTime) {
            onDone();
            return true;
        }
        return false;
    }

    @Override
    public long tickLength() {
        return length;
    }
    @Override
    public long ticksLeft() {
        if (!started)
            return tickLength();
        long ticksLeft = endTime - Clock.getTime();
        if (ticksLeft < 0)
            ticksLeft = 0;
        return ticksLeft;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getTicksLeft() {
        return ticksLeft;
    }

    public void setTicksLeft(long ticksLeft) {
        this.ticksLeft = ticksLeft;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TickCondition that = (TickCondition) o;

        if (endTime != that.endTime) return false;
        if (length != that.length) return false;
        if (started != that.started) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (started ? 1 : 0);
        result = 31 * result + (int) (length ^ (length >>> 32));
        result = 31 * result + (int) (endTime ^ (endTime >>> 32));
        return result;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = baseSerialize();
        if (length <0)
            length = 0;
        map.put("length",length);
        map.put("ticks-left",ticksLeft());
        return map;
    }
    public static TickCondition deserialize(Map<String, Object> map) {
        long length = new Long(String.valueOf(map.get("length")));
        long ticksLeft = new Long(String.valueOf(map.get("ticks-left")));
        boolean done = (boolean) map.get("done");
        return new TickCondition(length,done,ticksLeft);
    }
}
