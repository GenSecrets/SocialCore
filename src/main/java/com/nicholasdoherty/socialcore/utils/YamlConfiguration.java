package com.nicholasdoherty.socialcore.utils;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfigurationOptions;
import org.bukkit.configuration.file.YamlConstructor;
import org.bukkit.configuration.file.YamlRepresenter;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.representer.Representer;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;

public class YamlConfiguration extends FileConfiguration {
    protected static final String COMMENT_PREFIX = "# ";
    protected static final String BLANK_CONFIG = "{}\n";
    private final DumperOptions yamlOptions = new DumperOptions();
    private final Representer yamlRepresenter = new YamlRepresenter();
    private final Yaml yaml;

    public YamlConfiguration() {
        this.yaml = new Yaml(new YamlConstructor(), this.yamlRepresenter, this.yamlOptions);
    }

    public String saveToString() {
        this.yamlOptions.setIndent(this.options().indent());
        this.yamlOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        this.yamlOptions.setAllowUnicode(true);
        this.yamlRepresenter.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        String header = this.buildHeader();
        String dump = this.yaml.dump(this.getValues(false));
        if(dump.equals("{}\n")) {
            dump = "";
        }

        return header + dump;
    }

    public void loadFromString(String contents) throws InvalidConfigurationException {
        Validate.notNull(contents, "Contents cannot be null");

        Map input;
        try {
            input = (Map)this.yaml.load(contents);
        } catch (YAMLException var4) {
            throw new InvalidConfigurationException(var4);
        } catch (ClassCastException var5) {
            throw new InvalidConfigurationException("Top level is not a Map.");
        }

        String header = this.parseHeader(contents);
        if(header.length() > 0) {
            this.options().header(header);
        }

        if(input != null) {
            this.convertMapsToSections(input, this);
        }

    }

    protected void convertMapsToSections(Map<?, ?> input, ConfigurationSection section) {
        Iterator i$ = input.entrySet().iterator();

        while(i$.hasNext()) {
            Map.Entry entry = (Map.Entry)i$.next();
            String key = entry.getKey().toString();
            Object value = entry.getValue();
            if(value instanceof Map) {
                this.convertMapsToSections((Map)value, section.createSection(key));
            } else {
                section.set(key, value);
            }
        }

    }

    protected String parseHeader(String input) {
        String[] lines = input.split("\r?\n", -1);
        StringBuilder result = new StringBuilder();
        boolean readingHeader = true;
        boolean foundHeader = false;

        for(int i = 0; i < lines.length && readingHeader; ++i) {
            String line = lines[i];
            if(line.startsWith("# ")) {
                if(i > 0) {
                    result.append("\n");
                }

                if(line.length() > "# ".length()) {
                    result.append(line.substring("# ".length()));
                }

                foundHeader = true;
            } else if(foundHeader && line.length() == 0) {
                result.append("\n");
            } else if(foundHeader) {
                readingHeader = false;
            }
        }

        return result.toString();
    }

    protected String buildHeader() {
        String header = this.options().header();
        if(this.options().copyHeader()) {
            Configuration builder = this.getDefaults();
            if(builder != null && builder instanceof FileConfiguration) {
                FileConfiguration lines = (FileConfiguration)builder;
                try {
                    Method method = FileConfiguration.class.getMethod("buildHeader");
                    String startedHeader = (String) method.invoke(lines);
                    if(startedHeader != null && startedHeader.length() > 0) {
                        return startedHeader;
                    }
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

            }
        }

        if(header == null) {
            return "";
        } else {
            StringBuilder var6 = new StringBuilder();
            String[] var7 = header.split("\r?\n", -1);
            boolean var8 = false;

            for(int i = var7.length - 1; i >= 0; --i) {
                var6.insert(0, "\n");
                if(var8 || var7[i].length() != 0) {
                    var6.insert(0, var7[i]);
                    var6.insert(0, "# ");
                    var8 = true;
                }
            }

            return var6.toString();
        }
    }

    public YamlConfigurationOptions options() {
        if(this.options == null) {
            this.options = new com.nicholasdoherty.socialcore.utils.YamlConfigurationOptions(this);
        }

        return (YamlConfigurationOptions)this.options;
    }

    public static YamlConfiguration loadConfiguration(File file) {
        Validate.notNull(file, "File cannot be null");
        YamlConfiguration config = new YamlConfiguration();

        try {
            config.load(file);
        } catch (FileNotFoundException var3) {
            ;
        } catch (IOException var4) {
            Bukkit.getLogger().log(Level.SEVERE, "Cannot load " + file, var4);
        } catch (InvalidConfigurationException var5) {
            Bukkit.getLogger().log(Level.SEVERE, "Cannot load " + file, var5);
        }

        return config;
    }

    /** @deprecated */
    @Deprecated
    public static YamlConfiguration loadConfiguration(InputStream stream) {
        Validate.notNull(stream, "Stream cannot be null");
        YamlConfiguration config = new YamlConfiguration();

        try {
            config.load(stream);
        } catch (IOException var3) {
            Bukkit.getLogger().log(Level.SEVERE, "Cannot load configuration from stream", var3);
        } catch (InvalidConfigurationException var4) {
            Bukkit.getLogger().log(Level.SEVERE, "Cannot load configuration from stream", var4);
        }

        return config;
    }

    public static YamlConfiguration loadConfiguration(Reader reader) {
        Validate.notNull(reader, "Stream cannot be null");
        YamlConfiguration config = new YamlConfiguration();

        try {
            config.load(reader);
        } catch (IOException var3) {
            Bukkit.getLogger().log(Level.SEVERE, "Cannot load configuration from stream", var3);
        } catch (InvalidConfigurationException var4) {
            Bukkit.getLogger().log(Level.SEVERE, "Cannot load configuration from stream", var4);
        }

        return config;
    }
}
