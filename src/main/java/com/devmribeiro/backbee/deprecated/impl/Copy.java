package com.devmribeiro.backbee.impl;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import com.devmribeiro.backbee.log.Log;

public class Copy extends SimpleFileVisitor<Path> {

	private long count = 0;

    private Path sourceDir;
    private Path targetDir;

    public Copy(Path sourceDir, Path targetDir) {
        this.sourceDir = sourceDir;
        this.targetDir = targetDir;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) {
        try {
            Path targetFile = targetDir.resolve(sourceDir.relativize(file));
            Files.copy(file, targetFile, LinkOption.NOFOLLOW_LINKS);
            Log.i("copying -> " + file);
            count += 1;
        } catch (IOException ex) {
            Log.e("Error copying" + ex);
        }
        return FileVisitResult.CONTINUE;
    }

    // Prevents an AccessDeniedException
    @Override
    public FileVisitResult visitFileFailed(Path file, IOException e) throws IOException {
    	return FileVisitResult.SKIP_SUBTREE;
	}

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attributes) {
        try {
            Path newDir = targetDir.resolve(sourceDir.relativize(dir));
            Files.createDirectories(newDir);
        } catch (IOException ex) {
            Log.e("Error creating directory" + ex);
        }
        return FileVisitResult.CONTINUE;
    }

    public long getCount() {
    	return count;
    }
}