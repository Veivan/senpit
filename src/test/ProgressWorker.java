package test;

import java.util.List;

import javax.swing.JTextArea;
import javax.swing.SwingWorker;

public class ProgressWorker extends SwingWorker {

    public static final int MAX = 50;
	private JTextArea textArea;

	public ProgressWorker(JTextArea textArea) {
		this.textArea = textArea;
	}

    @Override
    protected Object doInBackground() throws Exception {
		setProgress(0);

		for (int index = 0; index < MAX; index++) {
            Thread.sleep(250);
            setProgress(Math.round((index / (float) MAX) * 100f));
            publish(1);
        }
        return null;
    }

    @Override
    protected void process(List chunks) {
    	// TODO Auto-generated method stub
    	//super.process(chunks);
		for (Object number : chunks) {
			textArea.append(number.toString() + "\n");
		}
   }        
	/**
	 * This method is called in EDT after {@link #doInBackground()} is finished.
	 */
	@Override
	protected void done() {
		textArea.append("Finita");	}
}
