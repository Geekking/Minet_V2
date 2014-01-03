import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import sun.misc.BASE64Encoder;


public class MD5 {
	public static String EncoderByMD5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		BASE64Encoder temp = new BASE64Encoder(); 
		String newstr = temp.encode(md5.digest(str.getBytes("utf-8")));
		return newstr;
		
	}
	
	public static boolean CheckPassword(String newpw, String oldpw) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		if (EncoderByMD5(newpw).equals(oldpw)) {
			return true;
		} else {
			return false;
		}
	}
}
