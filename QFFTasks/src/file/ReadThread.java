package file;

import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ReadThread implements Runnable {
    private long startIndex;
    private RandomAccessFile source;
    private List<byte[]> result;
    private CountDownLatch doneSignal;

    public ReadThread(long startIndex, RandomAccessFile source, List<byte[]> result, CountDownLatch doneSignal) {
        this.startIndex = startIndex;
        this.source = source;
        this.result = result;
        this.doneSignal = doneSignal;
    }

    public synchronized void run() {
        try {
            source.seek(startIndex);
            byte[] content;
            int contentLength = (int)(source.length() - startIndex);
            // The last part of source may be less than Main.SINGLE_LENGTH
            // In case of EOFException
            if (contentLength < Main.SINGLE_LENGTH) {
                content = new byte[contentLength];
                source.readFully(content, 0, contentLength);
            } else {
                content = new byte[Main.SINGLE_LENGTH];
                source.readFully(content, 0, Main.SINGLE_LENGTH);
            }
            result.add(content);
            doneSignal.countDown();
        } catch (EOFException e) {
            // Do nothing
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
