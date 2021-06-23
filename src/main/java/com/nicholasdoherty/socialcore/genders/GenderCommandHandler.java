package com.nicholasdoherty.socialcore.genders;

import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.SocialPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GenderCommandHandler implements CommandExecutor {
    
    private final SocialCore sc;
    private final Genders genders;
    
    public GenderCommandHandler(final SocialCore sc, Genders genderHandler) {
        this.sc = sc;
        this.genders = genderHandler;
    }
    
    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if(sender instanceof Player) {
            SocialPlayer sp = sc.save.getSocialPlayer(sender.getName());
            Player player = (Player)sender;
            if(args.length==0 || (args.length==1 && args[0].equalsIgnoreCase("help"))){
                sender.sendMessage(ChatColor.GOLD+"----------=Gender - "+sender.getName()+"=----------");
                sender.sendMessage(sc.commandColor + "Gender statistics");
                sender.sendMessage(sc.messageColor+"Your gender: " + sc.commandColor+sp.getGender().getName());
                for (Gender gender : genders.getGenders()) {
                    if (gender.getName() != "UNSPECIFIED") {
                        sender.sendMessage(sc.messageColor + "-" + gender.getName() + ": " + sc.commandColor + sc.save.getCountGender(gender.getName().toUpperCase()));
                    } else {
                        sender.sendMessage(sc.messageColor + "-" + gender.getName() + ": " + sc.commandColor + sc.save.getCountGender("null"));
                    }
                }
                sender.sendMessage("");
                sender.sendMessage(sc.commandColor + "Gender commands");
                sender.sendMessage(sc.messageColor+"-Check someone's gender: " + sc.commandColor + "/"+sc.defaultAlias+" <name>");
                sender.sendMessage(sc.messageColor+"-Edit your gender: " + sc.commandColor + "/gender set <gender>");
                if(player.hasPermission("sc.gender.admin")) {
                    sender.sendMessage(sc.messageColor+"-Edit a players genders: " + sc.commandColor + "/gender set <player> <gender>");
                }
            } else if (args.length == 1) {
                return checkOtherPlayerGender(sender, args[0]);
            } else if (args.length == 2) {
                if(args[0].equalsIgnoreCase("set")){
                    if(genders.getGenderNames().contains(args[1].toUpperCase())){
                        if(sp.getGender().getName().equalsIgnoreCase("UNSPECIFIED")){
                            if(genders.getAwaitingConfirmation().containsKey(sp.getPlayerName()) && genders.getAwaitingConfirmation().get(sp.getPlayerName()).getName().toUpperCase().equalsIgnoreCase(args[1].toUpperCase())){
                                sp.setGender(genders.getGender(args[1]));
                                sc.save.saveSocialPlayer(sp);
                                sender.sendMessage(sc.prefix+sc.messageColor+"You have chosen the gender: " + sc.commandColor + sp.getGender().getName());
                            } else if(genders.getAwaitingConfirmation().containsKey(sp.getPlayerName())){
                                genders.getAwaitingConfirmation().remove(sp.getPlayerName());
                                sender.sendMessage(sc.prefix+sc.messageColor+"You have attempted to choose a different gender than you were awaiting confirmation for! Your choice has been reset.");
                            } else {
                                Gender gender = new Gender(args[1]);
                                genders.getAwaitingConfirmation().put(sp.getPlayerName(), gender);
                                sender.sendMessage(sc.prefix + sc.messageColor + "Are you sure you want to set your gender to "+sc.commandColor+gender.getName()+sc.messageColor + "? The only way to undo this is to submit a court case for a judge to change it! If you are certain, run the same command again to set your gender.");
                            }
                        } else {
                            sender.sendMessage(sc.prefix + sc.messageColor + "You have already chosen a gender, in order to change you need to seek out the courts!");
                        }
                    } else {
                        sender.sendMessage(sc.prefix + sc.messageColor + "Incorrect usage, that's not an available gender! Check out " + sc.commandColor + "/gender");
                    }
                } else {
                    sender.sendMessage(sc.prefix + sc.messageColor + "Incorrect usage! Check out the command format " + sc.commandColor + "/gender");
                }
            } else if (args.length == 3 && args[0].equalsIgnoreCase("set")){
                if(player.hasPermission("sc.gender.admin")){
                    if(genders.getGenderNames().contains(args[2].toUpperCase())){
                        SocialPlayer other = sc.save.getSocialPlayer(args[1]);
                        if(other != null){
                            other.setGender(genders.getGender(args[2]));
                            sc.save.saveSocialPlayer(other);
                            sender.sendMessage(sc.prefix+sc.messageColor+ "You have changed " + other.getPlayerName() + "'s gender to " + sc.commandColor + other.getGender().getName());
                        } else {
                            sender.sendMessage(sc.prefix + sc.errorColor + "Could not find player!");
                        }
                    } else {
                        sender.sendMessage(sc.prefix + sc.messageColor + "Incorrect usage, that's not an available gender! Check out " + sc.commandColor + "/gender");
                    }
                } else {
                    sender.sendMessage(sc.prefix + sc.errorColor + "You do not have permission to set other players' genders!");
                }
            } else {
                sender.sendMessage(sc.prefix + sc.messageColor + "Incorrect usage! Check out the command format " + sc.commandColor + "/gender");
            }
        } else {
            sender.sendMessage(sc.prefix + ChatColor.YELLOW + "You can only use this command in game!");
        }
        return true;
    }

    public boolean checkOtherPlayerGender(CommandSender sender, String name){
        SocialPlayer other = sc.save.getSocialPlayer(name);
        if(other == null){
            sender.sendMessage(sc.prefix + sc.errorColor + "Could not find player!");
        } else {
            sender.sendMessage(ChatColor.GOLD+"----------=Gender=----------");
            sender.sendMessage(sc.commandColor + "Information");
            sender.sendMessage(sc.messageColor+other.getPlayerName()+"'s gender: " + sc.commandColor+other.getGender().getName());
        }
        return true;
    }
}
