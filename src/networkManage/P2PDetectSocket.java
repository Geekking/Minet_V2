package networkManage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import view.P2PChatResponse;

public class P2PDetectSocket extends ServerSocket{
		private ServerSocket detectSocket;
		private int detectport;
		public P2PDetectSocket()throws Exception{
			
			detectSocket = new ServerSocket(0);			
			System.out.println();
			new DetectThread();
		}
		public int getDetectPort(){
			return detectSocket.getLocalPort() ;
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
							new P2PChatResponse(socket);
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