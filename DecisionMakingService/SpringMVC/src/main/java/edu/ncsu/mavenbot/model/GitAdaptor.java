package edu.ncsu.mavenbot.model;

public class GitAdaptor {

	private String url;
	private String userName;
	private String token;
	private String command;
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	@Override
	public String toString() {
		return "GitAdaptor [url=" + url + ", userName=" + userName + ", token=" + token + ", command=" + command + "]";
	}
	
	
}
