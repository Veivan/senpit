package client;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JTextArea;
import javax.swing.SwingWorker;

/**
 * Проверка прокси, хранящихся в БД и удаление дохлых
 */
public class dbProxyChecker extends SwingWorker<String, String>{

	private JTextArea textArea;
	
	private int prcountbefore;
	private int prcountafter;
	private int taskQueuesize;

	DbConnectorSenPit dbConnector;
	
	public dbProxyChecker(JTextArea textArea) {
		this.textArea = textArea;
		dbConnector = new DbConnectorSenPit();
	}

	@Override
	protected String doInBackground() throws Exception {
		String proxyIP = "";
		int proxyPort = 0;
		setProgress(0);

		ExecutorService cachedPool = Executors.newCachedThreadPool();
		Queue<Future<?>> taskQueue = new LinkedList<Future<?>>();

		List<String> list = dbConnector.GetProxsFromDB();
		int i = 0;
		for (String data : list) {			
			String[] sp = data.split(":");
			if (sp.length > 1) {
				proxyIP = sp[0];
				proxyPort = Integer.parseInt(sp[1]);
				taskQueue.add(cachedPool.submit(new SenPitClient(proxyIP,
						proxyPort, false)));
				String message = String.format("Checking N %d %s:%d", i++,
						proxyIP, proxyPort);
				publish(message);
			}
		}

		cachedPool.shutdown();

		prcountbefore = dbConnector.GetProxsCountFromDB();
		taskQueuesize = taskQueue.size();
		int progress = 0;
		int countdone = 0;
		while (!taskQueue.isEmpty()) {
			Future<?> checkTask = taskQueue.remove();
			if (checkTask.isDone()) {
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
			} else
				taskQueue.add(checkTask);
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
		int badcnt = prcountbefore - prcountafter;

		textArea.append(String.format("Прокси в БД перед проверкой : %d \n", prcountbefore));
		textArea.append(String.format("		Удалено : %d \n", badcnt));
		textArea.append(String.format("Прокси в БД после импорта : %d \n", prcountafter));
		textArea.append("Finita\n");
	}

}
