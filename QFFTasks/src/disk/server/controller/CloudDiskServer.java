/*
 * The server that can start with the main method.
 */
package disk.server.controller;

import disk.server.service.ServiceThread;
import disk.server.repository.User;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CloudDiskServer {
    private static final String USERS_INFORMATION_FILEPATH ="src\\disk\\server\\repository\\users.txt";
    // For stopping the server
    private volatile boolean running = true;
    // Keys are the usernames, values are the passwords
    // Must use ConcurrentHashmap
    private ConcurrentHashMap<String, User> userMap;
    // Singleton pattern
    private static final CloudDiskServer server = new CloudDiskServer();

    public static void main(String[] args) throws Exception {
        CloudDiskServer server = CloudDiskServer.server;
        server.userMap = server.loadUsers();
        server.acceptClients();
        server.updateUsers();
    }

    // Singleton pattern
    private CloudDiskServer() {}
    public static CloudDiskServer getInstance() {
        return server;
    }

    public ConcurrentHashMap<String, User> loadUsers() throws Exception {
        ConcurrentHashMap<String, User> result;
        try (FileInputStream fileInputStream =
                     new FileInputStream(CloudDiskServer.USERS_INFORMATION_FILEPATH);
             ObjectInputStream stream = new ObjectInputStream(fileInputStream)) {
            result = (ConcurrentHashMap<String, User>) stream.readObject();
        // If the file is empty
        } catch (EOFException e) {
            result = new ConcurrentHashMap<>();
        }
        return result;
    }

    public void acceptClients() throws IOException {
        ExecutorService pool = Executors.newCachedThreadPool();
        try (ServerSocket serverSocket = new ServerSocket(8189)) {
            // Make sure it can be stopped
            serverSocket.setSoTimeout(2000);

            Thread stopListener = new Thread(new ServerListener(server));
            stopListener.start();

            while (server.running) {
                // Waiting for a client
                try {
                    Socket incoming = serverSocket.accept();
                    pool.submit(new ServiceThread(incoming, userMap));
                    System.out.println("A new client had connected.");
                } catch (SocketTimeoutException e) {
                    // Do nothing
                    // Recheck whether the server should stop
                }
            }
        } finally {
            pool.shutdown();
        }
    }

    // Will be called by class ServerListener
    public void stop() {
        running = false;
    }

    public void updateUsers() throws IOException {
        File file = new File(CloudDiskServer.USERS_INFORMATION_FILEPATH);
        if (!file.delete()) {
            System.out.println("File delete failed.");
            throw new IOException();
        }
        if (!file.createNewFile()) {
            System.out.println("File create failed.");
            throw new IOException();
        }
        try (ObjectOutputStream stream = new ObjectOutputStream(
                new FileOutputStream(file))) {
            stream.writeObject(userMap);
        }
    }

    public ConcurrentHashMap<String, User> getUserMap() {
        return userMap;
    }
}
