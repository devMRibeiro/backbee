package com.devmribeiro.backbee.service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.YearMonth;

import com.devmribeiro.backbee.dao.BackupDao;

public class BackupService {

	public void backup() {

		LocalDateTime dateTimeFilter = LocalDateTime.now();

		if (BackupDao.get(dateTimeFilter.toLocalDate()) != null) {
			System.out.println("Backup already done");
			return;
		}

		File source = new File("C:/Users/Michael Ribeiro/Teste1");
		File target = new File("C:/Users/Michael Ribeiro/Backups/backup_" + YearMonth.now().toString().replace("-", "_"));

		removeOldBackup(target);
		
		if (copy(source, target))
			BackupDao.insert(dateTimeFilter);
	}

	private boolean copy(File source, File target) {

		if (source.isDirectory()) {

			if (!target.exists())
				target.mkdirs();

			String files[] = source.list();

			for (String file : files) {
				File srcFile = new File(source, file);
				File destFile = new File(target, file);

				if (!copy(srcFile, destFile))
					return false;
			}
			return true;
		} else {
			try {
				Files.copy(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}
	}

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