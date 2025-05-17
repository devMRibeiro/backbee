package com.devmribeiro.backbee.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Properties;

import com.devmribeiro.backbee.log.Log;

public class BackbeeUtil {
	public static final String USER_HOME = System.getProperty("user.home");
	public static final Path BASE_FOLDER = Paths.get(USER_HOME, "backbee");
	public static final Path PROPERTIES_FILE = Paths.get(BASE_FOLDER.toString(), "bb.properties");

	public static boolean createDirectoryAndFile() {
		try {
			if (!Files.exists(BASE_FOLDER))
				Files.createDirectories(BASE_FOLDER);

			if (!Files.exists(PROPERTIES_FILE))
				Files.createFile(PROPERTIES_FILE);

		} catch (IOException e) {
			Log.e("Could not create 'backbee' or 'bb.properties' directory in: " + PROPERTIES_FILE.toString() + 
				  "\nTo work around this error, create 'bb.properties' manually in the path: " + PROPERTIES_FILE.toString() + 
				  "\nMake sure the file has the extension '.properties'.", e);
			return false;
		}
		return true;
	}

	public static Boolean hasBackupCurrentMonth() {
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(PROPERTIES_FILE.toFile()));
		} catch (IOException e) {
			Log.e("Error loading 'bb.properties' file. Check that the file name or extension is correct.", e);
			return null;
		}

		String lastBkp = props.getProperty("last-backup");

		if (lastBkp == null || lastBkp.isBlank())
			return false;

		return lastBkp.equals(String.valueOf(LocalDate.now()));
	}

	public static boolean udpatePropertiesFile() {
		try {
			Properties props = new Properties();
			props.load(new FileInputStream(PROPERTIES_FILE.toFile()));
			props.setProperty("last-backup", String.valueOf(LocalDate.now()));
			props.store(new FileOutputStream(PROPERTIES_FILE.toFile()), "Do not modify or delete this file. Application may result in failure.");
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static String getTimeExec(long start, long diffMs) {
		long seconds = (diffMs / 1000) % 60;
		long minutes = (diffMs / 60000) % 60;
		return String.format("%02d:%02d:%03d", minutes, seconds, diffMs);
	}
}