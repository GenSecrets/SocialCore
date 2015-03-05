package com.nicholasdoherty.socialcore;

/**
 * Created with IntelliJ IDEA.
 * User: john
 * Date: 11/18/13
 * Time: 20:03
 * To change this template use File | Settings | File Templates.
 */
public class Fixer {
	SocialCore plugin;

	public Fixer(SocialCore plugin) {
		this.plugin = plugin;
	}
	public void fix() {
		plugin.store.fixTables();
		plugin.store.fixEngagements();
		plugin.store.fixMarriages();
	}
}
