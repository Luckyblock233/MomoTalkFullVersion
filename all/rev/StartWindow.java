package all.rev;

import all.common.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;

import static all.rev.Client.*;

public class StartWindow {
    protected JFrame startFrame;
    private BackgroundPanel mainPanel;
    private Font generalFont;
    private JPanel northLeftPanel;
    private JPanel northRightPanel;
    private JPanel northPanel;
    private JPanel accountPanel;
    private JPanel usernamePanel;
    private JPanel passwordPanel;
    private JPanel confirmPasswordPanel;
    private JPanel centerPanel;
    private JPanel southPanel;
    private JPanel buttonPanel;
    protected JTextField accountFiled;
    protected JTextField usernameFiled;
    protected JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton registerButton;
    private JButton loginButton;
    private JButton confirmRegisterButton;
    private JButton cancelRegisterButton;
    private JButton minButton;
    private JButton exitButton;
    private ImageIcon close;
    private ImageIcon min;
    private ImageIcon close_selected;
    private ImageIcon min_selected;
    private boolean isDraging;
    private int xx, yy;

//    public static void main(String[] args) {
//        new StartWindow(new Client());
//    }

    public StartWindow(Client client) {

        startFrame = new JFrame("MomoTalk");
        startFrame.setIconImage(null);
        generalFont = new Font("微软雅黑", Font.PLAIN, 18);

        try { // 使用Windows的界面风格
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        user = new User();
        UIManager.put("Button.font", generalFont);
        UIManager.put("TextField.font", new Font("微软雅黑", Font.PLAIN, 23));
        UIManager.put("Label.font", new Font("微软雅黑", Font.BOLD, 23));

        close = new ImageIcon("img/close.png");
        close.setImage(close.getImage().getScaledInstance(28, 28, Image.SCALE_SMOOTH));
        close_selected = new ImageIcon("img/close_selected.png");
        close_selected.setImage(close_selected.getImage().getScaledInstance(22, 22, Image.SCALE_SMOOTH));
        min = new ImageIcon("img/min.png");
        min.setImage(min.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
        min_selected = new ImageIcon("img/min_selected.png");
        min_selected.setImage(min_selected.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));

        northLeftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ImageIcon icon = new ImageIcon("img/MomoTalk.png");
        icon.setImage(icon.getImage().getScaledInstance(80, 50, Image.SCALE_SMOOTH));
        northLeftPanel.add(new JLabel(icon));
        northLeftPanel.add(new JLabel("MomoTalk") {
            {
                this.setForeground(Color.white);
                this.setFont(new Font("微软雅黑", Font.BOLD, 30));
            }
        });
        northLeftPanel.setOpaque(false);
        FlowLayout layout = new FlowLayout(FlowLayout.RIGHT);
        layout.setHgap(20);
        northRightPanel = new JPanel(layout);
        minButton = new JButton();
        minButton.setOpaque(false);
        minButton.setFocusable(false);
        minButton.setIcon(min);
        minButton.setPreferredSize(new Dimension(30, 30));
        minButton.setContentAreaFilled(false);
        exitButton = new JButton();
        exitButton.setOpaque(false);
        exitButton.setFocusable(false);
        exitButton.setContentAreaFilled(false);
        exitButton.setIcon(close);
        exitButton.setPreferredSize(new Dimension(30, 30));
        northRightPanel.add(minButton);
        northRightPanel.add(exitButton);
        northRightPanel.setOpaque(false);
        northPanel = new JPanel(new GridLayout(1, 2));
        northPanel.add(northLeftPanel);
        northPanel.add(northRightPanel);
        northPanel.setOpaque(false);

        accountFiled = new JTextField();
        accountFiled.setPreferredSize(new Dimension(200, 28));
        usernameFiled = new JTextField();
        usernameFiled.setPreferredSize(new Dimension(200, 28));
        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(200, 28));
        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setPreferredSize(new Dimension(200, 28));

        accountPanel = new JPanel(new FlowLayout());
        accountPanel.add(new JLabel("账号：") {
            {
                this.setForeground(Color.white);
            }
        });
        accountPanel.add(accountFiled);
        accountPanel.setOpaque(false);
        usernamePanel = new JPanel(new FlowLayout());
        usernamePanel.add(new JLabel("昵称：") {
            {
                this.setForeground(Color.white);
            }
        });
        usernamePanel.add(usernameFiled);
        usernamePanel.setOpaque(false);

        passwordPanel = new JPanel(new FlowLayout());
        passwordPanel.add(new JLabel("密码：") {
            {
                this.setForeground(Color.white);
            }
        });
        passwordPanel.add(passwordField);
        passwordPanel.setOpaque(false);
        confirmPasswordPanel = new JPanel(new FlowLayout());
        confirmPasswordPanel.add(new JLabel("确认密码：") {
            {
                this.setForeground(Color.white);
            }
        });
        confirmPasswordPanel.add(confirmPasswordField);
        confirmPasswordPanel.add(new JLabel("      "));
        confirmPasswordPanel.setOpaque(false);
        centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(3, 1));
        centerPanel.add(accountPanel);

        centerPanel.add(passwordPanel);
        centerPanel.setOpaque(false);

        registerButton = new JButton("注册");
        registerButton.setPreferredSize(new Dimension(125, 40));
        registerButton.setOpaque(false);
        confirmRegisterButton = new JButton("确认注册");
        confirmRegisterButton.setPreferredSize(new Dimension(150, 40));
        cancelRegisterButton = new JButton("取消");
        cancelRegisterButton.setPreferredSize(new Dimension(125, 40));
        loginButton = new JButton("登录");
        loginButton.setPreferredSize(new Dimension(125, 40));
        loginButton.setOpaque(false);
        southPanel = new JPanel();
        southPanel.setLayout(new BorderLayout());
        buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(registerButton);
        buttonPanel.add(loginButton);
        buttonPanel.setOpaque(false);
        southPanel.add(new JLabel(), "Center");
        southPanel.add(buttonPanel, "South");
        southPanel.setOpaque(false);

//        mainPanel = new BackgroundPanel(Toolkit.getDefaultToolkit().createImage("img/background.jpg"));
        mainPanel = new BackgroundPanel(Toolkit.getDefaultToolkit().createImage("img/background.png"));
        mainPanel.setLayout(new GridLayout(3, 1));
        mainPanel.add(northPanel);
        mainPanel.add(centerPanel);
        mainPanel.add(southPanel);
        mainPanel.setOpaque(false);


        startFrame.setSize(600, 400);
        startFrame.add(mainPanel);
        startFrame.setUndecorated(true);

        startFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        FramePosition.toCenter(startFrame);
        startFrame.setVisible(true);

        startFrame.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                isDraging = true;
                xx = e.getX();
                yy = e.getY();
            }

            public void mouseReleased(MouseEvent e) {
                isDraging = false;
            }
        });
        startFrame.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (isDraging) {
                    int left = startFrame.getLocation().x;
                    int top = startFrame.getLocation().y;
                    startFrame.setLocation(left + e.getX() - xx, top + e.getY() - yy);

                }
            }
        });

        minButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startFrame.setExtendedState(JFrame.ICONIFIED);
            }
        });

        minButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                minButton.setIcon(min_selected);
            }
        });

        minButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                minButton.setIcon(min);
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        exitButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                exitButton.setIcon(close_selected);
            }
        });

        exitButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                exitButton.setIcon(close);
            }
        });

        registerButton.addActionListener(new ActionListener() { //注册按钮
            @Override
            public void actionPerformed(ActionEvent e) {
                buttonPanel.removeAll();
                buttonPanel.add(confirmRegisterButton);
                buttonPanel.add(cancelRegisterButton);
                buttonPanel.repaint();
                buttonPanel.revalidate();
                accountFiled.setText(null);
                passwordField.setText(null);
                confirmPasswordField.setText(null);
                centerPanel.setLayout(new GridLayout(4, 1));
                centerPanel.removeAll();
                centerPanel.add(accountPanel);
                centerPanel.add(usernamePanel);
                centerPanel.add(passwordPanel);
                centerPanel.add(confirmPasswordPanel);
                centerPanel.repaint();
                centerPanel.revalidate();
            }
        });

        confirmRegisterButton.addActionListener(new ActionListener() { //确认注册按钮
            @Override
            public void actionPerformed(ActionEvent e) {
                String account;
                String password;
                String username;
                String confirmPassword;
                try {
                    account = accountFiled.getText();
                    if (account == null || account.isEmpty()) {
                        throw new Exception("账号不能为空！");
                    }
                    username = usernameFiled.getText();
                    if (username == null || username.isEmpty()) {
                        throw new Exception("账号不能为空！");
                    }
                    password = new String(passwordField.getPassword());
                    if (password.isEmpty()) {
                        throw new Exception("密码不能为空！");
                    }
                    confirmPassword = new String(confirmPasswordField.getPassword());
                    if (confirmPassword.isEmpty()) {
                        throw new Exception("请确认密码！");
                    }
                    if (!confirmPassword.equals(password)) {
                        throw new Exception("密码不一致！");
                    }
                    if (!isConnected) {
                        socket = new Socket("127.0.0.1", 6666);// 根据端口号和服务器IP建立连接
                        writer = new PrintWriter(socket.getOutputStream());
                        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        isConnected = true;
                    }
                    // 发送客户端用户基本信息
                    user.setAccount(account);
                    user.setPassword(password);
                    user.setUserName(username);
                    client.sendMessage("REGISTER@" + user.getAccount() + "@" + user.getPassword() + "@" + user.getUserName());
                    String message = reader.readLine();
                    if (message.equals("SUCCESS")) {
                        JOptionPane.showMessageDialog(startFrame, "注册成功！", "成功", JOptionPane.PLAIN_MESSAGE);
                        toMainPanel();
                    } else {
                        throw new Exception("账号已被注册！");
                    }

                } catch (IOException exc) {
                    JOptionPane.showMessageDialog(startFrame, "服务器未开启！", "错误", JOptionPane.PLAIN_MESSAGE);
                } catch (Exception exc2) {
                    JOptionPane.showMessageDialog(startFrame, exc2.getMessage(), "错误", JOptionPane.PLAIN_MESSAGE);
                }
            }
        });

        cancelRegisterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toMainPanel();
            }
        });

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String account;
                String password;
                String nickName;
                try {
                    account = accountFiled.getText();
                    if (account == null || account.isEmpty()) {
                        throw new Exception("账号不能为空！");
                    }
                    password = new String(passwordField.getPassword());
                    if (password.isEmpty()) {
                        throw new Exception("密码不能为空！");
                    }
                    if (!isConnected) {
                        socket = new Socket("127.0.0.1", 6666);// 根据端口号和服务器IP建立连接
                        writer = new PrintWriter(socket.getOutputStream());
                        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    }
                    // 发送客户端用户基本信息(用户名和IP地址)
                    user.setAccount(account);
                    user.setPassword(password);

                    client.sendMessage("LOGIN@" + user.getAccount() + "@" + user.getPassword());

                    String message = reader.readLine();
                    StringTokenizer st = new StringTokenizer(message, "@");
                    String command = st.nextToken();
                    switch (command) {
                        case "MAX": // 人数已达上限
                            JOptionPane.showMessageDialog(startFrame, "服务器缓冲区已满！", "错误", JOptionPane.PLAIN_MESSAGE);
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

                            break;
                        case "SUCCESS":
                            String username = st.nextToken();
                            user.setUserName(username);
                            client.sendMessage(user.getUserName());
                            // 开启接收消息的线程
                            messageThread = new MessageThread(reader, textAreas,client);
                            messageThread.start();
                            isConnected = true;// 已经连接上了

                            nameLabel.setText(" Hello, " + user.getUserName());
                            texttitlelabel.setText("MomoTalk " + user.getUserName());
                            startFrame.setVisible(false);
                            infFrame.setVisible(true);
                            break;
                        case "DUPLICATED":
                            throw new Exception("重复登录");
                        default:
                            throw new Exception("登录失败，账号或密码错误！");
                    }
                } catch (IOException exc) {
                    JOptionPane.showMessageDialog(startFrame, "服务器未开启！", "错误", JOptionPane.PLAIN_MESSAGE);
                } catch (Exception exc2) {
                    JOptionPane.showMessageDialog(startFrame, exc2.getMessage(), "错误", JOptionPane.PLAIN_MESSAGE);
                }
            }
        });
    }

    public void toMainPanel() {
        buttonPanel.removeAll();
        buttonPanel.add(registerButton);
        buttonPanel.add(loginButton);
        buttonPanel.repaint();
        buttonPanel.revalidate();
        accountFiled.setText(null);
        passwordField.setText(null);
        confirmPasswordField.setText(null);
        centerPanel.setLayout(new GridLayout(2, 1));
        centerPanel.removeAll();
        centerPanel.add(accountPanel);
        centerPanel.add(passwordPanel);
        centerPanel.repaint();
        centerPanel.revalidate();
    }
}