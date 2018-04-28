package com.cnlaunch.mycar.updatecenter.model;

import java.io.File;
import java.util.ArrayList;

public class UpdateInfo
{
    public ArrayList<File> fileList;
    public String vehiecle;
    public String version;
    public String language;
    public String baseDir;
    public UpdateInfo(ArrayList<File> fileList, String vehiecle, String version, String language,String baseDir)
    {
        super();
        this.fileList = fileList;
        this.vehiecle = vehiecle;
        this.version = version;
        this.language = language;
        this.baseDir = baseDir;
    }
}
