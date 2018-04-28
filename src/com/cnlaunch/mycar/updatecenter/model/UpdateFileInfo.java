package com.cnlaunch.mycar.updatecenter.model;

import java.io.File;

public class UpdateFileInfo
{
    public File file               = null;                       // 源文件
    public long totalBytes         = 0;                          // 所有需要升级的文件的总字节数
    public long byteSent           = 0;                          // 已经发送的字节数
    public boolean isContinueSend  = false;                      // 是否需要断点续传
    public long breakPointPos      = 0;                          // 断点位置
    public int fileCount           = 0;                          // 文件数
    public int fileNo              = 0;                          // 当前文件在总文件列表中的位置
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
