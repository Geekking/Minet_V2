import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;


public class P2PChat {
	private String ipaddr;
	private String port;
	static Socket newsocket;
	static String uname;
	public P2PChat(String ipaddr, String port, String un) throws UnknownHostException, IOException {
		this.ipaddr = ipaddr;
		this.port = port;
		this.uname = un;
		newsocket = new Socket("123", 123);
		shakehands();
		while (true) {
			Timer timer = new Timer();
			timer.schedule(new Beat(), 0, 10000);
		}
		
	}
	
	public void shakehands() throws UnsupportedEncodingException, IOException {
		String string2 = "MINET P2P\r\n";
		newsocket.getOutputStream().write(string2.getBytes("utf-8"));
	}
	
	public void sendmessage(String data) throws UnsupportedEncodingException, IOException {
		String string = "P2P1.0 " + "P2PMESSAGE " + uname + "\r\n";
		string += "Date " + getTime() + "\r\n";
		string += "Content-Type " +"text/html; charset=ISO-8859-1\r\n\r\n";
		string += data + "\r\n";
		newsocket.getOutputStream().write(string.getBytes("utf-8"));
	}
	
	public void sendleavemessage() throws UnsupportedEncodingException, IOException {
		String string = "CS1.0 " + "LEAVE " + uname + "\r\n";
		string += "Date " + getTime() + "\r\n";
		string += "\r\n\r\n";
		newsocket.getOutputStream().write(string.getBytes("utf-8"));
	}
	public static String getTime() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		String nowDate = format.format(date);
		return nowDate;
	}
	

}
