package com.devmribeiro.backbee.core.model;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.YearMonth;

public record BackupInfo(
		Path backupPath,
		YearMonth reference,
		LocalDateTime createdAt
) { }