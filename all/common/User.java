package all.common;

//用户信息类
public class User {
	private String account;
	private String password;
	private String username;

	public User(String account, String password, String username) {
		this.account = account;
		this.password = password;
		this.username = username;
	}
	public User(String account, String username) {
		this.account = account;
		this.username = username;
	}
	public User() {
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUserName() {
		return username;
	}
	public void setUserName(String name) {
		this.username = name;
	}
}