package com.devmribeiro.backbee.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

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

		BackbeeUtil.removeOldBackups(backupTarget);

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
}