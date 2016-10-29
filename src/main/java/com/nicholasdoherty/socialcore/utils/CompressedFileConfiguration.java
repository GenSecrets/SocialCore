package com.nicholasdoherty.socialcore.utils;

import com.google.common.io.Files;
import org.apache.commons.lang3.Validate;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by john on 1/10/15.
 */
public class CompressedFileConfiguration extends YamlConfiguration{
    String id;
    public CompressedFileConfiguration(String id) {
        this.id = id;
    }

    @Override
    public void save(File file) throws IOException {
        Validate.notNull(file, "File cannot be null");

         Files.createParentDirs(file);
         String data = saveToString();

        Map<String, String> env = new HashMap<>();
        env.put("create","true");
        Path path = file.toPath();
        URI uri = URI.create("jar:" + path.toUri());
        try (FileSystem fs = FileSystems.newFileSystem(uri,env)) {
            Path nf = fs.getPath(id);
            try (Writer writer = java.nio.file.Files.newBufferedWriter(nf, StandardCharsets.UTF_8,StandardOpenOption.CREATE)) {
                writer.write(data);
            }
        }
    }

    @Override
    public void load(File file) throws IOException, InvalidConfigurationException {
        Validate.notNull(file, "File cannot be null");
        Path path = file.toPath();
        Map<String, String> env = new HashMap<>();
        env.put("create","true");
        URI uri = URI.create("jar:" + path.toUri());
        try(FileSystem fs = FileSystems.newFileSystem(uri,env)) {
            Path nf = fs.getPath(id);
            if (!java.nio.file.Files.exists(nf)) {
                java.nio.file.Files.createFile(nf);
            }
            try(Reader reader = java.nio.file.Files.newBufferedReader(nf, StandardCharsets.UTF_8)) {
                load(reader);
            }
        }
    }
    public static CompressedFileConfiguration loadConfiguration(File file, String id) throws IOException, InvalidConfigurationException {
          Validate.notNull(file, "File cannot be null");

          CompressedFileConfiguration config = new CompressedFileConfiguration(id);
        config.load(file);

          return config;
      }
}
