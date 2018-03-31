package com.nicholasdoherty.socialcore.emotes.extend;

/**
 * @author amy
 * @since 3/27/18.
 */
@FunctionalInterface
public interface EmoteExtension {
    String processText(String text, String[] data);
}
