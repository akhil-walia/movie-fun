package org.superbiz.moviefun.blobstore;

import java.io.InputStream;

public class Blob {
    public  String name;
    public  InputStream inputStream;
    public  String contentType;

    public Blob(String name, InputStream inputStream, String contentType) {
        this.name = name;
        this.inputStream = inputStream;
        this.contentType = contentType;
    }

}