package view;

import java.net.Socket;

import networkManage.P2PDetectSocket;

public class Main {
	public static void main(String args[]){
		
		try {
			int detectPort=(new P2PDetectSocket()).getDetectPort();
			new LoginRegisterView(detectPort);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
