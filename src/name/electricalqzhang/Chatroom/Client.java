package name.electricalqzhang.Chatroom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
	private Socket socket;
	
	public Client() throws UnknownHostException, IOException {
		System.out.println("Connecting to the server...");
		socket = new Socket("192.168.42.248", 8088);
		System.out.println("Successful connectiong to the server.");
	}
	
	public void start() {
		OutputStreamWriter osw = null;
		PrintWriter pw = null;
		Scanner scan = new Scanner(System.in);
		
		ServerHandler handler= new ServerHandler();
		Thread t = new Thread(handler);
		t.setDaemon(true);
		t.start();
		
		try {
			osw = new OutputStreamWriter(socket.getOutputStream(), "UTF-8");
			pw = new PrintWriter(osw, true);
			
			pw.println(scan.nextLine());
			
			String str = null;
			while (true) {
				str = scan.nextLine();
				pw.println(str);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if (osw != null) {
				try {
					osw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if (pw != null) {
				pw.close();
			}
			
			scan.close();
		}
	}
	
	public static void main(String[] args) {
		Client client;
		try {
			client = new Client();
			client.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private class ServerHandler implements Runnable {
		@Override
		public void run() {
			InputStreamReader isr = null;
			BufferedReader br = null;
			try {
               isr = new InputStreamReader(socket.getInputStream(), "UTF-8");
               br = new BufferedReader(isr);
               
                while(true){
                    System.out.println(br.readLine());
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
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
