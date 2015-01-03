package com.minet;
import java.io.PrintWriter;
import java.net.Socket;

public class User {
	private String id;
	private String name;
	private Socket socket;
	private PrintWriter outputWriter;
	private boolean isOnline;
	private int p2pListenPort;
	private String ipaddress;

	public User(String id, String name, Socket socket,
			PrintWriter outputWriter, int p2pListenPort, String ipaddress) {
		this.id = id;
		this.name = name;
		this.socket = socket;
		this.outputWriter = outputWriter;
		this.p2pListenPort = p2pListenPort;
		this.ipaddress = ipaddress;
		this.isOnline = true;
	}

	public String getId() {
		return id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public Socket getSocket() {
		return socket;
	}

	public int getP2pListenPort() {
		return p2pListenPort;
	}

	public String getIpAddress() {
		return ipaddress;
	}

	public void outputToWriter(String str) {
		outputWriter.println(str);
		outputWriter.flush();
	}

	public PrintWriter getOutputWriter() {
		return outputWriter;
	}
	
	public boolean isOnline(){
		return isOnline;
	}
	
	public void setOnlineStatus(boolean b){
		this.isOnline = b;
	}
}