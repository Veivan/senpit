package test;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class SwingWorkerProgressExample {

    public static void main(String[] args) {
        new SwingWorkerProgressExample();
    }

    public SwingWorkerProgressExample() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                    ex.printStackTrace();
                }

                JFrame frame = new JFrame("Testing");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.add(new TestPane());
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }

    public class TestPane extends JPanel {

        /**
		 * 
		 */
		private static final long serialVersionUID = -5847892839183869925L;
		private JProgressBar pb;
        private JTextArea memo;

        public TestPane() {

            //setLayout(new GridBagLayout());
            
            setLayout(new GridLayout(2, 1));

            pb = new JProgressBar(0, 100);
            pb.setIndeterminate(true);
            add(pb);
    		memo = new JTextArea();
    		JScrollPane scroll = new JScrollPane(memo,
    				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
    				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
           add(scroll);
            memo.append("qq");

            ProgressWorker worker = new ProgressWorker(memo);
            worker.addPropertyChangeListener(new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    /*if ("state".equalsIgnoreCase(evt.getPropertyName())) {
                        SwingWorker worker = (SwingWorker) evt.getSource();
                        switch (worker.getState()) {
                            case DONE:
                                // Clean up here...
                                break;
                        }
                    } else */if ("progress".equalsIgnoreCase(evt.getPropertyName())) {
                        // You could get the SwingWorker and use getProgress, but I'm lazy... 
                        System.out.println(EventQueue.isDispatchThread());
                        pb.setIndeterminate(false);
                        pb.setValue((Integer) evt.getNewValue());
                    }
                }
            });
            worker.execute();

        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(200, 200);
        }

    }

   /* public static class ProgressWorker extends SwingWorker {

        public static final int MAX = 1000;
    	private JTextArea textArea;

    	ProgressWorker(JTextArea textArea) {
    		this.textArea = textArea;
    	}

        @Override
        protected Object doInBackground() throws Exception {
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

    } */

}
