package main;

import java.io.IOException;
import java.net.Socket;

public class SenPitClient extends Thread {
	static final int port = 1967;
	static Socket s;
	
	public static String command;
	static String proxyIP = "101.50.1.2"; // ��������� �����
	static int proxyPort = 80;              // ��������� ����
	static String proxyType = "HTTP";

	public static void main(String args[]) throws IOException {
		try {
			// ��������� ����� � ����������� � localhost:port
			// �������� ����� �������
			s = new Socket("localhost", port);

			// ���� ����� ������ � ������� ���� ������ ��������
			// �������� ��� ������, ����� ��������� ������ � ��� ����
			//args[0] = "getit" + "\n" + s.getInetAddress().getHostAddress() + ":" + s.getLocalPort();
			
			command = String.format("%s:%s:%d:%s", "check", proxyIP, proxyPort, proxyType);
			
			//s.getOutputStream().write(args[0].getBytes());
			s.getOutputStream().write(command.getBytes());

			// ������ �����
			byte buf[] = new byte[64 * 1024];
			int r = s.getInputStream().read(buf);
			String data = new String(buf, 0, r);

			// ������� ����� � �������
			System.out.println(data);
			
			
		} catch (Exception e) {
			System.out.println("client error: " + e);
		} // ����� ����������
		finally {
			s.close();
		}
		
	}
}
