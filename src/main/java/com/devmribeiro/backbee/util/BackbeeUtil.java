package com.devmribeiro.backbee.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

import com.devmribeiro.backbee.log.Log;

public class BackbeeUtil {
	public static final String USER_HOME = System.getProperty("user.home");
	public static final Path BASE_FOLDER = Paths.get(USER_HOME, "backbee");
	public static final Path PROPERTIES_FILE = Paths.get(BASE_FOLDER.toString(), "bb.properties");
	public static final String KEY_LAST_BACKUP = "last-backup";

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

		String lastBackup = props.getProperty(KEY_LAST_BACKUP);

		if (lastBackup == null || lastBackup.isBlank())
			return false;

		LocalDate lastBackupDate = LocalDate.parse(lastBackup, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

		LocalDate now = LocalDate.now();

		if (lastBackupDate.getYear() != now.getYear() || lastBackupDate.getMonthValue() != now.getMonthValue())
			return false;

		if (now.getDayOfMonth() <= 15 && lastBackupDate.isBefore(now.withDayOfMonth(1)))
			return false;

		if (now.getDayOfMonth() > 15 && lastBackupDate.getDayOfMonth() <= 15)
			return false;

		return true;
	}

	public static boolean udpatePropertiesFile() {
		try {
			Properties props = new Properties();
			props.load(new FileInputStream(PROPERTIES_FILE.toFile()));

			props.setProperty(KEY_LAST_BACKUP, LocalDate.now().toString());

			props.store(new FileOutputStream(PROPERTIES_FILE.toString()), "Do not modify or delete this file. Application may result in failure.");
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static void removeOldBackups(File target) {
	    File backupDir = target.getParentFile();
	    File[] backups = backupDir.listFiles();

	    if (backups == null) return;

	    while (true) {
	        List<File> validBackups = new ArrayList<File>();

	        for (int i = 0; i < backups.length; i++) {

	        	File dir = backups[i];

	            if (dir.isDirectory() && dir.getName().startsWith("backup-")) {
	                LocalDate date = getDateByNameFile(dir.getName());

	                if (date != null)
	                    validBackups.add(dir);
	            }
	        }

	        // Order from oldest to newest
	        validBackups.sort(Comparator.comparing(f -> getDateByNameFile(f.getName())));

	        while (validBackups.size() > 3) {
	            File oldest = validBackups.remove(0);
	            deleteFolder(oldest);
	            Log.i("Old backup deleted: " + oldest.getName());
	        }
	    }
	}
	
	public static void main(String[] args) {
		List<Integer> list = new ArrayList<Integer>();
		
		list.add(1);
		list.add(2);
		list.add(3);
		list.add(4);
	}

	private static void deleteFolder(File dir) {
		if (dir.isDirectory()) {
			File[] children = dir.listFiles();
			if (children != null)
				for (int i = 0; i < children.length; i++)
					deleteFolder(children[i]);
		}
		dir.delete();
	}

	private static LocalDate getDateByNameFile(String filename) {
	    try {
	        String[] parts = filename.split("backup-");
	        if (parts.length == 2)
	            return LocalDate.parse(parts[1], DateTimeFormatter.ofPattern("yyyy-MM-dd"));

	    } catch (DateTimeParseException e) {
	        Log.e("Failed to parse backup folder date: " + filename, e);
	    }
	    return null;
	}
	
	public static String getTimeExec(long start, long diffMs) {
		long seconds = (diffMs / 1000) % 60;
		long minutes = (diffMs / 60000) % 60;
		return minutes != 0 ? String.format("%02dm %02ds", minutes, seconds) : String.format("%02ds", seconds);
	}
}