package com.nicholasdoherty.socialcore.utils;
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import org.apache.commons.lang.Validate;
import org.bukkit.configuration.file.FileConfigurationOptions;

public class YamlConfigurationOptions extends FileConfigurationOptions {
    private int indent = 2;

    protected YamlConfigurationOptions(YamlConfiguration configuration) {
        super(configuration);
    }

    public org.bukkit.configuration.file.YamlConfiguration configuration() {
        return (org.bukkit.configuration.file.YamlConfiguration)super.configuration();
    }

    public YamlConfigurationOptions copyDefaults(boolean value) {
        super.copyDefaults(value);
        return this;
    }

    public YamlConfigurationOptions pathSeparator(char value) {
        super.pathSeparator(value);
        return this;
    }

    public YamlConfigurationOptions header(String value) {
        super.header(value);
        return this;
    }

    public YamlConfigurationOptions copyHeader(boolean value) {
        super.copyHeader(value);
        return this;
    }

    public int indent() {
        return this.indent;
    }

    public YamlConfigurationOptions indent(int value) {
        Validate.isTrue(value >= 2, "Indent must be at least 2 characters");
        Validate.isTrue(value <= 9, "Indent cannot be greater than 9 characters");
        this.indent = value;
        return this;
    }
}