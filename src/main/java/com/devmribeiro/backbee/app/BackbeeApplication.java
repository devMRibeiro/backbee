package com.devmribeiro.backbee.app;

import java.nio.file.Path;
import java.util.Scanner;

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

        System.out.println("Diretórios: " + configLoader.load().sources());
        System.out.println("Quantidade: " + configLoader.load().maxBackups());

        if (!configLoader.exists()) {
            config = new BackupCLIInput().createConfig();
            configLoader.save(config);
        } else {

        	
            System.out.println("Pressione ENTER em até 15 segundos para alterar configuração...");
            
            if (waitForKeyPress(15)) {
                config = new BackupCLIInput().updateConfig(configLoader.load());
                configLoader.save(config);
            } else {
                config = configLoader.load();
            }
        }

        startBackup(config);
	}
	
	@SuppressWarnings("resource")
	private static boolean waitForKeyPress(int timeoutSeconds) {

	    long end = System.currentTimeMillis() + timeoutSeconds * 1000;

	    try {
	        while (true) {

	            long remaining = (end - System.currentTimeMillis()) / 1000;

	            if (remaining < 0) {
	                System.out.print("\r"); // limpa linha final
	                System.out.flush();
	                return false;
	            }

	            System.out.print("\rAlterar config? ENTER (" + remaining + "s) ");
	            System.out.flush();

	            if (System.in.available() > 0) {
	                new Scanner(System.in).nextLine();
	                System.out.print("\r");
	                System.out.flush();
	                return true;
	            }

	            Thread.sleep(200);
	        }

	    } catch (Exception e) {
	        return false;
	    }
	}



	private static void startBackup(BackupConfig config) {
		// Infra
        DirectoryCopier copier = new DirectoryCopier();

        BackupScanner scanner = new FileSystemBackupScanner(config.destination());

        BackupExecutor executor = new FileSystemBackupExecutor(config.destination(), config.sources(), copier);

        BackupRemover remover = new FileSystemBackupRemover(config.destination());
        
        // Core
        BackupService service = new BackupService(scanner, executor, remover, new RetentionPolicy());

        service.execute(config.maxBackups());
	}
}