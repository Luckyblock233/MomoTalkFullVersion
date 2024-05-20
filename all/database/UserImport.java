package all.database;

import all.common.*;

import java.sql.SQLException;
import java.util.ArrayList;

public interface UserImport {
    public User login(String userId, String userPassword) throws SQLException;
    public Boolean register(User user) throws SQLException;
    public ArrayList<User> getFriendList(String user_id) throws SQLException;
    public ArrayList<Group> getGroupList(String user_id) throws SQLException;
    public Boolean addFriend(String userId1, String userId2) throws SQLException;
    public Boolean deleteFriend(String userId1, String userId2) throws SQLException;
    public Group createGroup(Group group) throws SQLException;
    public Boolean deleteGroup(String groupId) throws SQLException;
    public Boolean addGroupMember(String userId, String GroupId) throws SQLException;
    public Boolean deleteGroupMember(String userId, String GroupId) throws SQLException;
}
