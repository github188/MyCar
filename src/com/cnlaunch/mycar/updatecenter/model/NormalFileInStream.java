package com.cnlaunch.mycar.updatecenter.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class NormalFileInStream extends FileInputStream implements DPUUpdateFileInStream
{

    public NormalFileInStream(File file) throws FileNotFoundException
    {
        super(file);
    }
    
    @Override
    public int readFile(byte[] buffer)
    {
        try
        {
            return super.read(buffer);
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void closeStream()
    {
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
        return 0;
    }
}
