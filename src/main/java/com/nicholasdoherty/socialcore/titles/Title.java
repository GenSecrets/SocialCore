package com.nicholasdoherty.socialcore.titles;

/**
 * Created by john on 7/2/15.
 */
public class Title {
    private String name,prefix,suffix;

    public Title(String name, String prefix, String suffix) {
        this.name = name;
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public String getName() {
        return name;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getSuffix() {
        return suffix;
    }
}
