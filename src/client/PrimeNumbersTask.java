package client;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JTextArea;
import javax.swing.SwingWorker;

public class PrimeNumbersTask extends SwingWorker<List<Integer>, Integer> {

	int numbersToFind;

	List<Integer> numbers = new ArrayList<Integer>();

	private JTextArea textArea;

	PrimeNumbersTask(JTextArea textArea, int numbersToFind) {
		this.textArea = textArea;
		this.numbersToFind = numbersToFind;
	}

	@Override
	public List<Integer> doInBackground() {
		boolean enough = false;
		while (!enough && !isCancelled()) {
			Integer number = nextPrimeNumber();
			publish(number);
			setProgress(100 * numbers.size() / numbersToFind);
			enough = numbers.size() >= numbersToFind;
		}
		return numbers;
	}

	@Override
	protected void process(List chunks) {
		// TODO Auto-generated method stub
		// super.process(chunks);
		for (Object number : chunks) {
			textArea.append(number.toString() + "\n");
		}
	}

	/**
	 * This method is called in EDT after {@link #doInBackground()} is finished.
	 */
	@Override
	protected void done() {
		/*
		 * for (Integer number : numbers) { textArea.append(number.toString() +
		 * "\n"); }
		 * 
		 * String message = "qq"; if (SwingUtilities.isEventDispatchThread()){
		 * textArea.setText(message); }else { SwingUtilities.invokeLater(new
		 * Runnable(){ public void run(){ textArea.setText(message); } }); }
		 */
	}

	private int nextPrimeNumber() {
		Random random = new Random();
		/*
		 * try { Thread.sleep(100); } catch (InterruptedException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 */
		int x = random.nextInt();
		numbers.add(x);
		return x;
	}

}
