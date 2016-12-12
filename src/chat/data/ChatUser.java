package chat.data;

public class ChatUser {
	private String name;
	private String pass;
	private int clientThread = -1;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPass() {
		return pass;
	}
	public void setPass(String pass) {
		this.pass = pass;
	}
	public int getClientThread() {
		return clientThread;
	}
	public void setClientThread(int clientThread) {
		this.clientThread = clientThread;
	}
	public ChatUser(String name, String pass) {
		super();
		this.name = name;
		this.pass = pass;
	}



}
