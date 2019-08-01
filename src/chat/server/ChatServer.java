package chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//import chat.server.ServerUI;

public class ChatServer {
	final int port;
	ServerSocket ss;
	
	Thread clientListener;
    java.util.List<Socket> clients = new ArrayList<>();
    Map<Socket, ClientHandler> handlers = new HashMap<>();
    ServerUI ui;
    
    public void setUI(ServerUI ui) { this.ui = ui; }
    public ChatServer(int port) { 
        this.port = port;
    }
 
    public void start() {
    	//启动一个线程监听
    	clientListener = new Thread(new Runnable(){
    		public void run() {
    			try {
					ss = new ServerSocket(port);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
    			System.out.println("启动服务器成功，等待端口号:"+port);
    			 
    			while(!Thread.currentThread().isInterrupted()) {
    				doWork();
    			}
    			
    			try {
					ss.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
    		} 
    		
    		private void doWork() {
    			try {
    	             Socket client = ss.accept();// 阻塞，当客户机请求连接时，创建一条链接
    	             System.out.println("连接成功！来自" + client.toString());
    	             ui.onAccept();
    	             
    	             clients.add(client);
    	             ClientHandler handler = new ClientHandler(ChatServer.this, client);
    	             handlers.put(client, handler);
    	             handler.start();
    	             
    	         } catch (Exception ex) {
    	             System.err.println(ex.getMessage());
    	         }	
    		}
    	});
    	
    	clientListener.start();
    }
    
    public void sendMsg(String msg) {//向所有客户端发送消息
        try {
        	for (Socket c : clients) {
        		ClientHandler handler = handlers.get(c);
        		handler.writeUTF8("【服务器】" + msg);
        	}
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
    
    public void onReceive(String msg) {
    	ui.onReceive(msg);
    }
    
    public void onDisconnect(Socket c) {
    	ui.onDisConnect();
    	
    	System.out.println("链接  "+ c +"已断开");
    	clients.remove(c);
    	ClientHandler handler = handlers.get(c);
    	handler.shutdown();
    	handlers.remove(c);
    }
    
    public void shutdown() {
    	//关闭所有线程
    	//关闭所有套接字
    	clientListener.interrupt();
    	for (Socket c : clients) {
    		ClientHandler handler = handlers.get(c);
    		handler.shutdown();
    	}
    	
    }
}
