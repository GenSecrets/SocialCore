package com.nicholasdoherty.socialcore.courts.judges.secretaries;


import com.nicholasdoherty.socialcore.courts.judges.Judge;
import com.nicholasdoherty.socialcore.courts.objects.Citizen;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.Map;
import java.util.UUID;

/**
 * Created by john on 1/3/15.
 */
public class Secretary extends Citizen implements ConfigurationSerializable{
    private Judge judge;

    public Secretary(String name, UUID uuid, Judge judge) {
        super(name, uuid);
        this.judge = judge;
    }

    public Judge getJudge() {
        return judge;
    }
    public Secretary(Map<String, Object> map) {
        super(map);
    }
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = super.serialize();
        return map;
    }

    public void setJudge(Judge judge) {
        this.judge = judge;
    }
}
