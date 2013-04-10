package gomoku;

import gomoku.gui.GomokoGUI;
import gomoku.util.Utils;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class MainGomoku {

	private static void createAndShowGUI() {
		GomokoGUI jf = new GomokoGUI();
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setVisible(true);
	}

	public static void main(String[] args) {
		Utils.useCoolSkin();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				createAndShowGUI();
			}
		});
	}

}
