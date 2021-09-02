package com.nicholasdoherty.socialcore.components.emotes.extend;

/**
 * @author amy
 * @since 3/27/18.
 */
@FunctionalInterface
public interface EmoteExtension {
    String processText(String text, String[] data);
}
