/*
 * Perform when the user want to upload or download files.
 */
package disk.server.service;

import disk.server.repository.User;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class FileService {
    public static final long SINGLE_TRANSFER_LENGTH = 256;
    public static final String CLOUD_FILES_DIRECTORY_PATH = "src\\disk\\server\\repository\\cloudfiles\\";

    private final Socket incoming;
    private final File directory;

    private final Scanner inputFromClient;
    private final PrintWriter outputToClient;

    public FileService(Socket incoming, User user,
                       Scanner inputFromClient, PrintWriter outputToClient) {
        this.incoming = incoming;
        directory = new File(CLOUD_FILES_DIRECTORY_PATH
                                + user.getFolderName());
        this.inputFromClient = inputFromClient;
        this.outputToClient = outputToClient;
    }

    public void perform() throws IOException {
        String input = "";
        outputToClient.println("0  Quit.");
        outputToClient.println("1  Show files in your cloud disk.");
        outputToClient.println("2  Upload a file to your cloud disk.");
        outputToClient.println("3  Download a file from your cloud disk.");
        while (!input.equals("0")) {
            input = inputFromClient.nextLine();
            switch (input) {
                case "1":
                    showFiles();
                    break;
                case "2":
                    upload();
                    break;
                case "3":
                    download();
                    break;
                default:
            }
        }
    }

    public void showFiles() {
        if (directory.list().length == 0) {
            outputToClient.println("There is no file in your cloud disk.");
            return;
        }
        for (String filename : directory.list()) {
            outputToClient.println(filename);
        }
    }

    public void upload() throws IOException {
        outputToClient.println("Please make sure the file " +
                "is in your directory \"local files\".");
        outputToClient.println("Please input the file " +
                "name you want to upload:(including extension)");
        File file = new File(directory + "\\" + inputFromClient.nextLine());
        // May return false, means there is a file already
        file.createNewFile();
        // Resume from break point, file.length() is the break point
        outputToClient.println("Current file length:" + file.length());
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw")) {
            // "Total file length:"
            long totalFileLength = Long.parseLong(inputFromClient.nextLine().substring(18));
            long count = file.length();
            randomAccessFile.seek(count);
            InputStream streamFromClient = incoming.getInputStream();
            byte[] part = new byte[(int) FileService.SINGLE_TRANSFER_LENGTH];
            while (count < totalFileLength) {
                int haveRead = streamFromClient.read(part);
                randomAccessFile.write(part, 0, haveRead);
                count += FileService.SINGLE_TRANSFER_LENGTH;
            }
        }

    }

    public void download() throws IOException {
        outputToClient.println("Here's your file list.");
        showFiles();
        outputToClient.println("Please input the file " +
                "name you want to download:(including extension)");
        // Existed check
        File file = new File(directory + "\\" + inputFromClient.nextLine());
        while (!file.exists()) {
            outputToClient.println("File does not exist.");
            file = new File(directory + "\\" + inputFromClient.nextLine());
        }
        outputToClient.println("Valid filename.");
        // Transfer
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw")) {
            outputToClient.println("Total file length:" + file.length());
            long fileIndex = Long.parseLong(inputFromClient.nextLine().substring(20));
            randomAccessFile.seek(fileIndex);
            byte[] part = new byte[(int) FileService.SINGLE_TRANSFER_LENGTH];
            OutputStream streamToClient = incoming.getOutputStream();
            int partLength = (int) FileService.SINGLE_TRANSFER_LENGTH;
            while (fileIndex < file.length()) {
                int notRead = (int) (randomAccessFile.length() - fileIndex);
                // The last part of source may be less than Main.SINGLE_LENGTH
                // In case of EOFException
                if (notRead < FileService.SINGLE_TRANSFER_LENGTH) {
                    partLength = notRead;
                }
                randomAccessFile.readFully(part, 0, partLength);
                fileIndex += partLength;
                streamToClient.write(part, 0, partLength);
            }
        }
    }
}