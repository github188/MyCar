package com.cnlaunch.mycar.updatecenter.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class ContinueUpdateFileInStream extends RandomAccessFile implements DPUUpdateFileInStream
{

    public ContinueUpdateFileInStream(File file, String mode, long pos) throws FileNotFoundException
    {
        super(file,mode);
        try
        {
            super.seek(pos);
            
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    @Override
    public int readFile(byte[] buffer)
    {
        // TODO Auto-generated method stub
        try
        {
            return super.read(buffer);
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 0;
    }
    @Override
    public void closeStream()
    {
        // TODO Auto-generated method stub
        try
        {
            super.close();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    /**
     * 得到文件位置
     * @return
     * @since DBS V100
     */
    public long getFilePoint()
    {
        try
        {
            return super.getFilePointer();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return 0;
        }
    }
}
