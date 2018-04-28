package com.cnlaunch.mycar.updatecenter.model;

public interface DPUUpdateFileInStream
{

    /**
     * 读文件
     * @param buffer
     * @since DBS V100
     */
    public int readFile(byte[] buffer);
    
    /**
     * 关闭文件流
     * @return
     * @since DBS V100
     */
    public void closeStream();
    
    /**
     * 得到文件位置
     * @return
     * @since DBS V100
     */
    public long getFilePoint();
}
