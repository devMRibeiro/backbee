package com.devmribeiro.backbee.infrastructure.filesystem;

import java.nio.file.Path;
import java.time.YearMonth;
import java.util.List;

import com.devmribeiro.backbee.core.service.contract.BackupExecutor;
import com.devmribeiro.backbee.log.Log;

public class FileSystemBackupExecutor implements BackupExecutor {

    private final Path backupRoot;
    private final List<Path> sources;
    private final DirectoryCopier copier;

    public FileSystemBackupExecutor(Path backupRoot, List<Path> sources, DirectoryCopier copier) {
        this.backupRoot = backupRoot;
        this.sources = sources;
        this.copier = copier;
    }

    @Override
    public void executeBackup(YearMonth month) {
    	Log.i("Iniciando backup do mês " + month);
        Path target = backupRoot.resolve(month.toString());
        copier.copy(sources, target);
        Log.i("Backup finalizado: " + target);
    }
}