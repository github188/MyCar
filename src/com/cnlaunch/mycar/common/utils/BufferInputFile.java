package com.cnlaunch.mycar.common.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class BufferInputFile {
	public String read(String fileName) throws IOException
	{
		// reading input by lines
		BufferedReader bufferedReader = new BufferedReader(
				new FileReader(fileName));
		String line;
		StringBuffer sb = new StringBuffer();
		while ((line = bufferedReader.readLine()) != null)
		{
			sb.append(line + "\n");
		}
		
		bufferedReader.close();
		return sb.toString();
	}
    public static void main(String[] args)
    {
    	BufferInputFile bif = new BufferInputFile();
    	try {
		    System.out.println("¡î¡î¡î¡î¡î" + 	bif.read("HttpDownloader.java"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
