package com.devmribeiro.backbee.model;

import java.time.LocalDateTime;

/**
 * This model represents 'backup' table
 */
public class BackupModel {
	private Integer backupId = null;
	private LocalDateTime backupCreatedDate = null;

	public Integer getBackupId() {
		return backupId;
	}

	public void setBackupId(Integer backupId) {
		this.backupId = backupId;
	}

	public LocalDateTime getBackupCreatedDate() {
		return backupCreatedDate;
	}

	public void setBackupCreatedDate(LocalDateTime backupCreatedDate) {
		this.backupCreatedDate = backupCreatedDate;
	}
}