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
 * Класс выполняет проверку прокси. Понимает команды : 
 * <b>stops</b> - остановка сервиса 
 * <b>check:000.000.000.000:1234:ProxyType</b> - проверка прокси; 
 * возвращается булево значение 
 * <b>getit:000.000.000.000:1234:ProxyType</b> - проверка прокси;
 * возвращается либо поданный на проверку адрес, если он рабочий; Если поданный
 * адрес не рабочий, то возвращается другой рабочий адрес из БД.
 * ProxyType может быть HTTP или Socks (в любом регистре)
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
	static int timeout = 30000; // время ожидания ответа (в милисекундах)

	public static void main(String args[]) throws IOException {
		Object sync = new Object();
		Sema sema = new Sema();
		sema.isworking = true;

		try {
			int i = 0; // счётчик подключений

			server = new ServerSocket(port, 0,
					InetAddress.getByName("localhost"));

			System.out.println("server is started");

			// слушаем порт
			while (sema.isworking) {
				// ждём нового подключения, после чего запускаем обработку
				// клиента
				// в новый вычислительный поток и увеличиваем счётчик на
				// единичку
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

		// запускаем новый вычислительный поток (см. ф-ю run())
		setDaemon(true);
		setPriority(NORM_PRIORITY);
		start();
	}

	public void run() {
		String proxyIP = "88.132.10.72"; // указываем адрес
		int proxyPort = 8088; // указываем порт
		String proxyType = "HTTP";

		try {
			// из сокета клиента берём поток входящих данных
			InputStream is = s.getInputStream();
			// и оттуда же - поток данных от сервера к клиенту
			OutputStream os = s.getOutputStream();

			// буффер данных в 64 килобайта
			byte buf[] = new byte[64 * 1024];
			// читаем 64кб от клиента, результат - кол-во реально принятых
			// данных
			int r = is.read(buf);

			// создаём строку, содержащую полученную от клиента информацию
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
				res = check(proxyIP, proxyPort, proxyType); //вызываем функцию проверки
				break;

			case "getit":
				res = check(proxyIP, proxyPort, proxyType); //вызываем функцию проверки
				break;

			case "stops":
				synchronized (sync) {
					this.sema.isworking = false;
				}

				break;

			default:
				break;
			}

			// добавляем данные об адресе сокета:
			//data = "" + num + ": " + "\n" + data;

			if (res == false)
				data = "";
			else
				data = String.format("%s:%d:%s", proxyIP, proxyPort, proxyType);

			// выводим данные:
			os.write(data.getBytes());

			// завершаем соединение
			s.close();

			//System.out.println("connected ");

		} catch (Exception e) {
			System.out.println("server error: " + e);
		} // вывод исключений
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
		
		/*REMOTE_ADDR
	HTTP_FORWARDED
	HTTP_X_FORWARDED_FOR
	HTTP_CLIENT_IP
	HTTP_X_REAL_IP
	
	HTTP_VIA
	*/
	//, , HTTP_FORWARDED_FOR, HTTP_X_FORWARDED, , , HTTP_FORWARDED_FOR_IP, VIA, X_FORWARDED_FOR, FORWARDED_FOR, X_FORWARDED, FORWARDED, CLIENT_IP, FORWARDED_FOR_IP, HTTP_PROXY_CONNECTION

	/*REMOTE_ADDR=178.54.44.24
			HTTP_FORWARDED=na
			HTTP_X_FORWARDED_FOR=134.119.217.246
			HTTP_CLIENT_IP=na
			HTTP_X_REAL_IP=na
			HTTP_VIA=1.1 178.54.44.24 (Mikrotik HttpProxy)
			HTTP_XROXY_CONNECTION=na
			HTTP_PROXY_CONNECTION=na
			HTTP_USERAGENT_VIA=na
			HTTP_USER_AGENT=Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)
			HTTP_ACCEPT_LANGUAGE=na
			REMOTE_HOST=na
			HTTP_CONNECTION=na
			SERVER_PROTOCOL=HTTP/1.1
			HTTP_REFERER=http://xseo.in/ptest.php
			HTTP_ACCEPT=
			HTTP_CACHE_CONTROL=na
			HTTP_CACHE_INFO=na
			HTTP_FORWARDED_FOR=na
			HTTP_COMING_FROM=na
			HTTP_X_COMING_FROM=na
			HTTP_ACCEPT_ENCODING=na
			HTTP_ACCEPT_CHARSET=na
			HTTP_HOST=xseo.in
			HTTP_KEEP_ALIVE=na
			HTTP_COOKIE=0
			HTTP_UA_CPU=na
			KEEP_ALIVE=na
			HTTP_MAX_FORWARDS=na
			MAX_FORWARDS=na
			HTTP_X_BLUECOAT_VIA=na
			HTTP_PC_REMOTE_ADDR=na
			HTTP_X_FWD_IP_ADDR=na
			HTTP_X_HOST=na
			HTTP_X_REFERER=na
			HTTP_X_SERVER_HOSTNAME=na
			PROXY_HOST=na
			PROXY_PORT=na
			PROXY_REQUEST=na
			HTTP_PRAGMA=na */
			
			return res;
			
		}

}
