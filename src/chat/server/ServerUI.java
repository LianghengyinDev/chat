package chat.server;

//import chat.server.ChatServer;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class ServerUI extends JFrame {
    JTextArea mainArea;
    JTextArea sendArea;
    JTextField indexArea;
    
    final ChatServer server;
    int numConn;
    
    public ServerUI(final ChatServer server) {
        super("聊天程序----服务器端");
        this.server = server;
        server.setUI(this);
        
        Container contain = getContentPane();
        contain.setLayout(new BorderLayout());
        
        mainArea = new JTextArea(); //用于显示收发信息
        JScrollPane mainAreaP = new JScrollPane(mainArea);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        
        sendArea = new JTextArea(3, 8);
        
        JButton sendBtn = new JButton("发送");
        indexArea = new JTextField(2);//用于显示当前连接数
        indexArea.setText("0");
        
        JPanel tmpPanel = new JPanel();
        tmpPanel.add(sendBtn);
        tmpPanel.add(indexArea);
        
        panel.add(tmpPanel, BorderLayout.EAST);
        panel.add(sendArea, BorderLayout.CENTER);
        contain.add(mainAreaP, BorderLayout.CENTER);
        contain.add(panel, BorderLayout.SOUTH);
        
        sendBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
            	//因为发送的时间可能过长，因此要将
	        	 SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
	                 @Override
	                 protected Void doInBackground() throws Exception {
	                	 server.sendMsg(sendArea.getText());//heavy work
	                     return null;
	                 }
	             };
	             worker.execute();
	             mainArea.append("【服务器】" + sendArea.getText() + "\n");
	             sendArea.setText("");
            }
        });
        
        addWindowListener(new WindowAdapter() {
	         public void windowClosing(WindowEvent e){
	        	 server.shutdown();
 	             dispose();
	         }
	      });
        
        setSize(500, 500);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
 
    //注意swing的事件调度是单线程，因此要使用invokeLater防止并发问题
    public void onReceive(final String msg) {
    	SwingUtilities.invokeLater(new Runnable() {
  	      	public void run() {
  	      		mainArea.append(msg + "\n");
  	      	}
  	  	});
    }
    
    public void onAccept() {
    	SwingUtilities.invokeLater(new Runnable() {
  	      	public void run() {
  	      		numConn++;
  	      		indexArea.setText(String.valueOf(numConn));
  	      	}
  	  	});
    }
    
    public void onDisConnect() {
    	SwingUtilities.invokeLater(new Runnable() {
  	      	public void run() {
  	      		numConn--;
  	      		indexArea.setText(String.valueOf(numConn));
  	      	}
  	  	});
    }
    
    public static void main(String[] args) {
    	int port = 7777;
    	ChatServer server = new ChatServer(port);
    	server.start();
        ServerUI ui = new ServerUI(server);
    }
}
 
