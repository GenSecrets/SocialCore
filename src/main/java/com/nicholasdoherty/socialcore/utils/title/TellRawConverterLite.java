package com.nicholasdoherty.socialcore.utils.title;

import org.bukkit.ChatColor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 2/27/15.
 */
public class TellRawConverterLite {
    public static String convertToJSON(String string)
    {
        JSONObject json = new JSONObject();
        json.put("text", "");

        JSONArray texts = new JSONArray();

        List colors = new ArrayList();
        for (int i = 0; i < string.length() - 1; i++) {
            String region = string.substring(i, i + 2);
            if (region.matches("(\u00A7([a-fk-or0-9]))")) {
                colors.add(region);
            }
        }
        String[] split = string.split("(\u00A7([a-fk-or0-9]))");
        for (int i = 0; i < colors.size(); i++) {
            JSONObject raw = new JSONObject();
            raw.put("text", split[(i + 1)]);
            raw.put("color", ChatColor.getByChar(((String) colors.get(i)).substring(1)).name().toLowerCase());
            texts.add(raw);
        }

        json.put("extra", texts);
        return json.toString();
    }
}
