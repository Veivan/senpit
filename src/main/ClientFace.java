package main;

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
import javax.swing.JTextField;

/**
 * 
 */
public class ClientFace extends JFrame {
	private static final long serialVersionUID = -1441851739039937804L;
	JTextArea memo;
	JButton btCheckDBproxies;
	JButton btImportBanners;
	JTextField field1;

	ClientFace() {
		// Создаём панели
		JPanel windowContent = new JPanel();
		BorderLayout bl = new BorderLayout();
		windowContent.setLayout(bl);

		JPanel p1 = new JPanel();
		GridLayout gl = new GridLayout(4, 2);
		p1.setLayout(gl);

		JPanel p2 = new JPanel();
		GridLayout fl = new GridLayout(1, 1);
		p2.setLayout(fl);

		JPanel p3 = new JPanel();
		BorderLayout bl3 = new BorderLayout();
		// GridLayout bl3 = new GridLayout(2, 1);
		p3.setLayout(bl3);

		// Создаём компоненты в памяти
		JLabel label1 = new JLabel("phrase 1:");
		field1 = new JTextField(10);
		JLabel label2 = new JLabel("phrase 2:");
		JTextField field2 = new JTextField(10);
		JLabel label3 = new JLabel("phrase 3:");
		JTextField field3 = new JTextField(10);
		btCheckDBproxies = new JButton("Check proxies in DB");
		btCheckDBproxies.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SenPitClient.CheckProxyDB(memo);
						//JOptionPane.showMessageDialog(null,"Введите параметры поиска!");
			}
		});

		btImportBanners = new JButton("Import Banners to DB");
		btImportBanners.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//SenPitClient.CheckProxyDB(memo);
				JOptionPane.showMessageDialog(null,"Import Banners to DB");
			}
		});				
		memo = new JTextArea();
		JScrollPane scroll = new JScrollPane(memo,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		JTextArea memo3 = new JTextArea();
		memo3.setEditable(false);
    	memo3.append(" Просто.\n");
    	memo3.append(" текст ");

		// Добавляем компоненты на панель
		p1.add(label1);
		p1.add(field1);
		p1.add(label2);
		p1.add(field2);
		p1.add(label3);
		p1.add(field3);
		p1.add(btCheckDBproxies);
		p1.add(btImportBanners);
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
				//JFrame.setDefaultLookAndFeelDecorated(true);
				new ClientFace();
			}
		});

	}

}
