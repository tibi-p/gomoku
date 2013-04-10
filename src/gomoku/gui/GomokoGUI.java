package gomoku.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

public class GomokoGUI extends JFrame {

	/**
	 * Generated serial UID.
	 */
	private static final long serialVersionUID = 3218319186149327858L;

	private JTextField recentAverageFiller;
	private final NeuralMap neuralMap = new NeuralMap();

	public GomokoGUI() {
		try {
			Dimension recommendedSize = new Dimension(1024, 668);

			JPanel statusPanel = new JPanel(new GridLayout(1, 2));
			recentAverageFiller = new JTextField(10);
			recentAverageFiller.setEditable(false);
			statusPanel.add(new JLabel(
					"Average filler across recent generations"));
			statusPanel.add(recentAverageFiller);

			JPanel infoPanel = new JPanel(new BorderLayout());
			JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
			tabbedPane.add("map", neuralMap);
			infoPanel.add(tabbedPane, BorderLayout.CENTER);
			infoPanel.add(statusPanel, BorderLayout.SOUTH);

			final GomokuPanel gomokuPanel = new GomokuPanel(this);
			gomokuPanel.setSize(recommendedSize);
			gomokuPanel.setMinimumSize(recommendedSize);

			JPanel controlPanel = new JPanel(new GridLayout(3, 2));
			SpinnerModel spinnerModel;
			spinnerModel = new SpinnerNumberModel(8, 1, 100, 1);
			final JSpinner rowsSpinner = addLabeledSpinner(controlPanel,
					"Number of Rows", spinnerModel);
			spinnerModel = new SpinnerNumberModel(8, 1, 100, 1);
			final JSpinner columnsSpinner = addLabeledSpinner(controlPanel,
					"Number of Columns", spinnerModel);
			final JButton startButton = new JButton("Start");
			final JButton stopButton = new JButton("Stop");
			startButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int rows = (Integer) rowsSpinner.getValue();
					int columns = (Integer) columnsSpinner.getValue();

					gomokuPanel.start(rows, columns);
					startButton.setEnabled(false);
					stopButton.setEnabled(true);
				}
			});
			stopButton.setEnabled(false);
			stopButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					gomokuPanel.stop();
					startButton.setEnabled(true);
					stopButton.setEnabled(false);
				}
			});
			controlPanel.add(startButton);
			controlPanel.add(stopButton);

			JPanel mainPanel = new JPanel(new BorderLayout());
			mainPanel.add(gomokuPanel, BorderLayout.CENTER);
			mainPanel.add(controlPanel, BorderLayout.SOUTH);
			mainPanel.add(infoPanel, BorderLayout.EAST);

			getContentPane().add(mainPanel);
			setTitle("Gomoku");
			setSize(recommendedSize);
			centerWindow();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setRecentAverageFeeding(String text) {
		recentAverageFiller.setText(text);
	}

	public void updateNeuralMap(double[][] output) {
		neuralMap.setMap(output);
	}

	private void centerWindow() {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolkit.getScreenSize();
		Dimension windowSize = getSize();
		setLocation((screenSize.width - windowSize.width) / 2,
				(screenSize.height - windowSize.height) / 2);
	}

	public static JSpinner addLabeledSpinner(Container c, String text,
			SpinnerModel model) {
		JLabel l = new JLabel(text);
		c.add(l);

		JSpinner spinner = new JSpinner(model);
		l.setLabelFor(spinner);
		c.add(spinner);

		return spinner;
	}

}
