package client;

import java.util.ArrayList;
import java.util.List;

public class ProviderDB implements IProxyProvider {

	DbConnectorSenPit dbConnector = new DbConnectorSenPit();

	@Override
	public List<ProxyRecord> getProxyList() {
		List<ProxyRecord> ProxyList = new ArrayList<ProxyRecord>();
		List<String> list = dbConnector.GetProxsFromDB();
		for (String data : list) {
			String[] sp = data.split(":");
			if (sp.length > 1) {
				ProxyRecord pRec = new ProxyRecord(sp[0],
						Integer.parseInt(sp[1]));
				ProxyList.add(pRec);
			}
		}
		return ProxyList;
	}

}
