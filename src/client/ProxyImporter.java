package client;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JTextArea;

public class ProxyImporter {

	/**
	 * ������ ������ �� ����� proxy.txt
	 * @throws FileNotFoundException 
	 */
	public static void ImportFromTxt(JTextArea memo) throws FileNotFoundException
	{
		DbConnectSingle dbConnector = DbConnectSingle.getInstance();  
		ExecutorService cachedPool = Executors.newCachedThreadPool();
		Scanner in = new Scanner(new FileReader("proxy.txt"));	
		int i = 0;
		while (in.hasNext()) 
		{ 
			String data = in.next();
			//System.out.println (in.next()); 
			cachedPool.submit(new SenPitClient(data, dbConnector, i, memo));
			i++;
		}
		in.close();
		cachedPool.shutdown();
	}
	
}
