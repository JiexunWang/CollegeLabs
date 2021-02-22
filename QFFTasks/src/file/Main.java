/**
 * Read a file with multi-thread and write it to another file.
 * @author WangJiexun
 * @date 2020.10.19
 */

package file;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static final int SINGLE_LENGTH = 512;

    public static void main(String[] args) throws Exception {
        File source = new File("src\\file\\source.txt");
        File destination = new File("src\\file\\destination.txt");
        Main a = new Main();
        a.multiThreadCopyFile(source, destination);
    }

    public void multiThreadCopyFile(File sourceFile, File destinationFile) throws Exception {
        List<byte[]> contents = splitFile(sourceFile);
        mergeContents(contents, destinationFile);
    }

    public List<byte[]> splitFile(File source) throws Exception {
        long fileLength = source.length();
        long threadAmount = fileLength / Main.SINGLE_LENGTH;
        if (threadAmount == 0 && fileLength != 0) {
            threadAmount = 1;
        }
        threadAmount = fileLength / Main.SINGLE_LENGTH == 0 ? threadAmount : threadAmount + 1;
        // Make sure all the thread finish its work
        CountDownLatch doneSignal = new CountDownLatch((int)threadAmount);
        // When using CachedThreadPool, deadlock appears more frequent
        ExecutorService pool = Executors.newFixedThreadPool((int)threadAmount);
        // CopyOnWriteArrayList is thread-safe, while ArrayList is not.
        List<byte[]> result = new CopyOnWriteArrayList<>();
        try (RandomAccessFile sourceFile = new RandomAccessFile(source, "rw")){
            for (long i = 0; i < threadAmount; ++i) {
                pool.submit(new ReadThread(i * SINGLE_LENGTH, sourceFile, result, doneSignal));
                Thread.sleep(100);
            }
            doneSignal.await();
        } finally {
            // Make sure thread pool will be shut down
            pool.shutdown();
        }

        return result;
    }

    public void mergeContents(List<byte[]> contents, File destination) throws IOException{
        try (PrintWriter writer = new PrintWriter(destination, "UTF-8")) {
            for (int i = 0; i < contents.size(); ++i) {
                String content = new String(contents.get(i), StandardCharsets.UTF_8);
                writer.println("Thread " + i + " : ");
                writer.println(content);
                writer.flush();
            }
        }
    }
}
