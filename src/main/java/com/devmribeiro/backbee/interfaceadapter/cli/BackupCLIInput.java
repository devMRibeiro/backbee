package com.devmribeiro.backbee.interfaceadapter.cli;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import com.devmribeiro.backbee.shared.BackupConfig;

public class BackupCLIInput {
	private final Scanner scanner = new Scanner(System.in);

    public BackupConfig load() {
        System.out.print("Pastas de origem (Exemplo: C:\\Users\\User Name\\Documents): ");

        List<String> folders = Arrays.asList(scanner.nextLine().split(","));
        List<Path> sources = new ArrayList<Path>();

        for (int i = 0; i < folders.size(); i++) {
        	String folder = folders.get(i);
        	sources.add(Path.of(folder.trim()));
        }

        System.out.print("Pasta de destino do backup: ");
        Path destination = Path.of(scanner.nextLine().trim());

        System.out.print("Quantidade máxima de backups: ");
        int maxBackups = Integer.parseInt(scanner.nextLine().trim());

        return new BackupConfig(sources, destination, maxBackups);
    }
}