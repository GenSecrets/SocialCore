package com.nicholasdoherty.socialcore.courts.cases;

import com.nicholasdoherty.socialcore.courts.courtroom.PostCourtAction;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by john on 1/21/15.
 */
public class Resolve implements ConfigurationSerializable{
    List<String> stringPostCourtActions;

    public Resolve(List<String> postCourtActionList) {
        this.stringPostCourtActions = postCourtActionList;
    }
    public static Resolve fromPost(List<PostCourtAction> postCourtActions) {
        List<String> postCourtStrings = new ArrayList<>();
        for (PostCourtAction postCourtAction : postCourtActions) {
            postCourtStrings.add(postCourtAction.prettyDescription());
        }
        return new Resolve(postCourtStrings);
    }

    public List<String> getPostCourtActionList() {
        return stringPostCourtActions;
    }

    public Resolve(Map<String, Object> map)  {
        stringPostCourtActions = new ArrayList<>();
        if (map.containsKey("post-court-actions")) {
            List<String> postCourtS = (List<String>) map.get("post-court-actions");
            stringPostCourtActions.addAll(postCourtS);
        }
    }
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("post-court-actions",stringPostCourtActions);
        return map;
    }
}
