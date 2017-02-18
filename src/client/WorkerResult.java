package client;

public class WorkerResult {
	private String proxyIP;
	private int proxyPort;
	private boolean IsOk;
	
	public WorkerResult(String proxyIP, int proxyPort, boolean IsOk)
	{
		this.proxyIP = proxyIP;
		this.proxyPort = proxyPort;
		this.IsOk = IsOk;
	}

	public String getProxyIP() {
		return proxyIP;
	}

	public int getProxyPort() {
		return proxyPort;
	}

	public boolean isIsOk() {
		return IsOk;
	}
	
}
