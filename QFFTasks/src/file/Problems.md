### 本文件简述编写时遇到的问题（给自己看）
1. 没有使用线程安全的集合  
在使用List<byte[]>存储文件中的字节时，  
开始时使用的是ArrayList  
导致频繁报错，改用CopyOnWriteArrayList后，报错消失  
ArrayList在多个线程同时写入时线程不安全  
CopyOnWriteArrayList线程安全  
2. 读取文件时频繁出现EOFException  
原因不明，由于不影响正常功能，直接catch，未作处理  
3. 主程序关闭过早  
各线程还没有完成工作，主程序就关闭  
报IOException：Stream Closed  
使用CountDownLatch(threadAmount)  
可以让主程序等待所有线程结束  
4. 线程池的选择  
开始时使用CachedThreadPool  
程序开始后长时间无响应  
改换成FixedThreadPool后，问题解决  
原因不明  
5. 线程数过多易出错  
当线程数过多时（比如多于十个），  
复制的文本会出现重复  
线程数较少则不会    
原因不明    