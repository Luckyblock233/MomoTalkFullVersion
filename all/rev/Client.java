package all.rev;

import all.common.*;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;

public class Client {

    protected static StartWindow startWindow;
    protected static JFrame textFrame; //大的文本界面
    protected static JFrame infFrame;  //小的右边侧边栏界面
    private Font generalFont;
    private JTextField textField;
    protected static JLabel nameLabel; // 侧边栏的上面提示
    private JButton sendButton;
    private JButton createGroupButton;
    private JButton endButton;
    private BackgroundPanel westPanel;
    private JPanel southPanel;
    private JPanel infPanel;
    private JPanel infNorthPanel;
    private JPanel TextNorthRightPanel;
    private JPanel InfNorthRightPanel;
    private JPanel TextNorthLeftPanel;
    private JPanel InfNorthLeftPanel;
    private JPanel TextNorthTopPanel;
    private JPanel InfNorthTopPanel;
    private JScrollPane userListScroll;
    private JScrollPane friendListScroll;
    private JScrollPane groupListScroll;
    private JTabbedPane ListPanel;
    protected static JTabbedPane leftPanel;
    private JPopupMenu addMenu;
    private JPopupMenu friendMenu;
    private JPopupMenu groupMenu;
    private JMenuItem addMenuItem;
    private JMenuItem tempStartMenuItem;
    private JMenuItem startMenuItem;
    private JMenuItem endMenuItem;
    private JMenuItem groupStartMenuItem;
    private JMenuItem groupInviteMenuItem;
    private JMenuItem checkGroupMenuItem;
    private JMenuItem quitGroupMenuItem;
    private JList<String> userList;
    protected static DefaultListModel<String> listModel;
    private JList<String> friendList;
    protected static DefaultListModel<String> friendListModel;
    private JList<String> groupList;
    protected static DefaultListModel<String> groupListModel;
    protected static ArrayList<JTextArea> textAreas;//当前有哪些聊天，它们对于的文本框
    protected static ArrayList<String> Accounts;
    private JButton TextExitButton;
    private JButton TextMinButton;
    private JButton InfExitButton;
    private JButton InfMinButton;
    private ImageIcon close;
    private ImageIcon close_selected;
    private ImageIcon min;
    private ImageIcon min_selected;
    private boolean InfIsDragging;
    private boolean TextIsDragging;
    private int Textxx, Textyy, Infxx, Infyy;

    protected static User user;

    protected static boolean isConnected = false; //是否与服务器建立了连接

    protected static Socket socket;
    protected static PrintWriter writer;
    protected static BufferedReader reader;
    protected static MessageThread messageThread;    // 负责接收消息的线程
    protected static HashMap<String, Group> groups;  //
    protected static HashMap<String, User> onLineUsers = new HashMap<String, User>();// 所有在线用户
    protected static JLabel texttitlelabel;

    // 主方法,程序入口
    public static void main(String[] args) {
        new Client();
    }
    // 构造方法
    public Client() {
        generalFont = new Font("微软雅黑", Font.PLAIN, 20);
        startWindow = new StartWindow(this);
        textFrame = new JFrame("MomoTalk");
        infFrame = new JFrame("MomoTalk");
        try { // 使用Windows的界面风格
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        UIManager.put("Button.font", generalFont);
        UIManager.put("TextField.font", generalFont);
        UIManager.put("List.font", new Font("微软雅黑", Font.BOLD, 20));
        UIManager.put("List.foreground", Color.white);
        UIManager.put("List.background", new Color(0, 0, 0, 0));
        UIManager.put("List.selectionForeground", Color.BLACK);
        UIManager.put("List.selectionBackground", new Color(255, 255, 255, 155));
        UIManager.put("TextArea.font", new Font("微软雅黑", Font.PLAIN, 22));
        UIManager.put("Label.font", generalFont);
        UIManager.put("MenuItem.font", generalFont);
        UIManager.put("TabbedPane.contentOpaque", false);
        UIManager.put("TabbedPane.contentBorderInsets", new Insets(0, 0, 0, 0));

        close = new ImageIcon("img/close.png");
        close.setImage(close.getImage().getScaledInstance(28, 28, Image.SCALE_SMOOTH));
        close_selected = new ImageIcon("img/close_selected.png");
        close_selected.setImage(close_selected.getImage().getScaledInstance(22, 22, Image.SCALE_SMOOTH));
        min = new ImageIcon("img/min.png");
        min.setImage(min.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
        min_selected = new ImageIcon("img/min_selected.png");
        min_selected.setImage(min_selected.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));

        TextNorthLeftPanel = new JPanel(new FlowLayout());
        texttitlelabel = new JLabel("MomoTalk sss");
        texttitlelabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        TextNorthLeftPanel.add(texttitlelabel);
        TextNorthLeftPanel.setOpaque(false);
        InfNorthLeftPanel = new JPanel(new FlowLayout());
        InfNorthLeftPanel.add(new JLabel("MomoTalk") {
            {
                this.setFont(new Font("微软雅黑", Font.BOLD, 24));
            }
        });
        InfNorthLeftPanel.setOpaque(false);
        FlowLayout layout = new FlowLayout(FlowLayout.RIGHT);
        layout.setHgap(20);
        TextNorthRightPanel = new JPanel(layout);
        TextMinButton = new JButton();
        TextMinButton.setOpaque(false);
        TextMinButton.setFocusable(false);
        TextMinButton.setIcon(min);
        TextMinButton.setPreferredSize(new Dimension(30, 30));
        TextMinButton.setContentAreaFilled(false);
        TextExitButton = new JButton();
        TextExitButton.setOpaque(false);
        TextExitButton.setFocusable(false);
        TextExitButton.setContentAreaFilled(false);
        TextExitButton.setIcon(close);
        TextExitButton.setPreferredSize(new Dimension(30, 30));
        TextNorthRightPanel.add(TextMinButton);
        TextNorthRightPanel.add(TextExitButton);
        TextNorthRightPanel.setOpaque(false);
        InfNorthRightPanel = new JPanel(layout);
        InfMinButton = new JButton();
        InfMinButton.setOpaque(false);
        InfMinButton.setFocusable(false);
        InfMinButton.setIcon(min);
        InfMinButton.setPreferredSize(new Dimension(30, 30));
        InfMinButton.setContentAreaFilled(false);
        InfExitButton = new JButton();
        InfExitButton.setOpaque(false);
        InfExitButton.setFocusable(false);
        InfExitButton.setContentAreaFilled(false);
        InfExitButton.setIcon(close);
        InfExitButton.setPreferredSize(new Dimension(30, 30));
        InfNorthRightPanel.add(InfMinButton);
        InfNorthRightPanel.add(InfExitButton);
        InfNorthRightPanel.setOpaque(false);
        TextNorthTopPanel = new JPanel(new BorderLayout());
        TextNorthTopPanel.add(TextNorthRightPanel, "East");
        TextNorthTopPanel.add(TextNorthLeftPanel, "West");
        TextNorthTopPanel.setOpaque(false);
        InfNorthTopPanel = new JPanel(new BorderLayout());
        InfNorthTopPanel.add(InfNorthRightPanel, "East");
        InfNorthTopPanel.add(InfNorthLeftPanel, "West");
        InfNorthTopPanel.setOpaque(false);

        textAreas = new ArrayList<>();
        Accounts = new ArrayList<>();
        groups = new HashMap<>();
        textField = new JTextField();
        sendButton = new JButton("发送");
        sendButton.setOpaque(false);
        listModel = new DefaultListModel<>();
        friendListModel = new DefaultListModel<>();
        groupListModel = new DefaultListModel<>();
        userList = new JList<>(listModel);
        friendList = new JList<>(friendListModel);
        groupList = new JList<>(groupListModel);

        ListPanel = new JTabbedPane();
        ListPanel.setFont(generalFont);
        ListPanel.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        ListPanel.setOpaque(false);
        userList.setOpaque(false);
        friendList.setOpaque(false);
        groupList.setOpaque(false);

        userListScroll = new JScrollPane(userList);
        userListScroll.setOpaque(false);
        userListScroll.getViewport().setOpaque(false);
        friendListScroll = new JScrollPane(friendList);
        friendListScroll.setOpaque(false);
        friendListScroll.getViewport().setOpaque(false);
        groupListScroll = new JScrollPane(groupList);
        groupListScroll.setOpaque(false);
        groupListScroll.getViewport().setOpaque(false);
        ListPanel.addTab(" 在线用户 ", userListScroll);
        ListPanel.addTab(" 好友列表 ", friendListScroll);
        ListPanel.addTab(" 群聊列表 ", groupListScroll);

        leftPanel = new JTabbedPane();
        westPanel = new BackgroundPanel(Toolkit.getDefaultToolkit().createImage("img/text_background.png"));
        westPanel.setLayout(new BorderLayout());
        leftPanel.setBorder(new LineBorder(new Color(0,0,0,0),2));
        westPanel.add(TextNorthTopPanel, "North");
        westPanel.add(leftPanel, "Center");
        southPanel = new JPanel(new BorderLayout());
        southPanel.setBorder(new TitledBorder(new LineBorder(new Color(0,0,0,0)), "发送区", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.TOP, generalFont));
        southPanel.setOpaque(false);
        southPanel.add(textField, "Center");
        southPanel.add(sendButton, "East");
        westPanel.add(southPanel, "South");
        westPanel.setBorder(new LineBorder(Color.BLACK, 1));

        createGroupButton = new JButton("创建群聊");
        endButton = new JButton("重新登录");
        infNorthPanel = new JPanel(new BorderLayout());
        JPanel panel = new JPanel(new BorderLayout());
        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel butPanel = new JPanel(new FlowLayout());
        nameLabel = new JLabel(" Hello, User");
        nameLabel.setFont(new Font("微软雅黑", Font.BOLD, 28));
        nameLabel.setForeground(Color.white);
        namePanel.add(nameLabel);
        namePanel.setOpaque(false);
        ImageIcon createGroupIcon = new ImageIcon("img/createGroup.png");
        createGroupIcon.setImage(createGroupIcon.getImage().getScaledInstance(30,30,Image.SCALE_SMOOTH));
        createGroupButton.setOpaque(false);
        createGroupButton.setFocusable(false);
        createGroupButton.setIcon(createGroupIcon);
        ImageIcon reloginIcon = new ImageIcon("img/relogin.png");
        reloginIcon.setImage(reloginIcon.getImage().getScaledInstance(30,30,Image.SCALE_SMOOTH));
        endButton.setOpaque(false);
        endButton.setFocusable(false);
        endButton.setIcon(reloginIcon);
        butPanel.add(createGroupButton);
        butPanel.add(endButton);
        butPanel.setOpaque(false);
        panel.setOpaque(false);
        panel.add(namePanel, "Center");
        panel.add(InfNorthTopPanel, "North");
        infNorthPanel.add(panel, "North");
        infNorthPanel.add(butPanel, "Center");
        infNorthPanel.setOpaque(false);

        infPanel = new BackgroundPanel(Toolkit.getDefaultToolkit().createImage("img/inf_background.jpg"));
        infPanel.setLayout(new BorderLayout());
        infPanel.add(ListPanel, "Center");
        infPanel.add(infNorthPanel, "North");
        infPanel.setOpaque(false);
        infPanel.setBorder(new LineBorder(Color.BLACK, 1));

        addMenu = new JPopupMenu();
        friendMenu = new JPopupMenu();
        groupMenu = new JPopupMenu();
        addMenuItem = new JMenuItem("添加好友");
        tempStartMenuItem = new JMenuItem("临时聊天");
        startMenuItem = new JMenuItem("开始聊天");
        endMenuItem = new JMenuItem("删除好友");
        groupStartMenuItem = new JMenuItem("打开群聊");
        checkGroupMenuItem = new JMenuItem("查看群成员");
        groupInviteMenuItem = new JMenuItem("邀请好友");
        quitGroupMenuItem = new JMenuItem("退出群聊");
        addMenu.add(tempStartMenuItem);
        addMenu.add(addMenuItem);
        friendMenu.add(startMenuItem);
        friendMenu.add(endMenuItem);
        groupMenu.add(groupStartMenuItem);
        groupMenu.add(checkGroupMenuItem);
        groupMenu.add(groupInviteMenuItem);
        groupMenu.add(quitGroupMenuItem);

        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userList.setFixedCellHeight(25);
        userList.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                if (e.isPopupTrigger() && e.getY() < userList.getFixedCellHeight() * (userList.getSelectedIndex() + 1)
                        && e.getY() > userList.getFixedCellHeight() * userList.getSelectedIndex())
                    addMenu.show(userList, e.getX(), e.getY());
            }
        });
        friendList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        friendList.setFixedCellHeight(25);
        friendList.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                if (e.isPopupTrigger() && e.getY() < friendList.getFixedCellHeight() * (friendList.getSelectedIndex() + 1)
                        && e.getY() > friendList.getFixedCellHeight() * friendList.getSelectedIndex()) {
                    if (listModel.contains(friendList.getSelectedValue())) {
                        startMenuItem.setEnabled(true);
                        endMenuItem.setEnabled(true);
                    } else {
                        startMenuItem.setEnabled(false);
                        endMenuItem.setEnabled(false);
                    }
                    friendMenu.show(friendList, e.getX(), e.getY());
                }

            }
        });
        groupList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        groupList.setFixedCellHeight(25);
        groupList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                if (e.isPopupTrigger() && e.getY() < groupList.getFixedCellHeight() * (groupList.getSelectedIndex() + 1)
                        && e.getY() > groupList.getFixedCellHeight() * groupList.getSelectedIndex()) {
                    if (groupList.getSelectedIndex() == 0) {
                        quitGroupMenuItem.setEnabled(false);
                        checkGroupMenuItem.setEnabled(false);
                        groupInviteMenuItem.setEnabled(false);
                    } else {
                        quitGroupMenuItem.setEnabled(true);
                        checkGroupMenuItem.setEnabled(true);
                        groupInviteMenuItem.setEnabled(true);
                    }
                    groupMenu.show(groupList, e.getX(), e.getY());
                }
            }
        });

        textFrame.setLayout(new BorderLayout());
        textFrame.add(westPanel, "Center");
        textFrame.setSize(800, 700);
        textFrame.setUndecorated(true);
        int screen_width = Toolkit.getDefaultToolkit().getScreenSize().width;
        int screen_height = Toolkit.getDefaultToolkit().getScreenSize().height;
        textFrame.setLocation((screen_width - textFrame.getWidth()) / 2, (screen_height - textFrame.getHeight()) / 2);

        infFrame.setLayout(new BorderLayout());
        infFrame.add(infPanel, "Center");
        infFrame.setUndecorated(true);
        infFrame.setSize(315, 750);
        infFrame.setLocation(screen_width - infFrame.getWidth() - 200, (screen_height - infFrame.getHeight()) / 2);
        infFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        initializeTab();

        infFrame.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                InfIsDragging = true;
                Infxx = e.getX();
                Infyy = e.getY();
            }

            public void mouseReleased(MouseEvent e) {
                InfIsDragging = false;
            }
        });
        infFrame.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (InfIsDragging) {
                    int left = infFrame.getLocation().x;
                    int top = infFrame.getLocation().y;
                    infFrame.setLocation(left + e.getX() - Infxx, top + e.getY() - Infyy);

                }
            }
        });

        textFrame.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                TextIsDragging = true;
                Textxx = e.getX();
                Textyy = e.getY();
            }

            public void mouseReleased(MouseEvent e) {
                TextIsDragging = false;
            }
        });
        textFrame.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (TextIsDragging) {
                    int left = textFrame.getLocation().x;
                    int top = textFrame.getLocation().y;
                    textFrame.setLocation(left + e.getX() - Textxx, top + e.getY() - Textyy);
                }
            }
        });

        TextMinButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textFrame.setExtendedState(JFrame.ICONIFIED);
            }
        });

        TextMinButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                TextMinButton.setIcon(min_selected);
            }
        });

        TextMinButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                TextMinButton.setIcon(min);
            }
        });

        TextExitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textFrame.setVisible(false);
            }
        });

        TextExitButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                TextExitButton.setIcon(close_selected);
            }
        });

        TextExitButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                TextExitButton.setIcon(close);
            }
        });

        InfMinButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                infFrame.setExtendedState(JFrame.ICONIFIED);
            }
        });

        InfMinButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                InfMinButton.setIcon(min_selected);
            }
        });

        InfMinButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                InfMinButton.setIcon(min);
            }
        });

        InfExitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isConnected) {
                    closeConnection();// 关闭连接
                }
                System.exit(0);// 退出程序
            }
        });

        InfExitButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                InfExitButton.setIcon(close_selected);
            }
        });

        InfExitButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                InfExitButton.setIcon(close);
            }
        });

        // 加好友
        addMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String account = userList.getSelectedValue();
                account = account.substring(account.indexOf("(") + 1, account.indexOf(")"));
                if (!friendListModel.contains(userList.getSelectedValue())) {
                    sendMessage("COMMAND@ADDFRIEND@" + user.getAccount() + "@" + account);
                } else {
                    JOptionPane.showMessageDialog(infFrame, "您已添加 " + account + " 为好友",
                            "错误", JOptionPane.PLAIN_MESSAGE);
                }
            }
        });

        // 开启临时聊天
        tempStartMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String account = userList.getSelectedValue();
                account = account.substring(account.indexOf("(") + 1, account.indexOf(")"));
                if (!Accounts.contains(account)) {
                    String userName = onLineUsers.get(account).getUserName();
                    createTab(userName, account);
                    leftPanel.setSelectedIndex(textAreas.size() - 1);
                } else {
                    leftPanel.setSelectedIndex(Accounts.indexOf(account));
                }
                if (!textFrame.isVisible())
                    textFrame.setVisible(true);
            }
        });

        // 删除好友
        endMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String account = friendList.getSelectedValue();
                account = account.substring(account.indexOf("(") + 1, account.indexOf(")"));
                sendMessage("COMMAND@ENDFRIEND@" + user.getAccount() + "@" + account);
                sendMessage("COMMAND@UPDATE_DATABASE_ENDFRIEND@" + user.getAccount() + "@" + account);
                if (Accounts.contains(account)) {
                    int index = Accounts.indexOf(account);
                    textAreas.remove(index);
                    Accounts.remove(index);
                    leftPanel.removeTabAt(index);
                }
                friendListModel.removeElementAt(friendList.getSelectedIndex());
            }
        });

        // 开启私人聊天
        startMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String account = friendList.getSelectedValue();
                account = account.substring(account.indexOf("(") + 1, account.indexOf(")"));
                if (!Accounts.contains(account)) {
                    String nickName = onLineUsers.get(account).getUserName();
                    createTab(nickName, account);
                    leftPanel.setSelectedIndex(textAreas.size() - 1);
                } else {
                    leftPanel.setSelectedIndex(Accounts.indexOf(account));
                }
                if (!textFrame.isVisible())
                    textFrame.setVisible(true);
            }
        });

        // 打开群聊
        groupStartMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String account = groupList.getSelectedValue();
                account = account.substring(account.indexOf("(") + 1, account.indexOf(")"));
                if (!Accounts.contains(account)) {
                    String name = groups.get(account).getName();
                    createTab(name, account);
                    leftPanel.setSelectedIndex(textAreas.size() - 1);
                } else {
                    leftPanel.setSelectedIndex(Accounts.indexOf(account));
                }
                if (!textFrame.isVisible())
                    textFrame.setVisible(true);
            }
        });

        // 查看群成员
        checkGroupMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String account = groupList.getSelectedValue();
                account = account.substring(account.indexOf("(") + 1, account.indexOf(")"));

                StringBuilder allMember = new StringBuilder();
                for (int i = 0; i < groups.get(account).getMembers().size(); i++) {
                    User member = groups.get(account).getMembers().get(i);
                    allMember.append(member.getUserName()).append(" (").append(member.getAccount()).append(")\n");
                }
                JOptionPane.showMessageDialog(infFrame, allMember.toString(), "群成员", JOptionPane.PLAIN_MESSAGE);
            }
        });

        // 邀请好友进群
        groupInviteMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame frame = new JFrame("邀请好友入群");
                JPanel mainPanel = new JPanel(new BorderLayout());
                JPanel itemPanel = new JPanel(new GridLayout(0, 1));
                JScrollPane scrollPane = new JScrollPane(itemPanel);
                Vector<JCheckBox> checkBoxes = new Vector<>();
                JPanel buttonPanel = new JPanel(new FlowLayout());
                JButton confirmButton = new JButton("确认");
                JButton cancelButton = new JButton("取消");
                String groupAccount = groupList.getSelectedValue();
                groupAccount = groupAccount.substring(groupAccount.indexOf("(") + 1, groupAccount.indexOf(")"));
                String groupName = groups.get(groupAccount).getName();
                ArrayList<User> members = groups.get(groupAccount).getMembers();
                for (int i = 0; i < friendListModel.size(); i++) {
                    String friend = friendListModel.get(i);
                    String friendAccount = friend.substring(friend.indexOf("(") + 1, friend.indexOf(")"));
                    JCheckBox checkBox = new JCheckBox(friend);
                    checkBox.setFont(generalFont);
                    if (members.contains(friendAccount)) {
                        checkBox.setEnabled(false);
                    }
                    JPanel panel1 = new JPanel(new FlowLayout());
                    panel1.add(checkBox);
                    itemPanel.add(panel1);
                    checkBoxes.add(checkBox);
                }
                buttonPanel.add(confirmButton);
                buttonPanel.add(cancelButton);
                mainPanel.add(scrollPane, "Center");
                mainPanel.add(buttonPanel, "South");
                frame.add(mainPanel);
                frame.setSize(200, 400);
                frame.setLocationRelativeTo(infFrame);
                frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                frame.setVisible(true);
                String finalGroupAccount = groupAccount;
                confirmButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        for (JCheckBox checkBox : checkBoxes) {
                            if (checkBox.isSelected()) {
                                String account = checkBox.getText();
                                account = account.substring(account.indexOf("(") + 1, account.indexOf(")"));
                                sendMessage("COMMAND@JOINGROUP@" + user.getAccount() + "@" + account + "@" +
                                        finalGroupAccount + "@" + groupName);
                            }
                        }
                        frame.dispose();
                    }
                });
                cancelButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        frame.dispose();
                    }
                });
            }
        });

        // 退出群聊
        quitGroupMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String groupAccount = groupList.getSelectedValue();
                groupAccount = groupAccount.substring(groupAccount.indexOf("(") + 1, groupAccount.indexOf(")"));
                sendMessage("COMMAND@UPDATE_DATABASE_QUITGROUP@" + user.getAccount() + "@" + groupAccount);
                sendMessage("COMMAND@QUITGROUP@" + user.getAccount() + "@" + groupAccount);
                if (Accounts.contains(groupAccount)) {
                    int index = Accounts.indexOf(groupAccount);
                    textAreas.remove(index);
                    Accounts.remove(index);
                    leftPanel.removeTabAt(index);
                }
                groups.remove(groupAccount);
                groupListModel.removeElementAt(groupList.getSelectedIndex());
            }
        });
        // 写消息的文本框中按回车键时事件
        textField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                send();
            }
        });

        // 单击发送按钮时事件
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                send();
            }
        });

        // 单击创建群聊按钮时事件
        createGroupButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String groupName;
                while (true) {
                    groupName = JOptionPane.showInputDialog(infFrame, "请输入群聊名称：", "输入名称"
                            , JOptionPane.PLAIN_MESSAGE);
                    if (groupName == null) {
                        return;
                    } else if (groupName.isEmpty()) {
                        JOptionPane.showMessageDialog(infFrame, "不能为空！",
                                "错误", JOptionPane.PLAIN_MESSAGE);
                    } else {
                        break;
                    }
                }
                sendMessage("COMMAND@CREATEGROUP@" + user.getAccount() + "@" + groupName);
            }
        });

        // 单击断开按钮时事件
        endButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!isConnected) {
                    JOptionPane.showMessageDialog(infFrame, "已处于断开状态，不要重复断开!", "错误", JOptionPane.PLAIN_MESSAGE);
                    return;
                }
                try {
                    int isRelogin = JOptionPane.showConfirmDialog(infFrame, "确定重新登录吗", "重新登陆", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
                    if (isRelogin == 0) {
                        closeConnection();// 断开连接
                        startWindow.accountFiled.setText(null);
                        startWindow.passwordField.setText(null);
                        startWindow.startFrame.setVisible(true);
                        FramePosition.toCenter(startWindow.startFrame);
                        infFrame.setVisible(false);
                        textFrame.setVisible(false);
                    }
                } catch (Exception exc) {
                    JOptionPane.showMessageDialog(infFrame, exc.getMessage(), "错误", JOptionPane.PLAIN_MESSAGE);
                }
            }
        });

        infFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (isConnected) {
                    closeConnection();// 关闭连接
                }
                System.exit(0);// 退出程序
            }
        });
    }

    public void initializeTab() {
        groupListModel.add(0, "用户广播 (0)");
        groups.put("0", new Group("用户广播", "0"));
        String account = "0";
        String name = groups.get(account).getName();
        textAreas.add(new JTextArea());
        JTab tab = new JTab(name, 0);
        textAreas.get(0).setEditable(false);
        textAreas.get(0).setOpaque(false);
        textAreas.get(0).setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(textAreas.get(0));
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        Accounts.add(account);
        leftPanel.addTab(name, scrollPane);
        leftPanel.setTabComponentAt(0, tab);
        leftPanel.setSelectedIndex(textAreas.size() - 1);
    }

    // 执行发送
    public void send() {
        if (!isConnected) {
            JOptionPane.showMessageDialog(textFrame, "还没有连接服务器，无法发送消息！", "错误", JOptionPane.PLAIN_MESSAGE);
            return;
        }
        String message = textField.getText().trim();
        if (message.isEmpty()) {
            JOptionPane.showMessageDialog(textFrame, "消息不能为空！", "错误", JOptionPane.PLAIN_MESSAGE);
            return;
        }
        if (Accounts.get(leftPanel.getSelectedIndex()).equals("0")) {
            sendMessage("MESSAGE@" + user.getUserName() + "@ALL@" + message);
        } else if (onLineUsers.containsKey(Accounts.get(leftPanel.getSelectedIndex()))) {
            sendMessage("MESSAGE@" + user.getAccount() + "@" + Accounts.get(leftPanel.getSelectedIndex()) + "@" + message);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = df.format(new Date());
            textAreas.get(leftPanel.getSelectedIndex()).append("你" + "      " + time + "\r\n"  + message + " \r\n");
        } else {
            sendMessage("MESSAGE@" + user.getUserName() + "@" + Accounts.get(leftPanel.getSelectedIndex()) + "@" + message);
        }
        textField.setText(null);
    }

    // 新建消息窗口
    public void createTab(String name, String account) {
        textAreas.add(new JTextArea());
        JTab tab = new JTab(name, textAreas.size() - 1);
        textAreas.get(textAreas.size() - 1).setEditable(false);
        textAreas.get(textAreas.size() - 1).setOpaque(false);
        Accounts.add(account);
        JScrollPane scrollPane = new JScrollPane(textAreas.get(textAreas.size() - 1));
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        leftPanel.addTab(name, scrollPane);
        leftPanel.setTabComponentAt(textAreas.size() - 1, tab);
        JPopupMenu tabMenu = new JPopupMenu();
        JMenuItem closeMenuItem = new JMenuItem("关闭聊天");
        tabMenu.add(closeMenuItem);
        if (!textFrame.isVisible())
            textFrame.setVisible(true);
        tab.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    tabMenu.show(tab, e.getX(), e.getY());
                } else {
                    leftPanel.setSelectedIndex(tab.getIndex());
                }
            }
        });
        closeMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int index = tab.getIndex();
                Accounts.remove(index);
                textAreas.remove(index);
                leftPanel.removeTabAt(index);
                if (leftPanel.getSelectedIndex() == index) {
                    leftPanel.setSelectedIndex(0);
                }
            }
        });
    }

    // 发送消息
    public void sendMessage(String message) {
        writer.println(message);
        writer.flush();
    }

    // 客户端主动关闭连接
    public synchronized void closeConnection() {
        try {
            sendMessage("COMMAND@CLOSE");// 发送断开连接命令给服务器
            // 释放资源
            if (reader != null) {
                reader.close();
            }
            if (writer != null) {
                writer.close();
            }
            if (socket != null) {
                socket.close();
            }
            isConnected = false;
            listModel.removeAllElements();
        } catch (IOException e1) {
            e1.printStackTrace();
            isConnected = true;
        }
    }

}