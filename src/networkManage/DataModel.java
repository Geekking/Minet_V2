
import java.util.ArrayList;

public class DataModel {
	private static String userName;
	private static String passWord;
	private static int csMessagePort;
	private static int p2pAcceptPort=5202;
	private static DataModel userInfo = null;
	private static String serverIPAddr = "222.200.180.109";
	private static int serverPort = 1;
	private static ArrayList<ArrayList<String> > onlineUsers;
	private DataModel(){}
	
	
	public synchronized  static  DataModel getInstance(){
		if (userInfo == null){
			userInfo = new DataModel();
		}
		return userInfo;
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
	public boolean initOnlineUserlist(ArrayList onlineUserlist){
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
	public ArrayList getOnlineUser(int index){
		if (index <0 || onlineUsers.size() < index){
			return null;
		}
		else {
			return onlineUsers.get(index);
		}
	}
	public ArrayList addAOnlineUser(ArrayList UserMessage){
		if (onlineUsers.add(UserMessage) ){
			return onlineUsers;
		}
		else{
			return null;
		}
	}
}
