package me.ranol.serverisalive.gui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class PlayerObject {
	private String nickName, uuid;

	public PlayerObject(String nick, String uuid) {
		this.nickName = nick;
		this.uuid = uuid;
	}

	public String getUUID() {
		return uuid;
	}

	public Icon getPlayerHead() {
		return new ImageIcon("https://crafatar.com/avatars/" + uuid);
	}

	public String getName() {
		return nickName;
	}

	public static void download(String path, String link) {
		FileOutputStream fos = null;
		InputStream is = null;
		boolean fail = false;
		try {
			fos = new FileOutputStream(path);

			URL url = new URL(link);
			URLConnection urlConnection = url.openConnection();
			is = urlConnection.getInputStream();
			byte[] buffer = new byte[1024];
			int readBytes;
			while ((readBytes = is.read(buffer)) != -1) {
				fos.write(buffer, 0, readBytes);
			}
			JOptionPane.showMessageDialog(null, "다운로드 완료", "Download",
					JOptionPane.INFORMATION_MESSAGE);
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, "다운로드 실패: 이 유저는 정품이 아닙니다.",
					"Download", JOptionPane.ERROR_MESSAGE);
			fail = true;
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null,
					"다운로드 실패: 웹 사이트에서 스킨을 요청할 수 없습니다.", "Download",
					JOptionPane.ERROR_MESSAGE);
			fail = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
				if (is != null) {
					is.close();
				}
				if (fail) {
					File f = new File(path);
					if (!f.isDirectory() && f.exists())
						f.delete();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public String toString() {
		return "PlayerObject [" + nickName + ", " + uuid + "]";
	}

}
