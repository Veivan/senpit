package client;

import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.swing.JTextArea;
import javax.swing.SwingWorker;

public class ProxyImporter extends SwingWorker<String, String> {

	private JTextArea textArea;
	private boolean DoCheckANM;
	
	private int prcountbefore;
	private int prcountafter;
	private int taskQueuesize;

	DbConnectorSenPit dbConnector;
	
	public ProxyImporter(JTextArea textArea, boolean DoCheckANM) {
		this.textArea = textArea;
		this.DoCheckANM = DoCheckANM;
		dbConnector = new DbConnectorSenPit();
	}

	@Override
	protected String doInBackground() throws Exception {
		String proxyIP = "";
		int proxyPort = 0;
		setProgress(0);

		ExecutorService cachedPool = Executors.newCachedThreadPool();
		Queue<Future<?>> taskQueue = new LinkedList<Future<?>>();

		Scanner in = new Scanner(new FileReader("proxy.txt"));
		int i = 0;
		while (in.hasNext()) {
			String data = in.next();
			String[] sp = data.split(":");
			if (sp.length > 1) {
				proxyIP = sp[0];
				proxyPort = Integer.parseInt(sp[1]);
				taskQueue.add(cachedPool.submit(new SenPitClient(proxyIP,
						proxyPort, DoCheckANM)));
				String message = String.format("Checking N %d %s:%d", i++,
						proxyIP, proxyPort);
				publish(message);
			}
		}
		in.close();

		try {
			cachedPool.shutdown();
			cachedPool.awaitTermination(120, TimeUnit.SECONDS);
		}
		catch (InterruptedException e) {
		    System.err.println("tasks interrupted");
		}
		finally {
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
			if (checkTask.isDone() ||  checkTask.isCancelled()) {
				countdone++;
				WorkerResult res = (WorkerResult) checkTask.get();
				int isalive = res.isIsOk() ? 1 : 0;
				proxyIP = res.getProxyIP();
				proxyPort = res.getProxyPort();
				dbConnector.SaveProxy(proxyIP, proxyPort, isalive);
				String message = String.format("%s:%d is %s", proxyIP,
						proxyPort, (isalive == 0 ? "bad" : "ok"));
				progress = Math.round((countdone / (float) taskQueuesize) * 100f);
				setProgress(progress);
				publish(message);
			} 
			//else taskQueue.add(checkTask);
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
		int newcnt = prcountafter - prcountbefore;

		textArea.append(String.format("Прокси в БД перед импортом : %d \n", prcountbefore));
		textArea.append(String.format("Прокси для обработки : %d \n", taskQueuesize));
		textArea.append(String.format("       негодные прокси : %d \n", taskQueuesize - Math.abs(newcnt)));
		textArea.append(String.format("       добавлено новых : %d \n", newcnt));
		textArea.append(String.format("Прокси в БД после импорта : %d \n", prcountafter));
		textArea.append("Finita\n");
	}

}
