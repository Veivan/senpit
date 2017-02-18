package client;

import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JTextArea;
import javax.swing.SwingWorker;

public class ProxyImporter extends SwingWorker<String, String> {

	private JTextArea textArea;
	private boolean DoCheckANM;

	public ProxyImporter(JTextArea textArea, boolean DoCheckANM) {
		this.textArea = textArea;
		this.DoCheckANM = DoCheckANM;
	}

	@Override
	protected String doInBackground() throws Exception {
		String proxyIP = "";
		int proxyPort = 0;
		setProgress(0);

		DbConnectorSenPit dbConnector = new DbConnectorSenPit();
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

		int progress = 0;
		while (!taskQueue.isEmpty()) {
			int taskQueuesize = taskQueue.size();
			Future<?> checkTask = taskQueue.remove();
			if (checkTask.isDone()) {
				int isalive = ((boolean)checkTask.get() == false) ? 0 : 1;
				dbConnector.SaveProxy(proxyIP, proxyPort, isalive);
				String message = String.format("%s:%d is %s", proxyIP, proxyPort, (isalive == 0 ? "bad" : "ok"));
				progress += 20;
	            setProgress(Math.round((taskQueuesize - 1 / (float) taskQueuesize) * 100f));
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
		textArea.append("Finita");
	}

}
