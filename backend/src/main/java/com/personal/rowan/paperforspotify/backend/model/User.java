package com.personal.rowan.paperforspotify.backend.model;

import java.util.regex.Pattern;

public class User {

	public final static String KEY_PARENT = "UserParent";
	public final static String KEY_ENTITY = "User";

	public final static String KEY_ID = "userId";
	public final static String KEY_NAME = "name";
	public final static String KEY_IMAGE_URL = "imageUrl";
	public final static String KEY_REG_ID = "regId";

	private String id;
	private String name;
	private String imageUrl;
	private String regId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public String getFirstName() {
		return name.split(Pattern.quote("+"))[0];
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getRegId() {
		return regId;
	}

	public void setRegId(String regId) {
		this.regId = regId;
	}
}
