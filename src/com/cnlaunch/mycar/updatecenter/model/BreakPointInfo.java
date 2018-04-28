package com.cnlaunch.mycar.updatecenter.model;

import java.util.ArrayList;

public class BreakPointInfo
{
    public String                softName;              // 软件名称           
    public String                softVersion;           // 软件版本
    public String                softLanguage;          // 软件语言
    public boolean               isUpdated;             // 是否升级完成，00：完成，其他：为完成
    public int                   completeFileCount;     // 已经升级完成的文件数
    public ArrayList<String>     fileArray;             // 已经升级
    public String                updatingFileName;      // 正在升级中的文件名
    public long                  receivedByteCount;     // 已经接收到的字节数
    public String                md5;                   // 已经接收到的文件的MD5校验
    public BreakPointInfo(String softName, String softVersion, String softLanguage, boolean isUpdated, int completeFileCount, ArrayList<String> fileArray, String updatingFileName,
        Long receivedByteCount, String md5)
    {
        super();
        this.softName = softName;
        this.softVersion = softVersion;
        this.softLanguage = softLanguage;
        this.isUpdated = isUpdated;
        this.completeFileCount = completeFileCount;
        this.fileArray = fileArray;
        this.updatingFileName = updatingFileName;
        this.receivedByteCount = receivedByteCount;
        this.md5 = md5;
    }
    public BreakPointInfo()
    {
        super();
    }
    @Override
    public String toString()
    {
        return "BreakPointInfo [softName=" + softName + ", softVersion=" + softVersion + ", softLanguage=" + softLanguage + ", isUpdated=" + isUpdated + ", completeFileCount=" + completeFileCount
            + ", fileArray=" + fileArray + ", updatingFileName=" + updatingFileName + ", receivedByteCount=" + receivedByteCount + ", md5=" + md5 + "]";
    }
    
}
