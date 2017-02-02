package client;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

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

	public static void CustomPrint(JTextArea memo, String message) {
		if (memo != null) {
			if (message == null)
				memo.setText(null);
			else
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						memo.append(message + "\n");
					}
				});

		} else if (message != null)
			System.out.println(message);
	}

}
