package chat.client;

import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class ClientUI extends JFrame {
	final int port = 7777;
	
    JTextArea mainArea;
    JTextArea sendArea;
    ChatClient client;
    JTextField ipArea;
    JButton btnLink;
    public void setClient(ChatClient client) {
        this.client = client;
    }
 
    public ClientUI() {
        super("聊天程序----客户端");
        Container contain = getContentPane();
        contain.setLayout(new BorderLayout());
        mainArea = new JTextArea();
        JScrollPane mainAreaP = new JScrollPane(mainArea);// 为文本区添加滚动条
 
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        sendArea = new JTextArea(3, 8);
        JButton sendBtn = new JButton("发送");
 
        sendBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                client.sendMsg(sendArea.getText());
                mainArea.append("【客户端】" + sendArea.getText() + "\n");
                sendArea.setText("");
            }
        });
 
        JPanel ipPanel = new JPanel();
        ipPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        ipPanel.add(new JLabel("服务器："));
        ipArea = new JTextField(12);
        ipArea.setText("127.0.0.1");
        ipPanel.add(ipArea);
        btnLink = new JButton("连接");
        ipPanel.add(btnLink);
 
        btnLink.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                client = new ChatClient(ipArea.getText(), port, ClientUI.this);
                ClientUI.this.setClient(client);
            }
        });
 
        panel.add(sendBtn, BorderLayout.EAST);
        panel.add(sendArea, BorderLayout.CENTER);
 
        contain.add(ipPanel, BorderLayout.NORTH);
        contain.add(mainAreaP, BorderLayout.CENTER);
        contain.add(panel, BorderLayout.SOUTH);
        setSize(400, 400);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
 
    public static void main(String[] args) {
        ClientUI ui = new ClientUI();
    }
}
 
// 通讯类ChatClient负责守候数据到来
class ChatClient extends Thread {
    Socket sc;// 对象sc，用来处理与服务器的通信
    BufferedReader in;// 声明输入流缓冲区，用于存储服务器发来的信息
    PrintStream out;// 声明打印输出流，用于信息的发送
    ClientUI ui;
    public ChatClient(String ip, int port, ClientUI ui) {// 初始化ChatClient类
        this.ui = ui;
        try {
            sc = new Socket(ip, port); // 创建sc, 用服务器ip和端口作参数
            System.out.println("已顺利联接到服务器。");
            out = new PrintStream(sc.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(sc.getInputStream()));
        } catch (Exception e) {
            System.out.println(e);
        }
        start();
    }
 
    public void run() { // 用于监听服务器端发送来的信息
        String msg = "";
        while (true) {
            try {
                msg = in.readLine();// 从缓冲区读入一行字符存于msg
            } catch (SocketException ex) {
                System.out.println(ex);
                break;
            } catch (Exception ex) {
                System.out.println(ex);
            }
            if (msg != null && msg.trim() != "") {// 若msg信息不为空
                System.out.println(">>" + msg);
                ui.mainArea.append(msg + "\n");// 把msg信息添加到客户端的文本区域内
            }
        }
    }
 
    public void sendMsg(String msg) {// 用于发送信息
        try {
            out.println("【客户端】" + msg);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
