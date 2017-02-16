package client;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class ProxyImporter implements Runnable {

	private JTextArea memo;
	private JTextArea memostatus;
	private boolean DoCheckANM;
	ArrayList<CompletableFuture<Void>> futures = new ArrayList<CompletableFuture<Void>>();

	public ProxyImporter(JTextArea memo, JTextArea memostatus, boolean DoCheckANM) {
		this.memo = memo;
		this.memostatus = memostatus;
		this.DoCheckANM = DoCheckANM;
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
			//Future<?> runnableFuture = cachedPool.submit(new SenPitClient(data, dbConnector, i, memo));
			
			final CompletableFuture<Void> runnableFuture = CompletableFuture.runAsync( new SenPitClient(data,
					dbConnector, i, memo, DoCheckANM), cachedPool);
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
				for (CompletableFuture<Void> future : futures) {
					if (future.isDone())
						finished++;
				}
				String message = "Checked " + finished + " from " + all;
				System.out.println(message);

	            if (SwingUtilities.isEventDispatchThread()){
					memostatus.setText(message);
	            }else{
	                SwingUtilities.invokeLater(new Runnable(){
	                    public void run(){
							memostatus.setText(message);
	                    }
	                });
	            }

				Thread.sleep(3000);
			} 
			System.out.println("Finita");

		} catch (FileNotFoundException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
