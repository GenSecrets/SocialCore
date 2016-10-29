package com.nicholasdoherty.socialcore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.java.JavaPlugin;

import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.courts.inputlib.InputLib;
import com.nicholasdoherty.socialcore.courts.inventorygui.InventoryGUIManager;
import com.nicholasdoherty.socialcore.emotes.EmoteCommand;
import com.nicholasdoherty.socialcore.emotes.EmoteListener;
import com.nicholasdoherty.socialcore.emotes.Emotes;
import com.nicholasdoherty.socialcore.emotes.ForceEmoteCommand;
import com.nicholasdoherty.socialcore.genders.GenderCommandHandler;
import com.nicholasdoherty.socialcore.genders.Genders;
import com.nicholasdoherty.socialcore.marriages.MarriageCommandHandler;
import com.nicholasdoherty.socialcore.marriages.Marriages;
import com.nicholasdoherty.socialcore.marriages.PetnameCommand;
import com.nicholasdoherty.socialcore.marriages.PurgeInvalidCommand;
import com.nicholasdoherty.socialcore.marriages.StatusCommand;
import com.nicholasdoherty.socialcore.misc.GlobalMute;
import com.nicholasdoherty.socialcore.races.Race;
import com.nicholasdoherty.socialcore.races.Races;
import com.nicholasdoherty.socialcore.store.SQLStore;
import com.nicholasdoherty.socialcore.time.Clock;
import com.nicholasdoherty.socialcore.time.condition.TimeConditionManager;
import com.nicholasdoherty.socialcore.titles.TitleManager;
import com.nicholasdoherty.socialcore.utils.VaultUtil;




public class SocialCore extends JavaPlugin {
	private InventoryGUIManager inventoryGUIManager;
	//logger
	public final Logger log = Logger.getLogger("Minecraft");
		
	//lang
	public SCLang lang;
	
	//save
	public SaveHandler save;
	public Races races;
	//players
	public HashMap<String,SocialPlayer>socialPlayersCache;
	
	//genders
	public Genders genders;
	
	//marriages
	public Marriages marriages;
	public static SocialCore plugin;
	
	// globalmute
	public GlobalMute globalMute;
	
	//emotes
	public Emotes emotes;
	public SQLStore store;
	private InputLib inputLib;
	private Courts courts;
	private TimeConditionManager timeConditionManager;
	public enum Gender {
		MALE,FEMALE,UNSPECIFIED;
	}

	public InventoryGUIManager getInventoryGUIManager() {
		return inventoryGUIManager;
	}

	public InputLib getInputLib() {
		return inputLib;
	}

	@Override
	public void onEnable() {
		plugin = this;
        try {
            VaultUtil.setup(this.getServer());
        } catch (VaultUtil.NotSetupException e) {
            this.getLogger().severe("Vault not detected, exceptions ahoy");
        }
        Clock.start(SocialCore.plugin);
		timeConditionManager = new TimeConditionManager();
		inventoryGUIManager = new InventoryGUIManager(this);
		inputLib = new InputLib(this);
		socialPlayersCache = new HashMap<String,SocialPlayer>();
		//helpers
		checkConfig();
		String directory = getDataFolder().toString();
		races = new Races(this);
		save = new SaveHandler(directory, this);
		marriages = new Marriages(this);
		store = new SQLStore();
        courts = new Courts(this);
        globalMute = new GlobalMute(this);
        //commands
				SCCommandHandler scCommandHandler = new SCCommandHandler(this);
		MarriageCommandHandler marriageCommandHandler = new MarriageCommandHandler(this);
		GenderCommandHandler genderCommandHandler = new GenderCommandHandler(this);
		getCommand("socialcore").setExecutor(scCommandHandler);
		getCommand("male").setExecutor(genderCommandHandler);
		getCommand("female").setExecutor(genderCommandHandler);
		getCommand("gender").setExecutor(genderCommandHandler);
		getCommand("marriage").setExecutor(marriageCommandHandler);
		getCommand("marriages").setExecutor(marriageCommandHandler);
		getCommand("engagements").setExecutor(marriageCommandHandler);
		getCommand("propose").setExecutor(marriageCommandHandler);
		getCommand("marry").setExecutor(marriageCommandHandler);
		getCommand("divorce").setExecutor(marriageCommandHandler);
		getCommand("divorces").setExecutor(marriageCommandHandler);
		getCommand("adivorce").setExecutor(marriageCommandHandler);
		getCommand("amarry").setExecutor(marriageCommandHandler);
		getCommand("share").setExecutor(marriageCommandHandler);
		getCommand("unengage").setExecutor(marriageCommandHandler);
		getCommand("aunengage").setExecutor(marriageCommandHandler);
		getCommand("status").setExecutor(new StatusCommand());
		getCommand("purgeinvalids").setExecutor(new PurgeInvalidCommand());
		getCommand("petname").setExecutor(new PetnameCommand());
	//
		//testing
		getCommand("getpermissions").setExecutor(new ViewPermissionsCommand());
		//langs
		lang = new SCLang(this);
		lang.loadConfig();

		//events
		getServer().getPluginManager().registerEvents(new SCListener(this), this);

		//players
//
		//genders
		genders = new Genders();

		//marriages
//
		//emotes
		emotes = new Emotes(this);
		new EmoteListener(this);
		new EmoteCommand(this);
		new FixerCommand(this);
		new ForceEmoteCommand(this);
		races.reloadRaces();
		for (Player p : Bukkit.getOnlinePlayers()) {
			SocialPlayer socialPlayer = save.getSocialPlayer(p.getName());
			if (socialPlayer.getRace() != null) {
				Race race = socialPlayer.getRace();
				PermissionAttachment permissionAttachment = null;
				if (p.hasMetadata("pa")) {
					permissionAttachment = (PermissionAttachment) p.getMetadata("pa").get(0).value();
				}
				if (permissionAttachment == null) {
					permissionAttachment = p.addAttachment(this);
				}
//
//
				for (String key : permissionAttachment.getPermissions().keySet()) {
					permissionAttachment.unsetPermission(key);
				}
				if (p.hasPermission("sc.race.issupernatural")) {
					socialPlayer.setRace(races.getDefaultRace().getName());
					races.getDefaultRace().applyRace(p,permissionAttachment);
					return;
				}
				race.applyRace(p,permissionAttachment);
			}
		}
		titleManager = new TitleManager(this);
	}
	private TitleManager titleManager;

	public TimeConditionManager getTimeConditionManager() {
		return timeConditionManager;
	}

	@Override
	public void onDisable() {
		Clock.save();
		courts.onDisable();
		for(String p : SCListener.riding.keySet()) {
			if (p != null) {
				Player player = Bukkit.getPlayer(p);
				try {
					player.leaveVehicle();
				}catch (Exception e) {}
			}
		}
		titleManager.onDisable();
		titleManager.saveCacheFile();
	}
	public void onReload() {

	}
	
	private void checkConfig() {
		if (!this.getDataFolder().isDirectory()) {
			this.getDataFolder().mkdirs();
		}
		if (!new File(this.getDataFolder(), "config.yml").isFile()) {
			this.writeConfig();
		}
	}
	private void writeConfig() {
		if (this.writeDefaultFileFromJar(new File(this.getDataFolder(), "config.yml"), "config.yml", true)) {
			log.info("[SocialCore] Saved default config.");
		}
	}
	private boolean writeDefaultFileFromJar(File writeName, String jarPath, boolean backupOld) {
		try {
			File fileBackup = new File(this.getDataFolder(), "backup-" + writeName);
			File jarloc = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI()).getCanonicalFile();
			if (jarloc.isFile()) {
				JarFile jar = new JarFile(jarloc);
				JarEntry entry = jar.getJarEntry(jarPath);
				if (entry != null && !entry.isDirectory()) {
					InputStream in = jar.getInputStream(entry);
					InputStreamReader isr = new InputStreamReader(in, "UTF8");
					if (writeName.isFile()) {
						if (backupOld) {
							if (fileBackup.isFile()) {
								fileBackup.delete();
							}
							writeName.renameTo(fileBackup);
						} else {
							writeName.delete();
						}
					}
					FileOutputStream out = new FileOutputStream(writeName);
					OutputStreamWriter osw = new OutputStreamWriter(out, "UTF8");
					char[] tempbytes = new char[512];
					int readbytes = isr.read(tempbytes, 0, 512);
					while (readbytes > -1) {
						osw.write(tempbytes, 0, readbytes);
						readbytes = isr.read(tempbytes, 0, 512);
					}
					osw.close();
					isr.close();
					
					return true;
				}
				jar.close();
			}
			return false;
		} catch (Exception ex) {
			log.warning("[SocialCore] Failed to write default config. Stack trace follows:");
			ex.printStackTrace();
			return false;
		}
	}

}
