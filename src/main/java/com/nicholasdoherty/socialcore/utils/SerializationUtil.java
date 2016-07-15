package com.nicholasdoherty.socialcore.utils;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

/**
 * Created by john on 3/4/15.
 */
public class SerializationUtil {
    public static String serialize(ConfigurationSerializable configurationSerializable) {
        org.bukkit.configuration.file.YamlConfiguration yamlConfiguration = new YamlConfiguration();
        yamlConfiguration.set("obj",configurationSerializable);
        return yamlConfiguration.saveToString();
    }
    public static Object deserialize(String in) {
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        try {
            yamlConfiguration.loadFromString(in);
            return yamlConfiguration.get("obj");
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
        return null;
    }
}
