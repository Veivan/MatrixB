package service;

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

import dbaware.DbConnector;

public class ImportRandContent extends JFrame{
	private static final long serialVersionUID = 1L;
	JTextArea text;
	JTextField picfile;
	JTextField url;
	JButton btImport;
	int twit_id = 2;

	DbConnector dbConnector = DbConnector.getInstance();

	ImportRandContent() {
		JPanel windowContent = new JPanel();
		windowContent.setLayout(new GridLayout(4, 2));

		JLabel label1 = new JLabel("text");
		text = new JTextArea();
		JScrollPane scroll = new JScrollPane(text,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		windowContent.add(label1);
		windowContent.add(scroll);
		
		JLabel label2 = new JLabel("picfile");
		picfile = new JTextField("", 5);
		windowContent.add(label2);
		windowContent.add(picfile);
	
		JLabel label3 = new JLabel("url");
		url = new JTextField("", 5);
		windowContent.add(label3);
		windowContent.add(url);

		btImport = new JButton("Import");
		btImport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
					StoreText2DB();
			}
		});				

		windowContent.add(btImport);

		getContentPane().add(windowContent);
		setTitle("Import random twits");
		setSize(400, 300);
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
				new ImportRandContent();
			}
		});
	}

	private void StoreText2DB() {
		String rtext = text.getText();
		String picpath = picfile.getText();
		byte[] picture = null;
		int pic_id = 0;
		if (rtext.isEmpty())
		{
			JOptionPane.showMessageDialog(null,"Текст отсутствует!");
			return;
		}
		if (!picpath.isEmpty())
		{
			picture = Utils.readBytesFromFile(picpath);
			pic_id = dbConnector.SaveImage(picture, 3);
		}
		dbConnector.StoreRandText(rtext, pic_id, url.getText(), twit_id);
		text.setText("");
	}
}
