package networkManage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import view.*;
public class DataModel {
	private static  String userName=null;
	private  String passWord=null;
	private  int p2pAcceptPort=5202;
	private static DataModel userInfo = null;
	private  String serverIPAddr = "172.18.157.254";
	private int serverPort = 1234;
	private  ArrayList<ArrayList<String> > onlineUsers;
	
	public synchronized  static  DataModel getInstance() throws Exception{
		if (userInfo == null){
			userInfo = new DataModel();
		}
		return userInfo;
	}
	
	private DataModel() throws Exception{
		//new P2PDetectSocket();
		onlineUsers = new ArrayList<ArrayList<String> >();
	}
	
	
	public boolean addOnlineUsers(ArrayList<String> aUser){
		return onlineUsers.add(aUser);
	}
	public void removeOnlineUsers(int index){
		onlineUsers.remove(index);
	}
	public void resetOnlineUsers(){
		onlineUsers.clear();
	}
	public String getUserName(){
		return userName;
	}
	
	public int getp2pAcceptPort(){
		return p2pAcceptPort;
	}
	public String getServerIP(){
		return serverIPAddr;
	}
	public int getServerPort(){
		return this.serverPort;
	}
	public void setUserName(String userName){
		DataModel.userName = userName;
	}
	public void setpassWord(String password){
		passWord = password;
	}
	
	public void setp2pAcceptPort(int p2pport){
		p2pAcceptPort = p2pport;
	}
	public boolean initUserInfo(String username,int p2pport){
		if(userName == null){
			userName = username;
			p2pAcceptPort = p2pport;
			return true;
		}
		else return false;
	}
	public boolean initOnlineUserlist(ArrayList<ArrayList<String> > onlineUserlist){
		if (userName != null){
			onlineUsers = onlineUserlist;
			return true;
		}
		return false;
	}
	public boolean updatePassword(String newPassword){
		if (userName != null && passWord != null){
			passWord = newPassword;
			return true;
		}
		return false;
	}
	public ArrayList<String> getOnlineUser(int index){
		if (index <0 || onlineUsers.size() < index){
			return null;
		}
		else {
			return onlineUsers.get(index);
		}
	}
	
	public class P2PDetectSocket extends ServerSocket{
		ServerSocket detectSocket;
		public P2PDetectSocket()throws Exception{
			super(DataModel.getInstance().getp2pAcceptPort());
			detectSocket = this;
			p2pAcceptPort = this.getLocalPort();
			new DetectThread();
		}
		public class DetectThread extends Thread{
			public DetectThread(){
				start();
			}
			public void run(){
				while (true) {
					try {
						Socket socket = detectSocket.accept();
						try {
							new P2PChatRequest(socket);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
		}
		
	}
	
}
