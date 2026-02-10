package com.devmribeiro.backbee.interfaceadapter.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import com.devmribeiro.backbee.shared.BackupConfig;

public class PropertiesConfigLoader {
	private final Path configPath;

    public PropertiesConfigLoader(Path configPath) {
        this.configPath = configPath;
    }

    public boolean exists() {
        return Files.exists(configPath);
    }

    public BackupConfig load() {
        try (InputStream in = Files.newInputStream(configPath)) {
            Properties props = new Properties();
            props.load(in);

            List<String> backupSources = Arrays.asList(props.getProperty("backup.sources").split(","));
            List<Path> sources = new ArrayList<Path>();

            for (int i = 0; i < backupSources.size(); i++)
            	sources.add(Path.of(backupSources.get(i).trim()));
            
            Path destination = Path.of(props.getProperty("backup.destination"));
            int max = Integer.parseInt(props.getProperty("backup.max"));

            return new BackupConfig(sources, destination, max);

        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar config", e);
        }
    }

    public void save(BackupConfig config) {
        try {
            Files.createDirectories(configPath.getParent());

            Properties props = new Properties();

            StringBuilder sbBackupSources = new StringBuilder();
            for (int i = 0; i < config.sources().size(); i++)
            	sbBackupSources.append(config.sources().get(i).toString()).append(",");

            props.setProperty("backup.sources", sbBackupSources.substring(0, sbBackupSources.length() - 1).toString());
            props.setProperty("backup.destination", config.destination().toString());
            props.setProperty("backup.max", String.valueOf(config.maxBackups()));

            try (OutputStream out = Files.newOutputStream(configPath)) {
                props.store(out, "Backbee configuration");
            }

        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar config", e);
        }
    }
}