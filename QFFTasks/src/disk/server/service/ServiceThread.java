/*
 * Offer service for a client.
 * Each client has one ServiceThread.
 */
package disk.server.service;

import disk.server.repository.User;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceThread implements Runnable {
    private final Socket incoming;
    private ConcurrentHashMap<String, User> userMap;
    private User user;
    //private final Scanner inputFromKeyboard;

    public ServiceThread(Socket incoming, ConcurrentHashMap<String, User> userMap) {
        this.incoming = incoming;
        this.userMap = userMap;
        //inputFromKeyboard = new Scanner(System.in);
    }

    public void run() {
        try (Scanner inputFromClient = new Scanner(incoming.getInputStream());
        PrintWriter outputToClient = new PrintWriter(incoming.getOutputStream(), true)) {
            UserService userService = new UserService(userMap, inputFromClient, outputToClient);
            user = userService.logIn();
            if (user != null) {
                FileService fileService = new FileService(incoming, user,
                        inputFromClient, outputToClient);
                fileService.perform();
            }
            outputToClient.println("Goodbye.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}