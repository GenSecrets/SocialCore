package com.nicholasdoherty.socialcore.store;

import com.nicholasdoherty.socialcore.SocialPlayer;

/**
 * Created with IntelliJ IDEA.
 * User: john
 * Date: 11/14/13
 * Time: 18:53
 * To change this template use File | Settings | File Templates.
 */
public abstract class Store {
	public abstract void create(String name);
	public abstract void syncSocialPlayer(SocialPlayer socialPlayer);
	public abstract SocialPlayer getSocialPlayer(String name);
}

