package all.rev;

import all.common.*;

import javax.swing.*;
import java.io.BufferedReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

import static all.rev.Client.*;

public class MessageThread extends Thread{
    private BufferedReader reader;
    private ArrayList<JTextArea> textAreas;

    private Client client;

    // 接收消息线程的构造方法
    public MessageThread(BufferedReader reader, ArrayList<JTextArea> textAreas, Client client) {
        this.reader = reader;
        this.textAreas = textAreas;
        this.client=client;
    }

    // 被动的关闭连接
    public synchronized void closeCon() throws Exception {
        // 清空用户列表
        listModel.removeAllElements();
        // 被动的关闭连接释放资源
        if (reader != null) {
            reader.close();
        }
        if (writer != null) {
            writer.close();
        }
        if (socket != null) {
            socket.close();
        }
        isConnected = false;// 修改状态为断开
    }

    public void run() {
        String message;
        while (isConnected) {
            try {
                message = reader.readLine();
                if (message == null) continue;
                System.out.println(message);

                StringTokenizer stringTokenizer = new StringTokenizer(message, "@");
                String type = stringTokenizer.nextToken();// 命令
                if (type.equals("COMMAND")) {
                    solveCommond(stringTokenizer);
                } else if (type.equals("MESSAGE")) {
                    solveMessage(stringTokenizer);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void solveCommond(StringTokenizer  stringTokenizer) {
        try {
            String command = stringTokenizer.nextToken();
            switch (command) {
                case "CLOSE":// 服务器已关闭命令
                    textAreas.get(Accounts.indexOf("0")).append("服务器已关闭!\r\n");
                    closeCon();// 被动的关闭连接
                    client.closeConnection();// 断开连接
                    startWindow.accountFiled.setText(null);
                    startWindow.passwordField.setText(null);
                    startWindow.startFrame.setVisible(true);
                    FramePosition.toCenter(startWindow.startFrame);
                    infFrame.setVisible(false);
                    textFrame.setVisible(false);
                    JOptionPane.showMessageDialog(infFrame, "服务器已关闭!", "提示", JOptionPane.PLAIN_MESSAGE);
                    return;// 结束线程

                case "ADD": {// 有用户上线更新在线列表
                    String userAccount;
                    String userName = "";
                    if ((userAccount = stringTokenizer.nextToken()) != null
                            && (userName = stringTokenizer.nextToken()) != null) {
                        User user = new User(userAccount, userName);
                        textAreas.get(Accounts.indexOf("0")).append(user.getUserName() + " (" + user.getAccount() + ")" + " 上线了！\r\n");
                        onLineUsers.put(userAccount, user);
                        listModel.addElement(user.getUserName() + " (" + userAccount + ")");
                    }
                    String account;
                    for (int i = 0; i < friendListModel.size(); i++) {
                        account = friendListModel.get(i);
                        account = account.substring(account.indexOf("(") + 1, account.indexOf(")"));
                        if (account.equals(userAccount)) {
                            friendListModel.set(i, userName + " (" + userAccount + ")");
                        }
                    }
                    break;
                }
                case "DELETE": {// 有用户下线更新在线列表
                    String userAccount = stringTokenizer.nextToken();
                    User user = onLineUsers.get(userAccount);
                    textAreas.get(Accounts.indexOf("0")).append(user.getUserName() + " (" + user.getAccount() + ")" + " 下线了！\r\n");
                    onLineUsers.remove(user.getAccount());
                    listModel.removeElement(user.getUserName() + " (" + userAccount + ")");
                    if (Accounts.contains(userAccount)) {
                        int index = Accounts.indexOf(userAccount);
                        textAreas.remove(index);
                        Accounts.remove(index);
                        leftPanel.removeTabAt(index);
                    }
                    break;
                }
                case "FRIENDLIST": {
                    int size = Integer.parseInt(stringTokenizer.nextToken());
                    String userAccount;
                    String userName;
                    for (int i = 0; i < size; i++) {
                        userAccount = stringTokenizer.nextToken();
                        userName = stringTokenizer.nextToken();
                        friendListModel.addElement(userName + " (" + userAccount + ")");
                    }
                    break;
                }
                case "GROUPLIST": {
                    int size = Integer.parseInt(stringTokenizer.nextToken());
                    for (int i = 0; i < size; i++) {
                        String groupAccount = stringTokenizer.nextToken();
                        String groupName = stringTokenizer.nextToken();
                        int groupSize = Integer.parseInt(stringTokenizer.nextToken());
                        Group group = new Group(groupName, groupAccount);

                        for (int j = 0 ; j < groupSize; ++ j) {
                            String memberAccount = stringTokenizer.nextToken();
                            String memberName = stringTokenizer.nextToken();
                            group.getMembers().add(new User(memberAccount, memberName));
                        }
                        groups.put(groupAccount, group);
                        groupListModel.addElement(groupName + " (" + groupAccount + ")");
                    }
                    break;
                }

                case "USERLIST": {// 加载在线用户列表
                    int size = Integer.parseInt(stringTokenizer.nextToken());
                    String userAccount;
                    String username;
                    for (int i = 0; i < size; i++) {
                        userAccount = stringTokenizer.nextToken();
                        username = stringTokenizer.nextToken();
                        User user = new User(userAccount, username);
                        onLineUsers.put(userAccount, user);
                        listModel.addElement(username + " (" + userAccount + ")");
                    }
                    break;
                }
                case "KICK": {// 强制下线
                    client.closeConnection();// 断开连接
                    JOptionPane.showMessageDialog(textFrame, "你被移出了服务器！", "提示", JOptionPane.PLAIN_MESSAGE);
                    startWindow.accountFiled.setText(null);
                    startWindow.passwordField.setText(null);
                    startWindow.startFrame.setVisible(true);
                    FramePosition.toCenter(startWindow.startFrame);
                    infFrame.setVisible(false);
                    textFrame.setVisible(false);
                    return;// 结束线程
                }
                case "ADDFRIEND": {
                    String source = stringTokenizer.nextToken();
                    int choice = JOptionPane.showConfirmDialog(infFrame,
                            onLineUsers.get(source).getUserName() + " (" + source + ") 请求添加您为好友",
                            "好友请求", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
                    if (choice == JOptionPane.YES_OPTION) {
                        User friend = onLineUsers.get(source);
                        friendListModel.addElement(friend.getUserName() + " (" + friend.getAccount() + ")");
                        client.sendMessage("COMMAND@FRIENDAGREED@" + user.getAccount() + "@" + source);
                    }
                    break;
                }
                case "FRIENDAGREED": {
                    String source = stringTokenizer.nextToken();
                    JOptionPane.showMessageDialog(infFrame,
                            onLineUsers.get(source).getUserName() + " (" + source + ") 同意了您的好友申请",
                            "通知", JOptionPane.PLAIN_MESSAGE);
                    User friend = onLineUsers.get(source);
                    friendListModel.addElement(friend.getUserName() + " (" + friend.getAccount() + ")");
                    client.sendMessage("COMMAND@UPDATE_DATABASE_FRIENDAGREED@" + source + "@" + user.getAccount());
                    break;
                }
                case "ENDFRIEND": {
                    String source = stringTokenizer.nextToken();
                    JOptionPane.showMessageDialog(infFrame,
                            onLineUsers.get(source).getUserName() + " (" + source + ") 将您删除好友",
                            "通知", JOptionPane.PLAIN_MESSAGE);
                    if (Accounts.contains(source)) {
                        int index = Accounts.indexOf(source);
                        textAreas.remove(index);
                        Accounts.remove(index);
                        leftPanel.removeTabAt(index);
                    }
                    User friend = onLineUsers.get(source);
                    friendListModel.removeElement(friend.getUserName() + " (" + friend.getAccount() + ")");
                    break;
                }
                case "CREATEGROUP": {
                    String groupAccount = stringTokenizer.nextToken();
                    String groupName = stringTokenizer.nextToken();
                    Group group = new Group(groupName, groupAccount);
                    group.getMembers().add(user);
                    groups.put(groupAccount, group);
                    groupListModel.addElement(groupName + " (" + groupAccount + ")");
                    JOptionPane.showMessageDialog(infFrame,
                            "群 " + groupName + " 创建成功！群号为：" + groupAccount,
                            "提示", JOptionPane.PLAIN_MESSAGE);
                    break;
                }
                case "QUITGROUP": {
                    String source = stringTokenizer.nextToken();
                    String groupAccount = stringTokenizer.nextToken();
                    JOptionPane.showMessageDialog(infFrame,
                            onLineUsers.get(source).getUserName() + " (" + source + ") 退出群聊" +
                                    groups.get(groupAccount).getName() + " (" + groupAccount + ")",
                            "群聊信息", JOptionPane.PLAIN_MESSAGE);
                    groups.get(groupAccount).getMembers().remove(source);
                    break;
                }
                case "DELETEGROUP": {
                    String groupAccount = stringTokenizer.nextToken();
                    String groupName = stringTokenizer.nextToken();
                    JOptionPane.showMessageDialog(infFrame,
                             "群聊" + groupName + " (" + groupAccount + ") 已因群成员为空自动解散",
                            "群聊信息", JOptionPane.PLAIN_MESSAGE);
                    break;
                }
                case "JOINGROUP": {
                    String source = stringTokenizer.nextToken();
                    String groupAccount = stringTokenizer.nextToken();
                    String groupName = stringTokenizer.nextToken();
                    int choice = JOptionPane.showConfirmDialog(infFrame,
                            onLineUsers.get(source).getUserName() + " (" + source + ") 邀请您加入群聊" +
                                    groupName + " (" + groupAccount + ")",
                            "群聊信息", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
                    if (choice == JOptionPane.YES_OPTION) {
                        Group group = new Group(groupName, groupAccount);
                        groups.put(groupAccount, group);
                        groupListModel.addElement(groupName + " (" + groupAccount + ")");
                        client.sendMessage("COMMAND@JOINAGREED@" + user.getAccount() + "@" + source + "@" + groupAccount);
                    }
                    break;
                }
                case "JOINAGREED": {
                    String source = stringTokenizer.nextToken();
                    String groupAccount = stringTokenizer.nextToken();
                    groups.get(groupAccount).getMembers().add(user);
                    JOptionPane.showMessageDialog(infFrame, onLineUsers.get(source).getUserName() +
                            "同意了加入群" + groupAccount, "提示", JOptionPane.PLAIN_MESSAGE);
                    StringBuilder allMember = new StringBuilder();
                    for (int i = 0; i < groups.get(groupAccount).getMembers().size(); i++) {
                        User member = groups.get(groupAccount).getMembers().get(i);
                        allMember.append(member.getAccount()).append("#").append(member.getUserName()).append("#");
                    }
                    client.sendMessage("COMMAND@UPDATE_DATABASE_JOINAGREED@" + onLineUsers.get(source).getAccount() +
                            "@" + groupAccount);
                    client.sendMessage("COMMAND@UPDATEGROUP@" + user.getAccount() + "@" + source + "@" + groupAccount +
                            "@" + groups.get(groupAccount).getMembers().size() + "@" + allMember);
                    break;
                }
                case "UPDATEGROUP": {
                    stringTokenizer.nextToken();
                    String groupAccount = stringTokenizer.nextToken();
                    int memberSize = Integer.parseInt(stringTokenizer.nextToken());
                    String members = stringTokenizer.nextToken();
                    StringTokenizer allMember = new StringTokenizer(members, "#");
                    groups.get(groupAccount).getMembers().clear();
                    for (int i = 0; i < memberSize; i++) {
                        String memberAccount = allMember.nextToken();
                        String memberUserName = allMember.nextToken();
                        groups.get(groupAccount).getMembers().add(new User(memberAccount, memberUserName));
                    }
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void solveMessage(StringTokenizer  stringTokenizer) {
        try {
            String target = stringTokenizer.nextToken();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = df.format(new Date());
            switch (target) {
                case "ALL": {
                    String nickName = stringTokenizer.nextToken();
                    String text = stringTokenizer.nextToken();
                    textAreas.get(Accounts.indexOf("0")).append(nickName + "      " + time + "\r\n"  + text + "\r\n");
                    break;
                }
                case "PERSONAL": {
                    String account = stringTokenizer.nextToken();
                    String text = stringTokenizer.nextToken();
                    if (Accounts.contains(account)) {// 已经打开了聊天窗口
                        int index = Accounts.indexOf(account);
                        textAreas.get(index).append("对方" + "      " + time + "\r\n"  + text + "\r\n");
                        leftPanel.setSelectedIndex(index);
                    } else {// 打开聊天窗口
                        String name = onLineUsers.get(account).getUserName();
                        client.createTab(name, account);
                        textAreas.getLast().append("对方" + "      " + time + "\r\n"  + text + "\r\n");
                        leftPanel.setSelectedIndex(textAreas.size() - 1);
                    }
                    if (!textFrame.isVisible())
                        textFrame.setVisible(true);
                    break;
                }
                case "GROUP": {
                    String nickName = stringTokenizer.nextToken();
                    String groupAccount = stringTokenizer.nextToken();
                    String text = stringTokenizer.nextToken();
                    if (Accounts.contains(groupAccount)) {// 已经打开了聊天窗口
                        int index = Accounts.indexOf(groupAccount);
                        textAreas.get(index).append(nickName + "      " + time + "\r\n"  + text + "\r\n");
                        leftPanel.setSelectedIndex(index);
                    } else {// 打开聊天窗口
                        String name = groups.get(groupAccount).getName();
                        client.createTab(name, groupAccount);
                        textAreas.getLast().append(nickName + "      " + time + "\r\n"  + text + "\r\n");
                        leftPanel.setSelectedIndex(textAreas.size() - 1);
                    }
                    if (!textFrame.isVisible())
                        textFrame.setVisible(true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
