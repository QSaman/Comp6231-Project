package comp6231.project.frontEnd;

public class Info {
	public String json;
	public int port;
	public String timeOutGroup;
	
	public Info(String json , int port) {
		this.json = json;
		this.port = port;
		timeOutGroup = "";
	}
	
	public Info(String json) {
		this.json = json;
		port = -1;
		timeOutGroup = "";
	}
}