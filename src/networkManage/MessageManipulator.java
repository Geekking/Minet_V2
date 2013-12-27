package networkManage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
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
	public HashMap<String,String> getHeadRequest(BufferedReader in) throws IOException{
		HashMap<String,String> headRequest = new HashMap<String,String>();
		String headrequestline = in.readLine();
		String[] headrequestValues = headrequestline.split(" ");
		for(int j=0;j<headrequestValues.length;j++){
			Integer intj = new Integer(j);
			headRequest.put(intj.toString(), headrequestValues[j]);
		}
		return headRequest;
	}
	public HashMap<String,String> getHeadLine(BufferedReader in) throws IOException{
		HashMap<String,String> headline = new HashMap<String,String>();
		String headlineStr = in.readLine();
		while(headlineStr.length() >0){
			String[] headlineValues = headlineStr.split(" ");
			headline.put(headlineValues[0], headlineValues[1]);
			headlineStr = in.readLine();
		}
		return headline;
	}
	public HashMap<String,String> getEntityMessage(BufferedReader in) throws IOException{
		HashMap<String,String> bodyline = new HashMap<String,String>();
		
		String oneEntityline = in.readLine();
		String entityMsg = "";
		while(oneEntityline.length() >0){
			entityMsg += oneEntityline+"\n";
			oneEntityline = in.readLine();
		}
		bodyline.put("Entity",entityMsg);
		return bodyline;
		
	}
	public HashMap<String,HashMap<String,String> > parseMessage(BufferedReader in) throws IOException{
		HashMap<String,HashMap<String,String> > msgMap =  new HashMap<String,HashMap<String,String> >();
		HashMap<String,String> headRequest = new HashMap<String,String>();
		HashMap<String,String> headline = new HashMap<String,String>();
		HashMap<String,String> bodyline = new HashMap<String,String>();
		
		String headrequestline = in.readLine();
		String[] headrequestValues = headrequestline.split(" ");
		for(int j=0;j<headrequestValues.length;j++){
			Integer intj = new Integer(j);
			headRequest.put(intj.toString(), headrequestValues[j]);
		}
		String headlineStr = in.readLine();
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
	
	public String formatAMessage(HashMap<String,HashMap<String,String> > msgMap){
		String msg = new String();
		//get REQUEST line:
		HashMap<String,String> headRequest = msgMap.get("requestline");
		HashMap<String,String> headline = msgMap.get("headline");
		HashMap<String,String> bodyline = msgMap.get("body");
		//process headRrequest
		Iterator<String> itr = headRequest.keySet().iterator();
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
			System.out.println(clientShakeHandMessage);
			//TODO:set a time out
			String result = in.readLine();
			System.out.println(result);
			if(result.equals(type+" "+"Server")){
				return true;
			}
			
		}catch (Exception e){
			
		}
		return false;
	}
}
