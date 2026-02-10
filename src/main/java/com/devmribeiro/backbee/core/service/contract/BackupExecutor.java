package com.devmribeiro.backbee.core.service.contract;

import java.time.YearMonth;

public interface BackupExecutor {
	void executeBackup(YearMonth month);
}