package all.database;

import all.common.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

public class UserImportSqlite implements UserImport {
    private int getMaxId(String table) throws SQLException{
        int max_id = 1;
        Connection connection=DBConnection.getConnection();
        String sql="select max(id) as max_id from " + table;
        PreparedStatement statement=connection.prepareStatement(sql);
        ResultSet resultSet = statement.executeQuery();
        if(resultSet.next()) {
            max_id = Integer.parseInt(resultSet.getString("max_id"));
        }
        statement.close();
        resultSet.close();
        return max_id;
    }

    private User getUser(String userId) throws SQLException {
        Connection connection=DBConnection.getConnection();
        String sql="select * from tbl_user where user_id=?";
        PreparedStatement statement=connection.prepareStatement(sql);
        statement.setString(1,userId);
        ResultSet resultSet = statement.executeQuery();
        User user = null;
        if(resultSet.next()) {
            user = new User(resultSet.getString("user_id"),
                    resultSet.getString("password"),
                    resultSet.getString("user_name"));
        }
        statement.close();
        resultSet.close();
        return user;
    }

    @Override
    public User login(String userId, String userPassword) throws SQLException {
        User user = getUser(userId);
        if (user != null && !Objects.equals(user.getPassword(), userPassword)) user = null;
        return new User(user.getAccount(), user.getUserName());
    }

    @Override
    public Boolean register(User user) throws SQLException {
        if (getUser(user.getAccount()) != null) return false;

        Connection connection=DBConnection.getConnection();
        String sql="insert into tbl_user values(?,?,?)";
        PreparedStatement stmt=null;
        try {
            stmt=connection.prepareStatement(sql);
            stmt.setString(1, user.getAccount());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getUserName());
            int ret=stmt.executeUpdate();
            if(ret>0) return true;
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                if(stmt!=null){
                    stmt.close();
                }
            } catch (Exception e2) {
                // TODO: handle exception
            }

        }
        return false;
    }

    @Override
    public ArrayList<User> getFriendList(String userId) throws SQLException {
        ArrayList<User>friendsList=new ArrayList<>();
        Connection connection=DBConnection.getConnection();
        String sql = "select * from v_friends where user_id=?";
        PreparedStatement statement=connection.prepareStatement(sql);
        statement.setString(1,userId);
        ResultSet resultSet=statement.executeQuery();
        while (true) {
            User user=createUserFriends(resultSet);
            if(user==null) {
                break;
            }
            else {
                friendsList.add(user);
            }
        }
        statement.close();;
        resultSet.close();
        return friendsList;
    }

    private User createUserFriends(ResultSet resultSet) throws SQLException {
        User user=null;
        if(resultSet.next()) {
            user=new User();
            user.setAccount(resultSet.getString("friend_id"));
            user.setUserName(resultSet.getString("friend_name"));
        }
        return user;
    }

    @Override
    public ArrayList<Group> getGroupList(String userId) throws SQLException {
        ArrayList<Group>groupsList=new ArrayList<>();
        Connection connection=DBConnection.getConnection();
        String sql = "select * from v_groups where user_id=?";
        PreparedStatement statement=connection.prepareStatement(sql);
        statement.setString(1,userId);
        ResultSet resultSet=statement.executeQuery();
        while (true) {
            Group group=createUserGroups(resultSet);
            if (group == null) {
                break;
            } else {
                groupsList.add(group);
            }
        }
        statement.close();
        resultSet.close();
        return groupsList;
    }

    private Group createUserGroups(ResultSet resultSet) throws SQLException {
        Group group=null;
        if (resultSet.next()) {
            group=new Group("", "");
            group.setAccount(resultSet.getString("group_id"));
            group.setName(resultSet.getString("group_name"));

            Connection connection=DBConnection.getConnection();
            String sql = "select * from v_groupMember where group_id=?";
            PreparedStatement statement=connection.prepareStatement(sql);
            statement.setString(1,resultSet.getString("group_id"));
            ResultSet resultSet1=statement.executeQuery();
            while (true) {
                User user=createGroupMember(resultSet1);
                if (user == null) {
                    break;
                } else {
                    group.getMembers().add(user);
                }
            }
            statement.close();
            resultSet1.close();
        }
        return group;
    }

    private User createGroupMember(ResultSet resultSet) throws SQLException {
        User user=null;
        if(resultSet.next()) {
            user=new User();
            user.setAccount(resultSet.getString("groupMember_id"));
            user.setUserName(resultSet.getString("groupMember_name"));
        }
        return user;
    }

    @Override
    public Boolean addFriend(String userId1, String userId2) throws SQLException {
        Connection connection=DBConnection.getConnection();
        String sql="insert into friends values(?,?,?), (?,?,?)";
        int friendMaxId = getMaxId("friends");
        PreparedStatement stmt = null;
        try {
            stmt=connection.prepareStatement(sql);
            stmt.setString(1, String.valueOf(friendMaxId + 1));
            stmt.setString(2, userId1);
            stmt.setString(3, userId2);
            stmt.setString(4, String.valueOf(friendMaxId + 2));
            stmt.setString(5, userId2);
            stmt.setString(6, userId1);

            int ret = stmt.executeUpdate();
            if(ret > 0) return true;
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                if(stmt!=null){
                    stmt.close();
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return false;
    }

    public Boolean deleteFriend(String userId1, String userId2) throws SQLException {
        Connection connection=DBConnection.getConnection();
        String sql="delete from friends where (user_id=? and friend_id=?) or (user_id=? and friend_id=?)";
        PreparedStatement stmt = null;
        try {
            stmt=connection.prepareStatement(sql);
            stmt.setString(1, userId1);
            stmt.setString(2, userId2);
            stmt.setString(3, userId2);
            stmt.setString(4, userId1);

            int ret = stmt.executeUpdate();
            if(ret > 0) return true;
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                if(stmt!=null){
                    stmt.close();
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return false;
    }

    public Group createGroup(Group group) throws SQLException {
        Connection connection=DBConnection.getConnection();
        String sql="insert into groups values(?,?)";
        int maxId = getMaxId("groups");
        group.setAccount(String.valueOf(maxId + 1));
        PreparedStatement stmt = null;
        try {
            stmt=connection.prepareStatement(sql);
            stmt.setString(1, group.getAccount());
            stmt.setString(2, group.getName());

            int ret = stmt.executeUpdate();
            if(ret > 0) return group;
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                if(stmt!=null){
                    stmt.close();
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return null;
    }
    public Boolean deleteGroup(String groupId) throws SQLException {
        Connection connection=DBConnection.getConnection();
        String sql="delete from groups where (id = ?)";
        PreparedStatement stmt = null;
        try {
            stmt=connection.prepareStatement(sql);
            stmt.setString(1, groupId);
            int ret = stmt.executeUpdate();
            if(ret > 0) return true;
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                if(stmt!=null){
                    stmt.close();
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return false;
    }
    public Boolean addGroupMember(String userId, String GroupId) throws SQLException {
        Connection connection=DBConnection.getConnection();
        String sql="insert into groupMember values(?,?,?)";
        int maxId = getMaxId("groupMember");
        PreparedStatement stmt = null;
        try {
            stmt=connection.prepareStatement(sql);
            stmt.setString(1, String.valueOf(maxId + 1));
            stmt.setString(2, userId);
            stmt.setString(3, GroupId);
            int ret = stmt.executeUpdate();
            if(ret > 0) return true;
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                if(stmt!=null){
                    stmt.close();
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return false;
    }
    public Boolean deleteGroupMember(String userId, String GroupId) throws SQLException {
        Connection connection=DBConnection.getConnection();
        String sql="delete from groupMember where (groupMember_id = ?) and (group_id = ?)";
        PreparedStatement stmt = null;
        try {
            stmt=connection.prepareStatement(sql);
            stmt.setString(1, userId);
            stmt.setString(2, GroupId);
            int ret = stmt.executeUpdate();
            if(ret > 0) return true;
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                if(stmt!=null){
                    stmt.close();
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return false;
    }
}
