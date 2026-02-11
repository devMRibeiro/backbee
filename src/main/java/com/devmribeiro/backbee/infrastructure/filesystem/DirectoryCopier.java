package com.devmribeiro.backbee.infrastructure.filesystem;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

import com.devmribeiro.backbee.log.Log;

public class DirectoryCopier {
	public void copy(List<Path> sources, Path destination) {
        try {
            Files.createDirectories(destination);

            for (Path source : sources) {
                Path target = destination.resolve(source.getFileName());

                Log.i("Diretório -> " + source.getFileName().toString());

                copyRecursive(source, target);
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao copiar diretórios", e);
        }
    }

    private void copyRecursive(Path source, Path target) throws IOException {
    	Files.walkFileTree(source, new SimpleFileVisitor<>() {

    	    @Override
    	    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {

    	        String nome = dir.getFileName().toString();

    	        if (nome.contains("-backbee"))
    	            return FileVisitResult.SKIP_SUBTREE;

    	        Path relative = source.relativize(dir);
    	        Path destDir = target.resolve(relative);
    	        Files.createDirectories(destDir);

    	        return FileVisitResult.CONTINUE;
    	    }

    	    @Override
    	    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

    	        Path relative = source.relativize(file);
    	        Path destFile = target.resolve(relative);

    	        Log.i("Copiando -> " + relative);
    	        Files.copy(file, destFile, StandardCopyOption.REPLACE_EXISTING);

    	        return FileVisitResult.CONTINUE;
    	    }
    	});
    }
}