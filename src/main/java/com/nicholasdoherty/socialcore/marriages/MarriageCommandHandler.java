package com.nicholasdoherty.socialcore.marriages;

import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.SocialPlayer;
import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.courts.cases.Case;
import com.nicholasdoherty.socialcore.courts.cases.CaseCategory;
import com.nicholasdoherty.socialcore.genders.Gender;
import com.nicholasdoherty.socialcore.marriages.Marriages.Status;
import com.voxmc.voxlib.util.VaultUtil;
import com.voxmc.voxlib.util.VaultUtil.NotSetupException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@SuppressWarnings({"UseOfObsoleteDateTimeApi", "NullableProblems"})
public class MarriageCommandHandler implements CommandExecutor {
    
    private final SocialCore sc;
    private final SimpleDateFormat parserSDF = new SimpleDateFormat("MMMMM d, yyyy");
    
    public MarriageCommandHandler(final SocialCore sc) {
        this.sc = sc;
    }
    
    private static void removeLineWith(final List<String> l, @SuppressWarnings("SameParameterValue") final CharSequence with) {
        new ArrayList<>(l).stream().filter(s -> s.contains(with)).forEach(l::remove);
    }
    
    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if(sender.isOp() && args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            sc.marriageConfig.loadConfig();
            sender.sendMessage(ChatColor.GREEN + "Reloaded marriages.");
        }
        if(sender instanceof Player) {
            final Player player = (Player) sender;
            if(cmd.getName().equalsIgnoreCase("marriage")) {//show all marriage commands
                if(player.hasPermission("sc.marriage")) {
                    player.sendMessage(ChatColor.GOLD + "---------=Marriage Commands=---------");
                    if(player.hasPermission("sc.marriage")) {
                        player.sendMessage(ChatColor.AQUA + "/marriage - view list of marriage commands");
                    }
                    if(player.hasPermission("sc.view.marriages")) {
                        player.sendMessage(ChatColor.AQUA + "/marriages - view all marriages on the server");
                    }
                    if(player.hasPermission("sc.view.engagements")) {
                        player.sendMessage(ChatColor.AQUA + "/engagements - view all engagements on the server");
                    }
                    if(player.hasPermission("sc.propose")) {
                        player.sendMessage(ChatColor.AQUA + "/propose <player name> - propose to another player");
                        player.sendMessage(ChatColor.AQUA + "/propose accept - accept a proposal");
                        player.sendMessage(ChatColor.AQUA + "/propose deny - deny a proposal");
                    }
                    if(player.hasPermission("sc.unengage")) {
                        player.sendMessage(ChatColor.AQUA + "/unengage - To unengage your partner");
                    }
                    if(player.hasPermission("sc.priest")) {
                        player.sendMessage(ChatColor.AQUA + "/marry <player1> <player2> - marry two players");
                    }
                    if(player.hasPermission("sc.view.divorces")) {
                        player.sendMessage(ChatColor.AQUA + "/engagements - view all divorces on the server");
                    }
                    if(player.hasPermission("sc.fileDivorce")) {
                        player.sendMessage(ChatColor.AQUA + "/divorce - divorce your spouse");
                        player.sendMessage(ChatColor.AQUA + "/divorce cancel - cancel a pending divorce");
                    }
                    if(player.hasPermission("sc.lawyer")) {
                        player.sendMessage(ChatColor.AQUA + "/divorce <player1> <player2> - divorce two players");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "You do not have permission to view marriage commands!");
                }
            } else if(cmd.getName().equalsIgnoreCase("marriages")) {//view all marriages in the db
                int page = 0;
                final List<String> allMarriages = sc.save.getAllMarriageNames();
                if(args.length == 1) {
                    page = Integer.parseInt(args[0]) - 1;
                }
                if(page < 0) {
                    page = 0;
                }
                final int perPage = 5;
                int lowerbound = page * perPage;
                int upperbound = lowerbound + 5;
                if(upperbound >= allMarriages.size()) {
                    upperbound = allMarriages.size();
                    if(upperbound < 0) {
                        upperbound = 0;
                    }
                    if(lowerbound >= allMarriages.size()) {
                        lowerbound = upperbound - perPage;
                        if(lowerbound < 0) {
                            lowerbound = 0;
                        }
                    }
                }
                if(player.hasPermission("sc.view.marriages")) {
                    player.sendMessage(ChatColor.GOLD + "These are the marriages on the server: (Page " + (page + 1) + ')');
                    if(upperbound - lowerbound > 0) {
                        for(final String s : allMarriages.subList(lowerbound, upperbound)) {
                            final Marriage m = sc.save.getMarriage(s);
                            player.sendMessage(ChatColor.GREEN + m.getHusband().getPlayerName() + " to " + m.getWife().getPlayerName() + " on " + m.getDate() + " by " + m.getPriest());
                        }
                        if(allMarriages.size() - 1 > upperbound) {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Type &c/marriages " + (page + 2) + "&6 to read the next page."));
                        } else {
                            player.sendMessage(ChatColor.GOLD + "No more marriages to show");
                        }
                    } else {
                        player.sendMessage(ChatColor.GREEN + "None");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "You do not have permission to view a list of marriages!");
                }
            } else if(cmd.getName().equalsIgnoreCase("engagements")) {//view all marriages in the db
                final List<String> allEngagements = sc.save.getAllEngagements();
                int page = 0;
                if(args.length == 1) {
                    page = Integer.parseInt(args[0]) - 1;
                }
                if(page < 0) {
                    page = 0;
                }
                final int perPage = 5;
                int lowerbound = page * perPage;
                int upperbound = lowerbound + 5;
                if(upperbound >= allEngagements.size()) {
                    upperbound = allEngagements.size();
                    if(upperbound < 0) {
                        upperbound = 0;
                    }
                    if(lowerbound >= allEngagements.size()) {
                        lowerbound = upperbound - perPage;
                        if(lowerbound < 0) {
                            lowerbound = 0;
                        }
                    }
                }
                if(player.hasPermission("sc.view.engagements")) {
                    player.sendMessage(ChatColor.GOLD + "These are the marriages on the server: (Page " + (page + 1) + ')');
                    if(upperbound - lowerbound > 0) {
                        for(final String s : allEngagements.subList(lowerbound, upperbound)) {
                            final Engagement e = sc.save.getEngagement(s);
                            player.sendMessage(ChatColor.GREEN + e.getFHusband().getPlayerName() + " and " + e.getFWife().getPlayerName() + " on " + e.getDate());
                        }
                        if(allEngagements.size() - 1 > upperbound) {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Type &c/engagements " + (page + 2) + "&6 to read the next page."));
                        } else {
                            player.sendMessage(ChatColor.GOLD + "No more engagements to show");
                        }
                    } else {
                        player.sendMessage(ChatColor.GREEN + "None");
                    }
                }
                /*	player.sendMessage(ChatColor.GOLD+"These are the engagements on the server:");
                    for (String s : sc.save.getAllEngagements()) {
						Engagement e = sc.save.getEngagement(s);
						player.sendMessage(ChatColor.GREEN+e.getFHusband().getPlayerName()+" and "+e.getFWife().getPlayerName()+" on "+e.getDate());
					}
				}*/
                else {
                    player.sendMessage(ChatColor.RED + "You do not have permission to view a list of engagements!");
                }
            } else if(cmd.getName().equalsIgnoreCase("propose")) {
				/*ItemStack is = new ItemStack(Material.BEDROCK);
				ItemMeta me = is.getItemMeta();
				me.setDisplayName(ChatColor.RED+"Bedrock");
				is.setItemMeta(me);
				for (int i =0;i<64;i++)
					player.getInventory().addItem(is);*/
                if(args.length > 0) {
                    if(args[0].equalsIgnoreCase("accept")) {
                        final SocialPlayer proposeFrom = sc.save.getSocialPlayer(player.getName());
                        if(sc.marriages.getStatus(proposeFrom) == Status.ProposeFrom) {
                            final SocialPlayer proposeTo = sc.marriages.proposals.get(proposeFrom);
                            final Player p = Bukkit.getServer().getPlayer(proposeTo.getPlayerName());
                            if(p != null) {
                                p.sendMessage(ChatColor.GREEN + proposeFrom.getPlayerName() + " has accepted your hand in marriage! Congratulations!");
                            }
                            player.sendMessage(ChatColor.GREEN + "You have accepted " + proposeTo.getPlayerName() + "'s hand in marriage! Congratulations!");
                            sc.marriages.proposals.remove(proposeFrom);
                            
                            final Engagement e = new Engagement(proposeTo, proposeFrom);
                            final String dateBuilder = getMonth() + ' ' + Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + ", " + Calendar.getInstance().get(Calendar.YEAR);
                            e.setDate(dateBuilder);
                            e.setTime(System.currentTimeMillis());
                            
                            proposeFrom.setEngaged(true);
                            proposeFrom.setEngagedTo(proposeTo.getPlayerName());
                            proposeTo.setEngaged(true);
                            proposeTo.setEngagedTo(proposeFrom.getPlayerName());
                            
                            sc.save.saveEngagement(e);
                            sc.save.saveSocialPlayer(proposeFrom);
                            sc.save.saveSocialPlayer(proposeTo);
                            
                            out:
                            for(final ItemStack item : p.getInventory().getContents()) {
                                if(item != null) {
                                    for(final MarriageGem gem : sc.marriageConfig.marriageGems) {
                                        if(gem.getBlockID() == item.getType()) {
                                            if(item.getItemMeta() != null) {
                                                if(item.getItemMeta().getDisplayName() != null) {
                                                    if(item.getItemMeta().getDisplayName().contains(gem.getName())) {
                                                        final ItemMeta meta = item.getItemMeta();
                                                        final List<String> l = new ArrayList<>();
                                                        if(meta.getLore() != null) {
                                                            l.addAll(meta.getLore());
                                                        }
                                                        l.add(proposeTo.getPlayerName() + " + " + proposeFrom.getPlayerName() + " 4ever");//
                                                        l.add("Engaged on " + e.getDate());
                                                        meta.setLore(l);
                                                        item.setItemMeta(meta);
                                                        player.getInventory().addItem(item);
                                                        p.updateInventory();
                                                        player.updateInventory();
                                                        break out;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            
                            Bukkit.getServer().getOnlinePlayers().stream().filter(pr -> pr.hasPermission("sc.priest")).forEach(pr -> pr.sendMessage(ChatColor.GREEN + proposeTo.getPlayerName() + " and " + proposeFrom.getPlayerName() + " have become engaged!"));
                        } else {
                            player.sendMessage(ChatColor.RED + "You have not been proposed to!");
                        }
                    } else if(args[0].equalsIgnoreCase("deny")) {
                        final SocialPlayer proposeFrom = sc.save.getSocialPlayer(player.getName());
                        if(sc.marriages.getStatus(proposeFrom) == Status.ProposeFrom) {
                            final SocialPlayer proposeTo = sc.marriages.proposals.get(proposeFrom);
                            final Player p = Bukkit.getServer().getPlayer(proposeTo.getPlayerName());
                            if(p != null) {
                                p.sendMessage(ChatColor.DARK_RED + proposeFrom.getPlayerName() + " has declined your hand in marriage! :(");
                            }
                            player.sendMessage(ChatColor.DARK_RED + "You have declined " + proposeTo.getPlayerName() + "'s hand in marriage!");
                            sc.marriages.proposals.remove(proposeFrom);
                        } else {
                            player.sendMessage(ChatColor.RED + "You have not been proposed to!");
                        }
                    } else {
                        final Player p = Bukkit.getServer().getPlayer(args[0]);
                        if(p == null) {
                            player.sendMessage(ChatColor.RED + "Could not find player '" + args[0] + "'. Are they online?");
                            return true;
                        }
                        if(p.getUniqueId() == ((Player) sender).getUniqueId()) {
                            player.sendMessage(ChatColor.RED + "You aren't allowed to marry yourself!");
                            return true;
                        }
                        
                        final SocialPlayer proposeTo = sc.save.getSocialPlayer(player.getName());
                        final SocialPlayer proposeFrom = sc.save.getSocialPlayer(p.getName());
                        for(final String divorceName : sc.save.getAllDivorces()) {
                            if(divorceName.contains(proposeFrom.getPlayerName()) || divorceName.contains(proposeTo.getPlayerName())) {
                                final Divorce divorce = sc.save.getDivorce(divorceName);
                                if(divorce != null) {
                                    try {
                                        final Date date = parserSDF.parse(divorce.getDate());
                                        final long divorceTime = date.getTime();
                                        final long currentTime = new Date().getTime();
                                        final long elapsedMillis = currentTime - divorceTime;
                                        if(elapsedMillis < sc.marriageConfig.divorceProposeCooldownMillis) {
                                            if(divorceName.contains(proposeFrom.getPlayerName())) {
                                                player.sendMessage(ChatColor.RED + "You have divorced too recently to propose!");
                                            } else {
                                                player.sendMessage(ChatColor.RED + "The player you are proposing too has divorced too recently!");
                                            }
                                            return true;
                                        }
                                    } catch(final ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                        switch(sc.marriages.getStatus(proposeTo)) {
                            case Married:
                                player.sendMessage(ChatColor.RED + "You are already married! This isn't a polygamy state!");
                                return true;
                            case Engaged:
                                player.sendMessage(ChatColor.RED + "You are already engaged! This isn't a polygamy state!");
                                return true;
                            case ProposeTo:
                                player.sendMessage(ChatColor.RED + "You have already proposed to someone! This isn't a polygamy state!");
                                return true;
                            case ProposeFrom:
                                player.sendMessage(ChatColor.RED + "You have already been proposed to! Type /propose deny to crush your previous lover's heart!");
                                return true;
                            case Single:
                                break;
                            default:
                                break;
                        }
                        
                        switch(sc.marriages.getStatus(proposeFrom)) {
                            case Married:
                                player.sendMessage(ChatColor.RED + "That player is already married! This isn't a polygamy state!");
                                return true;
                            case Engaged:
                                player.sendMessage(ChatColor.RED + "That player is already engaged! This isn't a polygamy state!");
                                return true;
                            case ProposeTo:
                                player.sendMessage(ChatColor.RED + "That player has already proposed to someone! This isn't a polygamy state!");
                                return true;
                            case ProposeFrom:
                                player.sendMessage(ChatColor.RED + "That player has already been proposed to! This isn't a polygamy state!");
                                return true;
                            case Single:
                                break;
                            default:
                                break;
                        }
                        
                        if(proposeTo.getGender().getName() == proposeFrom.getGender().getName() && !player.hasPermission("sc.samesex")) {
                            player.sendMessage(ChatColor.RED + "Sorry, you need permission to have a samesex marriage.");
                            return true;
                        }
                        
                        if(!player.hasPermission("sc.propose")) {
                            player.sendMessage(ChatColor.RED + "You do not have permision to marry another player!");
                            return true;
                        }
                        if(!p.hasPermission("sc.propose")) {
                            player.sendMessage(ChatColor.RED + proposeFrom.getPlayerName() + " does not have permission to marry!");
                            return true;
                        }
                        
                        boolean okay = false;
                        
                        out:
                        for(final ItemStack item : player.getInventory().getContents()) {
                            if(item != null) {
                                for(final MarriageGem gem : sc.marriageConfig.marriageGems) {
                                    if(gem.getBlockID() == item.getType()) {
                                        if(item.getItemMeta() != null) {
                                            if(item.getItemMeta().getDisplayName() != null) {
                                                if(item.getItemMeta().getDisplayName().contains(gem.getName())) {
                                                    boolean used = false;
                                                    if(item.getItemMeta().hasLore()) {
                                                        if(!item.getItemMeta().getLore().isEmpty()) {
                                                            if(item.getItemMeta().getLore().get(0).contains("4ever")) {
                                                                used = true;
                                                            }
                                                        }
                                                    }
                                                    if(!used) {
                                                        okay = true;
                                                        break out;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if(!okay) {
                            player.sendMessage(ChatColor.RED + "You must have a marriage gem in order to propose!");
                            return true;
                        }
                        
                        sc.marriages.proposals.put(proposeFrom, proposeTo);
                        player.sendMessage(ChatColor.GREEN + "You have asked " + p.getName() + " for their hand in marriage!");
                        p.sendMessage(ChatColor.GREEN + player.getName() + " is asking for your hand in marriage! Type '/propose accept' to accept it, or '/propose deny' to crush their heart!");
                        return true;
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "Usage: /propose <player name> or /propose accept/deny (to accept or deny a proposal");
                }
            } else if(cmd.getName().equalsIgnoreCase("marry")) {
                if(player.hasPermission("sc.priest")) {
                    if(args.length > 1) {
                        final Player p1 = Bukkit.getServer().getPlayer(args[0]);
                        if(p1 == null) {
                            player.sendMessage(ChatColor.RED + "Player '" + args[0] + "' cannot be found. Are they online?");
                            return true;
                        }
                        final Player p2 = Bukkit.getServer().getPlayer(args[1]);
                        if(p2 == null) {
                            player.sendMessage(ChatColor.RED + "Player '" + args[1] + "' cannot be found. Are they online?");
                            return true;
                        }
                        
                        final SocialPlayer player1 = sc.save.getSocialPlayer(args[0]);
                        final SocialPlayer player2 = sc.save.getSocialPlayer(args[1]);
                        
                        if(!player1.isEngaged()) {
                            player.sendMessage(ChatColor.RED + player1.getPlayerName() + " is not engaged!");
                            return true;
                        }
                        if(!player2.isEngaged()) {
                            player.sendMessage(ChatColor.RED + player2.getPlayerName() + " is not engaged!");
                            return true;
                        }
                        if(!player1.getEngagedTo().equalsIgnoreCase(player2.getPlayerName())) {
                            player.sendMessage(ChatColor.RED + player1.getPlayerName() + " is not engaged to " + player2.getPlayerName());
                            return true;
                        }
                        if(!player2.getEngagedTo().equalsIgnoreCase(player1.getPlayerName())) {
                            player.sendMessage(ChatColor.RED + player2.getPlayerName() + " is not engaged to " + player1.getPlayerName());
                            return true;
                        }
                        
                        p1.sendMessage(ChatColor.GREEN + "Father " + player.getName() + " is beginning the ceremony...");
                        if(player.getLocation().distance(p1.getLocation()) > sc.marriageConfig.priestDistance) {
                            player.sendMessage(ChatColor.RED + "The priest is too far away from " + player1.getPlayerName() + '!');
                            p1.sendMessage(ChatColor.RED + "The priest is too far away from " + player1.getPlayerName() + '!');
                            p2.sendMessage(ChatColor.RED + "The priest is too far away from " + player1.getPlayerName() + '!');
                            return true;
                        }
                        if(player.getLocation().distance(p2.getLocation()) > sc.marriageConfig.priestDistance) {
                            player.sendMessage(ChatColor.RED + "The priest is too far away from " + player2.getPlayerName() + '!');
                            p1.sendMessage(ChatColor.RED + "The priest is too far away from " + player2.getPlayerName() + '!');
                            p2.sendMessage(ChatColor.RED + "The priest is too far away from " + player2.getPlayerName() + '!');
                            return true;
                        }
                        if(p1.getLocation().distance(p2.getLocation()) > sc.marriageConfig.coupleDistance) {
                            player.sendMessage(ChatColor.RED + player1.getPlayerName() + " is too far away from " + player2.getPlayerName() + '!');
                            p1.sendMessage(ChatColor.RED + player1.getPlayerName() + " is too far away from " + player2.getPlayerName() + '!');
                            p2.sendMessage(ChatColor.RED + player1.getPlayerName() + " is too far away from " + player2.getPlayerName() + '!');
                            return true;
                        }
                        
                        final Engagement e = sc.save.getEngagement(player1, player2);
                        final Marriage m = new Marriage(e.getFHusband(), e.getFWife());
                        m.setPriest(player.getName());
                        final String dateBuilder = getMonth() + ' ' + Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + ", " + Calendar.getInstance().get(Calendar.YEAR);
                        m.setDate(dateBuilder);
                        
                        boolean okay = false;
                        out:
                        for(final ItemStack item : p1.getInventory().getContents()) {
                            if(item != null) {
                                for(final MarriageGem gem : sc.marriageConfig.marriageGems) {
                                    if(gem.getBlockID() == item.getType()) {
                                        if(item.getItemMeta() != null) {
                                            if(item.getItemMeta().getDisplayName() != null) {
                                                if(item.getItemMeta().getDisplayName().contains(gem.getName())) {
                                                    okay = true;
                                                    break out;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        
                        if(!okay) {
                            player.sendMessage(ChatColor.RED + "Uh oh! " + player1.getPlayerName() + " has misplaced their marriage gem!");
                            p1.sendMessage(ChatColor.RED + "Uh oh! " + player1.getPlayerName() + " has misplaced their marriage gem!");
                            p2.sendMessage(ChatColor.RED + "Uh oh! " + player1.getPlayerName() + " has misplaced their marriage gem!");
                            return true;
                        }
                        
                        okay = false;
                        out:
                        for(final ItemStack item : p2.getInventory().getContents()) {
                            if(item != null) {
                                for(final MarriageGem gem : sc.marriageConfig.marriageGems) {
                                    if(gem.getBlockID() == item.getType()) {
                                        if(item.getItemMeta() != null) {
                                            if(item.getItemMeta().getDisplayName() != null) {
                                                if(item.getItemMeta().getDisplayName().contains(gem.getName())) {
                                                    final ItemMeta meta = item.getItemMeta();
                                                    final List<String> l = new ArrayList<>();
                                                    if(meta.getLore() != null) {
                                                        l.addAll(meta.getLore());
                                                    }
                                                    removeLineWith(l, "Engaged on");
                                                    l.add(player1.getPlayerName() + " + " + player2.getPlayerName() + " 4ever");//
                                                    l.add("Married on " + m.getDate() + " by " + m.getPriest());
                                                    meta.setLore(l);
                                                    item.setItemMeta(meta);
                                                    p2.updateInventory();
                                                    okay = true;
                                                    break out;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        
                        if(!okay) {
                            player.sendMessage(ChatColor.RED + "Uh oh! " + player2.getPlayerName() + " has misplaced their marriage gem!");
                            p1.sendMessage(ChatColor.RED + "Uh oh! " + player2.getPlayerName() + " has misplaced their marriage gem!");
                            p2.sendMessage(ChatColor.RED + "Uh oh! " + player2.getPlayerName() + " has misplaced their marriage gem!");
                            return true;
                        }
                        out:
                        for(final ItemStack item : p1.getInventory().getContents()) {
                            if(item != null) {
                                for(final MarriageGem gem : sc.marriageConfig.marriageGems) {
                                    if(gem.getBlockID() == item.getType()) {
                                        if(item.getItemMeta() != null) {
                                            if(item.getItemMeta().getDisplayName() != null) {
                                                if(item.getItemMeta().getDisplayName().contains(gem.getName())) {
                                                    final ItemMeta meta = item.getItemMeta();
                                                    final List<String> l = new ArrayList<>();
                                                    if(meta.getLore() != null) {
                                                        l.addAll(meta.getLore());
                                                    }
                                                    removeLineWith(l, "Engaged on");
                                                    l.add(player1.getPlayerName() + " + " + player2.getPlayerName() + " 4ever");//
                                                    l.add("Married on " + m.getDate() + " by " + m.getPriest());
                                                    meta.setLore(l);
                                                    item.setItemMeta(meta);
                                                    p1.updateInventory();
                                                    break out;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        
                        sc.save.removeEngagement(e);
                        player1.setEngaged(false);
                        player1.setEngagedTo("");
                        player1.setMarried(true);
                        player1.setMarriedTo(player2.getPlayerName());
                        player2.setEngaged(false);
                        player2.setEngagedTo("");
                        player2.setMarried(true);
                        player2.setMarriedTo(player1.getPlayerName());
                        sc.save.saveSocialPlayer(player1);
                        sc.save.saveSocialPlayer(player2);
                        sc.save.saveMarriage(m);
                        
                        player.sendMessage(ChatColor.GREEN + "You have married " + player1.getPlayerName() + " and " + player2.getPlayerName() + '!');
                        String toSendPlayer1 = "You have taken " + player2.getPlayerName() + " to be your loftly wedded spouse. Happy ever after!";
                        String toSendPlayer2 = "You have taken " + player1.getPlayerName() + " to be your loftly wedded spouse. Happy ever after!";
                        p1.sendMessage(toSendPlayer1);
                        p2.sendMessage(toSendPlayer2);
                        for(final Player p : Bukkit.getServer().getOnlinePlayers()) {
                            p.sendMessage(ChatColor.YELLOW + "[SocialCore] Father " + player.getName() + " has married " + player1.getPlayerName() + " and " + player2.getPlayerName() + "! Wish them a happy ever after!");
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "Usage: /marry <player1> <player2>");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "You do not have permission to marry players!");
                }
            } else if(cmd.getName().equalsIgnoreCase("divorces")) {
                if(player.hasPermission("sc.view.divorces")) {
                    final List<String> allDivorces = sc.save.getAllDivorces();
                    int page = 0;
                    if(args.length == 1) {
                        page = Integer.parseInt(args[0]) - 1;
                    }
                    if(page < 0) {
                        page = 0;
                    }
                    final int perPage = 5;
                    int lowerbound = page * perPage;
                    int upperbound = lowerbound + 5;
                    if(upperbound >= allDivorces.size()) {
                        upperbound = allDivorces.size();
                        if(upperbound < 0) {
                            upperbound = 0;
                        }
                        if(lowerbound >= allDivorces.size()) {
                            lowerbound = upperbound - perPage;
                            if(lowerbound < 0) {
                                lowerbound = 0;
                            }
                        }
                    }
                    player.sendMessage(ChatColor.GOLD + "These are the divorces on the server:");
                    if(upperbound - lowerbound > 0) {
                        for(final String s : allDivorces.subList(lowerbound, upperbound)) {
                            final Engagement e = sc.save.getEngagement(s);
                            player.sendMessage(ChatColor.GREEN + e.getFHusband().getPlayerName() + " and " + e.getFWife().getPlayerName() + " filed on " + e.getDate());
                        }
                        if(allDivorces.size() - 1 > upperbound) {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Type &c/divorces " + (page + 2) + "&6 to read the next page."));
                        } else {
                            player.sendMessage(ChatColor.GOLD + "No more divorces to show");
                        }
                    } else {
                        player.sendMessage(ChatColor.GREEN + "None");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "You do not have permission to view a list of divorces!");
                }
            } else if(cmd.getName().equalsIgnoreCase("divorce")) {
                if(args.length < 1) {
                    if(player.hasPermission("sc.fileDivorce")) {
                        final SocialPlayer p1 = sc.save.getSocialPlayer(player.getName());
                        if(!p1.isMarried()) {
                            player.sendMessage(ChatColor.RED + "You are not married!");
                            return true;
                        }
                        if(sc.marriages.getPendingDivorces().contains(p1.getPlayerName())) {
                            
                            for(final String s : sc.save.getAllDivorces()) {
                                final Divorce d = sc.save.getDivorce(s);
                                if(d.getFiledBy().equalsIgnoreCase(p1.getPlayerName())) {
                                    player.sendMessage(ChatColor.RED + "You have already filed for a divorce. Please be patient while a lawyer looks into your case. Type /divorce cancel to cancel the divorce");
                                    return true;
                                }
                                if(d.getExhusband().getPlayerName().equalsIgnoreCase(player.getName())) {
                                    player.sendMessage(ChatColor.RED + "Your spouse has already filed for a divorce. Please be patient while a lawyer looks into your case.");
                                    return true;
                                }
                                if(d.getExwife().getPlayerName().equalsIgnoreCase(player.getName())) {
                                    player.sendMessage(ChatColor.RED + "Your spouse has already filed for a divorce. Please be patient while a lawyer looks into your case.");
                                    return true;
                                }
                            }
                            
                            final SocialPlayer p2 = sc.save.getSocialPlayer(p1.getMarriedTo());
                            
                            final Divorce divorce = new Divorce(p1, p2);
                            final Case alreadyCase = Courts.getCourts().getDivorceManager().getCase(divorce);
                            if(alreadyCase != null && alreadyCase.getCaseCategory() == CaseCategory.DIVORCE && !alreadyCase.isDone()) {
                                player.sendMessage(ChatColor.RED + "You already have a court case pertaining to this divorce.");
                                return true;
                            }
                            final int cost = Courts.getCourts().getCourtsConfig().getCaseFilingCost();
                            try {
                                if(!VaultUtil.charge(player, cost)) {
                                    player.sendMessage(ChatColor.RED + "You do not have the " + cost + " voxels to file for divorce.");
                                }
                            } catch(final NotSetupException e) {
                                return false;
                            }
                            player.sendMessage(ChatColor.GREEN + "You have been charged " + cost + " voxels by filing for divorce.");
                            final String dateBuilder = getMonth() + ' ' + Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + ", " + Calendar.getInstance().get(Calendar.YEAR);
                            divorce.setDate(dateBuilder);
                            divorce.setFiledBy(player.getName());
                            Courts.getCourts().getDivorceManager().createCaseForDivorce(divorce);
                            sc.save.saveDivorce(divorce);
                            
                            sc.marriages.getPendingDivorces().remove(p1.getPlayerName());
                            player.sendMessage(ChatColor.AQUA + "A divorce has been filed for you and " + p1.getMarriedTo() + ". A lawyer must review your application. Type /divorce cancel to cancel.");
                            
                            final Player pl = Bukkit.getServer().getPlayer(p1.getMarriedTo());
                            if(pl != null) {
                                pl.sendMessage(ChatColor.YELLOW + p1.getPlayerName() + " has filed for a divorce :(");
                            }
                            
                            Bukkit.getServer().getOnlinePlayers().stream().filter(p -> p.hasPermission("sc.lawyer")).forEach(p -> p.sendMessage(ChatColor.GREEN + "Lawyer, " + p1.getPlayerName() + " has filed for a divorce against " + p1.getMarriedTo()));
                        } else {
                            for(final String s : sc.save.getAllDivorces()) {
                                final Divorce d = sc.save.getDivorce(s);
                                if(d.getFiledBy().equalsIgnoreCase(p1.getPlayerName())) {
                                    player.sendMessage(ChatColor.RED + "You have already filed for a divorce. Please be patient while a lawyer looks into your case. Type /divorce cancel to cancel the divorce");
                                    return true;
                                }
                                if(d.getExhusband().getPlayerName().equalsIgnoreCase(player.getName())) {
                                    player.sendMessage(ChatColor.RED + "Your spouse has already filed for a divorce. Please be patient while a lawyer looks into your case.");
                                    return true;
                                }
                                if(d.getExwife().getPlayerName().equalsIgnoreCase(player.getName())) {
                                    player.sendMessage(ChatColor.RED + "Your spouse has already filed for a divorce. Please be patient while a lawyer looks into your case.");
                                    return true;
                                }
                            }
                            
                            player.sendMessage(ChatColor.GOLD + "Are you sure you wish to divorce " + p1.getMarriedTo() + "? Type /divorce again to confirm.");
                            sc.marriages.getPendingDivorces().add(p1.getPlayerName());
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "You do not have permission to divorce!");
                    }
                } else if(args.length > 0 && args[0].equalsIgnoreCase("cancel")) {
                    if(player.hasPermission("sc.fileDivorce")) {
                        final SocialPlayer p = sc.save.getSocialPlayer(player.getName());
                        Divorce divorce = null;
                        for(final String s : sc.save.getAllDivorces()) {
                            final Divorce d = sc.save.getDivorce(s);
                            if(d.getFiledBy().equalsIgnoreCase(player.getName())) {
                                divorce = d;
                                break;
                            }
                        }
                        
                        if(divorce == null) {
                            player.sendMessage(ChatColor.RED + "You have not filed for a divorce!");
                            return true;
                        }
                        
                        sc.save.removeDivorce(divorce);
                        player.sendMessage(ChatColor.GREEN + "Your filing has been removed from the queue. Hopefully you two can work things out.");
                        
                        final Player pl = Bukkit.getServer().getPlayer(p.getMarriedTo());
                        if(pl != null) {
                            pl.sendMessage(ChatColor.YELLOW + "Your spouse has removed their request for a divorce. Hopefully you two can work things out.");
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "You do not have permission to divorce!");
                    }
                } else if(args.length > 1) {
                    if(player.hasPermission("sc.lawyer")) {
                        final SocialPlayer p1 = sc.save.getSocialPlayer(args[0]);
                        final SocialPlayer p2 = sc.save.getSocialPlayer(args[1]);
                        if(!p1.isMarried()) {
                            player.sendMessage(ChatColor.RED + args[0] + " is not married!");
                            return true;
                        }
                        if(!p1.getMarriedTo().equalsIgnoreCase(args[1])) {
                            player.sendMessage(ChatColor.RED + args[0] + " is not married to " + args[1] + '!');
                            return true;
                        }
                        
                        Divorce divorce = null;
                        for(final String s : sc.save.getAllDivorces()) {
                            final Divorce d = sc.save.getDivorce(s);
                            if(d.getFiledBy().equalsIgnoreCase(p1.getPlayerName())) {
                                divorce = d;
                                break;
                            }
                        }
                        if(divorce == null) {
                            player.sendMessage(ChatColor.RED + "That divorce could not be found!");
                            return true;
                        }
                        
                        final Player pl = Bukkit.getServer().getPlayer(divorce.getFiledBy());
                        if(pl != null) {
                            pl.sendMessage(ChatColor.AQUA + "Lawyer " + player.getName() + " is reviewing your request to divorce " + p1.getMarriedTo());
                        }
                        
                        final Marriage marriage = sc.save.getMarriage(p1, p2);
                        
                        sc.save.removeMarriage(marriage);
                        sc.save.removeDivorce(divorce);
                        
                        player.sendMessage(ChatColor.GREEN + p1.getPlayerName() + " and " + p2.getPlayerName() + " are now divorced!");
                        
                        final Player pl1 = Bukkit.getServer().getPlayer(p1.getPlayerName());
                        final Player pl2 = Bukkit.getServer().getPlayer(p2.getPlayerName());
                        
                        if(pl1 != null) {
                            pl1.sendMessage(ChatColor.YELLOW + "You and " + p1.getMarriedTo() + " are now divorced. Better luck next time.");
                        }
                        if(pl2 != null) {
                            pl2.sendMessage(ChatColor.YELLOW + "You and " + p2.getMarriedTo() + " are now divorced. Better luck next time.");
                        }
                        
                        p1.setMarried(false);
                        p1.setMarriedTo("");
                        p2.setMarried(false);
                        p2.setMarriedTo("");
                        
                        sc.save.saveSocialPlayer(p1);
                        sc.save.saveSocialPlayer(p2);
                        
                        for(final Player p : Bukkit.getServer().getOnlinePlayers()) {
                            p.sendMessage(ChatColor.YELLOW + "[SocialCore] " + p1.getPlayerName() + " and " + p2.getPlayerName() + " are now divorced :(");
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "Only a lawyer can complete a divorce!");
                    }
                }
            } else if(cmd.getName().equalsIgnoreCase("adivorce")) {
                if(player.hasPermission("sc.admin")) {
                    if(args.length > 1) {
                        
                        final SocialPlayer sp1 = sc.save.getSocialPlayer(args[0]);
                        final SocialPlayer sp2 = sc.save.getSocialPlayer(args[1]);
                        sp1.setEngaged(false);
                        sp1.setEngagedTo("");
                        sp1.setMarried(false);
                        sp1.setMarriedTo("");
                        sp2.setEngaged(false);
                        sp2.setEngagedTo("");
                        sp2.setMarried(false);
                        sp2.setMarriedTo("");
                        
                        sc.save.saveSocialPlayer(sp1);
                        sc.save.saveSocialPlayer(sp2);
                        
                        final Divorce divorce = sc.save.getDivorce(sp1, sp2);
                        if(divorce == null) {
                            player.sendMessage(ChatColor.RED + "This is not a valid divorce");
                            return true;
                        }
                        sc.save.removeDivorce(divorce);
                        
                        final Marriage marriage = sc.save.getMarriage(sp1, sp2);
                        if(marriage == null) {
                            player.sendMessage(ChatColor.RED + "This is not a valid marriage");
                            return true;
                        }
                        sc.save.removeMarriage(marriage);
                        
                        player.sendMessage(ChatColor.GREEN + "Forced divorce.");
                    } else {
                        player.sendMessage(ChatColor.RED + "Usage: /adivorce <player1> <player2>");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "You do not have permission to force a divorce");
                }
            } else if(cmd.getName().equalsIgnoreCase("amarry")) {
                if(player.hasPermission("sc.admin")) {
                    if(args.length > 1) {
                        
                        final SocialPlayer sp1 = sc.save.getSocialPlayer(args[0]);
                        final SocialPlayer sp2 = sc.save.getSocialPlayer(args[1]);
                        
                        sp1.setEngaged(false);
                        sp1.setEngagedTo("");
                        sp1.setMarried(true);
                        sp1.setMarriedTo(args[1]);
                        sp2.setEngaged(false);
                        sp2.setEngagedTo("");
                        sp2.setMarried(true);
                        sp2.setMarriedTo(args[0]);
                        
                        sc.save.saveSocialPlayer(sp1);
                        sc.save.saveSocialPlayer(sp2);
                        
                        final Marriage marriage = new Marriage(sp1, sp2);
                        marriage.setPriest(player.getName());
                        final String dateBuilder = getMonth() + ' ' + Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + ", " + Calendar.getInstance().get(Calendar.YEAR);
                        marriage.setDate(dateBuilder);
                        
                        sc.save.saveMarriage(marriage);
                        
                        player.sendMessage(ChatColor.GREEN + "Force marriage.");
                    } else {
                        player.sendMessage(ChatColor.RED + "Usage: /amarry <player1> <player2>");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "You do not have permission to force a marriage");
                }
            } else if(cmd.getName().equalsIgnoreCase("share")) {
                final Player p1 = (Player) sender;
                final SocialPlayer sp1 = sc.save.getSocialPlayer(p1.getName());
                
                final Player p2 = Bukkit.getServer().getPlayer(sp1.getMarriedTo());
                
                if(!p1.hasPermission("sc.marriage.share") && !p2.hasPermission("sc.marriage.share")) {
                    p1.sendMessage(ChatColor.RED + "Your couple does not have permission to the sharing mechanic.");
                    return true;
                }
                if(!sp1.isMarried()) {
                    player.sendMessage(ChatColor.RED + "You are not married.");
                    return true;
                }
                if(p2 == null || !p2.isOnline()) {
                    player.sendMessage(ChatColor.RED + "Your Significant Other is offline!");
                    return true;
                }
                if(!p1.hasPermission("sc.viewspouseinventory")) {
                    p1.sendMessage(ChatColor.RED + "You do not have permission to view your spouse's inventory.");
                    return true;
                }
                final double maxDistaceSquared = sc.marriageConfig.maxShareInventDistanceSquared;
                if(p2.getWorld().getName().equalsIgnoreCase(p1.getWorld().getName()) && p2.getLocation().distanceSquared(p1.getLocation()) <= maxDistaceSquared) {
                    p1.openInventory(p2.getInventory());
                    p2.sendMessage(ChatColor.AQUA + p1.getName() + " is viewing your inventory!");
                    p1.sendMessage(ChatColor.AQUA + p2.getName() + "'s inventory");
                    if(p2.getOpenInventory() != null) {
                        p2.closeInventory();
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "Your Significant Other is too far away!");
                }
            }
            if(cmd.getName().equalsIgnoreCase("unengage")) {
                if(sender.hasPermission("sc.unengage")) {
                    final Player p1 = (Player) sender;
                    Engagement engagement = null;
                    for(final String eName : sc.save.getAllEngagements()) {
                        final Engagement engagement1 = sc.save.getEngagement(eName);
                        if(engagement1.getFHusband().getPlayerName().equalsIgnoreCase(p1.getName())) {
                            engagement = engagement1;
                        }
                        if(engagement1.getFWife().getPlayerName().equalsIgnoreCase(p1.getName())) {
                            engagement = engagement1;
                        }
                    }
                    if(engagement == null) {
                        sender.sendMessage(ChatColor.RED + "You are not engaged.");
                        return true;
                    }
                    engagement.getFHusband().setEngaged(false);
                    engagement.getFWife().setEngaged(false);
                    sc.save.removeEngagement(engagement);
                    final Player hus = Bukkit.getPlayer(engagement.getFHusband().getPlayerName());
                    final Player wife = Bukkit.getPlayer(engagement.getFWife().getPlayerName());
                    if(hus.isOnline()) {
                        hus.sendMessage(ChatColor.BLUE + "You have been unengaged.");
                    }
                    if(wife.isOnline()) {
                        hus.sendMessage(ChatColor.BLUE + "You have been unengaged.");
                    }
                    return true;
                } else {
                    sender.sendMessage(ChatColor.RED + "You do not have permission to unengage.");
                }
            }
        }
        if(cmd.getName().equalsIgnoreCase("aunengage")) {
            if(sender.hasPermission("sc.admin")) {
                if(args.length != 1) {
                    sender.sendMessage("Usage: /aunengage <player>");
                    return true;
                }
                final Player p1 = Bukkit.getPlayer(args[0]);
                if(p1 == null) {
                    sender.sendMessage(ChatColor.RED + args[0] + " is not a valid player.");
                    return true;
                }
                Engagement engagement = null;
                for(final String eName : sc.save.getAllEngagements()) {
                    final Engagement engagement1 = sc.save.getEngagement(eName);
                    if(engagement1.getFHusband().getPlayerName().equalsIgnoreCase(p1.getName())) {
                        engagement = engagement1;
                    }
                    if(engagement1.getFWife().getPlayerName().equalsIgnoreCase(p1.getName())) {
                        engagement = engagement1;
                    }
                }
                if(engagement == null) {
                    sender.sendMessage(ChatColor.RED + p1.getName() + " is not engaged.");
                    return true;
                }
                engagement.getFHusband().setEngaged(false);
                engagement.getFWife().setEngaged(false);
                final Player hus = Bukkit.getPlayer(engagement.getFHusband().getPlayerName());
                final Player wife = Bukkit.getPlayer(engagement.getFWife().getPlayerName());
                if(hus.isOnline()) {
                    hus.sendMessage(ChatColor.BLUE + "You have been unengaged.");
                }
                if(wife.isOnline()) {
                    hus.sendMessage(ChatColor.BLUE + "You have been unengaged.");
                }
                sc.save.removeEngagement(engagement);
                sender.sendMessage(ChatColor.GREEN + "You have successfully unengaged " + engagement.getFHusband().getPlayerName() + " and " + engagement.getFWife().getPlayerName());
            } else {
                sender.sendMessage(ChatColor.RED + "You do not have permission to force unengage somebody.");
            }
        }
        return true;
    }
    
    private String getMonth() {
        switch(Calendar.getInstance().get(Calendar.MONTH)) {
            case 0:
                return "January";
            case 1:
                return "February";
            case 2:
                return "March";
            case 3:
                return "April";
            case 4:
                return "May";
            case 5:
                return "June";
            case 6:
                return "July";
            case 7:
                return "August";
            case 8:
                return "September";
            case 9:
                return "October";
            case 10:
                return "November";
            case 11:
                return "December";
            default:
                return "ERROR";
        }
    }
}