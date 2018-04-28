package com.cnlaunch.mycar.updatecenter.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Formatter;

/**
 * 文件切割器将文件切割
 * 为指定大小的字节块
 * 以便拼装数据包进行发送
 * */
public class FileSlicer
{
	Object syncLock = new Object();
	File file;
	RandomAccessFile raf; 
	int totalChunk;
	int restBytes;
	int chunkSize;
	int index;
	boolean running = false;
	Chunk chunk;
	byte[] buff;
	Thread thread;
	
	public FileSlicer(File f,int size) throws FileNotFoundException
	{
		this.file = f;
		this.raf = new RandomAccessFile(f, "rw");
		this.chunkSize = size;
		this.totalChunk = (int) (f.length() / size);
		this.restBytes = (int) (f.length() % size);
		this.buff = new byte[size];
	}
	
	class SlicerThread extends Thread
	{
		public void run()
		{
			while(running)
			{
				synchronized (syncLock)
				{
					// get data chunck;
					try {
						syncLock.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					int count;
					try {
						count = raf.read(buff, 0, chunkSize);
						chunk = new Chunk();
						chunk.len = count;
						chunk.data = buff;
						if(count == -1)
						{
							thread = null;
							break;
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	public Chunk getDataChunck()
	{
		if(thread == null)
		{
			thread = new SlicerThread();
			thread.start();
		}
		syncLock.notifyAll();
		return this.chunk;
	}
	
	static class Chunk
	{
		int len;
		byte[] data;
	}
	
	public static void dumpDataChunk(Chunk ck)
	{
		System.out.println("=============================");
		System.out.println("[Chunck Length :]" + ck.len);
		StringBuilder sb = new StringBuilder();
		for (byte b : ck.data ) {
			sb.append(new Formatter().format("%02x-", b));
		}
		System.out.println("Chunk data :" + sb.toString());
		System.out.println("=============================");
	} 
}
