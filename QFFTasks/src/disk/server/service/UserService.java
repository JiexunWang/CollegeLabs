/*
 * Perform when a client want to log in.
 */

package disk.server.service;

import disk.server.repository.User;

import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class UserService {
    ConcurrentHashMap<String, User> userMap;

    Scanner inputFromKeyboard;
    Scanner inputFromClient;
    PrintWriter outputToClient;

    public UserService(ConcurrentHashMap<String, User> userList,
                       Scanner inputFromClient, PrintWriter outputToClient) {
        this.userMap = userList;
        inputFromKeyboard = new Scanner(System.in);
        this.inputFromClient = inputFromClient;
        this.outputToClient = outputToClient;
    }

    public User logIn() {
        User result = null;
        String input;
        boolean signal = true;
        while (signal) {
            outputToClient.println("0  Quit.");
            outputToClient.println("1  Register for a new account.");
            outputToClient.println("2  Log in with an existed account.");
            input = inputFromClient.nextLine();
            switch (input) {
                case "0":
                    signal = false;
                    break;
                case "1":
                    result = register();
                    signal = false;
                    break;
                case "2":
                    result = search();
                    signal = (result == null);
                    break;
                default:
                    // Loop again
            }
        }
        return result;
    }

    public User register() {
        outputToClient.println("Input the username:");
        String username = inputFromClient.nextLine();
        // Illegal character check
        if (username.contains("?") || username.contains("\\") || username.contains("/")
            || username.contains("*") || username.contains(":") || username.contains("\"")
            || username.contains("<") || username.contains(">") || username.contains("|")) {
            outputToClient.println("These characters are not allowed:?*:\"<>\\/|");
            username = inputFromClient.nextLine();
        }
        // Repeated username check
        if (userMap.containsKey(username)) {
            outputToClient.println("The username has been registered by others.");
            username = inputFromClient.nextLine();
        }
        // Password check
        String password;
        boolean repeat;
        do {
            repeat = false;
            outputToClient.println("Input the password:");
            password = inputFromClient.nextLine();
            outputToClient.println("Input the password again:");
            if (!inputFromClient.nextLine().equals(password)) {
                outputToClient.println("Different passwords in two times.");
                repeat = true;
            }
        } while(repeat);

        User newUser = new User(username, password);
        userMap.put(username, newUser);
        File directory = new File(FileService.CLOUD_FILES_DIRECTORY_PATH
                + newUser.getFolderName());
        if (!directory.mkdir()) {
            System.out.println("Failed to create directory.");
        }
        return newUser;
    }

    public User search() {
        // Username check
        outputToClient.println("Input the username:");
        String username = inputFromClient.nextLine();
        if (userMap.isEmpty() || !userMap.containsKey(username)) {
            outputToClient.println("There is no user "+ username + ".");
            return null;
        }
        // Password check
        outputToClient.println("Input the password:");
        String password = inputFromClient.nextLine();
        User goal = userMap.get(username);
        int timesOfTry = 1;
        while (!password.equals(goal.getPassword())) {
            outputToClient.println("Wrong password.");
            outputToClient.println("Input again:");
            password = inputFromClient.nextLine();
            ++timesOfTry;
            if (timesOfTry == 3) {
                outputToClient.println("Failed 3 times.");
                return null;
            }
        }
        return goal;
    }
}