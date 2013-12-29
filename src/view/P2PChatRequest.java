package view;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JOptionPane;

import networkManage.DataModel;
import networkManage.MessageManipulator;


public class P2PChatRequest {
	public static final String P2PChatRequest = null;
	private String ipaddr;
	private String port;
	static Socket newsocket;
	static String uname;
	private BufferedReader in=null;
	private boolean hasResponsed =false;
	public P2PChatRequest(String ipaddr, String port, String un) throws Exception {
		this.ipaddr = ipaddr;
		this.port = port;
		this.uname = un;
		newsocket = new Socket(ipaddr,Integer.valueOf(port));
		in = new BufferedReader(new InputStreamReader(newsocket.getInputStream(),"UTF-8"));
		if(MessageManipulator.getInstance().shakeHand(in,newsocket.getOutputStream(),"MINET")){
			sendRequestMessage();
		};
		
		/*
		while (true) {
			Timer timer = new Timer();
			timer.schedule(new Beat(), 0, 10000);
		}
		*/
	}
	public static String getTime() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		String nowDate = format.format(date);
		return nowDate;
	}
	public void sendRequestMessage() throws Exception{
		String string = "P2P1.0 " + "P2PREQUEST " + uname + "\r\n";
		string += "Date " + getTime() + "\r\n";
		string += "Content-Type " +"text/html; charset=ISO-8859-1\r\n\r\n";
		string += "\r\n";
		try {
			newsocket.getOutputStream().write(string.getBytes("utf-8"));
			TimOutCount();
			if(HandleResponse()){
				(new P2PChatView(newsocket)).setVisible(true);;
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public boolean HandleResponse(){
		JOptionPane.showMessageDialog(null, "Connecting...");
		try {
			if(MessageManipulator.getInstance().parseMessage(in) != null){
				hasResponsed =true;
				return true;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Failed to connect");
			return false;
			
		}
			
		return false;
	}
	
	public void TimOutCount(){
        Timer timer  = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
            	try {
            		if(hasResponsed ==false){
            			newsocket.close();
            		}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        }, 120000);
    }
	
}