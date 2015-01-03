package com.minet;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class ServerThread implements Runnable {
	private Socket socket;
	private BufferedReader inputReader;
	private PrintWriter outputWriter;
	private ProtocolHelper helper;
	public static ArrayList<User> userList = new ArrayList<User>();

	public ServerThread(Socket socket) {
		int ID = socket.getPort();
		System.out.println("Server " + ID + ":The connection established with "
				+ socket);
		this.socket = socket;
		helper = new ProtocolHelper();

	}

	public void run() {
		handle();
	}

	public void handle() {
		try {
			inputReader = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			outputWriter = new PrintWriter(socket.getOutputStream());
			String temp;
			while ((temp = inputReader.readLine()) == null)
				;
			helper.setInStr(temp);
			System.out.println("message from client " + temp);
			String action = helper.getPara("Action");

			if (action.indexOf("login") != -1) {
				login();
				return;
			} else if (action.indexOf("broadcast") != -1) {
				broadcast();
			} else if (action.indexOf("logout") != -1) {
				logout();
			} else {
				System.out.println("no matching action");
			}
			outputWriter.println(helper.generateOutStr());
			outputWriter.flush();
			inputReader.close();
			outputWriter.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void login() throws IOException {
		Map requestData = helper.getData();
		Map<String, Serializable> responseData = new HashMap<String, Serializable>();
		String name = requestData.get("Name").toString();
		int p2pListenPort = Integer.parseInt(requestData.get("p2pListenPort")
				.toString());
		String IP = socket.getInetAddress().toString();
		IP = IP.substring(1);
		System.out.println("-----------" + IP);
		String ID = Integer.toString(this.socket.getPort());
        
        
        String tempAction = "LoginNotice";
        Map<String, String> tempData = new HashMap<String, String>();
        tempData.put("id", ID);
        tempData.put("name", name);
        tempData.put("port", Integer.toString(p2pListenPort));
        tempData.put("ip", IP);
		for (int i = 0; i < userList.size(); ++i) {
			userList.get(i).outputToWriter(ProtocolHelper.generateOutStr(tempAction, tempData));
			/*System.out.println("finished broadcast to user "
					+ Integer.toString(i));*/
		}
        
		responseData.put("ID", ID);
		responseData.put("UserList", Array2MapList(userList));
		responseData.put("Status", "OK");
		helper.setResponseData(responseData);
		outputWriter.println(helper.generateOutStr());
		outputWriter.flush();
        User user = new User(ID, name, socket, outputWriter, p2pListenPort, IP);
		userList.add(user);
	}

	private static ArrayList<Map<String, String>> Array2MapList(
			ArrayList<User> userList) {
		ArrayList<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
		for (int i = 0; i < userList.size(); ++i) {
			Map<String, String> map = new HashMap<String, String>();
			User user = userList.get(i);
			map.put("id", user.getId());
			map.put("name", user.getName());
			map.put("p2pListenPort", Integer.toString(user.getP2pListenPort()));
			map.put("ipaddress", user.getIpAddress());
			mapList.add(map);
		}
		return mapList;
	}

	private void broadcast() {
		Map<?, ?> requestData = helper.getData();
		Map<String, Serializable> responseData = new HashMap<String, Serializable>();
		String broadcastStr = (String) requestData.get("BroadcastStr");
		String userName = (String) requestData.get("Name");
		for (int i = 0; i < userList.size(); ++i) {
			userList.get(i).outputToWriter(userName + " : " + broadcastStr);
			System.out.println("finished broadcast to user "
					+ Integer.toString(i));
		}
		responseData.put("Status", "OK");
		helper.setResponseData(responseData);
	}

	private void logout() throws IOException{
		Map requestData = helper.getData();
		Map<String, Serializable> responseData = new HashMap<String, Serializable>();
		String id = requestData.get("id").toString();

		for (int i = 0; i < userList.size(); ++i) {
			User user = userList.get(i);
			if (user.getId() == id) {
				user.getOutputWriter().close();
				user.getSocket().close();
				userList.remove(i);
			}
		}
		responseData.put("Status", "OK");
		helper.setResponseData(responseData);
		outputWriter.println(helper.generateOutStr());
		outputWriter.flush();
        
        
        String tempAction = "LogoutNotice";
        Map<String, String> tempData = new HashMap<String, String>();
        tempData.put("id", id);
		for (int i = 0; i < userList.size(); ++i) {
			userList.get(i).outputToWriter(ProtocolHelper.generateOutStr(tempAction, tempData));
			/*System.out.println("finished broadcast to user "
					+ Integer.toString(i));*/
		}
	}

	private int getUserIndexById(String ID) {
		for (int i = 0; i < userList.size(); ++i) {
			if (userList.get(i).getId() == ID) {
				return i;
			}
		}
		return -1;
	}
}
