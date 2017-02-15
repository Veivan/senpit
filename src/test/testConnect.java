package test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;

public class testConnect {

	public static final String testLinkSSL = "https://twitter.com";
	public static final String testLinkAn = "http://helpchildren.online/reqwinfo/getreqwinfo?";
	
	//http://veivan.ucoz.ru
	static int timeout = 30000; // время ожидания ответа (в милисекундах)
	
	static String proxyIP;
	static int proxyPort;

	public static void main(String[] args) {
		String proxy = "89.40.122.35:3128"; // good HTTPS 
		//String proxy = "104.198.110.101:80"; // good HTTPS 
			
		String[] sp = proxy.split(":");
		if (sp.length > 1) {
			proxyIP = sp[0];
			proxyPort = Integer.parseInt(sp[1]);			
		}
		boolean res = check(proxyIP, proxyPort, "HTTP");
		System.out.println(proxy + " : " + res);
	}

	private static boolean check(String pHost, int pPort, String pType) {
		SocketAddress addr = new InetSocketAddress(pHost, pPort);
		Proxy.Type _pType = (pType.equals("HTTP") ? Proxy.Type.HTTP
				: Proxy.Type.SOCKS);
		Proxy httpProxy = new Proxy(_pType, addr);
		HttpURLConnection urlConn = null;
		URL url;
		try {
			url = new URL(testLinkSSL);
			urlConn = (HttpURLConnection) url.openConnection(httpProxy);
			urlConn.setConnectTimeout(timeout);
			urlConn.setUseCaches(false);
			urlConn.connect();
			if (urlConn.getResponseCode() != 200) 
				return false;
			
			urlConn.disconnect();
			// Проверка анонимности
			url = new URL(testLinkAn);
			urlConn = (HttpURLConnection) url.openConnection(httpProxy);
			urlConn.setConnectTimeout(timeout);
			urlConn.setUseCaches(false);
			urlConn.connect();
			if (urlConn.getResponseCode() != 200) 
				return false;
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(urlConn.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();		
			System.out.println(response.toString());
			
			urlConn.disconnect();
		
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
}
