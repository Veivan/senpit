package client;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import common.Constants.RetCodes;

public class ProxyChecker extends SwingWorker<String, String>{

	private final JTextArea textArea;
	private final IProxyProvider ProxyProvider;
	private final IOuter outer;
	private final boolean DoCheckANM;

	private int prcountbefore;
	private int prcountafter;
	private int taskQueuesize;
	private int countvalid = 0;

	DbConnectorSenPit dbConnector;

	public ProxyChecker(JTextArea textArea, IProxyProvider ProxyProvider, boolean DoCheckANM, IOuter outer) {
		this.textArea = textArea;
		this.ProxyProvider = ProxyProvider;
		this.outer = outer;
		this.DoCheckANM = DoCheckANM;
		dbConnector = new DbConnectorSenPit();
	}

	@Override
	protected String doInBackground() throws Exception {
		setProgress(0);

		ExecutorService cachedPool = Executors.newCachedThreadPool();
		Queue<Future<?>> taskQueue = new LinkedList<Future<?>>();
		
		if (ProxyProvider == null)
			throw new Exception("ProxyProvider required");
		
		int i = 0;
		List<ProxyRecord> ProxyList = ProxyProvider.getProxyList();
		for (ProxyRecord proxyRec : ProxyList) {
			taskQueue.add(cachedPool.submit(new SenPitClient(proxyRec.getProxyIP(),
					proxyRec.getProxyPort(), DoCheckANM)));
			String message = String.format("Checking N %d %s:%d", i++,
					proxyRec.getProxyIP(), proxyRec.getProxyPort());
			publish(message);
		}
		
		try {
			cachedPool.shutdown();
			cachedPool.awaitTermination(120, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			System.err.println("tasks interrupted");
		} finally {
			if (!cachedPool.isTerminated()) {
				System.err.println("cancel non-finished tasks");
			}
			cachedPool.shutdownNow();
			System.out.println("shutdown finished");
		}
		prcountbefore = dbConnector.GetProxsCountFromDB();
		taskQueuesize = taskQueue.size();
		int progress = 0;
		int countdone = 0;
		while (!taskQueue.isEmpty()) {
			Future<?> checkTask = taskQueue.remove();
			if (checkTask.isDone() || checkTask.isCancelled()) {
				countdone++;
				WorkerResult res = (WorkerResult) checkTask.get();
				boolean isValid = res.getRetCode() == RetCodes.Valid;
				if (isValid)
					countvalid++;
				String proxyIP = res.getProxyIP();
				int proxyPort = res.getProxyPort();
				dbConnector.SaveProxy(proxyIP, proxyPort, isValid ? 1 : 0);
				String message = String.format("%s:%d is %s - %s", proxyIP,
						proxyPort, (isValid ? "ok" : "bad"), res.getRetCode());
				progress = Math
						.round((countdone / (float) taskQueuesize) * 100f);
				setProgress(progress);
				publish(message);
			}
			// else taskQueue.add(checkTask);
		}

		return null;
	}

	@Override
	protected void process(List<String> chunks) {
		for (String message : chunks) {
			textArea.append(message + "\n");
		}
	}

	/**
	 * This method is called in EDT after {@link #doInBackground()} is finished.
	 */
	@Override
	protected void done() {
		prcountafter = dbConnector.GetProxsCountFromDB();	
		if (outer != null)
			outer.MakeOut(textArea, taskQueuesize, prcountbefore, prcountafter, countvalid);
	}
}
