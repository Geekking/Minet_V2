package networkManage;
import java.util.ArrayList;

public class DataModel {
	private static String userName=null;
	private static String passWord=null;
	private static int csMessagePort=0;
	private static int p2pAcceptPort=5202;
	private static DataModel userInfo = null;
	private static String serverIPAddr = "172.18.157.254";
	private int serverPort = 1234;
	private static ArrayList<ArrayList<String> > onlineUsers;
	
	public synchronized  static  DataModel getInstance(){
		if (userInfo == null){
			userInfo = new DataModel();
		}
		return userInfo;
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
	public String getPassWord(){
		return DataModel.passWord;
	}
	public int getcsMessagePort(){
		return csMessagePort;
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
	public void setcsMessagePort(int csport){
		csMessagePort = csport;
	}
	public void setp2pAcceptPort(int p2pport){
		p2pAcceptPort = p2pport;
	}
	public boolean initUserInfo(String username,String password,int csport,int p2pport){
		if(userName == null){
			userName = username;
			passWord = password;
			csMessagePort = csport;
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
	
}
