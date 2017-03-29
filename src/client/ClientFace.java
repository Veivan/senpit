package client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import customizer.OuterDB;
import customizer.OuterFile;
import customizer.ProviderDB;
import customizer.ProviderFile;

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
	
		ProxyChecker workerProxyChecker; 
		IOuter outer;
		IProxyProvider proxyProvider;
		
		private String proxyType = "";
		
		public TestPane() {

			setLayout(new BorderLayout());

			JPanel p1 = new JPanel();
			GridLayout gl = new GridLayout(6, 2);
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
			memo.setFont(new Font("Serif", Font.PLAIN, 14));
			JScrollPane scroll = new JScrollPane(memo,
					JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
					JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

			JTextArea memo3 = new JTextArea();
			memo3.setEditable(false);
			memo3.append("");

		    JLabel label0 = new JLabel(
					"Select proxies type");
			String[] items = {
				    "HTTP",
				    "SOCKS"
				};
			JComboBox<String> comboBox = new JComboBox<String>(items);
			proxyType = (String)comboBox.getSelectedItem();
	        ActionListener actionListener = new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	                JComboBox<?> box = (JComboBox<?>)e.getSource();
	                proxyType = (String)box.getSelectedItem();
	            }
	        };
	        comboBox.addActionListener(actionListener);
				
		    JLabel label1 = new JLabel(
					"Import proxies from proxy.txt to DB + Check anonymous");
			btImportProxyFromTXT = new JButton("Start");
			btImportProxyFromTXT.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					execImportProxy(true, proxyType);
				}
			});

			JLabel label1_1 = new JLabel("Import proxies from proxy.txt to DB");
			btImportProxyFromTXTanm = new JButton("Start");
			btImportProxyFromTXTanm.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					execImportProxy(false, proxyType);
				}
			});

			JLabel label2 = new JLabel("Check proxies in DB");
			btCheckDBproxies = new JButton("Start");
			btCheckDBproxies.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					execCheckDBProxy();
				}
			});

			JLabel label3 = new JLabel("Import Banners to DB");
			btImportBanners = new JButton("Start");
			btImportBanners.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// SenPitClient.CheckProxyDB(memo);
					JOptionPane.showMessageDialog(null, "Import Banners to DB");
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
	        p1.add(label0);
	        p1.add(comboBox);
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
		}

		private void execImportProxy(boolean DoCheckANM, String proxyType) {
			IOuter outer = new OuterFile();
			IProxyProvider proxyProvider = new ProviderFile(proxyType);
			ProxyChecker workerProxyChecker = new ProxyChecker(memo, proxyProvider, DoCheckANM, outer);

			workerProxyChecker.addPropertyChangeListener(new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					if ("progress".equalsIgnoreCase(evt.getPropertyName())) {
						progressBar.setValue((Integer) evt.getNewValue());
					}
				}
			});
			workerProxyChecker.execute();
		}
		
		private void execCheckDBProxy() {
			boolean DoCheckANM = false;
			IOuter outer = new OuterDB();
			IProxyProvider proxyProvider = new ProviderDB();
			ProxyChecker workerProxyChecker = new ProxyChecker(memo, proxyProvider, DoCheckANM, outer);
		
			workerProxyChecker.addPropertyChangeListener(new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					if ("progress".equalsIgnoreCase(evt.getPropertyName())) {
						progressBar.setValue((Integer) evt.getNewValue());
					}
				}
			});
			workerProxyChecker.execute();
		}		

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(600, 600);
		}

	}

}
