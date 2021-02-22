package disk.client;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.util.Scanner;

class ReadThread implements Runnable{
    private final Socket socket;
    private final WriteThread writeThread;

    private Scanner inputFromServer;

    private String filename;
    private long fileIndex;

    public ReadThread(Socket socket, WriteThread writeThread) {
        this.socket = socket;
        this.writeThread = writeThread;
    }

    public void run() {
        try {
            try (Scanner scanner = new Scanner(socket.getInputStream())) {
                inputFromServer = scanner;
                while (!socket.isClosed() && inputFromServer.hasNextLine()) {
                    String input = inputFromServer.nextLine();
                    System.out.println(input);

                    if(input.equals("Please input the file " +
                            "name you want to download:(including extension)")) {
                        readFile();

                    } else if (input.equals("Please input the file " +
                            "name you want to upload:(including extension)")) {
                        writeFile();
                    }
                }
            }
        } catch(IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void readFile() throws IOException, InterruptedException {
        // Filename
        writeThread.setReadFileSignal();
        while (!inputFromServer.nextLine().equals("Valid filename.")) {
            System.out.println("File does not exist.");
            System.out.println("Please input file name again.");
        }
        writeThread.setValidFilename();
        Thread.sleep(100);
        File file = new File(Client.DIRECTORY + filename);
        file.createNewFile();
        // File length
        fileIndex = file.length();
        // Read file
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw")) {
            // "Total file length:"
            long totalFileLength = Long.parseLong(inputFromServer.nextLine().substring(18));
            long count = file.length();
            randomAccessFile.seek(count);
            InputStream streamFromServer = socket.getInputStream();
            byte[] part = new byte[(int) Client.SINGLE_TRANSFER_LENGTH];
            while (count < totalFileLength) {
                int haveRead = streamFromServer.read(part);
                randomAccessFile.write(part, 0, haveRead);
                count += Client.SINGLE_TRANSFER_LENGTH;
            }
        }
        System.out.println("File " + filename + " download complete.");
    }

    public void writeFile() {
        writeThread.setWriteFileSignal();
        // Current file length:
        long fileIndex = Long.parseLong(inputFromServer.nextLine().substring(20));
        writeThread.setFileIndex(fileIndex);
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public long getFileIndex() {
        return fileIndex;
    }
}
