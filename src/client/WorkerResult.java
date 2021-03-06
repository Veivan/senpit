package client;

import common.Constants.RetCodes;

public class WorkerResult {
	private String proxyIP;
	private int proxyPort;
	private String proxyType;
	private RetCodes retcode;
	
	public WorkerResult(String proxyIP, int proxyPort, String proxyType, RetCodes retcode)
	{
		this.proxyIP = proxyIP;
		this.proxyPort = proxyPort;
		this.proxyType = proxyType;
		this.retcode = retcode;
	}

	/**
	 * @return the proxyType
	 */
	public final String getProxyType() {
		return proxyType;
	}

	public String getProxyIP() {
		return proxyIP;
	}

	public int getProxyPort() {
		return proxyPort;
	}

	public RetCodes getRetCode() {
		return retcode;
	}
	
}
