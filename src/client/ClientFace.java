package client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import test.ProgressWorker;

public class ClientFace {

	public static void main(String[] args) {
		new ClientFace();
	}

	public ClientFace() {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager
							.getSystemLookAndFeelClassName());
				} catch (ClassNotFoundException | InstantiationException
						| IllegalAccessException
						| UnsupportedLookAndFeelException ex) {
					ex.printStackTrace();
				}

				JFrame frame = new JFrame("SenPit client");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.add(new TestPane());
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}

	/**
	 * 
	 */
	public class TestPane extends JPanel {
		private static final long serialVersionUID = -5847892839183869925L;
		private JProgressBar progressBar;
		private JTextArea memo;
		JButton btImportProxyFromTXT;
		JButton btImportProxyFromTXTanm;
		JButton btCheckDBproxies;
		JButton btImportBanners;
		JButton btMakeUproxy;

		ProgressWorker worker;

		public TestPane() {

			setLayout(new BorderLayout());

			JPanel p1 = new JPanel();
			GridLayout gl = new GridLayout(5, 2);
			p1.setLayout(gl);
			add("North", p1);

			JPanel p2 = new JPanel();
			GridLayout fl = new GridLayout(1, 1);
			p2.setLayout(fl);
			add("Center", p2);

			JPanel p3 = new JPanel();
			// BorderLayout bl3 = new BorderLayout();
			GridLayout bl3 = new GridLayout(1, 2);
			p3.setLayout(bl3);
			add("South", p3);

			progressBar = new JProgressBar(0, 100);

			memo = new JTextArea();
			JScrollPane scroll = new JScrollPane(memo,
					JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
					JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

			JTextArea memo3 = new JTextArea();
			memo3.setEditable(false);
			memo3.append("");

			JLabel label1 = new JLabel(
					"Import proxies from proxy.txt to DB + Check anonymous");
			btImportProxyFromTXT = new JButton("Start");
			btImportProxyFromTXT.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ProxyImporter pimp = new ProxyImporter(memo, memo3, true);
					pimp.run();
				}
			});

			JLabel label1_1 = new JLabel("Import proxies from proxy.txt to DB");
			btImportProxyFromTXTanm = new JButton("Start");
			btImportProxyFromTXTanm.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ProxyImporter pimp = new ProxyImporter(memo, memo3, false);
					pimp.run();
				}
			});

			JLabel label2 = new JLabel("Check proxies in DB");
			btCheckDBproxies = new JButton("Start");
			btCheckDBproxies.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dbProxyChecker.CheckProxyDB(memo);
				}
			});

			JLabel label3 = new JLabel("Import Banners to DB");
			btImportBanners = new JButton("Start");
			btImportBanners.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// SenPitClient.CheckProxyDB(memo);
					// JOptionPane.showMessageDialog(null,
					// "Import Banners to DB");
					worker.execute();
				}
			});

			JLabel label4 = new JLabel("Make uproxy from unsort");
			btMakeUproxy = new JButton("Start");
			btMakeUproxy.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ReadUnsort.MakeUProxy();
					JOptionPane.showMessageDialog(null, "Finish.");
				}
			});

			// Добавляем компоненты на панель
			p1.add(label1);
			p1.add(btImportProxyFromTXT);
			p1.add(label1_1);
			p1.add(btImportProxyFromTXTanm);
			p1.add(label2);
			p1.add(btCheckDBproxies);
			p1.add(label3);
			p1.add(btImportBanners);
			p1.add(label4);
			p1.add(btMakeUproxy);

			p2.add(scroll);

			p3.add(memo3);
			p3.add(progressBar);

			worker = new ProgressWorker(memo);
			worker.addPropertyChangeListener(new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					/*
					 * if ("state".equalsIgnoreCase(evt.getPropertyName())) {
					 * SwingWorker worker = (SwingWorker) evt.getSource();
					 * switch (worker.getState()) { case DONE: // Clean up
					 * here... break; } } else
					 */
					if ("progress".equalsIgnoreCase(evt.getPropertyName())) {
						progressBar.setValue((Integer) evt.getNewValue());
					}
				}
			});

		}

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(600, 600);
		}

	}

}
