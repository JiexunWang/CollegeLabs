# 勤奋蜂后端考核2
## 考核要求：
多线程读取文件，文件内容自定义，使用多个线程读取一个文件，每个线程读取一部分，然后拼接到另外一个文件，在每个线程读取内容的前面加上线程名。
注意：
1、在读取的时候，请尽量使用并发执行。
2、本次考核其实可以理解为文件的切割与合并。

## 我的思路  
1. 读取文件时使用多线程  
为了能随机读取文件，使用类RandomAccessFile  
将读取到的结果存放在一个List<byte[]>中  

2. 写入文件没有使用多线程  
将List<byte[]>中的数组依次写入新文件  

## 我的代码与测试文件：
### Main
其中的方法  
public void multiThreadCopyFile(File sourceFile, File destinationFile)  
为满足需要的方法  
main方法为测试用的方法  

### ReadThread  
读文件的单个线程类  
在Main的方法中会创建多个这个类实现对文件的多线程读取  
**以上两个类就实现了所需功能**

### source.txt
测试用源文件，将会被读取

### destination.txt
测试用目的文件，开始时为空  
调用测试函数main后会写入“线程名 + 源文件的内容”  

### Problems.md
编写时遇到的问题，供个人纪录，无用   