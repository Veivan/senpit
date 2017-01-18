package client;

import java.io.IOException;
import java.net.Socket;

import javax.swing.JTextArea;

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

	private JTextArea memo;

	/**
	 *  онструктор. Ќа входе строка вида "94.177.172.141:8080"
	 */
	public SenPitClient(String data, DbConnectSingle dbConnector, int norder,
			JTextArea memo) {
		String[] sp = data.split(":");
		if (sp.length > 1) {
			proxyIP = sp[0];
			proxyPort = Integer.parseInt(sp[1]);
		}
		this.dbConnector = dbConnector;
		this.norder = norder;
		this.memo = memo;
	}

	@Override
	public void run() {
		CheckIt();
	}

	private void CheckIt() {
		String msg = "N" + norder + " " + proxyIP + ":" + proxyPort;
		try {
			// открываем сокет и коннектимс€ к localhost:port
			// получаем сокет сервера
			s = new Socket("localhost", port);
			// берЄм поток вывода и выводим туда первый аргумент
			// заданный при вызове, адрес открытого сокета и его порт
			// args[0] = "getit" + "\n" + s.getInetAddress().getHostAddress() +
			// ":" + s.getLocalPort();

			command = String.format("%s:%s:%d:%s", "check", proxyIP, proxyPort,
					proxyType);

			String data = "";
			for (int i = 0; i < 2; i++) {
				utils.CustomPrint(memo, msg + String.format(" try (%d)", i));

				// s.getOutputStream().write(args[0].getBytes());
				s.getOutputStream().write(command.getBytes());

				// читаем ответ
				byte buf[] = new byte[64 * 1024];
				int r = s.getInputStream().read(buf);
				if (r > 0) {
					data = new String(buf, 0, r);
					break;
				}
			}
			
			if (dbConnector != null) {
				int isalive = data.isEmpty() ? 0 : 1;
				dbConnector.SaveProxy(proxyIP, proxyPort, isalive);
			}
			// выводим ответ в консоль
			if (data.isEmpty())
				msg += " is bad";
			else
				msg += " is ok";
			utils.CustomPrint(memo, msg);
		} catch (Exception e) {
			msg = "client error: " + e.getMessage();
			utils.CustomPrint(memo, msg);
		} finally {
			try {
				s.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/*
	 * public static void main(String args[]) throws IOException {
	 * ProxyImporter.ImportFromTxt(null); CheckProxyDB(); }
	 */
}
