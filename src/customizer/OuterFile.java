package customizer;

import javax.swing.JTextArea;

import client.IOuter;

public class OuterFile implements IOuter {

	@Override
	public void MakeOut(JTextArea textArea, int taskQueuesize,
			int prcountbefore, int prcountafter, int countvalid) {

		int newcnt = prcountafter - prcountbefore;

		textArea.append(String.format("������ � �� ����� �������� : %d \n",
				prcountbefore));
		textArea.append(String.format("������ ��� ��������� : %d \n",
				taskQueuesize));
		textArea.append(String.format("		�������� ������ : %d \n",
				countvalid));
		textArea.append(String.format("		��������� ����� : %d \n", newcnt));
		textArea.append(String.format("������ � �� ����� ������� : %d \n",
				prcountafter));
		textArea.append("Finita\n");
	}

}
