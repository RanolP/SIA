package me.ranol.serverisalive;

import java.awt.Color;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import javax.swing.text.SimpleAttributeSet;

import me.ranol.serverisalive.checker.CheckResults;
import me.ranol.serverisalive.checker.PingQuery_1_5;
import me.ranol.serverisalive.checker.PingQuery_1_7;
import me.ranol.serverisalive.checker.ProtocolQuery;
import me.ranol.serverisalive.checker.Query;
import me.ranol.serverisalive.checker.QueryKeys;
import me.ranol.serverisalive.checker.SocketQuery;
import me.ranol.serverisalive.gui.ColoredPane;
import me.ranol.serverisalive.gui.PictureBox;
import me.ranol.serverisalive.gui.PlayerObject;
import me.ranol.serverisalive.gui.PlayerRenderer;
import me.ranol.serverisalive.utils.DataCollector;
import me.ranol.serverisalive.utils.MotdParser.Colors;
import me.ranol.serverisalive.utils.ValueMap;

public class SimpleFrame extends JFrame {
	private static final long serialVersionUID = -3687728785137546391L;
	PictureBox icon = new PictureBox();
	JTextArea srvIP = new JTextArea("localhost");
	JTextArea srvPort = new JTextArea("25565");
	JProgressBar progress = new JProgressBar();
	JButton search = new JButton("정보 조회");
	JLabel lblProgress = new JLabel("진행률: 0%");
	JLabel players = new JLabel("플레이어: N / N");
	Vector<PlayerObject> playerVec = new Vector<>();
	JList<PlayerObject> playerList = new JList<>(playerVec);
	JLabel status = new JLabel("[◎]");
	JCheckBox mcProtocol = new JCheckBox("기본 프로토콜만 사용");
	JLabel bukkit = new JLabel("사용 버킷");
	JLabel version = new JLabel("서버 버전");

	private JPanel contentPane;
	private final ColoredPane motd = new ColoredPane();

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
		search.addActionListener(a -> {
			try {
				reset();
				int port = Integer.parseInt(srvPort.getText());
				String ip = srvIP.getText();
				new Thread(() -> {
					if (Query.isAlive(ip, port, 1500)) {
						status.setText("[✓] On");
						status.setForeground(Colors.DARK_GREEN.getAWTColor());
					} else {
						status.setText("[✗] Off");
						status.setForeground(Colors.RED.getAWTColor());
					}
				}).start();
				new Thread(() -> ping(ip, port, mcProtocol.isSelected()))
						.start();
				new Thread(() -> protocol(ip, port, mcProtocol.isSelected()))
						.start();
				new Thread(() -> socket(ip, port, mcProtocol.isSelected()))
						.start();
			} catch (NumberFormatException e) {
				showMessageBox("포트에 숫자를 넣어주세요!", "포트 설정", MessageType.WARN);

			}
		});
		status.setForeground(Color.GRAY);
		motd.setBackground(Color.BLACK);
		motd.setForeground(new Color(255, 255, 255));

		motd.setText("분석되지 않음");
		motd.setBounds(130, 44, 442, 50);
		motd.setEditable(false);
		motd.setFocusable(false);
		motd.setBorder(new LineBorder(Color.GRAY, 1));
		contentPane.add(motd);

		players.setBounds(167, 100, 187, 15);
		contentPane.add(players);

		icon.setBounds(10, 40, 100, 100);
		contentPane.add(icon);

		contentPane.add(playerList);
		playerList.setBounds(202, 125, 370, 190);
		playerList.setCellRenderer(new PlayerRenderer());

		contentPane.add(playerList);
		mcProtocol.setSelected(true);
		mcProtocol.setBounds(12, 283, 141, 23);

		contentPane.add(mcProtocol);
		status.setBounds(130, 100, 36, 15);

		contentPane.add(status);

		bukkit.setBounds(352, 100, 220, 15);
		contentPane.add(bukkit);
		version.setBounds(130, 125, 57, 15);

		contentPane.add(version);

		setVisible(true);
		SwingUtilities.invokeLater(() -> {
			try {
				UIManager.setLookAndFeel(new NimbusLookAndFeel());
				SwingUtilities.updateComponentTreeUI(this);
				UIManager.setLookAndFeel(new MetalLookAndFeel());
				SwingUtilities.updateComponentTreeUI(motd);
				motd.repaint();
				SwingUtilities.updateComponentTreeUI(icon);
				icon.repaint();
				SwingUtilities.updateComponentTreeUI(playerList);
				playerList.repaint();
				this.repaint();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	private void reset() {
		set(0);
		cOnline.clear();
		cMax.clear();
		cMotd.clear();
	}

	private void ping(String ip, int port, boolean sel) {
		icon.setImage(null);
		PingQuery_1_5 query2 = new PingQuery_1_5(ip, port);
		CheckResults result = query2.connect();
		if (result == CheckResults.CONNECTED) {
			collect(query2.getMotd(),
					query2.getPlayers(),
					query2.getMaxPlayers(),
					new ValueMap().set(QueryKeys.PROTOCOL_VERSION,
							query2.getProtocolVersion()));
		}
		PingQuery_1_7 query = new PingQuery_1_7(ip, port);
		result = query.connect();
		if (result == CheckResults.CONNECTED) {
			collect(query.getMotd(),
					query.getPlayers(),
					query.getMaxPlayers(),
					new ValueMap()
							.set(QueryKeys.SERVER_ICON, query.getServerIcon())
							.set(QueryKeys.ONLINE_PLAYERS,
									query.getOnlineUsers())
							.set(QueryKeys.PROTOCOL_VERSION,
									query.getProtocolVersion()));
		}
		increase(sel ? 50 : 34);
	}

	private void protocol(String ip, int port, boolean sel) {
		Query query = new ProtocolQuery(ip, port);
		CheckResults result = query.connect();
		if (result == CheckResults.CONNECTED) {
			collect(query.getMotd(), query.getPlayers(), query.getMaxPlayers(),
					null);
		}
		increase(sel ? 50 : 33);
	}

	DataCollector<String> cMotd = new DataCollector<>();
	DataCollector<Integer> cOnline = new DataCollector<>();
	DataCollector<Integer> cMax = new DataCollector<>();

	void collect(String motd, int online, int max, ValueMap map) {
		cMotd.collect(motd);
		cOnline.collect(online);
		cMax.collect(max);
		if (map == null)
			return;
		if (map.containsKey(QueryKeys.SERVER_ICON)) {
			icon.setImage(map.get(QueryKeys.SERVER_ICON));
		}
		if (map.containsKey(QueryKeys.ONLINE_PLAYERS)) {
			playerVec.clear();
			playerVec.addAll(map.get(QueryKeys.ONLINE_PLAYERS));
			playerList.updateUI();
		}
	}

	private void socket(String ip, int port, boolean sel) {
		if (sel)
			return;
		Query query = new SocketQuery(ip, port);
		CheckResults result = query.connect();
		if (result == CheckResults.CONNECTED) {
			collect(query.getMotd(), query.getPlayers(), query.getMaxPlayers(),
					null);
		}
		increase(33);
	}

	void increase(int i) {
		set(progress.getValue() == 100 ? i : progress.getValue() + i);
		lblProgress.setText("진행률: " + progress.getValue() + "%");
		if (progress.getValue() == 100) {
			showMessageBox("정보 수집이 완료되었습니다!\n정보를 표시합니다.", "완료",
					MessageType.INFO);
			view();
		}
	}

	void set(int i) {
		progress.setValue(i);
		lblProgress.setText("진행률: " + progress.getValue() + "%");
	}

	void view() {
		players.setText("플레이어: " + cOnline.max() + " / " + cMax.max());
		setMotd(cMotd.max());
	}

	void setMotd(String s) {
		SimpleAttributeSet sas = new SimpleAttributeSet();
		boolean[] bis = new boolean[3];
		Arrays.fill(bis, false);
		Color lastColor = Colors.WHITE.getAWTColor();
		int last = 0;
		int index = -1;
		motd.setText("");
		if (s == null) {
			motd.setText("Motd 분석이 불가능합니다.");
			return;
		}
		while ((index = s.indexOf("§", index + 1)) != -1) {
			String code = s.substring(last + 1, last + 2);
			if (code.equals("l"))
				bis[0] = true;
			if (code.equals("o"))
				bis[1] = true;
			if (code.equals("m"))
				bis[2] = true;
			if (code.matches("[0-9a-f]+")) {
				lastColor = Colors.getByCode("§" + code).getAWTColor();
				Arrays.fill(bis, false);
			}
			if (index == 0)
				continue;
			String text = s.substring(last, index).replaceAll(
					"§[0-9a-flom]{1}", "");
			motd.appendText(sas, bis, lastColor, text);
			last = index;
		}
		String code = s.substring(last + 1, last + 2);
		if (code.equals("l"))
			bis[0] = true;
		if (code.equals("o"))
			bis[1] = true;
		if (code.equals("s"))
			bis[2] = true;
		if (code.matches("[0-9a-f]+")) {
			lastColor = Colors.getByCode("§" + code).getAWTColor();
			Arrays.fill(bis, false);
		}
		String text = s.substring(last, s.length()).replaceAll(
				"§[0-9a-flom]{1}", "");
		motd.appendText(sas, bis, lastColor, text);
	}

	void showMessageBox(String msg, String title, MessageType type) {
		JOptionPane.showMessageDialog(this, msg, title, type.i);
	}

	enum MessageType {
		INFO(JOptionPane.INFORMATION_MESSAGE), WARN(JOptionPane.WARNING_MESSAGE), ERROR(
				JOptionPane.ERROR_MESSAGE);
		int i;

		MessageType(int i) {
			this.i = i;
		}
	}
}
