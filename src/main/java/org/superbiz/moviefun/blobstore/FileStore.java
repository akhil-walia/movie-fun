package org.superbiz.moviefun.blobstore;

import javassist.bytecode.ByteArray;
import org.apache.tika.Tika;
import org.apache.tika.io.IOUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static java.lang.ClassLoader.getSystemResource;

public class FileStore implements BlobStore {

    @Override
    public void put(Blob blob) throws IOException {

        File targetFile = new File(blob.name);

        targetFile.delete();
        targetFile.getParentFile().mkdirs();
        targetFile.createNewFile();

        try (FileOutputStream outputStream = new FileOutputStream(targetFile)) {
            IOUtils.copy(blob.inputStream, outputStream);
        }
    }

    @Override
    public Optional<Blob> get(String coverPathName) throws IOException {
        File coverFile = new File(coverPathName);

        if (!coverFile.exists()){
            return Optional.empty();
        }

        String contentType = new Tika().detect(coverFile);

        return Optional.of(new Blob(coverPathName, new FileInputStream(coverFile), contentType));
    }

    @Override
    public void deleteAll() {
        File cover = new File("");
    }
}
