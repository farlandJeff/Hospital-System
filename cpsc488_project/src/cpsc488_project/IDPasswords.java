package cpsc488_project;

import java.util.HashMap;


public class IDPasswords {

	HashMap<String,String> logininfo = new HashMap<String,String>(); 
	
	IDPasswords(){
		
		logininfo.put("Admin", "123");
		logininfo.put("Christian", "123");
		logininfo.put("Wayne", "123");
		logininfo.put("Jeff", "123");
	}
	
	protected HashMap getLoginInfo(){
		return logininfo;
	}
}
