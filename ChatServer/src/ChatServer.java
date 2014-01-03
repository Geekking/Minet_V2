import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;


/**
 * 
 */

/**
 * @author Charles
 *
 */
public class ChatServer extends Thread{

	/**
	 * @param args
	 */
	private Socket client;
	private String un;
	String clientInfo;
	ChatServer cr;
	static HashMap<String, UserInfo> clientLists = new HashMap<String, UserInfo>();//store the list of clients
	private HashMap<String, String> dbclientLists = new HashMap<String, String>();//获取数据中的用户名和相应密码
	public ChatServer(String uname, Socket client){
		this.un = uname;
		this.client = client;
	}
	
	public static void main(String[] args) throws Exception{
		// TODO 自动生成的方法存根  响应客户端的请求
		ServerSocket welcomeSocket = new ServerSocket(1234);
		while (true) {
			Socket connectionSocket = welcomeSocket.accept();
			System.out.println("成功取得客户端请求！");
			//return welcome info to current client
			new ChatServer("name",connectionSocket).start();//start a new thread for each client
			Timer timer = new Timer();
			timer.schedule(new Task(), 0, 10000);
		}
	}
	
	//when add a new client, call start() and do sth
	@Override  
	public void run() {
		try {
			while (true) {
				BufferedReader inFromClient = new BufferedReader((new InputStreamReader(client.getInputStream(),"utf-8")));
				String[] lineArray;
				DBGetdbclientLists();//得到数据库所有用户数据
				int flag = 0;
				int flagl = 0;
				clientInfo = inFromClient.readLine();//information from client
				System.out.println(clientInfo);
				lineArray = clientInfo.toString().split(" ");
				//注册操作
				if (lineArray[0].equals("REGISTER")) {
					boolean is = true;
					
					for (String string : dbclientLists.keySet()) {		
						if (lineArray[1].equals(string)) {
							String string2 = "此用户名已经注册过，请使用其它用户名！\r\n";
							client.getOutputStream().write(string2.getBytes("utf-8"));
							is = false;
						}
					}
					if (is) {
						DBInsertOperation(lineArray[1], MD5.EncoderByMD5(lineArray[2]), lineArray[3]);
						dbclientLists.put(lineArray[1], MD5.EncoderByMD5(lineArray[2]));
						String string2 = "用户注册成功！\r\n";
						client.getOutputStream().write(string2.getBytes("utf-8"));
					}
					
				}
				//修改密码操作,客户端发送：用户名 旧密码 新密码
				if (lineArray[0].equals("MODIFYPASSWORD")) {
					boolean isa = true;
					for (String string : dbclientLists.keySet()) {
						if (lineArray[1].equals(string)) {
							if (MD5.CheckPassword(lineArray[2], dbclientLists.get(string))) {
								String string2 = "密码修改成功！\r\n";
								client.getOutputStream().write(string2.getBytes("utf-8"));
								modifyPassword(lineArray[1], MD5.EncoderByMD5(lineArray[3]));
								isa = false;
							}						
						}
					}
					if (isa) {
						String string2 = "密码修改失败！\r\n";
						client.getOutputStream().write(string2.getBytes("utf-8"));
					}
					
				}
				
				if (lineArray[0].equals("MINET")) {//握手命令
					String string2 = "MIRO Server\r\n";
					client.getOutputStream().write(string2.getBytes("utf-8"));
					
				}
				else {
					if (lineArray[0].equals("CS1.0") && lineArray[1].equals("LOGIN")) {
						un = lineArray[2]; 
						flagl = 0;
						flag = 0;
						for (String string : dbclientLists.keySet()) {
							if (string.equals(lineArray[2])) {//用户名是否正确
								flagl = 1;
							}
						}
						if (flagl == 1) {
							for (String string : clientLists.keySet()) {
								if (string.equals(lineArray[2])) {//已登录不可再登陆
									String cl = "CS1.0 STATUS 0\r\n";
									cl += "Date ";
									cl += getTime();
									cl += "\r\n\r\n";
									cl += "用户已经登录，不可重复登录！\r\n\r\n";
									client.getOutputStream().write(cl.getBytes("utf-8"));
									flag = 1;
								}
							}
							if (flag == 0) {
								while (!clientInfo.equals("")) {
									clientInfo = inFromClient.readLine();
									lineArray = clientInfo.toString().split(" ");
									if (lineArray[0].equals("Password")) {
										if (!MD5.CheckPassword(lineArray[1], dbclientLists.get(un))) {
											String cl = "CS1.0 STATUS 0\r\n";
											cl += "Date ";
											cl += getTime();
											cl += "\r\n\r\n";
											cl += "登录名与密码不相符合！\r\n\r\n";
											client.getOutputStream().write(cl.getBytes("utf-8"));
											break;
										}
									}
									if (lineArray[0].equals("Port")) {
										clientLists.put(un, new UserInfo(un, lineArray[1], client, getTime()));//存储登录用户的用户名、监听端口号、Sockt
										String cl = "CS1.0 STATUS 1\r\n";
										cl += "Date ";
										cl += getTime();
										cl += "\r\n\r\n\r\n";
										client.getOutputStream().write(cl.getBytes("utf-8"));
										broadcastUpdateOnlineClientInfo(un, "online");
										
										
									}
								}
							}
						} else {
							String cl = "CS1.0 STATUS 0\r\n";
							cl += "Date ";
							cl += getTime();
							cl += "\r\n\r\n";
							cl += "用户不存在！\r\n";
							client.getOutputStream().write(cl.getBytes("utf-8"));
						}
						
						
					}
					//回应客户端获取在线用户表的请求
					if (lineArray[0].equals("CS1.0") && lineArray[1].equals("GETLIST")){
						
						sendClientListsToNewClient();
					}
					//处理用户下线
					if (lineArray[0].equals("CS1.0") && lineArray[1].equals("LEAVE")){
						clientLists.get(lineArray[2]).setSocket(client);				
						broadcastUpdateOnlineClientInfo(lineArray[2], "offline");
						clientLists.remove(lineArray[2]);
						client.close();
					}
					//用户发送群发消息给server
					if (lineArray[0].equals("CS1.0") && lineArray[1].equals("MESSAGE")){
						clientLists.get(lineArray[2]).setSocket(client);
						while (!inFromClient.readLine().equals("")) {
							inFromClient.readLine();				
						}
						broadcastMessage(lineArray[2], inFromClient.readLine());
					}
					//心跳信息处理
					if (lineArray[0].equals("CS1.0") && lineArray[1].equals("BEAT")){
						clientLists.get(lineArray[2]).setSocket(client);
						clientLists.get(lineArray[2]).setBeattime(getTime()); 
					}
				}
				
			}
		} catch (Exception e) {
			// TODO: handle exception
		}	
	}

	//broadcast  update info of client online to each client
	public static void broadcastUpdateOnlineClientInfo(String uname, String str) throws IOException {
		if (str.equals("online")) {
			for (String s : clientLists.keySet()) {
				String cl = "CS1.0 UPDATE 1 " + uname + "\r\n";
				cl += "Date ";
				cl += getTime();
				cl += "\r\n";
				cl += "IPAddr "+ clientLists.get(uname).getSocket().getInetAddress().getHostAddress() + "\r\nPort " + clientLists.get(uname).getPort() + "\r\n\r\n\r\n";
				clientLists.get(s).getSocket().getOutputStream().write(cl.getBytes("utf-8"));
			}
		} else {
				
			for (String s : clientLists.keySet()) {
				String cl = "CS1.0 UPDATE 0 " + uname + "\r\n";
				cl += "Date ";
				cl += getTime();
				cl += "\r\n";
				cl += "IPAddr "+ clientLists.get(uname).getSocket().getInetAddress().getHostAddress() + "\r\nPort " + clientLists.get(uname).getPort() + "\r\n\r\n\r\n";
				clientLists.get(s).getSocket().getOutputStream().write(cl.getBytes("utf-8"));
			}
			clientLists.get(uname).getSocket().close();
			clientLists.remove(uname);
		}
		
	}
	//broadcast  message
		public void broadcastMessage(String uname, String str) throws IOException {
		
				for (String s : clientLists.keySet()) {
					
					String cl = "CS1.0 CSMESSAGE " + uname + "\r\n";
					cl += "Date ";
					cl += getTime();
					cl += "\r\n\r\n";
					cl = cl + str + "\r\n\r\n";
					clientLists.get(s).getSocket().getOutputStream().write(cl.getBytes("utf-8"));
					//System.out.println(cl);
				}
				

			}
	
	//send client list to new client
	public void sendClientListsToNewClient() throws IOException {
		String cl = "CS1.0 LIST\r\n";
		cl += "Date ";
		cl += getTime();
		cl += "\r\n\r\n";
		for (String s : clientLists.keySet()) {
			cl = cl + s + ' ' + clientLists.get(s).getSocket().getInetAddress().getHostAddress() + ' ' + clientLists.get(s).getPort() + "\r\n";
		}
		cl += "\r\n";
		client.getOutputStream().write(cl.getBytes("utf-8"));//返回在线用户列表
	}
	
	//change password
	public void modifyPassword(String username, String newpassword) throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/chatroom?useUnicode=true&characterEncoding=utf8","root","123456");
		Statement stmt = connection.createStatement();
		String sql = "update user SET password = '" + newpassword + "' where username = '" + username +"'";
		stmt.executeUpdate(sql);
		
		dbclientLists.remove(username);
		dbclientLists.put(username, newpassword);
	}
	//获取数据库中用户列表
	public void DBGetdbclientLists() throws ClassNotFoundException, SQLException {
		
		Class.forName("com.mysql.jdbc.Driver");
		Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/chatroom?useUnicode=true&characterEncoding=utf8","root","123456");
		Statement stmt = connection.createStatement();
		String sql = "select * from user";
		ResultSet rSet = stmt.executeQuery(sql);
		
		while (rSet.next()) {
			dbclientLists.put(rSet.getString("username"), rSet.getString("password"));
		}
	}
	//插入数据库新注册的成员信息
	public void DBInsertOperation(String uname, String pword, String em) throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/chatroom?useUnicode=true&characterEncoding=utf8","root","123456");
		Statement stmt = connection.createStatement();
		String sql = "insert into user values('" + uname +"','" + pword +"','" + em +"')";
		stmt.executeUpdate(sql);
	}
	//time
	public static String getTime() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		String nowDate = format.format(date);
		return nowDate;
	}
	
	
	

}
