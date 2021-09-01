package com.nicholasdoherty.socialcore.genders;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.SocialPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("gender")
@CommandPermission("socialcore.genders")
@Description("Assign yourself or another player to a gender!")
public class GenderCommandHandler extends BaseCommand {
    
    private final SocialCore sc;
    private final Genders genders;
    
    public GenderCommandHandler(final SocialCore sc, Genders genderHandler) {
        this.sc = sc;
        this.genders = genderHandler;
    }
    
    @Default
    @CommandAlias("help")
    @CommandPermission("socialcore.genders")
    public boolean onGenderCommand(final CommandSender sender) {
        if(sender instanceof Player) {
            SocialPlayer sp = sc.save.getSocialPlayer(sender.getName());
            Player player = (Player)sender;
            sender.sendMessage(ChatColor.GOLD+"----------=Gender - "+sender.getName()+"=----------");
            sender.sendMessage(sc.commandColor + "Gender statistics");
            sender.sendMessage(sc.messageColor+"Your gender: " + sc.commandColor+sp.getGender().getName());

            for (Gender gender : genders.getGenders()) {
                sender.sendMessage(sc.messageColor + "-" + gender.getName() + ": " + sc.commandColor + genders.getGenderCache().get(gender.getName()));
            }
            sender.sendMessage("");
            sender.sendMessage(sc.commandColor + "Gender commands");
            sender.sendMessage(sc.messageColor+"-Check someone's gender: " + sc.commandColor + "/"+sc.defaultAlias+" <name>");
            sender.sendMessage(sc.messageColor+"-Edit your gender: " + sc.commandColor + "/gender set <gender>");
            if(player.hasPermission("socialcore.genders.admin")) {
                sender.sendMessage(sc.messageColor+"-Edit a players genders: " + sc.commandColor + "/gender set <gender> <player>");
            }
        } else {
            errorHandling(sender, "incorrect");
        }
        return true;
    }

    @Subcommand("set")
    @CommandPermission("socialcore.genders.set")
    public boolean set(CommandSender sender){
        errorHandling(sender, "incorrect");
        return true;
    }

    @Subcommand("set")
    @CommandCompletion("@genderNames")
    @CommandPermission("socialcore.genders.set")
    public boolean set(CommandSender sender, String genderName){
        if (sender instanceof Player){
            SocialPlayer sp = sc.save.getSocialPlayer(sender.getName());
            if(genders.getGenderNames().contains(genderName.toUpperCase())){
                if(sp.getGender().getName().equalsIgnoreCase("UNSPECIFIED")){
                    if(genders.getAwaitingConfirmation().containsKey(sp.getPlayerName()) && genders.getAwaitingConfirmation().get(sp.getPlayerName()).getName().toUpperCase().equalsIgnoreCase(genderName.toUpperCase())){
                        sp.setGender(genders.getGender(genderName));
                        sc.save.saveSocialPlayer(sp);
                        sender.sendMessage(sc.prefix+sc.messageColor+"You have chosen the gender: " + sc.commandColor + sp.getGender().getName());
                    } else if(genders.getAwaitingConfirmation().containsKey(sp.getPlayerName())){
                        genders.getAwaitingConfirmation().remove(sp.getPlayerName());
                        errorHandling(sender, "differentGender");
                    } else {
                        Gender gender = new Gender(genderName);
                        genders.getAwaitingConfirmation().put(sp.getPlayerName(), gender);
                        sender.sendMessage(sc.prefix + sc.messageColor + "Are you sure you want to set your gender to "+sc.commandColor+gender.getName()+sc.messageColor + "? The only way to undo this is to submit a court case for a judge to change it! If you are certain, run the same command again to set your gender.");
                    }
                } else {
                    errorHandling(sender, "alreadyGender");
                }
            } else {
                errorHandling(sender, "incorrect");
            }
        }
        return true;
    }

    @Subcommand("set")
    @CommandCompletion("@genderNames @players")
    @CommandPermission("socialcore.genders.admin")
    public void setAdmin(CommandSender sender, String genderName, String playerName){
        if(genders.getGenderNames().contains(genderName.toUpperCase())){
            SocialPlayer other = sc.save.getSocialPlayer(playerName);
            if(other != null){
                other.setGender(genders.getGender(genderName));
                sc.save.saveSocialPlayer(other);
                genders.loadGenderCache();
                sender.sendMessage(sc.prefix+sc.messageColor+ "You have changed " + other.getPlayerName() + "'s gender to " + sc.commandColor + other.getGender().getName());
            } else {
                sender.sendMessage(sc.prefix + sc.errorColor + "Could not find player!");
            }
        } else {
            errorHandling(sender, "incorrect");
        }
    }

    @Subcommand("check")
    @CommandCompletion("@players")
    @CommandPermission("socialcore.genders.check.others")
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

    @CatchUnknown
    public void errorHandling(CommandSender sender, String error){
        switch (error){
            case "alreadyGender":
                sender.sendMessage(sc.prefix + sc.messageColor + "You have already chosen a gender, in order to change you need to seek out the courts!");
                break;
            case "differentGender":
                sender.sendMessage(sc.prefix+sc.messageColor+"You have attempted to choose a different gender than you were awaiting confirmation for! Your choice has been reset.");
                break;
            case "incorrect":
            default:
                sender.sendMessage(sc.prefix + sc.messageColor + "Incorrect usage! Check out the command format " + sc.commandColor + "/gender");
        }
    }
}
