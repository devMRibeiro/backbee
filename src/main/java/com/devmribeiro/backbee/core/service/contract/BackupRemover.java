package com.devmribeiro.backbee.core.service.contract;

import java.util.List;

import com.devmribeiro.backbee.core.model.BackupInfo;

public interface BackupRemover {
	void remove(List<BackupInfo> backups);
}