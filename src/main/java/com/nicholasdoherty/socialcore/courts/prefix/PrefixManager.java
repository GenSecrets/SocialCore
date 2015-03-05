package com.nicholasdoherty.socialcore.courts.prefix;

import com.nicholasdoherty.socialcore.courts.Courts;

/**
 * Created by john on 3/1/15.
 */
public class PrefixManager {
    private Courts courts;
    public PrefixManager(Courts courts) {
        this.courts = courts;
        new PrefixListener(this);
    }

    public Courts getCourts() {
        return courts;
    }
}
