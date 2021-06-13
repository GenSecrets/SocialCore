package com.nicholasdoherty.socialcore.courts.courtroom.actions;

import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.courts.cases.Case;
import com.nicholasdoherty.socialcore.courts.cases.CaseCategory;
import com.nicholasdoherty.socialcore.courts.cases.category.CategoryConfig;
import com.nicholasdoherty.socialcore.courts.cases.category.SameSexCategoryConfig;
import com.nicholasdoherty.socialcore.courts.courtroom.PostCourtAction;
import com.nicholasdoherty.socialcore.courts.objects.Citizen;
import com.voxmc.voxlib.util.VaultUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by john on 3/2/15.
 */
@SuppressWarnings("unused")
public class GrantSameSexMarriage implements PostCourtAction, ConfigurationSerializable {
    private final int cazeId;
    
    public GrantSameSexMarriage(final Case caze) {
        cazeId = caze.getId();
    }
    
    public GrantSameSexMarriage(final Map<String, Object> map) {
        cazeId = (int) map.get("case-id");
    }
    
    @Override
    public Map<String, Object> serialize() {
        final Map<String, Object> map = new HashMap<>();
        map.put("case-id", cazeId);
        return map;
    }
    
    @Override
    public void doAction() {
        final Case caze = Courts.getCourts().getCaseManager().getCase(cazeId);
        if(caze == null) {
            return;
        }
        if(caze.getPlantiff() != null) {
            grantPermissions(caze.getPlantiff());
        }
        if(caze.getDefendent() != null) {
            grantPermissions(caze.getDefendent());
        }
    }
    
    public void grantPermissions(final Citizen citizen) {
        final CategoryConfig categoryConfgi = Courts.getCourts().getCourtsConfig().getCategoryConfgi(CaseCategory.SAMESEX_MARRIAGE);
        if(categoryConfgi instanceof SameSexCategoryConfig) {
            final SameSexCategoryConfig sameSexCategoryConfig = (SameSexCategoryConfig) categoryConfgi;
            for(final String perm : sameSexCategoryConfig.getPermissions()) {
                VaultUtil.givePermission(Bukkit.getOfflinePlayer(citizen.getName()), perm);
            }
        }
    }
    
    @Override
    public String prettyDescription() {
        return ChatColor.GREEN + "Grants petitioner and spouse permissions for same sex marriage.";
    }
}
