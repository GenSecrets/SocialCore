package com.nicholasdoherty.socialcore.components.emotes.extend;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author amy
 * @since 3/27/18.
 */
public class EmoteExtender {
    private final Map<String, EmoteExtension> extensions = new ConcurrentHashMap<>();
    private final Random random = new Random();
    
    public EmoteExtender() {
        extensions.put("roll", (text, data) -> {
            try {
                final int lim = Integer.parseInt(data[0]);
                final int roll = random.nextInt(lim) + 1;
                return Integer.toString(roll);
            } catch(final NumberFormatException e) {
                return String.format("[Invalid number: %s]", data[0]);
            } catch(final IndexOutOfBoundsException e) {
                return "[No number data]";
            }
        });
        extensions.put("random", (text, data) -> {
            try {
                final int lim = Integer.parseInt(data[0]);
                final int roll = random.nextInt(lim);
                return Integer.toString(roll);
            } catch(final NumberFormatException e) {
                return String.format("[Invalid number: %s]", data[0]);
            } catch(final IndexOutOfBoundsException e) {
                return "[No number data]";
            }
        });
    }
    
    public String process(String text) {
        final Pattern p = Pattern.compile("\\{([^}]*)}");
        final Matcher m = p.matcher(text);
        while(m.find()) {
            final String group = m.group(1);
            final String[] split = group.split(":", 2);
            final String[] data = new String[split.length - 1];
            System.arraycopy(split, 1, data, 0, data.length);
            text = text.replaceFirst("\\{" + group + "}", extensions.get(split[0]).processText(group, data));
        }
        return text;
    }
}
