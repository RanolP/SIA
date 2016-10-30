package me.ranol.serverisalive;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import me.ranol.serverisalive.utils.PictureBox;

public class SimpleFrame extends JFrame {
	private static final long serialVersionUID = -3687728785137546391L;
	JLabel msg = new JLabel("[SIA] 정상 가동중!");
	PictureBox icon = new PictureBox();
	JTextArea srvIP = new JTextArea("localhost");
	JTextArea srvPort = new JTextArea("25565");
	JProgressBar progress = new JProgressBar();
	JButton search = new JButton("정보 조회");
	JLabel lblProgress = new JLabel("진행률: 0%");
	JLabel players = new JLabel("플레이어: N / N");

	private JPanel contentPane;
	private final JTextPane motd = new JTextPane();
	private final JTextArea textArea = new JTextArea();

	/**
	 * Create the frame.
	 */
	public SimpleFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 600, 400);
		setTitle("[SIA - Server Is Alive?] Minecraft Server Checker");

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setContentPane(contentPane);

		progress.setBounds(12, 337, 560, 14);
		contentPane.add(progress);

		lblProgress.setBounds(12, 312, 80, 15);
		contentPane.add(lblProgress);

		srvIP.setBounds(40, 10, 200, 24);
		contentPane.add(srvIP);

		srvPort.setBounds(270, 10, 60, 24);
		contentPane.add(srvPort);

		JLabel lbl2 = new JLabel("주소:");
		lbl2.setBounds(12, 14, 30, 15);
		contentPane.add(lbl2);

		JLabel lbl1 = new JLabel("포트:");
		lbl1.setBounds(240, 14, 30, 15);
		contentPane.add(lbl1);

		search.setBounds(333, 10, 97, 23);
		contentPane.add(search);

		msg.setBounds(12, 44, 418, 15);
		contentPane.add(msg);

		JLabel lblMotd = new JLabel("Motd:");
		lblMotd.setBounds(12, 69, 45, 15);
		contentPane.add(lblMotd);

		motd.setText("분석되지 않음");
		motd.setBounds(51, 69, 200, 50);
		contentPane.add(motd);

		players.setBounds(263, 69, 187, 15);
		contentPane.add(players);

		textArea.setBounds(263, 94, 309, 233);
		contentPane.add(textArea);

		icon.setBounds(10, 125, 100, 100);
		contentPane.add(icon);

		setVisible(true);
		SwingUtilities.invokeLater(() -> {
			try {
				UIManager.setLookAndFeel(new NimbusLookAndFeel());
				SwingUtilities.updateComponentTreeUI(this);
				this.repaint();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
}
