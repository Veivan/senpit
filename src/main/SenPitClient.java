package main;

import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SenPitClient extends Thread {
	static final int port = 1967;
	// –аботать будем только с HTTP
	static String proxyType = "HTTP";
	
	private String proxyIP = "181.39.11.132"; 
	private int proxyPort = 80;    
	
	private DbConnectSingle dbConnector = null;
	
	private Socket s;
	private String command;	
	private int norder;

	/**
	 *  онструктор. Ќа входе строка вида "94.177.172.141:8080"
	 */
	public SenPitClient(String data, DbConnectSingle dbConnector, int norder)
	{
		String[] sp = data.split(":");
		if (sp.length > 1) {
			proxyIP = sp[0];
			proxyPort = Integer.parseInt(sp[1]);
		}
		this.dbConnector = dbConnector;
		this.norder = norder;
	}
	
	@Override
	public void run() {
		CheckIt();
	}
	
	private void CheckIt() {
		try {
			String msg = "N" + norder + " " + proxyIP + ":" + proxyPort;
			System.out.println(msg + " testing...");					
			// открываем сокет и коннектимс€ к localhost:port
			// получаем сокет сервера
			s = new Socket("localhost", port);

			// берЄм поток вывода и выводим туда первый аргумент
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

			if (dbConnector != null && !data.isEmpty())
			{
				dbConnector.SaveProxy(proxyIP, proxyPort);
			}
			// выводим ответ в консоль	
			if (data.isEmpty()) msg += " is bad";
			else msg += " is ok";
			System.out.println(msg);					
		} catch (Exception e) {
			System.out.println("client error: " + e);
		} // вывод исключений
		finally {
			try {
				s.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public static void main(String args[]) throws IOException {
		DbConnectSingle dbConnector = DbConnectSingle.getInstance();  
		ExecutorService cachedPool = Executors.newCachedThreadPool();
		Scanner in = new Scanner(new FileReader("proxy.txt"));	
		int i = 0;
		while (in.hasNext()) 
		{ 
			String data = in.next();
			//System.out.println (in.next()); 
			cachedPool.submit(new SenPitClient(data, dbConnector, i));
			i++;
		}
		in.close();
		cachedPool.shutdown();
	}
}
