package com.cnlaunch.mycar.updatecenter.model;

import java.util.ArrayList;

public class BreakPointInfo
{
    public String                softName;              // �������           
    public String                softVersion;           // ����汾
    public String                softLanguage;          // �������
    public boolean               isUpdated;             // �Ƿ�������ɣ�00����ɣ�������Ϊ���
    public int                   completeFileCount;     // �Ѿ�������ɵ��ļ���
    public ArrayList<String>     fileArray;             // �Ѿ�����
    public String                updatingFileName;      // ���������е��ļ���
    public long                  receivedByteCount;     // �Ѿ����յ����ֽ���
    public String                md5;                   // �Ѿ����յ����ļ���MD5У��
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
