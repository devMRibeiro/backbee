package com.devmribeiro.backbee.app;

import java.nio.file.Path;

import com.devmribeiro.backbee.core.policy.RetentionPolicy;
import com.devmribeiro.backbee.core.service.BackupService;
import com.devmribeiro.backbee.core.service.contract.BackupExecutor;
import com.devmribeiro.backbee.core.service.contract.BackupRemover;
import com.devmribeiro.backbee.core.service.contract.BackupScanner;
import com.devmribeiro.backbee.infrastructure.filesystem.DirectoryCopier;
import com.devmribeiro.backbee.infrastructure.filesystem.FileSystemBackupExecutor;
import com.devmribeiro.backbee.infrastructure.filesystem.FileSystemBackupRemover;
import com.devmribeiro.backbee.infrastructure.filesystem.FileSystemBackupScanner;
import com.devmribeiro.backbee.interfaceadapter.cli.BackupCLIInput;
import com.devmribeiro.backbee.interfaceadapter.config.PropertiesConfigLoader;
import com.devmribeiro.backbee.shared.BackupConfig;

public class BackbeeApplication {
	public static void main(String[] args) {
		
		Path configPath = Path.of(System.getProperty("user.home"), ".backbee", "config.properties");

        PropertiesConfigLoader configLoader = new PropertiesConfigLoader(configPath);

        BackupConfig config;

        if (configLoader.exists()) {
            config = configLoader.load();
        } else {
            BackupCLIInput cli = new BackupCLIInput();
            config = cli.load();
            configLoader.save(config);
        }

        // Infra
        DirectoryCopier copier = new DirectoryCopier();

        BackupScanner scanner = new FileSystemBackupScanner(config.destination());

        BackupExecutor executor = new FileSystemBackupExecutor(config.destination(), config.sources(), copier);

        BackupRemover remover = new FileSystemBackupRemover(config.destination());
        
        // Core
        BackupService service = new BackupService(scanner, executor, remover, new RetentionPolicy());

        service.execute(config.maxBackups());

//		new BackbeeImpl().backup();
	}
}