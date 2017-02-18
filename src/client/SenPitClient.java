package client;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Callable;

public class SenPitClient implements Callable<Object> {
	static final int port = 1967;
	// �������� ����� ������ � HTTP
	static String proxyType = "HTTP";
	private String proxyIP;
	private int proxyPort;

	private Socket s;
	private String command;
	private boolean DoCheckANM;

	/**
	 * �����������. 
	 */
	public SenPitClient(String proxyIP, int proxyPort, boolean DoCheckANM) {
		this.proxyIP = proxyIP;
		this.proxyPort = proxyPort;
		this.DoCheckANM = DoCheckANM;
	}

	@Override
	public Object call() {
		boolean IsOk = CheckIt();
		WorkerResult res = new WorkerResult(proxyIP, proxyPort, IsOk);
		return res;
	}

	private boolean CheckIt() {
		boolean result = false;

		try {
			// ��������� ����� � ����������� � localhost:port
			// �������� ����� �������
			s = new Socket("localhost", port);
			// ���� ����� ������ � ������� ���� ������ ��������
			// �������� ��� ������, ����� ��������� ������ � ��� ����
			// args[0] = "getit" + "\n" + s.getInetAddress().getHostAddress() +
			// ":" + s.getLocalPort();

			command = String.format("%s:%s:%d:%s", (DoCheckANM ? "checkanm" : "check"), proxyIP, proxyPort,
					proxyType);

			String data = "";
			for (int i = 0; i < 2; i++) {
				s.getOutputStream().write(command.getBytes());
				// ������ �����
				byte buf[] = new byte[64 * 1024];
				int r = s.getInputStream().read(buf);
				if (r > 0) {
					data = new String(buf, 0, r);
					break;
				}
			}		
			result = !data.isEmpty();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				s.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

}
