package com.nicholasdoherty.socialcore.components.courts.inputlib;

import com.nicholasdoherty.socialcore.components.courts.Courts;

import java.util.UUID;

/**
 * Created by john on 1/14/15.
 */
public abstract class IntegerInputRunnable implements InputRunnable {
    private UUID uuid;

    public IntegerInputRunnable(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public void run(String input) {
        if (input.equalsIgnoreCase("cancel"))
            return;
        try {
            int num = Integer.parseInt(input);
            if (valid(num)) {
                run(num);
            }else {
                InputLib inputLib = Courts.getCourts().getPlugin().getInputLib();
                inputLib.add(uuid, this);
            }
        }catch (Exception e) {
            InputLib inputLib = Courts.getCourts().getPlugin().getInputLib();
            inputLib.add(uuid, this);
        }
    }
    public void onInvalid() {

    }
    public abstract void run(int num);
    public boolean valid(int num) {
        return true;
    }
}
