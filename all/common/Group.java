package all.common;

import java.util.ArrayList;
import java.util.Vector;

public class Group {
    private ArrayList<User> members;
    private String name;
    private String account;

    public Group(String name, String account) {
        this.name = name;
        this.account = account;
        this.members = new ArrayList<>();
    }
    public Boolean haveMember(User user) {
        for (User member: members) {
            if (user.getAccount().equals(member.getAccount())) {
                return true;
            }
        }
        return false;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAccount() {
        return account;
    }
    public void setAccount(String account) {
        this.account = account;
    }
    public ArrayList<User> getMembers() {
        return members;
    }
    public void setMembers(ArrayList<User> members) {
        this.members = members;
    }
}
