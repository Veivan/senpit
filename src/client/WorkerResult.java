package client;

import common.Constants.RetCodes;

public class WorkerResult {
	private String proxyIP;
	private int proxyPort;
	private RetCodes retcode;
	
	public WorkerResult(String proxyIP, int proxyPort, RetCodes retcode)
	{
		this.proxyIP = proxyIP;
		this.proxyPort = proxyPort;
		this.retcode = retcode;
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
