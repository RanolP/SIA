package me.ranol.serverisalive;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

public class FileUtils {
	public static boolean delete(File folder) {
		if (!folder.isDirectory())
			return folder.delete();
		File[] files = folder.listFiles();
		boolean b = true;
		for (File f : files) {
			b = delete(f) && b;
		}
		return b && folder.delete();
	}

	public static List<String> readAll(File file) {
		List<String> data = new ArrayList<>();
		if (file.isDirectory() || !file.canRead() || !file.exists()
				|| file.isHidden())
			return data;
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String read = null;
			while ((read = br.readLine()) != null)
				data.add(read);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}

	public static void saveAll(File file, List<String> data) {
		if (file.isDirectory() || !file.canRead() || !file.exists()
				|| file.isHidden())
			return;
		try (BufferedWriter br = new BufferedWriter(new FileWriter(file))) {
			StringBuilder b = new StringBuilder();
			Iterator<String> it = data.iterator();
			while (it.hasNext())
				b.append(it.next());
			br.append(b.toString());
		} catch (ConcurrentModificationException e) {
			System.err.println("#�α� ���� ����#");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
