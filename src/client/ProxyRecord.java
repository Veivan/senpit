package client;

public class ProxyRecord {
	private String proxyIP;
	private int proxyPort;
	private final String proxyType;

	public ProxyRecord(String proxyIP, int proxyPort, String proxyType) {
		this.proxyIP = proxyIP;
		this.proxyPort = proxyPort;
		this.proxyType = proxyType;
	}

	/**
	 * @return the proxyType
	 */
	public final String getProxyType() {
		return proxyType;
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
