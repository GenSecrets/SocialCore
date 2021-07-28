package com.nicholasdoherty.socialcore.utils;

import com.nicholasdoherty.socialcore.SocialPlayer;
import com.nicholasdoherty.socialcore.marriages.types.Marriage;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MarriagesUtil {
    public static int[] paginateLists(int page, String[] args, List<String> listItems){
        if(args.length == 1) {
            page = Integer.parseInt(args[0]) - 1;
        }
        if(page < 0) {
            page = 0;
        }
        final int perPage = 10;
        int lowerBound = page * perPage;
        int upperBound = lowerBound + perPage;
        if(upperBound >= listItems.size()) {
            upperBound = listItems.size();
            if(upperBound < 0) {
                upperBound = 0;
            }
            if(lowerBound >= listItems.size()) {
                lowerBound = upperBound - perPage;
                if(lowerBound < 0) {
                    lowerBound = 0;
                }
            }
        }

        return new int[]{upperBound, lowerBound};
    }

    /**
     * @return - Return the text name of the month based on the month number
     */
    public static String getMonth() {
        switch (Calendar.getInstance().get(Calendar.MONTH)) {
            case 0 : return "January";
            case 1 : return "February";
            case 2: return "March";
            case 3 : return "April";
            case 4 : return "May";
            case 5 : return "June";
            case 6 : return "July";
            case 7 : return "August";
            case 8 : return "September";
            case 9 : return "October";
            case 10 : return "November";
            case 11 : return "December";
            default : return "ERROR";
        }
    }

    public static SimpleDateFormat parserSDF(){
        return new SimpleDateFormat("MMMMM d, yyyy");
    }

    public static List<String> removeLineWith(final List<String> l, @SuppressWarnings("SameParameterValue") final CharSequence with) {
        new ArrayList<>(l).stream().filter(s -> s.contains(with)).forEach(l::remove);
        return l;
    }

    public static ItemMeta removeEngagementLine(ItemMeta meta, SocialPlayer player1, SocialPlayer player2, Marriage m) {
        List<String> l = new ArrayList<>();
        if(meta.getLore() != null) {
            l.addAll(meta.getLore());
        }
        l = MarriagesUtil.removeLineWith(l, "Engaged on");
        l.add(player1.getPlayerName() + " + " + player2.getPlayerName() + " 4ever");
        l.add("Married on " + m.getDate() + " by " + m.getPriest());
        meta.setLore(l);
        return meta;
    }
}
