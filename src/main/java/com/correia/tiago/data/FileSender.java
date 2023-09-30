package com.correia.tiago.data;

import javax.xml.crypto.Data;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class FileSender {
    DatagramSocket sender_socket;
    DatagramPacket sender_packet;

    DatagramPacket receiver_packet;

    DatagramSocket receiver_socket;
    Integer chunk;
    Integer nChunks;

    File file;

    BufferedReader br;

    InetAddress ip;

    Integer port;

    public FileSender() throws SocketException {
        receiver_packet = new DatagramPacket(new byte[256], 256);
        receiver_socket = new DatagramSocket(2000);
    }


    public void waitForRequest() throws IOException {
        System.out.println("Waiting for client");
        this.receiver_socket.receive(receiver_packet);
        this.file = new File("./files/"+ new String(receiver_packet.getData(), 0,receiver_packet.getLength()));
        System.out.println("Received a request for the file " + file);
    }
    public void waitForChunkSize() throws IOException {
        this.receiver_packet = new DatagramPacket(new byte[256], 256);
        this.receiver_socket.receive(receiver_packet);
        String chunkSize = new String(receiver_packet.getData(), 0, receiver_packet.getLength());
        System.out.println("Chunk size received -> " + chunkSize);
        this.chunk = Integer.valueOf(chunkSize);
    }

    public void sendResponse() throws IOException {
        System.out.println(file.isFile());
        System.out.println(file.length());
        System.out.println(chunk);
        System.out.println((file.length() / chunk) +  1);
        this.nChunks = Math.toIntExact((file.length() / chunk) + 1);
        receiver_packet.setData(String.valueOf((file.length() / chunk) +  1).getBytes());
        receiver_packet.setLength(receiver_packet.getData().length);
        receiver_socket.send(receiver_packet);
    }


    public void sendFile() throws IOException {
        InputStream os = new FileInputStream(file);
        byte[] image = os.readAllBytes();
        for (Integer i = 1; i < nChunks; i++) {
            byte[] imageChunk = Arrays.copyOfRange(image, (i-1)*chunk, chunk * i);
            System.out.println(Arrays.toString(imageChunk));
            receiver_packet.setData(imageChunk);
            receiver_packet.setLength(chunk);
            receiver_socket.send(receiver_packet);
        }
    }
}
