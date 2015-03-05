package com.nicholasdoherty.socialcore.courts.inventorygui.views.calander;

import org.joda.time.DateTime;

/**
 * Created by john on 1/13/15.
 */
public interface ValidTimeSelector  {
    public boolean isValid(DateTime dateTime);
}
