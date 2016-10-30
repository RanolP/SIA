package me.ranol.serverisalive;

import java.util.Scanner;

import me.ranol.serverisalive.checker.PingQuery;
import me.ranol.serverisalive.checker.Query;
import me.ranol.serverisalive.checker.CheckResults;
import me.ranol.serverisalive.checker.ProtocolQuery;
import me.ranol.serverisalive.checker.SocketQuery;

public class SIA {
	public static void main(String[] args) throws Exception {
		Scanner scn = new Scanner(System.in);
		Options.set(Options.TIMEOUT, 3000);
		new SIAFrame();
		while (true) {
			System.out.print("������ �Է�[stop���� ����]: ");
			String ip = scn.nextLine();
			if (ip.equals("stop"))
				break;
			boolean val = true;
			while (val) {
				System.out.print("���� �ڵ�[s/pr/pi]: ");
				String c = scn.nextLine();
				if (c.startsWith("s")) {
					socket(ip, 25565);
					val = false;
				} else if (c.startsWith("pr")) {
					protocol(ip, 25565);
					val = false;
				} else if (c.startsWith("pi")) {
					ping(ip, 25565);
					val = false;
				}
			}
		}
		scn.close();
	}

	static void socket(String ip, int port) {
		Query query = new SocketQuery(ip, port);
		def(query);
	}

	static void ping(String ip, int port) {
		Query query = new PingQuery(ip, port);
		def(query);
	}

	static void def(Query query) {
		System.out.println(query.getIPAddress() + ":" + query.getPort()
				+ " �� ���� ��û�� �����ϴ�. / " + query.getClass().getSimpleName());
		CheckResults result = query.connect();
		System.out.println("���� ���� ����: " + query.isConnected());
		System.out.println("���� ���� �ð�: " + query.getCheckingTime());
		System.out.println("���� ��ȯ ��: " + result);
		System.out.println("Motd: \'" + query.getMotd() + "\'");
		System.out.println("�÷��̾�: " + query.getPlayers() + " / "
				+ query.getMaxPlayers());
	}

	static void protocol(String ip, int port) {
		Query query = new ProtocolQuery(ip, port);
		def(query);
		System.out.println("���� ID: " + query.get(ProtocolQuery.GAME_ID));
		System.out.println("���� Ÿ��: " + query.get(ProtocolQuery.GAME_TYPE));
		System.out.println("���� ������: " + query.get(ProtocolQuery.SERVER_IP));
		System.out.println("���� ��Ʈ: " + query.get(ProtocolQuery.SERVER_PORT));
		System.out.println("����: " + query.get(ProtocolQuery.VERSION));
		System.out.println("�� �̸�: " + query.get(ProtocolQuery.MAP_NAME));
		System.out.println("��� ��Ŷ: " + query.get(ProtocolQuery.BUKKIT_NAME));
		System.out.println("�÷����ε�: " + query.get(ProtocolQuery.PLUGINS));
		System.out.println("������: " + query.get(ProtocolQuery.CURRENT_PLAYERS));
	}
}
