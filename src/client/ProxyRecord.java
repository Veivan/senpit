package client;

public class ProxyRecord {
	private String proxyIP;
	private int proxyPort;

	public ProxyRecord(String proxyIP, int proxyPort) {
		this.proxyIP = proxyIP;
		this.proxyPort = proxyPort;
	}

	/**
	 * @return the proxyIP
	 */
	public String getProxyIP() {
		return proxyIP;
	}

	/**
	 * @return the proxyPort
	 */
	public int getProxyPort() {
		return proxyPort;
	}

}
