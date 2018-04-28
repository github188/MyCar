package com.cnlaunch.mycar.updatecenter.model;

import java.io.File;

public class UpdateFileInfo
{
    public File file               = null;                       // Դ�ļ�
    public long totalBytes         = 0;                          // ������Ҫ�������ļ������ֽ���
    public long byteSent           = 0;                          // �Ѿ����͵��ֽ���
    public boolean isContinueSend  = false;                      // �Ƿ���Ҫ�ϵ�����
    public long breakPointPos      = 0;                          // �ϵ�λ��
    public int fileCount           = 0;                          // �ļ���
    public int fileNo              = 0;                          // ��ǰ�ļ������ļ��б��е�λ��
    public UpdateFileInfo(File file, long totalBytes, long byteSent, boolean isContinueSend, long breakPointPos, int fileCount, int fileNo)
    {
        super();
        this.file = file;
        this.totalBytes = totalBytes;
        this.byteSent = byteSent;
        this.isContinueSend = isContinueSend;
        this.breakPointPos = breakPointPos;
        this.fileCount = fileCount;
        this.fileNo = fileNo;
    }
    
}
