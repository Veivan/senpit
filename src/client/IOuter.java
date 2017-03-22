package client;

import javax.swing.JTextArea;

public interface IOuter {

	void MakeOut(JTextArea textArea, int taskQueuesize, int prcountbefore,
			int prcountafter, int countvalid);

}
