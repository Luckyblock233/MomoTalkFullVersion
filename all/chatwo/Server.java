package all.chatwo;

import all.common.*;
import all.config.Config;
import all.database.ImportFactory;
import all.database.UserImportSqlite;

import javax.swing.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;

public class Server extends Servermethod {

    protected static JFrame frame; //主体界面
    protected static JTextArea contentArea; //消息显示区
    protected static JButton startButton;//开始按钮
    protected static JButton stopButton;//结束按钮
    protected static JTextField maxField;//人数上限
    protected static JTextField messageField;//发送消息文本框
    protected static JButton sendButton;//消息发送按钮
    protected static JMenuItem kick; //右键显示下线按钮

    //用户信息
    protected static DefaultListModel<String> listModel;
    protected static JList<String> userList; //在线用户列表
    protected static HashMap<String, User> onlineUsers;
    protected static HashMap<String, Group> groups;


    protected static ServerSocket serverSocket;
    protected static ServerThread serverThread;
    protected static ArrayList<ClientThread> clients;

    protected static boolean isStart = false; //服务器是否开启

    // 主方法,程序执行入口
    public static void main(String[] args) {
        new Server();
    }

    public Server() {
        Config.init();
        new ServerUI();
        stopButton.setEnabled(false);

        // 关闭窗口时事件
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (isStart) {
                    closeServer();// 关闭服务器
                }
                System.exit(0);// 退出程序
            }
        });

        // 单击启动服务器按钮时事件
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (isStart) {
                    JOptionPane.showMessageDialog(frame, "服务器已处于启动状态，不要重复启动！",
                            "错误", JOptionPane.PLAIN_MESSAGE);
                    return;
                }
                int max;
                try {
                    try {
                        max = Integer.parseInt(maxField.getText());
                    } catch (Exception e1) {
                        throw new Exception("人数上限为正整数！");
                    }
                    if (max <= 0) {
                        throw new Exception("人数上限为正整数！");
                    }

                    serverStart(max);

                    contentArea.append("服务器已成功启动!  人数上限：" + max + "\r\n");
                    frame.repaint();
                    JOptionPane.showMessageDialog(frame, "服务器成功启动!", "提示", JOptionPane.PLAIN_MESSAGE);
                    frame.repaint();

                    startButton.setEnabled(false);
                    maxField.setEnabled(false);
                    stopButton.setEnabled(true);
                    frame.repaint();

                } catch (Exception exc) {
                    JOptionPane.showMessageDialog(frame, exc.getMessage(), "错误", JOptionPane.PLAIN_MESSAGE);
                }
            }
        });
        // 单击停止服务器按钮时事件
        stopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!isStart) {
                    JOptionPane.showMessageDialog(frame, "服务器还未启动，无需停止！", "错误", JOptionPane.PLAIN_MESSAGE);
                    return;
                }
                try {
                    closeServer();

                    frame.repaint();
                    contentArea.append("服务器成功停止!\r\n");
                    JOptionPane.showMessageDialog(frame, "服务器成功停止！", "提示", JOptionPane.PLAIN_MESSAGE);
                    frame.repaint();

                    startButton.setEnabled(true);
                    maxField.setEnabled(true);
                    stopButton.setEnabled(false);
                    frame.repaint();

                } catch (Exception exc) {
                    JOptionPane.showMessageDialog(frame, "停止服务器发生异常！", "错误", JOptionPane.PLAIN_MESSAGE);
                }
            }
        });

        // 文本框按回车键时事件
        messageField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                send();
            }
        });
        // 单击发送按钮时事件
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                send();
            }
        });

        // 强制下线
        kick.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ClientThread ct = clients.get(userList.getSelectedIndex());
                ct.writer.println("COMMAND@KICK@");
                ct.writer.flush();
            }
        });
    }
    // 启动服务器
    public void serverStart(int max) throws java.net.BindException {
        try {
            clients = new ArrayList<>();
            onlineUsers = new HashMap<>();
            groups = new HashMap<>();
            groups.put("0", new Group("用户广播", "0"));
            serverSocket = new ServerSocket(6666);
            serverThread = new Server.ServerThread(serverSocket, max);
            serverThread.start();
            isStart = true;
        } catch (BindException e) {
            isStart = false;
            throw new BindException("端口号已被占用，请换一个！");
        } catch (Exception e1) {
            e1.printStackTrace();
            isStart = false;
            throw new BindException("启动服务器异常！");
        }
    }

    // 服务器线程，主要用做登录
    class ServerThread extends Thread {
        private ServerSocket serverSocket;
        private int max;// 人数上限
        private Boolean runningFlag = false;

        // 服务器线程的构造方法
        public ServerThread(ServerSocket serverSocket, int max) {
            this.serverSocket = serverSocket;
            this.max = max;
            this.runningFlag = true;
        }

        public void serverThreadClose() throws IOException{
            this.runningFlag = false;
            serverSocket.close();
        }

        public void run() {
            while (isStart && runningFlag) {// 不停的等待客户端的链接
                try {
                    Socket socket = serverSocket.accept();
                    BufferedReader r = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter w = new PrintWriter(socket.getOutputStream());
                    new Thread() {
                        public void run() {
                            try {//负责客户端的注册与登录操作
                                boolean isDone = false;
                                while (!isDone) {
                                    // 接收客户端的基本用户信息
                                    String inf = r.readLine();
                                    StringTokenizer st = new StringTokenizer(inf, "@");
                                    String command = st.nextToken();
                                    if (command.equals("REGISTER")) {
                                        String account = st.nextToken();
                                        String password = st.nextToken();
                                        String username = st.nextToken();
                                        if (ImportFactory.getUserImport().register(new User(account, password, username))) {
                                            w.println("SUCCESS");
                                        } else {
                                            w.println("DUPLICATED");
                                        }
                                        w.flush();
                                    } else if (command.equals("LOGIN")) {
                                        String account = st.nextToken();
                                        String password = st.nextToken();
                                        if (clients.size() == max) {// 如果已达人数上限
                                            // 反馈服务器满信息
                                            w.println("MAX");
                                            w.flush();
                                            isDone = true;
                                            // 释放资源
                                            r.close();
                                            w.close();
                                            socket.close();
                                        } else if (!onlineUsers.containsKey(account)) {
                                            try {
                                                User user = ImportFactory.getUserImport().login(account,password);
                                                w.println("SUCCESS@"+user.getUserName());
                                                w.flush();
                                                isDone = true;
                                                onlineUsers.put(account, user);
                                                ClientThread client = new ClientThread(socket, user);
                                                client.start();// 开启对此客户端服务的线程
                                                clients.add(client);
                                                listModel.addElement(client.getUser().getUserName() + " (" + client.getUser().getAccount() + ")");// 更新在线列表
                                                contentArea.append(client.getUser().getUserName() + " (" + client.getUser().getAccount() + ")" + "上线!\r\n");
                                                frame.repaint();
                                            } catch (NullPointerException e) {
                                                w.println("ERROR");
                                                w.flush();
                                            } catch (SQLException e) {
                                                e.printStackTrace();
                                            }
                                        } else if (onlineUsers.containsKey(account)) {
                                            w.println("DUPLICATED");
                                            w.flush();
                                        } else {
                                            w.println("ERROR");
                                            w.flush();
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 为一个客户端服务的线程
    class ClientThread extends Thread {
        protected Socket socket;
        protected BufferedReader reader;
        protected PrintWriter writer;
        private User user;
        private Boolean runningFlag = false;

        public BufferedReader getReader() {
            return reader;
        }

        public PrintWriter getWriter() {
            return writer;
        }
        public User getUser() {
            return user;
        }

        // 客户端线程的构造方法
        public ClientThread(Socket socket, User user) {
            try {
                this.socket = socket;
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new PrintWriter(socket.getOutputStream());
                this.user = user;
                this.runningFlag = true;
                // 反馈当前在线用户信息
                try {
                    ArrayList<User> friendList = ImportFactory.getUserImport().getFriendList(user.getAccount());
                    StringBuffer temp = new StringBuffer();
                    for (User friend: friendList) {
                        temp.append(friend.getAccount()).append("@").append(friend.getUserName()).append("@");
                    }
                    writer.println("COMMAND@FRIENDLIST@" + friendList.size() + "@" + temp);

                    ArrayList<Group> groupList = ImportFactory.getUserImport().getGroupList(user.getAccount());
                    temp.setLength(0);
                    for (Group group: groupList) {
                        if (!groups.containsKey(group.getAccount())) {
                            groups.put(group.getAccount(), group);
                        }
                        temp.append(group.getAccount()).append("@").append(group.getName()).append("@");
                        temp.append(group.getMembers().size()).append("@");
                        for (User member: group.getMembers()) {
                            temp.append(member.getAccount()).append("@").append(member.getUserName()).append("@");
                        }
                    }
                    writer.println("COMMAND@GROUPLIST@" + groupList.size() + "@" + temp);
                    writer.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (!clients.isEmpty()) {
                    StringBuilder temp = new StringBuilder();
                    for (int i = clients.size() - 1; i >= 0; i--) {
                        temp.append(clients.get(i).getUser().getAccount()).append("@").append(clients.get(i).getUser().getUserName()).append("@");
                    }
                    writer.println("COMMAND@USERLIST@" + clients.size() + "@" + temp);
                    writer.flush();
                }

                // 向所有在线用户发送该用户上线命令
                for (int i = clients.size() - 1; i >= 0; i--) {
                    System.out.println("COMMAND@ADD@" + user.getAccount() + "@" + user.getUserName());
                    clients.get(i).getWriter().println("COMMAND@ADD@" + user.getAccount() + "@" + user.getUserName());
                    clients.get(i).getWriter().flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void clientClose() {
            contentArea.append(this.getUser().getUserName() + " (" + this.getUser().getAccount() + ")" + "下线！\r\n");
            frame.repaint();
            // 断开连接释放资源
            try {
                reader.close();
                writer.close();
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 向所有在线用户发送该用户的下线命令
            for (int i = clients.size() - 1; i >= 0; i--) {
                clients.get(i).getWriter().println("COMMAND@DELETE@" + user.getAccount());
                clients.get(i).getWriter().flush();
            }
            listModel.removeElement(this.getUser().getUserName() + " (" + this.getUser().getAccount() + ")");// 更新在线列表
            onlineUsers.remove(this.getUser().getAccount());
            // 删除此条客户端服务线程
            for (int i = clients.size() - 1; i >= 0; i--) {
                if (clients.get(i).getUser() == user) {
                    ClientThread temp = clients.get(i);
                    clients.remove(i);// 删除此用户的服务线程
                    temp.runningFlag = false;// 停止这条服务线程
                    return;
                }
            }
        }

        public void run() {// 不断接收客户端的消息，进行处理。
            String message = null;
            while (runningFlag) {
                try {
                    message = reader.readLine();// 接收客户端消息
                    System.out.println(message);
                    StringTokenizer stringTokenizer = new StringTokenizer(message, "@");
                    String type = stringTokenizer.nextToken();
                    if (type.equals("COMMAND")) {
                        String command = stringTokenizer.nextToken();
                        switch (command) {
                            case "CLOSE": { // 下线命令
                                clientClose();
                                return ;
                            }
                            case "ADDFRIEND": {// 加好友命令
                                String source = stringTokenizer.nextToken();
                                String target = stringTokenizer.nextToken();
                                dispatchMessage("COMMAND@" + source + "@" + target + "@" + "ADDFRIEND");
                                break;
                            }
                            case "FRIENDAGREED": {// 同意加好友命令
                                String source = stringTokenizer.nextToken();
                                String target = stringTokenizer.nextToken();
                                dispatchMessage("COMMAND@" + source + "@" + target + "@" + "FRIENDAGREED");
                                break;
                            }

                            case "UPDATE_DATABASE_FRIENDAGREED" : { //同意加好友，更新数据库
                                String account1 = stringTokenizer.nextToken();
                                String account2 = stringTokenizer.nextToken();
                                try {
                                    ImportFactory.getUserImport().addFriend(account1, account2);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            }

                            case "ENDFRIEND": {// 删除好友命令
                                String source = stringTokenizer.nextToken();
                                String target = stringTokenizer.nextToken();
                                dispatchMessage("COMMAND@" + source + "@" + target + "@" + "ENDFRIEND");
                                break;
                            }
                            case "UPDATE_DATABASE_ENDFRIEND": {
                                String account1 = stringTokenizer.nextToken();
                                String account2 = stringTokenizer.nextToken();
                                try {
                                    ImportFactory.getUserImport().deleteFriend(account1, account2);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            }

                            case "CREATEGROUP": {// 创建群聊
                                String source = stringTokenizer.nextToken();
                                String groupName = stringTokenizer.nextToken();
                                Group group = null;

                                try {
                                    group = ImportFactory.getUserImport().createGroup(new Group(groupName, null));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                if (group == null) {
                                    break;
                                }

                                try {
                                    ImportFactory.getUserImport().addGroupMember(source, group.getAccount());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                group.getMembers().add(onlineUsers.get(source));
                                groups.put(group.getAccount(), group);

                                for (int i = clients.size() - 1; i >= 0; i--) {
                                    if (clients.get(i).getUser().getAccount().equals(source)) {
                                        System.out.println("COMMAND@CREATEGROUP@" + group.getAccount() +
                                                "@" + group.getName());
                                        clients.get(i).getWriter().println("COMMAND@CREATEGROUP@" + group.getAccount() +
                                                "@" + group.getName());
                                        clients.get(i).getWriter().flush();
                                        break;
                                    }
                                }
                                break;
                            }
                            case "QUITGROUP": {// 退群命令
                                String source = stringTokenizer.nextToken();
                                String groupAccount = stringTokenizer.nextToken();
                                String groupName = groups.get(groupAccount).getName();
                                groups.get(groupAccount).deleteMember(onlineUsers.get(source));
                                if (groups.get(groupAccount).getMembers().isEmpty()) {
                                    groups.remove(groupAccount);
                                    try {
                                        ImportFactory.getUserImport().deleteGroup(groupAccount);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    dispatchMessage("COMMAND@" + source + "@" + groupAccount + "@" + "DELETEGROUP"
                                            + "@" + groupName);
                                } else {
                                    dispatchMessage("COMMAND@" + source + "@" + groupAccount + "@" + "QUITGROUP");
                                }
                                break;
                            }
                            case "UPDATE_DATABASE_QUITGROUP": {
                                String source = stringTokenizer.nextToken();
                                String groupAccount = stringTokenizer.nextToken();
                                try {
                                    ImportFactory.getUserImport().deleteGroupMember(source, groupAccount);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            }

                            case "JOINGROUP": {// 加群命令
                                String source = stringTokenizer.nextToken();
                                String target = stringTokenizer.nextToken();
                                String groupAccount = stringTokenizer.nextToken();
                                String groupName = stringTokenizer.nextToken();
                                dispatchMessage("COMMAND@" + source + "@" + target + "@" + "JOINGROUP" +
                                        "@" + groupAccount + "@" + groupName);
                                break;
                            }

                            case "JOINAGREED": {// 同意加群
                                String source = stringTokenizer.nextToken();
                                String target = stringTokenizer.nextToken();
                                String groupAccount = stringTokenizer.nextToken();
                                groups.get(groupAccount).getMembers().add(onlineUsers.get(source));
                                dispatchMessage("COMMAND@" + source + "@" + target + "@" + "JOINAGREED" +
                                        "@" + groupAccount);
                                break;
                            }

                            case "UPDATE_DATABASE_JOINAGREED": {
                                String source = stringTokenizer.nextToken();
                                String groupAccount = stringTokenizer.nextToken();
                                try {
                                    ImportFactory.getUserImport().addGroupMember(source, groupAccount);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            }

                            case "UPDATEGROUP": {// 更新群成员
                                String source = stringTokenizer.nextToken();
                                String target = stringTokenizer.nextToken();
                                String groupAccount = stringTokenizer.nextToken();
                                String memberSize = stringTokenizer.nextToken();
                                String members = stringTokenizer.nextToken();
                                groups.get(groupAccount).getMembers().add(onlineUsers.get(source));
                                dispatchMessage("COMMAND@" + source + "@" + target + "@" + "UPDATEGROUP" +
                                        "@" + groupAccount + "@" + memberSize + "@" + members);
                                break;
                            }
                        }
                    } else if (type.equals("MESSAGE")) {
                        dispatchMessage(message);// 转发消息
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // 转发消息
        public void dispatchMessage(String message) {
            StringTokenizer stringTokenizer = new StringTokenizer(message, "@");
            String type = stringTokenizer.nextToken();
            String source = stringTokenizer.nextToken();
            String target = stringTokenizer.nextToken();
            String content = stringTokenizer.nextToken();
            if (type.equals("COMMAND")) {
                if (!content.equals("QUITGROUP") && !content.equals("JOINGROUP")
                        && !content.equals("JOINAGREED") && !content.equals("UPDATEGROUP")
                        && !content.equals("DELETEGROUP")) {
                    message = "COMMAND@" + content + "@" + source;
                    for (int i = clients.size() - 1; i >= 0; i--) {
                        if (clients.get(i).getUser().getAccount().equals(target)) {
                            clients.get(i).getWriter().println(message);
                            clients.get(i).getWriter().flush();
                        }
                    }
                } else {
                    switch (content) {
                        case "QUITGROUP": {
                            message = "COMMAND@" + content + "@" + source + "@" + target;
                            for (int i = clients.size() - 1; i >= 0; i--) {
                                if (groups.get(target).haveMember(clients.get(i).getUser())) {
                                    clients.get(i).getWriter().println(message);
                                    clients.get(i).getWriter().flush();
                                }
                            }
                            break;
                        }
                        case "DELETEGROUP": {
                            String groupName = stringTokenizer.nextToken();
                            message = "COMMAND@" + content + "@" + target + "@" + groupName;
                            for (int i = clients.size() - 1; i >= 0; i--) {
                                if (clients.get(i).getUser().getAccount().equals(source)) {
                                    clients.get(i).getWriter().println(message);
                                    clients.get(i).getWriter().flush();
                                }
                            }
                            break;
                        }
                        case "JOINGROUP": {
                            String groupAccount = stringTokenizer.nextToken();
                            String groupName = stringTokenizer.nextToken();
                            message = "COMMAND@" + content + "@" + source + "@" + groupAccount + "@" + groupName;
                            for (int i = clients.size() - 1; i >= 0; i--) {
                                if (clients.get(i).getUser().getAccount().equals(target)) {
                                    clients.get(i).getWriter().println(message);
                                    clients.get(i).getWriter().flush();
                                }
                            }
                            break;
                        }
                        case "JOINAGREED": {
                            String groupAccount = stringTokenizer.nextToken();
                            message = "COMMAND@" + content + "@" + source + "@" + groupAccount;
                            for (int i = clients.size() - 1; i >= 0; i--) {
                                if (clients.get(i).getUser().getAccount().equals(target)) {
                                    clients.get(i).getWriter().println(message);
                                    clients.get(i).getWriter().flush();
                                }
                            }
                            break;
                        }
                        case "UPDATEGROUP": {
                            String groupAccount = stringTokenizer.nextToken();
                            String memberSize = stringTokenizer.nextToken();
                            String members = stringTokenizer.nextToken();
                            message = "COMMAND@" + content + "@" + source + "@" + groupAccount + "@" + memberSize + "@" + members;
                            for (int i = clients.size() - 1; i >= 0; i--) {
                                if (groups.get(groupAccount).haveMember(clients.get(i).getUser())) {
                                    clients.get(i).getWriter().println(message);
                                    clients.get(i).getWriter().flush();
                                }
                            }
                            break;
                        }
                    }
                }

            } else if (type.equals("MESSAGE")) {
                if (target.equals("ALL")) {// 群发
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String time = df.format(new Date());
                    message = "MESSAGE@ALL@" + source + "@" + content;
                    contentArea.append(source + "      " + time + "\r\n"  + content + "\r\n");
                    frame.repaint();
                    for (int i = clients.size() - 1; i >= 0; i--) {
                        clients.get(i).getWriter().println(message);
                        clients.get(i).getWriter().flush();
                    }
                } else if (onlineUsers.containsKey(target)) { //独发
                    message = "MESSAGE@PERSONAL@" + source + "@" + content;
                    for (int i = clients.size() - 1; i >= 0; i--) {
                        if (clients.get(i).getUser().getAccount().equals(target)) {
                            clients.get(i).getWriter().println(message);
                            clients.get(i).getWriter().flush();
                        }
                    }
                } else {  //转发
                    message = "MESSAGE@GROUP@" + source + "@" + target + "@" + content;
                    for (int i = clients.size() - 1; i >= 0; i--) {
                        if (groups.get(target).haveMember(clients.get(i).getUser())) {
                            clients.get(i).getWriter().println(message);
                            clients.get(i).getWriter().flush();
                        }
                    }
                }
            }
        }
    }
}