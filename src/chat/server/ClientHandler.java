package chat.server;

import java.net.Socket;
import java.net.SocketException;
import java.io.*;

public class ClientHandler {
	BufferedReader in;
	PrintWriter out;
	ChatServer server;
	Socket client;
	Thread clientRecevicer;
	
	public ClientHandler(ChatServer server, Socket client) {
		this.server = server;
		this.client = client;
        try {
			in = new BufferedReader(new InputStreamReader(client
					.getInputStream()));
			out = new PrintWriter(client.getOutputStream(), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void start() {
		// 启动一条线程处理客户端请求
        clientRecevicer = new Thread(new Runnable() {
        	boolean reset = false;//标识链路是否断开
			public void run() {
				while(!Thread.currentThread().isInterrupted() && !reset ) {
					handleClient();
    			}
				server.onDisconnect(ClientHandler.this.client);
				try {
					client.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
        	
			public void handleClient() {
		        String msg = "";
	            try {
	                msg = in.readLine();
	            } catch (SocketException ex) {
	                System.err.println(ex);
	                reset = true;
	                return;
	            } catch (Exception ex) {
	            	reset = true;
	                System.err.println(ex);
	            }
	            
	            if (msg != null && msg.trim() != "") {
	                System.err.println(">>" + msg);
	                server.onReceive(msg);//接受消息后回调服务端读
	            }
		        
		    }
        });
        
        clientRecevicer.start();
	}
	
	public void writeUTF8(String msg) {
		out.println(msg);
	}
	
	public void shutdown() {
		clientRecevicer.interrupt();
	}

	
}
