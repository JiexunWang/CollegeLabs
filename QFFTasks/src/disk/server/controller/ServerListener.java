/*
 * Waiting for the input "quit" to stop the server.
 */
package disk.server.controller;

import disk.server.repository.User;

import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class ServerListener implements Runnable {
    CloudDiskServer server;

    public ServerListener(CloudDiskServer server) {
        this.server = server;
    }

    public void run() {
        prompt();
        Scanner inputFromKeyboard = new Scanner(System.in);
        while (inputFromKeyboard.hasNextLine()) {
            String input = inputFromKeyboard.nextLine().toUpperCase();
            if (input.equals("USERS")) {
                showUsers();
            } else if (input.equals("QUIT")) {
                server.stop();
                break;
            }
        }
    }

    public void prompt() {
        System.out.println("You can input orders below:");
        System.out.println("QUIT    End the Server.(Wait all the clients quit first)");
        System.out.println("USERS    Show all the users information that have registered.");
    }

    public void showUsers() {
        ConcurrentHashMap<String, User> userMap = server.getUserMap();
        if (userMap.isEmpty()) {
            System.out.println("There is no user now.");
        }
        for (Map.Entry<String, User> user : userMap.entrySet()) {
            System.out.println("Username: " + user.getKey());
            System.out.println("PassWord: " + user.getValue().getPassword());
            System.out.println();
        }
    }
}
