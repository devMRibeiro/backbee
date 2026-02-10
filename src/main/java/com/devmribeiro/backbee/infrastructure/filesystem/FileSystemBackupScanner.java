package com.devmribeiro.backbee.infrastructure.filesystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.devmribeiro.backbee.core.model.BackupInfo;
import com.devmribeiro.backbee.core.service.contract.BackupScanner;

public class FileSystemBackupScanner implements BackupScanner {

	private final Path backupRoot;

    public FileSystemBackupScanner(Path backupRoot) {
        this.backupRoot = backupRoot;
    }

    @Override
    public boolean existsForMonth(YearMonth month) {
        return Files.exists(backupRoot.resolve(month.toString()));
    }

    @Override
    public List<BackupInfo> scanExisting() {

        if (!Files.exists(backupRoot))
        	return Collections.emptyList();

        try {
        	List<Path> paths = Files.list(backupRoot).toList();
        	List<BackupInfo> result = new ArrayList<BackupInfo>();

        	for (int i = 0; i < paths.size(); i++) {
        		if (Files.isDirectory(paths.get(i)))
        			result.add(toBackupInfo(paths.get(i)));
        	}

        	return result;
        	
        } catch (IOException e) {
            throw new RuntimeException("Erro ao listar backups", e);
        }
    }

    private BackupInfo toBackupInfo(Path path) {
        YearMonth month = YearMonth.parse(path.getFileName().toString());

        try {
            LocalDateTime created = LocalDateTime.ofInstant(Files.getLastModifiedTime(path).toInstant(), java.time.ZoneId.systemDefault());
            return new BackupInfo(path, month, created);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}