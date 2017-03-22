package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

import common.Constants.RetCodes;

/**
 * Класс выполняет проверку прокси. Понимает команды : 
 * <b>stops</b> - остановка сервиса 
 * 
 * <b>checkanm:000.000.000.000:1234:ProxyType</b> - проверка прокси + проверка на анонимность; 
 * возвращается код Constants.RetCodes 

 * <b>check:000.000.000.000:1234:ProxyType</b> - проверка прокси без проверки на анонимность; 
 * возвращается код Constants.RetCodes 
 *
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

	public static final String testLinkSSL = "https://twitter.com";
	public static final String testLinkAn = "http://helpchildren.online/reqwinfo/getreqwinfo?";
	public static final String AnonymousPhrase = "IsAnonymous:1";

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
			RetCodes res = RetCodes.NullResult;
			String comm = sp[0].toLowerCase();

			switch (comm) {
			case "checkanm":
				res = check(proxyIP, proxyPort, proxyType, true); //вызываем функцию проверки
				break;

			case "check":
				res = check(proxyIP, proxyPort, proxyType, false); //вызываем функцию проверки
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

			data = res.toString();

			// выводим данные:
			os.write(data.getBytes());

			// завершаем соединение
			s.close();

			//System.out.println("connected ");

		} catch (Exception e) {
			System.out.println("server error: " + e);
		} // вывод исключений
	}

	private RetCodes check(String pHost, int pPort, String pType, boolean DoCheckANM) {
		SocketAddress addr = new InetSocketAddress(pHost, pPort);
		Proxy.Type _pType = (pType.equals("HTTP") ? Proxy.Type.HTTP
				: Proxy.Type.SOCKS);
		Proxy httpProxy = new Proxy(_pType, addr);
		HttpURLConnection urlConn = null;
		URL url;
		try {
			// Сначала проверка анонимности. Если не анонимный, дальше не проверяем
			if (DoCheckANM) 
			{
				url = new URL(testLinkAn);
				urlConn = (HttpURLConnection) url.openConnection(httpProxy);
				urlConn.setConnectTimeout(timeout);
				urlConn.setUseCaches(false);
				urlConn.connect();
				if (urlConn.getResponseCode() != 200) 
					return RetCodes.AnmNotConnect;
				BufferedReader in = new BufferedReader(
				        new InputStreamReader(urlConn.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();
	
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();		
				//System.out.println(response.toString());
				boolean IsAnm = response.indexOf(AnonymousPhrase) > -1;
				urlConn.disconnect();
				if (!IsAnm) 
					return RetCodes.AnmNotAnonymous;				
			}	

			url = new URL(testLinkSSL);
			urlConn = (HttpURLConnection) url.openConnection(httpProxy);
			urlConn.setConnectTimeout(timeout);
			urlConn.setUseCaches(false);
			urlConn.connect();
			boolean IsSsl = urlConn.getResponseCode() == 200;
			urlConn.disconnect();
			return IsSsl ? RetCodes.Valid : RetCodes.NotSSL;
		} catch (SocketException e) {
			System.out.println("SocketException: " + e);
			return RetCodes.SocketException;
		} catch (SocketTimeoutException e) {
			System.out.println("SocketTimeoutException: " + e);
			return RetCodes.SocketTimeoutException;
		} catch (Exception e) {
			System.out.println("Error: " + e);
			return RetCodes.Exception;
		}
	}
	
}
