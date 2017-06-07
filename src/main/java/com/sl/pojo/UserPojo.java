package com.sl.pojo;

public class UserPojo
{
    private long user_id;

    private long twitter_user_id;

    private String twitter_screen_name;

    private String access_token;

    private String access_token_secret;

	public long getUser_id() {
		return user_id;
	}

	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}

	public long getTwitter_user_id() {
		return twitter_user_id;
	}

	public void setTwitter_user_id(long twitter_user_id) {
		this.twitter_user_id = twitter_user_id;
	}

	public String getTwitter_screen_name() {
		return twitter_screen_name;
	}

	public void setTwitter_screen_name(String twitter_screen_name) {
		this.twitter_screen_name = twitter_screen_name;
	}

	public String getAccess_token() {
		return access_token;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

	public String getAccess_token_secret() {
		return access_token_secret;
	}

	public void setAccess_token_secret(String access_token_secret) {
		this.access_token_secret = access_token_secret;
	}

	@Override
	public String toString() {
		return "UserPojo [user_id=" + user_id + ", twitter_user_id="
				+ twitter_user_id + ", twitter_screen_name="
				+ twitter_screen_name + ", access_token=" + access_token
				+ ", access_token_secret=" + access_token_secret + "]";
	}

}
			