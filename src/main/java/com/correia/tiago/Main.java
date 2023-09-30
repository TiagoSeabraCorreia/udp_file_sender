package com.correia.tiago;

import com.correia.tiago.data.FileSender;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        FileSender server = new FileSender();
        server.waitForRequest();
        server.waitForChunkSize();
        server.sendResponse();
        server.sendFile();
    }
}