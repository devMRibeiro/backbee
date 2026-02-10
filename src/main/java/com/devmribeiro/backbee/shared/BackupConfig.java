package com.devmribeiro.backbee.shared;

import java.nio.file.Path;
import java.util.List;

public record BackupConfig(
        List<Path> sources,
        Path destination,
        int maxBackups
) {}