package networkManage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.Set;

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
	public LinkedHashMap<String,String> getHeadRequest(BufferedReader in) throws IOException{
		LinkedHashMap<String,String> headRequest = new LinkedHashMap<String,String>();
		
		String headrequestline = in.readLine();
		
		String[] headrequestValues = headrequestline.split(" ");
		for(int j=0;j<headrequestValues.length;j++){
			Integer intj = new Integer(j);
			headRequest.put(intj.toString(), headrequestValues[j]);
		}
		return headRequest;
	}
	
	public LinkedHashMap<String,String> getHeadLine(BufferedReader in) throws IOException{
		LinkedHashMap<String,String> headline = new LinkedHashMap<String,String>();
		String headlineStr = in.readLine();
		while(headlineStr.length() >0){
			String value = headlineStr.substring(headlineStr.indexOf(' '));
			String[] headlineValues = headlineStr.split(" ");
			value = value.substring(1, value.length());
			headline.put(headlineValues[0], value);
			headlineStr = in.readLine();
		}
		return headline;
	}
	public LinkedHashMap<String,String> getEntityMessage(BufferedReader in) throws IOException{
		LinkedHashMap<String,String> bodyline = new LinkedHashMap<String,String>();
		
		String oneEntityline = in.readLine();
		String entityMsg = "";
		while(oneEntityline.length() >0){
			entityMsg += oneEntityline+"\n";
			oneEntityline = in.readLine();
		}
		bodyline.put("Entity",entityMsg);
		return bodyline;
		
	}
	public LinkedHashMap<String,LinkedHashMap<String,String> > parseMessage(BufferedReader in) throws IOException{
		LinkedHashMap<String,LinkedHashMap<String,String> > msgMap =  new LinkedHashMap<String,LinkedHashMap<String,String> >();
		LinkedHashMap<String,String> headRequest = new LinkedHashMap<String,String>();
		LinkedHashMap<String,String> headline = new LinkedHashMap<String,String>();
		LinkedHashMap<String,String> bodyline = new LinkedHashMap<String,String>();
		
		String headrequestline = in.readLine();
		//System.out.println(headrequestline);
		
		String[] headrequestValues = headrequestline.split(" ");
		for(int j=0;j<headrequestValues.length;j++){
			Integer intj = new Integer(j);
			headRequest.put(intj.toString(), headrequestValues[j]);
		}
		String headlineStr = in.readLine();
		//System.out.println(headlineStr);
		
		while(headlineStr.length() >0){
			String[] headlineValues = headlineStr.split(" ");
			headline.put(headlineValues[0], headlineValues[1]);
			headlineStr = in.readLine();
		}
		String oneEntityline = in.readLine();
		String entityMsg = "";
		while(oneEntityline.length() >0){
			entityMsg += oneEntityline+"\n";
			oneEntityline = in.readLine();
			
		}
		bodyline.put("Entity",entityMsg);
		msgMap.put("requestline", headRequest);
		msgMap.put("headline", headline);
		msgMap.put("bodyline", bodyline);
		return msgMap;
	}
	
	public String formatAMessage(LinkedHashMap<String,LinkedHashMap<String,String> > msgMap){
		String msg = new String();
		//get REQUEST line:
		LinkedHashMap<String,String> headRequest = msgMap.get("requestline");
		LinkedHashMap<String,String> headline = msgMap.get("headline");
		LinkedHashMap<String,String> bodyline = msgMap.get("body");
		//process headRrequest
		Set<String> keyset = headRequest.keySet();
		
		boolean isFirst=true;
		for(String key:keyset){
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
		Iterator itr = headline.keySet().iterator();
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
			msg += bodyline.get(key);
			msg += "\r\n";
		}
		msg += "\r\n";
		//System.out.println(msg);
		return msg;
	}
	public boolean shakeHand(BufferedReader in,OutputStream out,String type) throws IOException{
		
		String clientShakeHandMessage = type+" "+"Client"+"\r\n";
		//System.out.println(clientShakeHandMessage);
		try{
			out.write(clientShakeHandMessage.getBytes("UTF-8"));
			//TODO:set a time out
			String result = in.readLine();
			//System.out.println(result);
			if(result.equals("MIRO"+" "+"Server")){
				return true;
			}
			
		}catch (Exception e){
			
		}
		return false;
	}
	public boolean HandleShankHandRequest(BufferedReader in,OutputStream out) throws IOException{
		
		String requestMessage = in.readLine();
		String hello = "MIRO Server\r\n";
		if(requestMessage.equals("MINET Client")){
			out.write(hello.getBytes("UTF-8"));
			return true;
		}
		return false;
	}
}
