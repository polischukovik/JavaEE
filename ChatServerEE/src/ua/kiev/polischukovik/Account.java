package ua.kiev.polischukovik;

public class Account {
	private String login;
	private String password;
	
	public Account(String login, String password) {
		super();
		this.login = login;
		this.password = password;
	}
	
	
	
	public String getLogin() {
		return login;
	}



	public String getPassword() {
		return password;
	}



	public boolean checkPass(String rawPassword){
		return Crypto.encrypt(rawPassword).equals(this.password);
	}
}
