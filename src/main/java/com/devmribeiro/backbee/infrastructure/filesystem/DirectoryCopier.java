package com.devmribeiro.backbee.infrastructure.filesystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class DirectoryCopier {
	public void copy(List<Path> sources, Path destination) {
        try {
            Files.createDirectories(destination);

            for (Path source : sources) {
                Path target = destination.resolve(source.getFileName());
                copyRecursive(source, target);
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao copiar diretórios", e);
        }
    }

    private void copyRecursive(Path source, Path target) throws IOException {
    	try {

    		List<Path> paths = Files.walk(source).toList();

    		for (int i = 0; i < paths.size(); i++) {
    			Path path = paths.get(i);
                Path relative = source.relativize(path);
                Path dest = target.resolve(relative);

                if (Files.isDirectory(path))
                    Files.createDirectories(dest);
                else
                    Files.copy(path, dest, StandardCopyOption.REPLACE_EXISTING);
    		}
    	} catch (IOException e) {
    		throw new RuntimeException(e);
    	}
    }
}