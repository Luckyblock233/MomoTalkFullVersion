package all.chatwo;

import all.common.*;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static all.chatwo.Server.*;

public class ServerUI {

    //使用了Server中的属性
    private final Font generalFont;
    private JPanel northPanel; //主体北
    private JPanel northPanelLeft;
    private JPanel northPanelRight;
    private JPanel southPanel; //主体下中的南
    private JPanel rightPanel;
    private JScrollPane rightScroll;
    private JScrollPane leftScroll;
    private JSplitPane centerSplit; //主体下
    private JPopupMenu kickMenu;

    public ServerUI() {
        //UI界面搭建
        frame = new JFrame("服务器");
        try { // 使用Windows的界面风格
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        generalFont = new Font("微软雅黑", Font.PLAIN, 20);
        UIManager.put("Button.font", generalFont);
        UIManager.put("TextField.font", generalFont);
        UIManager.put("TextArea.font", generalFont);
        UIManager.put("TextArea.background", new Color(240, 240, 240));
        UIManager.put("List.foreground", Color.BLACK);
        UIManager.put("List.background", new Color(0, 0, 0, 0));
        UIManager.put("List.selectionForeground", Color.white);
        UIManager.put("List.selectionBackground", new Color(0, 0, 0, 150));
        UIManager.put("List.font", generalFont);
        UIManager.put("Label.font", generalFont);

        contentArea = new JTextArea();
        contentArea.setEditable(false);
        messageField = new JTextField();
        maxField = new JTextField("10", 2);
        startButton = new JButton("启动");
        stopButton = new JButton("停止");
        sendButton = new JButton("发送");
        listModel = new DefaultListModel<String>();
        userList = new JList<String>(listModel);

        southPanel = new JPanel(new BorderLayout());
        southPanel.setBorder(new TitledBorder(new LineBorder(Color.BLACK), "写消息", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, generalFont, Color.blue));
        southPanel.add(messageField, "Center");
        southPanel.add(sendButton, "East");
        leftScroll = new JScrollPane(userList);
        leftScroll.setBorder(new TitledBorder(new LineBorder(Color.BLACK), "在线用户", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, generalFont, Color.blue));
        sendButton.setOpaque(false);
        startButton.setOpaque(false);
        stopButton.setOpaque(false);

        rightPanel = new JPanel(new BorderLayout());
        rightScroll = new JScrollPane(contentArea);
        contentArea.setOpaque(false);
        rightPanel.add(rightScroll, "Center");
        rightPanel.add(southPanel, "South");
        rightScroll.setBorder(new TitledBorder(new LineBorder(Color.BLACK), "消息显示区", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, generalFont, Color.blue));

        centerSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, rightPanel, leftScroll);
        centerSplit.setOpaque(false);
        rightPanel.setOpaque(false);
        leftScroll.setOpaque(false);
        leftScroll.getViewport().setOpaque(false);
        rightScroll.setOpaque(false);
        rightScroll.getViewport().setOpaque(false);
        southPanel.setOpaque(false);
        userList.setOpaque(false);
        centerSplit.setDividerLocation(600);

        northPanel = new JPanel(new BorderLayout());
        northPanelLeft = new JPanel(new FlowLayout());
        northPanelLeft.add(new JLabel("人数上限"));
        northPanelLeft.add(maxField);
        northPanelRight = new JPanel(new FlowLayout());
        northPanelRight.add(startButton);
        northPanelRight.add(stopButton);
        northPanel.add(northPanelLeft, "West");
        northPanel.add(northPanelRight, "East");
        northPanel.setBorder(new TitledBorder(new LineBorder(Color.BLACK), "配置信息",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, generalFont, Color.blue));
        northPanel.setOpaque(false);
        northPanelLeft.setOpaque(false);
        northPanelRight.setOpaque(false);

        kickMenu = new JPopupMenu();
        kick = new JMenuItem("强制下线");
        kickMenu.add(kick);

        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userList.setFixedCellHeight(25);
        userList.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger() && e.getY() < userList.getFixedCellHeight() * (userList.getSelectedIndex() + 1)
                        && e.getY() > userList.getFixedCellHeight() * userList.getSelectedIndex())
                    kickMenu.show(userList, e.getX(), e.getY());
            }
        });

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(northPanel, "North");
        panel.add(centerSplit, "Center");
        frame.add(panel);
        frame.setSize(800, 600);
        FramePosition.toCenter(frame);
        frame.setVisible(true);
    }
}
