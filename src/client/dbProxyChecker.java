package client;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JTextArea;

import client.utils;

/**
 * Прверка прокси, хранящихся в БД и удаление дохлых
 */
public class dbProxyChecker {

	static void CheckProxyDB(JTextArea memo)
	{
		String message = null;
		DbConnectorSenPit dbConnector = new DbConnectorSenPit();  
		ExecutorService cachedPool = Executors.newCachedThreadPool();		
		List<String> list = dbConnector.GetProxsFromDB();
		message = "Всего прокси : " + list.size();
		int i = 0;
		for (String str : list) {			
		//	cachedPool.submit(new SenPitClient(str, dbConnector, i, memo, false));
			i++;
		}
		cachedPool.shutdown();
	}

}
