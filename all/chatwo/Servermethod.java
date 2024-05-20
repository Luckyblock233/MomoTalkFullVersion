package all.chatwo;

import javax.swing.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static all.chatwo.Server.*;

public class Servermethod {
    //使用了Server的属性

    // 执行消息发送
    public void send() {
        if (!isStart) {
            JOptionPane.showMessageDialog(frame, "服务器还未启动,不能发送消息！", "错误", JOptionPane.PLAIN_MESSAGE);
            return;
        }
        if (clients.size() == 0) {
            JOptionPane.showMessageDialog(frame, "没有用户在线,不能发送消息！", "错误", JOptionPane.PLAIN_MESSAGE);
            return;
        }
        String message = messageField.getText().trim();
        if (message.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "消息不能为空！", "错误", JOptionPane.PLAIN_MESSAGE);
            return;
        }
        sendServerMessage(message);// 群发服务器消息
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = df.format(new Date());
        contentArea.append("服务器" + "      " + time + "\r\n"  + messageField.getText() + "\r\n");
        frame.repaint();
        messageField.setText(null);
    }

    // 关闭服务器
    public void closeServer() {
        try {
            serverThread.serverThreadClose();// 停止服务器线程

            for (int i = clients.size() - 1; i >= 0; i--) {
                // 给所有在线用户发送关闭命令
                clients.get(i).getWriter().println("COMMAND@CLOSE");
                clients.get(i).getWriter().flush();
                // 释放资源
                clients.get(i).clientClose();// 停止此条为客户端服务的线程
            }
            if (serverSocket != null) {
                serverSocket.close();// 关闭服务器端连接
            }
            listModel.removeAllElements();// 清空用户列表
            isStart = false;
        } catch (IOException e) {
            e.printStackTrace();
            isStart = true;
        }
    }

    // 群发服务器消息
    public void sendServerMessage(String message) {
        for (int i = clients.size() - 1; i >= 0; i--) {
            clients.get(i).getWriter().println("MESSAGE@ALL@服务器" + "@" + message);
            clients.get(i).getWriter().flush();
        }
    }
}
