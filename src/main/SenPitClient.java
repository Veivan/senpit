package main;

import java.io.IOException;
import java.net.Socket;

public class SenPitClient extends Thread {
	static final int port = 1967;
	static Socket s;
	
	public static String command;
	static String proxyIP = "181.39.11.132"; // указываем адрес
	static int proxyPort = 80;              // указываем порт
	static String proxyType = "HTTP";

	public static void main(String args[]) throws IOException {
		try {
			// открываем сокет и коннектимся к localhost:port
			// получаем сокет сервера
			s = new Socket("localhost", port);

			// берём поток вывода и выводим туда первый аргумент
			// заданный при вызове, адрес открытого сокета и его порт
			//args[0] = "getit" + "\n" + s.getInetAddress().getHostAddress() + ":" + s.getLocalPort();
			
			command = String.format("%s:%s:%d:%s", "check", proxyIP, proxyPort, proxyType);
			
			//s.getOutputStream().write(args[0].getBytes());
			s.getOutputStream().write(command.getBytes());

			// читаем ответ
			String data = "";
			byte buf[] = new byte[64 * 1024];
			int r = s.getInputStream().read(buf);
			if (r > 0) data = new String(buf, 0, r);

			// выводим ответ в консоль
			System.out.println(data);
			
			
		} catch (Exception e) {
			System.out.println("client error: " + e);
		} // вывод исключений
		finally {
			s.close();
		}
		
	}
}
