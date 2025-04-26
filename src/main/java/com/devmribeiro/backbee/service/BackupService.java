package com.devmribeiro.backbee.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.YearMonth;

import com.devmribeiro.backbee.dao.BackupDao;
import com.github.devmribeiro.zenlog.impl.Logger;

public class BackupService {

	private static final Logger log = new Logger(BackupService.class);
	private static final String USER_HOME = System.getProperty("user.home");
	private static final String[] FOLDER_TO_BACKUP = {
		"Desktop", "Documents", "Downloads", "Pictures", "Videos"
	};
	private long count = 0;

	public void backup() {
		long start = System.currentTimeMillis();

		LocalDateTime dateTimeFilter = LocalDateTime.now();

		if (BackupDao.get(dateTimeFilter.toLocalDate()) != null) {
			log.d("Backup already done");
			return;
		}

		File backupTarget = new File("E:/backbee/backups/backup_" + YearMonth.now().toString().replace("-", "_"));
		removeOldBackup(backupTarget);

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

		if (isSuccess)
			BackupDao.insert(dateTimeFilter);

		log.i("EXECUTION TIME 		 -> " + getTimeExec(start, System.currentTimeMillis() - start));
		log.i("TOTAL NUMBER OF FILES -> " + count);
	}
	
	private String getTimeExec(long start, long diffMs) {
		long ms = diffMs - start;
		long seconds = (ms / 1000) % 60;
		long minutes = (ms / 60000) % 60;
		return String.format("%02d:%02d:%03d", minutes, seconds, ms);
	}

	private boolean copy(Path source, Path target) {
		try {
			log.d("---------------------------" + source + "---------------------------");
			Copy copy = new Copy(source, target);
			Files.walkFileTree(source, copy);
			count = copy.getCount();
			log.d("---------------------------------END-PARENT---------------------------------");
        	return true;

        } catch (FileAlreadyExistsException ex) {
            log.e(target + " -> File already exists", ex);
        } catch (IOException ex) {
        	log.e("I/O Error when copying file", ex);
        }
		return false;
	}

//	private boolean copy(File source, File target) {
//		if (source.isDirectory()) {
//
//			if (!target.exists())
//				target.mkdirs();
//
//			log.d("Copying directory: " + source.getAbsolutePath());
//			
//			String files[] = source.list();
//
//			if (files == null) {
//				log.e("Failed to list files from: " + source.getAbsolutePath());
//	            return false;
//	        }
//
//			for (String file : files) {
//				File srcFile = new File(source, file);
//				File destFile = new File(target, file);
//
//				if (!copy(srcFile, destFile))
//					return false;
//			}
//			return true;
//		} else {
//			try {
//				Files.copy(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
//				return true;
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			return false;
//		}
//	}

	private void removeOldBackup(File target) {
		File backupDir = target.getParentFile();

		String oldBackupName = "backup_" + YearMonth.now().minusMonths(3).toString().replace("-", "_");

		File[] backups = backupDir.listFiles();

		if (backups == null) return;

		for (int i = 0; i < backups.length; i++) {
			File dir = backups[i];
			if (dir.getName().equals(oldBackupName))
				deleteFolder(dir);
		}
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

	public static void main(String[] args) {
		new BackupService().backup();
	}
}