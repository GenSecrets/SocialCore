package com.nicholasdoherty.socialcore.components.emotes;

import com.nicholasdoherty.socialcore.components.genders.Gender;

/**
 * Created with IntelliJ IDEA.
 * User: john
 * Date: 11/12/13
 * Time: 23:08
 * To change this template use File | Settings | File Templates.
 */
public class Replacement {
	String key,maleReplacement,femaleReplacement,replacement;

	public Replacement(String key, String maleReplacement, String femaleReplacement, String replacement) {
		this.key = key;
		this.maleReplacement = maleReplacement;
		this.femaleReplacement = femaleReplacement;
		this.replacement = replacement;
	}

	public Replacement(String key, String replacement) {
		this.key = key;
		this.replacement = replacement;
	}

	public String getKey() {
		return key;
	}

	public String getMaleReplacement() {
		return maleReplacement;
	}

	public String getFemaleReplacement() {
		return femaleReplacement;
	}

	public String getReplacement(Gender gender) {
		if (maleReplacement != null && gender.equals(new Gender("MALE")))
			return maleReplacement;
		if (femaleReplacement != null && gender.equals(new Gender("FEMALE")))
			return femaleReplacement;
		return replacement;
	}

	public String getReplacement() {
		return replacement;
	}
}
