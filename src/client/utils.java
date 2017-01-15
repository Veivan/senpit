package client;

import javax.swing.JTextArea;

public class utils {

	public static void CustomPrint(JTextArea memo, String message)
	{
		if (memo != null){
			if (message == null)
				memo.setText(null);
			else
				memo.append(message + "\n");			
		}
		else
			if (message != null)
				System.out.println (message); 		
	}

}
