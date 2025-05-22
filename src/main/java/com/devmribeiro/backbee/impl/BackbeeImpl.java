package com.devmribeiro.backbee.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.YearMonth;

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

		if (removeOldBackup(backupTarget))
			Log.i("Old backup deleted");

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

	private boolean removeOldBackup(File target) {
		File backupDir = target.getParentFile();

		String oldBackupName = "backup_" + YearMonth.now().minusMonths(2).toString().replace("-", "_");

		File[] backups = backupDir.listFiles();

		if (backups == null) return false;

		for (int i = 0; i < backups.length; i++) {
			File dir = backups[i];
			if (dir.getName().equals(oldBackupName)) {
				deleteFolder(dir);
				return true;
			}
		}
		return false;
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