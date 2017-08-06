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
import org.bukkit.configuration.file.YamlConstructor;
import org.bukkit.configuration.file.YamlRepresenter;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.representer.Representer;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

@SuppressWarnings("unused")
public class YamlConfiguration extends FileConfiguration {
    protected static final String COMMENT_PREFIX = "# ";
    protected static final String BLANK_CONFIG = "{}\n";
    private final DumperOptions yamlOptions = new DumperOptions();
    private final Representer yamlRepresenter = new YamlRepresenter();
    private final Yaml yaml;
    
    public YamlConfiguration() {
        yaml = new Yaml(new YamlConstructor(), yamlRepresenter, yamlOptions);
    }
    
    public static YamlConfiguration loadConfiguration(final File file) {
        Validate.notNull(file, "File cannot be null");
        final YamlConfiguration config = new YamlConfiguration();
        
        try {
            config.load(file);
        } catch(final IOException | InvalidConfigurationException var4) {
            Bukkit.getLogger().log(Level.SEVERE, "Cannot load " + file, var4);
        }
        
        return config;
    }
    
    /**
     * @deprecated
     */
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    public static YamlConfiguration loadConfiguration(final InputStream stream) {
        Validate.notNull(stream, "Stream cannot be null");
        final YamlConfiguration config = new YamlConfiguration();
        
        try {
            config.load(new InputStreamReader(stream));
        } catch(final IOException | InvalidConfigurationException var3) {
            Bukkit.getLogger().log(Level.SEVERE, "Cannot load configuration from stream", var3);
        }
        
        return config;
    }
    
    public static YamlConfiguration loadConfiguration(final Reader reader) {
        Validate.notNull(reader, "Stream cannot be null");
        final YamlConfiguration config = new YamlConfiguration();
        
        try {
            config.load(reader);
        } catch(final IOException | InvalidConfigurationException var3) {
            Bukkit.getLogger().log(Level.SEVERE, "Cannot load configuration from stream", var3);
        }
        
        return config;
    }
    
    public String saveToString() {
        yamlOptions.setIndent(options().indent());
        yamlOptions.setDefaultFlowStyle(FlowStyle.BLOCK);
        yamlOptions.setAllowUnicode(true);
        yamlRepresenter.setDefaultFlowStyle(FlowStyle.BLOCK);
        final String header = buildHeader();
        String dump = yaml.dump(getValues(false));
        if(dump.equals("{}\n")) {
            dump = "";
        }
        
        return header + dump;
    }
    
    public void loadFromString(final String contents) throws InvalidConfigurationException {
        Validate.notNull(contents, "Contents cannot be null");
        
        final Map input;
        try {
            input = (Map) yaml.load(contents);
        } catch(final YAMLException var4) {
            throw new InvalidConfigurationException(var4);
        } catch(final ClassCastException var5) {
            throw new InvalidConfigurationException("Top level is not a Map.");
        }
        
        final String header = parseHeader(contents);
        if(!header.isEmpty()) {
            options().header(header);
        }
        
        if(input != null) {
            convertMapsToSections(input, this);
        }
    }
    
    protected void convertMapsToSections(final Map<?, ?> input, final ConfigurationSection section) {
        
        for(final Object o : input.entrySet()) {
            final Entry entry = (Entry) o;
            final String key = entry.getKey().toString();
            final Object value = entry.getValue();
            if(value instanceof Map) {
                convertMapsToSections((Map) value, section.createSection(key));
            } else {
                section.set(key, value);
            }
        }
    }
    
    protected String parseHeader(final String input) {
        final String[] lines = input.split("\r?\n", -1);
        final StringBuilder result = new StringBuilder();
        boolean readingHeader = true;
        boolean foundHeader = false;
        
        for(int i = 0; i < lines.length && readingHeader; ++i) {
            final String line = lines[i];
            if(line.startsWith("# ")) {
                if(i > 0) {
                    result.append('\n');
                }
                
                if(line.length() > "# ".length()) {
                    result.append(line.substring("# ".length()));
                }
                
                foundHeader = true;
            } else if(foundHeader && line.isEmpty()) {
                result.append('\n');
            } else if(foundHeader) {
                readingHeader = false;
            }
        }
        
        return result.toString();
    }
    
    protected String buildHeader() {
        final String header = options().header();
        if(options().copyHeader()) {
            final Configuration builder = getDefaults();
            if(builder != null && builder instanceof FileConfiguration) {
                final FileConfiguration lines = (FileConfiguration) builder;
                try {
                    final Method method = FileConfiguration.class.getMethod("buildHeader");
                    final String startedHeader = (String) method.invoke(lines);
                    if(startedHeader != null && !startedHeader.isEmpty()) {
                        return startedHeader;
                    }
                } catch(final NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        
        if(header == null) {
            return "";
        } else {
            final StringBuilder var6 = new StringBuilder();
            final String[] var7 = header.split("\r?\n", -1);
            boolean var8 = false;
            
            for(int i = var7.length - 1; i >= 0; --i) {
                var6.insert(0, '\n');
                if(var8 || !var7[i].isEmpty()) {
                    var6.insert(0, var7[i]);
                    var6.insert(0, "# ");
                    var8 = true;
                }
            }
            
            return var6.toString();
        }
    }
    
    public org.bukkit.configuration.file.YamlConfigurationOptions options() {
        if(options == null) {
            options = new YamlConfigurationOptions(this);
        }
        
        //noinspection ConstantConditions
        return (org.bukkit.configuration.file.YamlConfigurationOptions) options;
    }
}
