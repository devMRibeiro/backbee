package com.devmribeiro.backbee.core.service.contract;

import java.time.YearMonth;
import java.util.List;

import com.devmribeiro.backbee.core.model.BackupInfo;

public interface BackupScanner {
	List<BackupInfo> scanExisting();
    boolean existsForMonth(YearMonth month);
}