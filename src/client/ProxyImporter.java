package client;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProxyImporter {

	/**
	 * Чтение прокси из файла proxy.txt
	 * @throws FileNotFoundException 
	 */
	public static void ImportFromTxt() throws FileNotFoundException
	{
		DbConnectSingle dbConnector = DbConnectSingle.getInstance();  
		ExecutorService cachedPool = Executors.newCachedThreadPool();
		Scanner in = new Scanner(new FileReader("proxy.txt"));	
		int i = 0;
		while (in.hasNext()) 
		{ 
			String data = in.next();
			//System.out.println (in.next()); 
			cachedPool.submit(new SenPitClient(data, dbConnector, i, null));
			i++;
		}
		in.close();
		cachedPool.shutdown();
	}
	
}
