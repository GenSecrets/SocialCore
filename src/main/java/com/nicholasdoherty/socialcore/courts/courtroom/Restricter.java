package com.nicholasdoherty.socialcore.courts.courtroom;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.UUID;

/**
 * Created by john on 1/16/15.
 */
public interface Restricter extends ConfigurationSerializable {
    public boolean canVote(UUID uuid);
}
