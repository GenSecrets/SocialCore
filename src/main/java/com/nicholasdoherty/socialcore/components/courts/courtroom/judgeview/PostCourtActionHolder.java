package com.nicholasdoherty.socialcore.components.courts.courtroom.judgeview;

import com.nicholasdoherty.socialcore.components.courts.cases.Case;
import com.nicholasdoherty.socialcore.components.courts.courtroom.PostCourtAction;

import java.util.List;

/**
 * Created by john on 3/2/15.
 */
public interface PostCourtActionHolder {
    public void addPostCourtAction(PostCourtAction postCourtAction);
    public Case getCase();
    public void removePostCourtAction(PostCourtAction postCourtAction);
    public List<PostCourtAction> getPostCourtActions();
}
