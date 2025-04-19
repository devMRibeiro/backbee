package com.devmribeiro.backbee.service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;

import com.devmribeiro.backbee.dao.BackupDao;
import com.devmribeiro.backbee.model.BackupModel;

public class BackupService {

	private LocalDateTime dateTimeFilter = null;

	public void backup() {

		dateTimeFilter = LocalDateTime.now();

		BackupModel backup;

		backup = BackupDao.get(dateTimeFilter.toLocalDate());

		if (backup != null) {
			System.out.println("Backup already done");
			return;
		}

		File source = new File("C:/Users/Michael Ribeiro/Teste1");
		File destination = new File("C:/Users/Michael Ribeiro/Teste1 - Backup");

		copy(source, destination);
		
		if (copy(source, destination))
			BackupDao.insert(dateTimeFilter);
	}

	private static boolean copy(File source, File target) {

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

	public static void main(String[] args) {
		new BackupService().backup();
	}
}