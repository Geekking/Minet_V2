package networkManage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class MessageManipulator {
	private static  MessageManipulator msgMan = null;
	private MessageManipulator(){};
	
	public static synchronized MessageManipulator getInstance(){
		if(msgMan == null){
			msgMan = new  MessageManipulator();
			return msgMan;
		}
		return msgMan;
	}
	
	public HashMap<String,HashMap<String,String> > parseMessage(String message){
		HashMap<String,HashMap<String,String> > msgMap =  new HashMap<String,HashMap<String,String> >();
		
		HashMap<String,String> headRequest = new HashMap<String,String>();
		HashMap<String,String> headline = new HashMap<String,String>();
		HashMap<String,String> bodyline = new HashMap<String,String>();
		
		String[] messageline = message.split("\r\n");
		int i=0;
		String[] headrequestValues = messageline[0].split(" ");
		for(int j=0;j<headrequestValues.length;j++){
			Integer intj = new Integer(j);
			headRequest.put(intj.toString(), headrequestValues[j]);
		}
		
		for(i=1;i<messageline.length;i++){
			if(messageline[i] == ""){
				break;
			}
			else{
				String[] headlineValues = messageline[i].split(" ");
				headline.put(headlineValues[0], headlineValues[1]);
			}
		}
		String entityMsg = "";
		for(;i<messageline.length;i++){
			if(messageline[i] == ""){
				break;
			}
			else{
				entityMsg += messageline[i];
			}
		}
		bodyline.put("Entity",entityMsg);
		msgMap.put("requestline", headRequest);
		msgMap.put("headline", headline);
		msgMap.put("bodyline", bodyline);
		
		return msgMap;
	}
	public String formatAMessage(HashMap<String,HashMap<String,String> > msgMap){
		String msg = new String();
		//get REQUEST line:
		HashMap<String,String> headRequest = msgMap.get("requestline");
		HashMap<String,String> headline = msgMap.get("headline");
		HashMap<String,String> bodyline = msgMap.get("body");
		//process headrequest
		Iterator itr = headRequest.keySet().iterator();
		boolean isFirst=true;
		while (itr.hasNext()){
			Object key = itr.next();
			if (isFirst){
				isFirst =false;
				msg += headRequest.get(key);
				
			}
			else{
				msg += " "+headRequest.get(key);
			}
		}
		msg += "\r\n";
		
		//process headline
		itr = headline.keySet().iterator();
		while (itr.hasNext()){
			Object key = itr.next();
			msg += key.toString();
			msg += " ";
			msg += headline.get(key);
			msg += "\r\n";
		}
		msg += "\r\n";
		
		//process bodyline
		itr = bodyline.keySet().iterator();
		while (itr.hasNext()){
			Object key = itr.next();
			msg += key.toString();
			msg += " ";
			msg += bodyline.get(key);
			msg += "\r\n";
		}
		msg += "\r\n";
		return msg;
	}
	public boolean shakeHand(BufferedReader in,PrintWriter out,String type) throws IOException{
		String clientShakeHandMessage = type+" "+"Client"+"\r\n";
		try{
			out.println(clientShakeHandMessage);
			//TODO:set a time out
			String result = in.readLine();
			if(result == type+" "+"Server"+"\r\n"){
				return true;
			}
		}catch (Exception e){
			
		}
		return false;
	}
}
