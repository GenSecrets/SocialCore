package com.nicholasdoherty.socialcore.utils;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

/**
 * @author amy
 * @since 7/26/18.
 */
@SuppressWarnings("unused")
public final class SCSerializer {
    private SCSerializer() {
    }
    
    public static String serialize(final ConfigurationSerializable configurationSerializable) {
        final YamlConfiguration yamlConfiguration = new YamlConfiguration();
        yamlConfiguration.set("obj", configurationSerializable);
        return yamlConfiguration.saveToString();
    }
    
    public static Object deserialize(final String in) {
        final YamlConfiguration yamlConfiguration = new YamlConfiguration();
        try {
            yamlConfiguration.loadFromString(in);
            return yamlConfiguration.get("obj");
        } catch(final InvalidConfigurationException e) {
            e.printStackTrace();
        }
        return null;
    }
}
