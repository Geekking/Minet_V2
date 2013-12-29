package view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.LinkedHashMap;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import networkManage.DataModel;
import networkManage.MessageManipulator;

import org.omg.CORBA.PUBLIC_MEMBER;

public class P2PChatResponse {
	BufferedReader in;
	OutputStream out;
	Socket st;
	public P2PChatResponse(Socket socket) throws Exception{
		in = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
		out= socket.getOutputStream();
		st = socket;
		if(MessageManipulator.getInstance().HandleShankHandRequest(in, out)){
			HandleRequest();
		}
	}
	private boolean HandleRequest() throws Exception{
		try {
			LinkedHashMap<String,LinkedHashMap<String,String> > msgMap =  MessageManipulator.getInstance().parseMessage(in);
			String CSVersiont = msgMap.get("requestline").get("0");
			String MsgType = msgMap.get("requestline").get("1");
			String username = msgMap.get("requestline").get("2");
			
			if(MsgType.equals("P2PREQUEST")){
				//TODO:process failed to login
				int n= JOptionPane.showConfirmDialog(null, "\""+username+"\"" + "请求与你建立P2P连接","Message",JOptionPane.YES_NO_OPTION);
				System.out.println(n);
				if(n !=0 ){
					in.close();
					out.close();
					st.close();
					return false;
				}else{
					LinkedHashMap<String,String> headRequest =new LinkedHashMap<String,String>();
					LinkedHashMap<String,String> headline = new LinkedHashMap<String,String>();
					LinkedHashMap<String,String> bodyline = new LinkedHashMap<String,String>();
					headRequest.put("P2PVersion", "P2P1.0");
					headRequest.put("MessageType","RESPONSEP2PASK");
					headRequest.put("UserName",DataModel.getInstance().getUserName());
					headline.put("Content-length", "0");
					headline.put("Time", (new java.util.Date()).toString());
					LinkedHashMap<String,LinkedHashMap<String,String> > msgMap1 = new LinkedHashMap<String,LinkedHashMap<String,String> >();
					msgMap1.put("requestline", headRequest);
					msgMap1.put("headline", headline);
					msgMap1.put("body", bodyline);
					String responseMsg = MessageManipulator.getInstance().formatAMessage(msgMap1);
					st.getOutputStream().write(responseMsg.getBytes("UTF-8"));
					
					(new P2PChatView(st)).setTitle(username);
					
					return true;
				}
			}
			else{
				in.close();
				out.close();
				st.close();
				return false;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
}
