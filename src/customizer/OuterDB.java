package customizer;

import javax.swing.JTextArea;

import client.IOuter;

public class OuterDB implements IOuter{
	
	public void MakeOut(JTextArea textArea, int taskQueuesize,
			int prcountbefore, int prcountafter, int countvalid)
	{
		int badcnt = prcountbefore - prcountafter;
		if (badcnt < 0) badcnt = 0;

		textArea.append(String.format("������ � �� ����� ��������� : %d \n",
				prcountbefore));
		textArea.append(String.format("		�������� ������ : %d \n",
				countvalid));
		textArea.append(String.format("		������� : %d \n", badcnt));
		textArea.append(String.format("������ � �� ����� ������� : %d \n",
				prcountafter));
		textArea.append("Finita\n");		
	}
	
}
