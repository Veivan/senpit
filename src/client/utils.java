package client;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class utils {
	private final static String inifile = "senpitclient.ini";

	public static String ReadConnStrINI() throws Exception {
		String connstring = null;
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(new File(inifile)));
			connstring = props.getProperty("CONNECT_STRING");
		} catch (Exception e) {
			throw new Exception("ERROR ReaderIni : ", e);
		}
		return connstring;
	}

}
