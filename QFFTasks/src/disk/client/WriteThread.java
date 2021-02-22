package disk.client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

class WriteThread implements Runnable {
    private final Socket socket;
    private ReadThread readThread;

    private Scanner inputFromKeyboard;
    private PrintWriter outputToServer;

    private boolean readFileSignal = false;
    private boolean writeFileSignal = false;
    private boolean validFilename = false;

    private long fileIndex;

    public WriteThread(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            inputFromKeyboard = new Scanner(System.in);
            try (PrintWriter output = new PrintWriter(socket.getOutputStream(), true)) {
                outputToServer = output;

                String input = "";
                while (!input.equals("quit") && inputFromKeyboard.hasNextLine()) {
                    if (writeFileSignal) {
                        writeFile();

                    } else if (readFileSignal) {
                        readFile();

                    } else {
                        input = inputFromKeyboard.nextLine();
                        outputToServer.println(input);
                    }
                }
            }  finally {
                socket.close();
            }
        } catch (IOException | InterruptedException e){
            e.printStackTrace();
        }
    }

    public void readFile() throws InterruptedException {
        String filename = "";
        while (!validFilename) {
            filename = inputFromKeyboard.nextLine();
            outputToServer.println(filename);
            Thread.sleep(100);
        }
        readThread.setFilename(filename);
        Thread.sleep(100);
        // Current file length:
        outputToServer.println("Current file length:" + readThread.getFileIndex());
        validFilename = false;
    }

    public void writeFile() throws IOException, InterruptedException {
        // Filename
        String filename = inputFromKeyboard.nextLine();
        File file = new File(Client.DIRECTORY + filename);
        while (!file.exists() || filename.isEmpty()) {
            System.out.println("The file " + file.getName() + " does not exist.");
            System.out.println("Please input valid file name.");
            filename = inputFromKeyboard.nextLine();
            file = new File(Client.DIRECTORY + filename);
        }
        outputToServer.println(filename);
        // File length
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw")) {
            outputToServer.println("Total file length:" + file.length());
            // Make sure fileIndex is modified by ReadThread
            Thread.sleep(100);
            // Write file
            randomAccessFile.seek(fileIndex);
            byte[] part = new byte[(int) Client.SINGLE_TRANSFER_LENGTH];
            OutputStream streamToServer = socket.getOutputStream();
            while (fileIndex < file.length()) {
                int notRead = (int) (randomAccessFile.length() - fileIndex);
                int partLength = (int) Client.SINGLE_TRANSFER_LENGTH;
                // The last part of source may be less than Main.SINGLE_LENGTH
                // In case of EOFException
                if (notRead < Client.SINGLE_TRANSFER_LENGTH) {
                    partLength = notRead;
                }
                randomAccessFile.readFully(part, 0, partLength);
                fileIndex += partLength;
                streamToServer.write(part, 0, partLength);
            }
            System.out.println("File " + filename + " upload complete.");
        }
        writeFileSignal = false;
    }

    public void setReadFileSignal() {
        readFileSignal = true;
    }

    public void setWriteFileSignal() {
        writeFileSignal = true;
    }

    public void setFileIndex(long fileIndex) {
        this.fileIndex = fileIndex;
    }

    public void setReadThread(ReadThread readThread) {
        this.readThread = readThread;
    }

    public void setValidFilename() {
        validFilename = true;
    }
}
