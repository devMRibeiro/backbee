package com.devmribeiro.backbee.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import com.devmribeiro.backbee.log.Log;
import com.devmribeiro.backbee.util.BackbeeUtil;

public class BackbeeImpl {
	
	private long count = 0;
	private static final String[] FOLDER_TO_BACKUP = {
		"Desktop", "Documents", "Downloads", "Pictures", "Videos"
	};
//	private final String[] FOLDER_TO_BACKUP = { // test
//		"Pictures", "Videos"
//	};

	public void backup() {
		
		long start = System.currentTimeMillis();

		if (!BackbeeUtil.createDirectoryAndFile())
			return;

		if (BackbeeUtil.hasBackupCurrentMonth() == null || BackbeeUtil.hasBackupCurrentMonth())
			return;
		
		File backupTarget = new File("E:/backbee/backups/backup-" + LocalDate.now());

		removeOldBackups(backupTarget);

		boolean isSuccess = true;

		for (String folder : FOLDER_TO_BACKUP) {
			Path source = Paths.get(BackbeeUtil.USER_HOME + "/" + folder);
			Path target = Paths.get(backupTarget + "/" + folder);

			if (Files.notExists(source)) {
				Log.e(source + " -> not exists");
				isSuccess = false;
			}

			if (!copy(source, target)) {
				Log.e("Error on while copying: " + source);
				isSuccess = false;
			}
		}

		if (isSuccess) {
			if (!BackbeeUtil.udpatePropertiesFile())
				Log.e("Error saving in 'bb.properties'.");

			Log.i("EXECUTION TIME -> " + BackbeeUtil.getTimeExec(start, System.currentTimeMillis() - start));
			Log.i("TOTAL NUMBER OF FILES -> " + count);
		}
	}

	private boolean copy(Path source, Path target) {
		try {
			Log.i("---------------------------" + source + "---------------------------");
			Copy copy = new Copy(source, target);
			Files.walkFileTree(source, copy);
			count += copy.getCount();
			Log.i("--------------------------------END--------------------------------\n");
        	return true;

        } catch (FileAlreadyExistsException ex) {
            Log.e(target + " -> File already exists", ex);
        } catch (IOException ex) {
        	Log.e("I/O Error when copying file", ex);
        }
		return false;
	}

	private void removeOldBackups(File target) {
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

	        if (validBackups.size() <= 3) break;

	        File oldest = validBackups.get(0);
	        LocalDate oldestDate = getDateByNameFile(oldest.getName());

	        for (int i = 1; i < validBackups.size(); i++) {
	            File current = validBackups.get(i);
	            LocalDate currentDate = getDateByNameFile(current.getName());

	            if (currentDate != null && currentDate.isBefore(oldestDate)) {
	                oldest = current;
	                oldestDate = currentDate;
	            }
	        }

	        deleteFolder(oldest);
	        Log.i("Old backup deleted");

	        backups = backupDir.listFiles();
	        if (backups == null) break;
	    }
	}

	private LocalDate getDateByNameFile(String name) {
	    try {
	        String[] parts = name.split("backup-");
	        if (parts.length == 2) {
	            return LocalDate.parse(parts[1], DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	        }
	    } catch (DateTimeParseException e) {
	        Log.e("Failed to parse backup folder date: " + name, e);
	    }
	    return null;
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