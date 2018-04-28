package com.cnlaunch.mycar.updatecenter.model;

public interface DPUUpdateFileInStream
{

    /**
     * ���ļ�
     * @param buffer
     * @since DBS V100
     */
    public int readFile(byte[] buffer);
    
    /**
     * �ر��ļ���
     * @return
     * @since DBS V100
     */
    public void closeStream();
    
    /**
     * �õ��ļ�λ��
     * @return
     * @since DBS V100
     */
    public long getFilePoint();
}
