package com.devmribeiro.backbee.interfaceadapter.cli;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.devmribeiro.backbee.shared.BackupConfig;

public class BackupCLIInput {

    private final Scanner scanner = new Scanner(System.in);

    public BackupConfig createConfig() {
        return readConfig();
    }

    public BackupConfig updateConfig(BackupConfig existing) {

        System.out.println("1 - Alterar pastas de origem");
        System.out.println("2 - Alterar quantidade de backups");
        System.out.println("3 - Alterar ambos");

        String option = scanner.nextLine();

        List<Path> sources = existing.sources();
        int maxBackups = existing.maxBackups();

        if (option.equals("1") || option.equals("3"))
            sources = readSources();

        if (option.equals("2") || option.equals("3"))
            maxBackups = readMaxBackups();

        return new BackupConfig(sources, existing.destination(), maxBackups);
    }
    
    private BackupConfig readConfig() {

        List<Path> sources = readSources();
        Path destination = readDestination();
        int maxBackups = readMaxBackups();

        return new BackupConfig(sources, destination, maxBackups);
    }

    private List<Path> readSources() {

        System.out.print("Caminho das pastas de origem (separadas por vírgula): ");
        String input = scanner.nextLine();

        String[] folders = input.split(",");

        List<Path> sources = new ArrayList<Path>();

        for (String folder : folders) {
            Path path = Path.of(folder.trim());

            if (!Files.exists(path))
                System.out.println("Aviso: caminho não existe -> " + path);

            sources.add(path);
        }
        return sources;
    }

    private Path readDestination() {

        System.out.print("Caminho da pasta de destino do backup: ");
        Path original = Path.of(scanner.nextLine().trim());

        Path renamed = original.resolveSibling(original.getFileName().toString() + "-backbee");

        try {
            if (Files.exists(original) && !Files.exists(renamed))
                Files.move(original, renamed);

        } catch (IOException e) {
            System.out.println("Erro ao renomear pasta: " + e.getMessage());
        }

        return renamed;
    }

    private int readMaxBackups() {

        while (true) {
            System.out.print("Quantidade máxima de backups: ");
            String input = scanner.nextLine().trim();

            try {
                int value = Integer.parseInt(input);

                if (value <= 0) {
                    System.out.println("Deve ser maior que zero.");
                    continue;
                }

                return value;

            } catch (NumberFormatException e) {
                System.out.println("Número inválido.");
            }
        }
    }
}