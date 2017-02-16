package client;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * 
 */
public class ClientFace extends JFrame {
	private static final long serialVersionUID = -1441851739039937804L;
	JTextArea memo;
	JButton btImportProxyFromTXT;
	JButton btImportProxyFromTXTanm;
	JButton btCheckDBproxies;
	JButton btImportBanners;
	JButton btMakeUproxy;

	ClientFace() {
		// ������ ������
		JPanel windowContent = new JPanel();
		BorderLayout bl = new BorderLayout();
		windowContent.setLayout(bl);

		JPanel p1 = new JPanel();
		GridLayout gl = new GridLayout(5, 2);
		p1.setLayout(gl);

		JPanel p2 = new JPanel();
		GridLayout fl = new GridLayout(1, 1);
		p2.setLayout(fl);

		JPanel p3 = new JPanel();
		BorderLayout bl3 = new BorderLayout();
		// GridLayout bl3 = new GridLayout(2, 1);
		p3.setLayout(bl3);

		// ������ ���������� � ������

		memo = new JTextArea();
		JScrollPane scroll = new JScrollPane(memo,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		JTextArea memo3 = new JTextArea();
		memo3.setEditable(false);
		memo3.append("");

		JLabel label1 = new JLabel("Import proxies from proxy.txt to DB + Check anonymous");
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

		// ��������� ���������� �� ������
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

		windowContent.add("North", p1);
		windowContent.add("Center", p2);
		windowContent.add("South", p3);

		// setContentPane(windowContent);
		getContentPane().add(windowContent);
		setTitle("SenPit client");
		setSize(600, 600);
		setLocationRelativeTo(null);
		setVisible(true);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// JFrame.setDefaultLookAndFeelDecorated(true);
				new ClientFace();
			}
		});

	}

}
