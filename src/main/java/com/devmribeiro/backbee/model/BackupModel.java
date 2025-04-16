package com.devmribeiro.backbee.model;

import java.time.LocalDateTime;

public record BackupModel(Integer backupId, LocalDateTime backupCreatedDate) { }