package me.ranol.serverisalive.gui;

public class PlayerObject {
	private String nickName, uuid;

	public PlayerObject(String nick, String uuid) {
		this.nickName = nick;
		this.uuid = uuid;
	}

	public String getUUID() {
		return uuid;
	}

	public String getName() {
		return nickName;
	}

	@Override
	public String toString() {
		return "PlayerObject [" + nickName + ", " + uuid + "]";
	}

	public String getUrl() {
		return "https://crafatar.com/avatars/" + uuid;
	}

}
