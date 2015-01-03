package com.minet;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerTest {
	private static ServerSocket server = null;

	public static void main(String[] args) throws IOException {
		server = new ServerSocket(4700);
		ExecutorService pool = Executors.newCachedThreadPool();

		System.out.println("The server has started.");
		while (true) {
			ServerThread st = new ServerThread(server.accept());
			pool.execute(st);
		}
	}

}