package disk.client;

import java.net.Socket;

public class Client {
    public static final long SINGLE_TRANSFER_LENGTH = 256;
    public static final String DIRECTORY = "src\\disk\\client\\localfiles\\";

    public static void main(String[] args) throws Exception {
        try (Socket socket = new Socket("localhost", 8189)) {
            WriteThread writeThread = new WriteThread(socket);
            ReadThread readThread = new ReadThread(socket, writeThread);
            writeThread.setReadThread(readThread);
            Thread writeToServer = new Thread(writeThread);
            Thread readFromServer = new Thread(readThread);
            readFromServer.start();
            writeToServer.start();
            // Wait read and write thread is dead, then close socket
            readFromServer.join();
        }
    }
}
