package com.devmribeiro.backbee.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;

import com.db.utility.ConnFactory;
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

		if (copyFolder(source, destination))
			BackupDao.insert(dateTimeFilter);
	}

	private static boolean copyFolder(File source, File destination) {

		if (source.isDirectory()) {

			if (!destination.exists())
				destination.mkdirs();

			String files[] = source.list();

			for (String file : files) {
				File srcFile = new File(source, file);
				File destFile = new File(destination, file);

				if (!copyFolder(srcFile, destFile))
					return false;
			}

			return true;

		} else {
			
			InputStream in = null;
			OutputStream out = null;

			try {
//				Files.copy(source.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);

				in = new FileInputStream(source);
				out = new FileOutputStream(destination);

				byte[] buffer = new byte[1024];

				int length;
				while ((length = in.read(buffer)) > 0)
					out.write(buffer, 0, length);

				return true;

			} catch (Exception e) {
				e.printStackTrace();

			} finally {
				ConnFactory.close(in, out);
			}
			return false;
		}
	}
	
	public static void main(String[] args) {
		new BackupService().backup();
	}
}