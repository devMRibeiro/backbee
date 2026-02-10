package com.devmribeiro.backbee.core.service;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import com.devmribeiro.backbee.core.model.BackupInfo;
import com.devmribeiro.backbee.core.policy.RetentionPolicy;
import com.devmribeiro.backbee.core.service.contract.BackupExecutor;
import com.devmribeiro.backbee.core.service.contract.BackupRemover;
import com.devmribeiro.backbee.core.service.contract.BackupScanner;

public class BackupService {
	private final BackupScanner scanner;
    private final BackupExecutor executor;
    private final BackupRemover remover;
    private final RetentionPolicy retentionPolicy;

    public BackupService(BackupScanner scanner, BackupExecutor executor, BackupRemover remover, RetentionPolicy retentionPolicy) {
        this.scanner = scanner;
        this.executor = executor;
        this.remover = remover;
        this.retentionPolicy = retentionPolicy;
    }

    public void execute(int maxBackups) {
        YearMonth currentMonth = YearMonth.now();

        if (!scanner.existsForMonth(currentMonth))
            executor.executeBackup(currentMonth);

        List<BackupInfo> existing = scanner.scanExisting();
        List<Integer> toRemoveIndexes = retentionPolicy.indexesToRemove(existing, maxBackups);

        if (!toRemoveIndexes.isEmpty()) {
        	
        	List<BackupInfo> toRemove = new ArrayList<BackupInfo>();
        	for (int i = 0; i < toRemoveIndexes.size(); i++)
        		toRemove.add(existing.get(i));

            remover.remove(toRemove);
        }
    }
}