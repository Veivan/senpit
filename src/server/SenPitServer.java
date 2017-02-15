package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * ����� ��������� �������� ������. �������� ������� : 
 * <b>stops</b> - ��������� ������� 
 * <b>check:000.000.000.000:1234:ProxyType</b> - �������� ������; 
 * ������������ ������ �������� 
 * <b>getit:000.000.000.000:1234:ProxyType</b> - �������� ������;
 * ������������ ���� �������� �� �������� �����, ���� �� �������; ���� ��������
 * ����� �� �������, �� ������������ ������ ������� ����� �� ��.
 * ProxyType ����� ���� HTTP ��� Socks (� ����� ��������)
 */

public class SenPitServer extends Thread {
	static final int port = 1967;
	Socket s;
	int num;
	static ServerSocket server;
	private Object sync;
	Sema sema;
	public boolean isworking = true;

	//static String testLink = "http://google.ru";
	public static final String testLink = "https://twitter.com";
	static int timeout = 30000; // ����� �������� ������ (� ������������)

	public static void main(String args[]) throws IOException {
		Object sync = new Object();
		Sema sema = new Sema();
		sema.isworking = true;

		try {
			int i = 0; // ������� �����������

			server = new ServerSocket(port, 0,
					InetAddress.getByName("localhost"));

			System.out.println("server is started");

			// ������� ����
			while (sema.isworking) {
				// ��� ������ �����������, ����� ���� ��������� ���������
				// �������
				// � ����� �������������� ����� � ����������� ������� ��
				// ��������
				new SenPitServer(i, server.accept(), sync, sema);
				i++;
				// isworking = SenPitServer.getIsWorking();
			}
		} catch (Exception e) {
			System.out.println("server error: " + e);
		} finally {
			server.close();
		}
	}

	private boolean getIsWorking() {
		synchronized (sync) {
			return isworking;
		}

	}

	static class Sema {
		public boolean isworking = true;
	}

	public SenPitServer(int num, Socket s, Object sync, Sema sema) {

		this.num = num;
		this.s = s;
		this.sync = sync;
		this.sema = sema;

		// ��������� ����� �������������� ����� (��. �-� run())
		setDaemon(true);
		setPriority(NORM_PRIORITY);
		start();
	}

	public void run() {
		String proxyIP = "88.132.10.72"; // ��������� �����
		int proxyPort = 8088; // ��������� ����
		String proxyType = "HTTP";

		try {
			// �� ������ ������� ���� ����� �������� ������
			InputStream is = s.getInputStream();
			// � ������ �� - ����� ������ �� ������� � �������
			OutputStream os = s.getOutputStream();

			// ������ ������ � 64 ���������
			byte buf[] = new byte[64 * 1024];
			// ������ 64�� �� �������, ��������� - ���-�� ������� ��������
			// ������
			int r = is.read(buf);

			// ������ ������, ���������� ���������� �� ������� ����������
			String data = new String(buf, 0, r);
			String[] sp = data.split(":");
			if (sp.length > 1) {
				proxyIP = sp[1];
				proxyPort = Integer.parseInt(sp[2]);
				proxyType = sp[3];
			}
			boolean res = false;
			String comm = sp[0].toLowerCase();

			switch (comm) {
			case "check":
				res = check(proxyIP, proxyPort, proxyType); //�������� ������� ��������
				break;

			case "getit":
				res = check(proxyIP, proxyPort, proxyType); //�������� ������� ��������
				break;

			case "stops":
				synchronized (sync) {
					this.sema.isworking = false;
				}

				break;

			default:
				break;
			}

			// ��������� ������ �� ������ ������:
			//data = "" + num + ": " + "\n" + data;

			if (res == false)
				data = "";
			else
				data = String.format("%s:%d:%s", proxyIP, proxyPort, proxyType);

			// ������� ������:
			os.write(data.getBytes());

			// ��������� ����������
			s.close();

			//System.out.println("connected ");

		} catch (Exception e) {
			System.out.println("server error: " + e);
		} // ����� ����������
	}

	private boolean check(String pHost, int pPort, String pType) {
		SocketAddress addr = new InetSocketAddress(pHost, pPort);
		Proxy.Type _pType = (pType.equals("HTTP") ? Proxy.Type.HTTP
				: Proxy.Type.SOCKS);
		Proxy httpProxy = new Proxy(_pType, addr);
		HttpURLConnection urlConn = null;
		URL url;
		try {
			url = new URL(testLink);
			urlConn = (HttpURLConnection) url.openConnection(httpProxy);
			urlConn.setConnectTimeout(timeout);
			urlConn.connect();
			Map<String, List<String>> headers =  urlConn.getHeaderFields();
			IsAnonymous(pHost, urlConn.getHeaderFields());
			return (urlConn.getResponseCode() == 200);
		} catch (SocketException e) {
			return false;
		} catch (SocketTimeoutException e) {
			return false;
		} catch (Exception e) {
			System.out.println("Error: " + e);
			return false;
		}
	}
	
	private boolean IsAnonymous(String pHost, Map<String, List<String>> headers) {
		boolean res = true;

		for (Map.Entry<String, List<String>> header : headers.entrySet())
		{
		    System.out.println(header.getKey() + "/");
			
			for (String val : header.getValue()) {
			    System.out.println(val);			
			}
		}	
			return res;
			
		}

}
