
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import networkManage.P2PDetectSocket;

import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;

import view.login.LoginRegisterView;

public class Main {
	public static void main(String args[]){
		
		try
	    {
			//设置本属性将改变窗口边框样式定义
	        BeautyEyeLNFHelper.frameBorderStyle = BeautyEyeLNFHelper.FrameBorderStyle.osLookAndFeelDecorated;
	        org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
	        UIManager.put("RootPane.setupButtonVisible", false);
	    }
	    catch(Exception e)
	    {
	        //TODO exception
	    }
		
		try {
			int detectPort=(new P2PDetectSocket()).getDetectPort();
			LoginRegisterView  view = new LoginRegisterView(detectPort);
			
					
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
