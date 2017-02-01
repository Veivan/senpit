package client;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class ProxyImporter implements Runnable {

	private JTextArea memo;
	private JTextArea memostatus;
	ArrayList<Future<?>> futures = new ArrayList<Future<?>>();

	public ProxyImporter(JTextArea memo, JTextArea memostatus) {
		this.memo = memo;
		this.memostatus = memostatus;
	}

	/**
	 * Чтение прокси из файла proxy.txt
	 * 
	 * @throws FileNotFoundException
	 * @throws InterruptedException
	 */
	private void ImportFromTxt() throws FileNotFoundException {
		DbConnectorSenPit dbConnector = new DbConnectorSenPit();
		ExecutorService cachedPool = Executors.newCachedThreadPool();
		Scanner in = new Scanner(new FileReader("proxy.txt"));
		int i = 0;
		while (in.hasNext()) {
			String data = in.next();
			// System.out.println (in.next());
			Future<?> runnableFuture = cachedPool.submit(new SenPitClient(data,
					dbConnector, i, memo));
			futures.add(runnableFuture);
			i++;
		}
		in.close();
		cachedPool.shutdown();
	}

	@Override
	public void run() {
		try {
			ImportFromTxt();
			int finished = 0;
			int all = futures.size();
			while (finished < all) {
				finished = 0;
				for (Future<?> future : futures) {
					if (future.isDone())
						finished++;
				}
				System.out.println(finished);
				String message = "Checked " + finished + " from " + all;

				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						memostatus.setText(message);
					}
				}); 

				Thread.sleep(500);
			} 
			System.out.println("Finita");

		} catch (FileNotFoundException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
