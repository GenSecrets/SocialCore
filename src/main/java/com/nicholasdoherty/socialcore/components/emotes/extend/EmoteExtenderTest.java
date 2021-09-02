package com.nicholasdoherty.socialcore.components.emotes.extend;

import org.junit.Test;

/**
 * @author amy
 * @since 3/27/18.
 */
public class EmoteExtenderTest {
    @Test
    public void process() {
        {
            final String process = new EmoteExtender().process("Test roll: {roll:20} / 20");
            System.out.println("Output: " + process);
        }
        {
            final String process = new EmoteExtender().process("Test roll: {roll:asdf} / asdf");
            System.out.println("Output: " + process);
        }
        {
            final String process = new EmoteExtender().process("Test roll: {roll} / ");
            System.out.println("Output: " + process);
        }
    }
}