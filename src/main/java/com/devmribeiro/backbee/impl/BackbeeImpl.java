package com.devmribeiro.backbee.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Properties;

import com.github.devmribeiro.zenlog.impl.Logger;

public class BackbeeImpl {
	private final Logger log = Logger.getLogger(BackbeeImpl.class);
	private final String USER_HOME = System.getProperty("user.home");
	private final Path BASE_FOLDER = Paths.get(USER_HOME, "backbee");
	private final Path PROPERTIES_FILE = Paths.get(BASE_FOLDER.toString(), "bb.properties");
	private long count = 0;
//	private static final String[] FOLDER_TO_BACKUP = {
//		"Desktop", "Documents", "Downloads", "Pictures", "Videos"
//	};
	private final String[] FOLDER_TO_BACKUP = {
		"Pictures", "Videos"
	};

	private boolean createDirectoryAndFile() {
		try {
			if (!Files.exists(BASE_FOLDER))
				Files.createDirectories(BASE_FOLDER);

			if (!Files.exists(PROPERTIES_FILE))
				Files.createFile(PROPERTIES_FILE);

		} catch (IOException e) {
			log.e("Could not create 'backbee' or 'bb.properties' directory in: " + PROPERTIES_FILE.toString() + 
				  "\nTo work around this error, create 'bb.properties' manually in the path: " + PROPERTIES_FILE.toString() + 
				  "\nMake sure the file has the extension '.properties'.", e);
			return false;
		}
		return true;
	}

	private Boolean hasBackupCurrentMonth() {
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(PROPERTIES_FILE.toFile()));
		} catch (IOException e) {
			log.e("Error loading 'bb.properties' file. Check that the file name or extension is correct.", e);
			return null;
		}

		String lastBkp = props.getProperty("last-backup");

		if (lastBkp == null || lastBkp.isBlank())
			return false;

		return lastBkp.equals(String.valueOf(LocalDate.now()));
	}

	private boolean udpatePropertiesFile() {
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

	public void backup() {
		
		long start = System.currentTimeMillis();

		if (!createDirectoryAndFile())
			return;

		if (hasBackupCurrentMonth() == null || hasBackupCurrentMonth())
			return;
		
		File backupTarget = new File("E:/backbee/backups/TESTEbackup_" + YearMonth.now().toString().replace("-", "_"));

		if (removeOldBackup(backupTarget))
			log.i("Old backup deleted");

		boolean isSuccess = true;
		for (String folder : FOLDER_TO_BACKUP) {
			Path source = Paths.get(USER_HOME + "/" + folder);
			Path target = Paths.get(backupTarget + "/" + folder);

			if (Files.notExists(source)) {
				log.e(source + " -> not exists");
				isSuccess = false;
			}

			if (!copy(source, target)) {
				log.e("Error on while copying: " + source);
				isSuccess = false;
			}
		}

		if (isSuccess) {
			if (!udpatePropertiesFile())
				log.e("Error saving in 'bb.properties'.");

			log.i("EXECUTION TIME -> " + getTimeExec(start, System.currentTimeMillis() - start));
			log.i("TOTAL NUMBER OF FILES -> " + count);
		}
	}

	private String getTimeExec(long start, long diffMs) {
		long seconds = (diffMs / 1000) % 60;
		long minutes = (diffMs / 60000) % 60;
		return String.format("%02d:%02d:%03d", minutes, seconds, diffMs);
	}

	private boolean copy(Path source, Path target) {
		try {
			log.i("---------------------------" + source + "---------------------------");
			Copy copy = new Copy(source, target);
			Files.walkFileTree(source, copy);
			count = copy.getCount();
			log.i("--------------------------------END--------------------------------\n");
        	return true;

        } catch (FileAlreadyExistsException ex) {
            log.e(target + " -> File already exists", ex);
        } catch (IOException ex) {
        	log.e("I/O Error when copying file", ex);
        }
		return false;
	}

	private boolean removeOldBackup(File target) {
		File backupDir = target.getParentFile();

		String oldBackupName = "backup_" + YearMonth.now().minusMonths(3).toString().replace("-", "_");

		File[] backups = backupDir.listFiles();

		if (backups == null) return false;

		for (int i = 0; i < backups.length; i++) {
			File dir = backups[i];
			if (dir.getName().equals(oldBackupName))
				deleteFolder(dir);
		}
		return true;
	}

	private void deleteFolder(File dir) {
		if (dir.isDirectory()) {
			File[] children = dir.listFiles();
			if (children != null)
				for (int i = 0; i < children.length; i++)
					deleteFolder(children[i]);
		}
		dir.delete();
	}
}