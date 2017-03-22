package client;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ProviderFile implements IProxyProvider {

	@Override
	public List<ProxyRecord> getProxyList() {
		List<ProxyRecord> ProxyList = new ArrayList<ProxyRecord>();
		Scanner in;
		try {
			in = new Scanner(new FileReader("proxy.txt"));
			while (in.hasNext()) {
				String data = in.next();
				String[] sp = data.split(":");
				if (sp.length > 1) {
					ProxyRecord pRec = new ProxyRecord(sp[0],
							Integer.parseInt(sp[1]));
					ProxyList.add(pRec);
				}
			}
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return ProxyList;
	}

}
