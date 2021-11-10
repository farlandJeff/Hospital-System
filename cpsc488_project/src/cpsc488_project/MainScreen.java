package cpsc488_project;

public class MainScreen {

	public static void main(String[] args) {
		
		IDPasswords idPasswords = new IDPasswords();
		
		LoginPage loginPage = new LoginPage(idPasswords.getLoginInfo());
		
	}
}
