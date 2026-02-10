package com.devmribeiro.backbee.infrastructure.filesystem;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

import com.devmribeiro.backbee.core.model.BackupInfo;
import com.devmribeiro.backbee.core.service.contract.BackupRemover;
import com.devmribeiro.backbee.log.Log;

public class FileSystemBackupRemover implements BackupRemover {

	private final Path backupRoot;

	public FileSystemBackupRemover(Path backupRoot) {
		this.backupRoot = backupRoot;
	}

	@Override
	public void remove(List<BackupInfo> backups) {
		for (BackupInfo backup : backups) {
            Path dir = backupRoot.resolve(backup.reference().toString());
            deleteRecursive(dir);
        }
	}

	private void deleteRecursive(Path path) {
	    if (!Files.exists(path))
	        return;

	    Log.i("Deletando arquivos em: " + path.toString());

	    try {
	        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {

	            @Override
	            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
	                Files.deleteIfExists(file);
	                return FileVisitResult.CONTINUE;
	            }

	            @Override
	            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
	                Files.deleteIfExists(dir);
	                return FileVisitResult.CONTINUE;
	            }
	        });
	    } catch (IOException e) {
	    	Log.e("Erro ao remover backup", e);
	        throw new RuntimeException("Erro ao remover backup", e);
	    }
	}
}