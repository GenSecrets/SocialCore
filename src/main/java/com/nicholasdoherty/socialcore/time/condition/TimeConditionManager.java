package com.nicholasdoherty.socialcore.time.condition;

import com.nicholasdoherty.socialcore.time.Clock;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by john on 8/6/14.
 */
public class TimeConditionManager {
    Set<TimeCondition> notDone;

    public TimeConditionManager(Set<TimeCondition> notDone) {
        this.notDone = notDone;
        Clock.register(new Runnable() {
            @Override
            public void run() {
                onTick();
            }
        });
    }

    public TimeConditionManager() {
        notDone = new HashSet<>();
        Clock.register(new Runnable() {
            @Override
            public void run() {
                onTick();
            }
        });
    }

    private void onTick() {
        Set<TimeCondition> toRemove =null ;
        for (TimeCondition timeCondition : new ArrayList<>(notDone)) {
            if (timeCondition.check()) {
                if (toRemove == null) {
                    toRemove = new HashSet<TimeCondition>();
                }
                toRemove.add(timeCondition);
            }
        }
        if (toRemove != null) {
            for (TimeCondition timeCondition : toRemove) {
                notDone.remove(timeCondition);
            }
        }
    }

    public void register(TimeCondition timeCondition) {
        notDone.add(timeCondition);
        if (timeCondition instanceof TickCondition) {
            ((TickCondition) timeCondition).start();
        }
    }

    public Set<TimeCondition> getNotDone() {
        return notDone;
    }

    public void remove(TimeCondition timeCondition) {
        notDone.remove(timeCondition);
    }
}
