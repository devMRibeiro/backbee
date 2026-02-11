package com.devmribeiro.backbee.interfaceadapter.cli;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import com.devmribeiro.backbee.shared.BackupConfig;

public class BackupCLIInput {
	private final Scanner scanner = new Scanner(System.in);

    public BackupConfig load() {
        System.out.print("Pastas de origem (Exemplo: C:\\Users\\User Name\\Desktop,C:\\Users\\User Name\\Documents, etc): ");

        List<String> folders = Arrays.asList(scanner.nextLine().split(","));
        List<Path> sources = new ArrayList<Path>();

        for (int i = 0; i < folders.size(); i++) {
        	String folder = folders.get(i);
        	sources.add(Path.of(folder.trim()));
        }

        System.out.print("Pasta de destino do backup (Exemplo: C:\\Users\\User Name\\Desktop\\Backup): ");

        Path original = Path.of(scanner.nextLine().trim());

        // Adiciona sufixo para pasta de destino. Evita que a pasta de backup seja incluída no backup
        Path novoCaminho = original.resolveSibling(original.getFileName().toString() + "-backbee");

        try {
            Files.move(original, novoCaminho);
        } catch (IOException e) {
            System.out.println("Erro ao renomear a pasta: " + e.getMessage());
        }

        System.out.print("Quantidade de backup: ");

        int maxBackups = Integer.parseInt(scanner.nextLine().trim());

        return new BackupConfig(sources, novoCaminho, maxBackups);
    }
}