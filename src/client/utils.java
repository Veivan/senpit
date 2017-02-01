package client;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class utils {

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
