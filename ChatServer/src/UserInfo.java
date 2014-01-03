import java.io.IOException;
import java.net.Socket;


public class UserInfo {
	private String port;
	private Socket socket;
	private String username;
	private String beattime;
	public UserInfo(String un, String port, Socket socket, String bt) {
		this.username = un;
		this.port = port;
		this.socket = socket;
		this.beattime = bt;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getBeattime() {
		return beattime;
	}
	public void setBeattime(String beattime) {
		this.beattime = beattime;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}

	public Socket getSocket() {
		return socket;
	}
	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	public void SocketClose() {
		try {
			this.socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
