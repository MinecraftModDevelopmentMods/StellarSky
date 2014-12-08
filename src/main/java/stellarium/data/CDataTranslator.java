package stellarium.data;

import java.util.LinkedList;

public class CDataTranslator {
	public static byte[] Strtobyte(String[] data){
		int len = 0;
		
		for(String str : data){
			len += str.length();
			len++;
		}
		
		byte[] dat = new byte[len];
		
		len = 0;
		
		for(String str : data){
			for(byte b : str.getBytes()){
				dat[len++] = b;
			}
			dat[len++] = ' ';
		}
		
		return dat;
	}
	
	public static String[] BytetoStr(byte[] data){
		int i;
		LinkedList<String> dat = new LinkedList<String>();
		String str = new String();
		
		for(byte b : data){
			if(b == ' '){
				dat.add(str);
				
				str = new String();
			}
			else str += b;
		}
		
		dat.add(str);
		
		return (String[]) dat.toArray();
	}
}
