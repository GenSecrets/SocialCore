package com.nicholasdoherty.socialcore.genders;

import com.nicholasdoherty.socialcore.SocialCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GenderTabCompleter implements TabCompleter {
    SocialCore sc;
    Genders genders;

    public GenderTabCompleter(SocialCore plugin, Genders genderHandler){
        sc = plugin;
        genders = genderHandler;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if(cmd.getName().equalsIgnoreCase("gender")){
            ArrayList<String> tabs = new ArrayList<>();
            if(args.length == 0){
                tabs.add("set");
            } else if (args.length == 1){
                tabs.addAll(getGenders());
            } else if (args.length == 2){
                tabs.addAll(getNames(sc.getServer().getOnlinePlayers()));
            }
            return tabs;
        }
        return null;
    }

    private ArrayList<String> getNames(Collection<? extends Player> onlinePlayers) {
        ArrayList<String> names = new ArrayList<>();
        for (Player player : onlinePlayers) {
            names.add(player.getName());
        }
        return names;
    }

    private ArrayList<String> getGenders(){
        return genders.getGenderNames();
    }
}
