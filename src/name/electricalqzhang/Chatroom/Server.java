package name.electricalqzhang.Chatroom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
	private ServerSocket serverSocket;
	private ExecutorService threadPool;
	private Map<String, PrintWriter> map;
	
	public Server() {
		try {
			serverSocket = new ServerSocket(8088);
			threadPool = Executors.newFixedThreadPool(5);
			map = new HashMap<String, PrintWriter>();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void start() {
		Socket socket = null;
		
		try {
			System.out.println("Waiting Clients to connect...");
			while (true) {
				socket = serverSocket.accept();
				ClientHandler clientHandler = new ClientHandler(socket);
				threadPool.execute(clientHandler);
				System.out.println("Have a client connection succeeded !");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	private synchronized void mapAdd(String name, PrintWriter pw) {
		map.put(name, pw);
	}
	
	private synchronized void mapRemove(String name) {
		map.remove(name);
	}
	
	private synchronized void sendMessage(PrintWriter pw2Client, String message) {
		if (message.indexOf("@") != -1) {
			String infoRegex = "^@[\\w\\u4e00-\\u9fa5]+ [\\w\\W]*";
			if (message.matches(infoRegex)) {
				String nameStr = message.substring(message.indexOf("@")+1, message.indexOf(" "));
				String messStr = message.substring(message.indexOf(" ")+1);
				PrintWriter pw = map.get(nameStr);
				if (pw != null) {
					pw.println(messStr);
				} else {
					pw2Client.println("There is no one called what you @" + nameStr);
				}
			} else {
				pw2Client.println("The format of information is error !");
			}
		} else {
			Set<Map.Entry<String , PrintWriter>> entrys = map.entrySet();
			for (Map.Entry<String , PrintWriter> entry : entrys) {
				PrintWriter pw = entry.getValue();
				pw.println(message);
			}
		}
	}
	
	public static void main(String[] args) {
		Server server = new Server();
		server.start();
	}
	
	private class ClientHandler implements Runnable {
		private Socket socket;
		
		private ClientHandler(Socket socket) {
			this.socket = socket;
		}

		@Override
		public void run() {
			InputStreamReader isr = null;
			BufferedReader br = null;
			
			OutputStreamWriter osw = null;
			PrintWriter pw = null;
			
			String name = null;
			
			String message = null;
			try {
				isr = new InputStreamReader(socket.getInputStream(), "UTF-8");
				br = new BufferedReader(isr);
				
				osw = new OutputStreamWriter(socket.getOutputStream(), "UTF-8");
				pw = new PrintWriter(osw, true);
				
				pw.println("Please input your name: ");
				name = br.readLine().trim();
				
				mapAdd(name, pw);
				
				pw.println("Account activated ! Happy chat !");
				while ((message=br.readLine()) != null) {
					sendMessage(pw, message);
					System.out.println(message);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {	
				
				mapRemove(name);
				
				if (socket != null) {
					try {
						System.out.println("There is a client disconnected !");
						socket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				if (isr != null) {
					try {
						isr.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
