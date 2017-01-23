package service;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 * Supply message output to Memo
 */
public class MemoProxy {
	private JTextArea memo;

	public MemoProxy(JTextArea memo) {
		this.memo = memo;
	}

	public void println(String message){
	SwingUtilities.invokeLater(new Runnable() {
		public void run() {
			memo.append(message + "\n");
		}
	});
	}

	public void replacetext(String message){
	SwingUtilities.invokeLater(new Runnable() {
		public void run() {
			memo.setText("");
			memo.append(message + "\n");
		}
	});
	}

}
