package me.ranol.serverisalive;

import me.ranol.serverisalive.checker.Query;
import me.ranol.serverisalive.checker.CheckResults;
import me.ranol.serverisalive.checker.ProtocolQuery;
import me.ranol.serverisalive.checker.SocketQuery;

public class SIA {
	public static void main(String[] args) throws Exception {
		Options.set(Options.TIMEOUT, 3000);
		protocol("127.0.0.1", 8080);
	}

	static void socket(String ip, int port) {
		Query query = new SocketQuery(ip, port);
		def(query);
	}

	static void def(Query query) {
		System.out.println(query.getIPAddress() + ":" + query.getPort()
				+ " 에 연결 요청을 보냅니다. / " + query.getClass().getSimpleName());
		CheckResults result = query.connect();
		System.out.println("쿼리 연결 여부: " + query.isConnected());
		System.out.println("쿼리 연결 시간: " + query.getCheckingTime());
		System.out.println("쿼리 반환 값: " + result);
		System.out.println("Motd: \'" + query.getMotd() + "\'");
		System.out.println("플레이어: " + query.getPlayers() + " / "
				+ query.getMaxPlayers());
	}

	static void protocol(String ip, int port) {
		Query query = new ProtocolQuery(ip, port);
		def(query);
		System.out.println("게임 ID: " + query.get(ProtocolQuery.GAME_ID));
		System.out.println("게임 타입: " + query.get(ProtocolQuery.GAME_TYPE));
		System.out.println("서버 아이피: " + query.get(ProtocolQuery.SERVER_IP));
		System.out.println("서버 포트: " + query.get(ProtocolQuery.SERVER_PORT));
		System.out.println("버전: " + query.get(ProtocolQuery.VERSION));
		System.out.println("맵 이름: " + query.get(ProtocolQuery.MAP_NAME));
		System.out.println("사용 버킷: " + query.get(ProtocolQuery.BUKKIT_NAME));
		System.out.println("플러그인들: " + query.get(ProtocolQuery.PLUGINS));
	}
}
