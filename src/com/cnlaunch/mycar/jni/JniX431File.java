package com.cnlaunch.mycar.jni;

/**********************************************************
 * 封装X431文件处理的java类，主要是调用本地方法中的C/C++代码对X431文件进行处理
 * 
 * @author （ID:2860）
 * 
 * @version
 * 
 * @since
 **********************************************************/

public class JniX431File {

	static {
		System.loadLibrary("x431file");
	}
	
	public static final int MODE_READ = 1;
	public static final int MODE_WRITE = 2;

	/* 产品类型 */
	public static final int PRODUCT_UNKNOWN = 0x00; /* Unknown */
	public static final int PRODUCT_X431 = 0x01; /* X431 */
	public static final int PRODUCT_X431INFINITE = 0x02; /* X431 Infinite */
	public static final int PRODUCT_X431TOP = 0x03; /* X431 Top */
	public static final int PRODUCT_X431TOOL = 0x04; /* X431 Tool */
	public static final int PRODUCT_X431PC = 0x05; /* X431 PC */

	public static final int PRODUCT_PCLINK = 0x11; /* PCLink */
	public static final int PRODUCT_PCCENTER = 0x12; /* PCCenter */
	public static final int PRODUCT_ADM = 0x13; /* ADM */
	public static final int PRODUCT_RECORDER = 0x14; /* 工况记录仪 */
	public static final int PRODUCT_CRECORDER = 0x15; /* CRecorder */

	/* 记录类型 */
	public static final int RECORD_DTC = 0x01; /* DTC */
	public static final int RECORD_DATASTREAM = 0x02; /* Data Stream */
	public static final int RECORD_VERSIONINFO = 0x04; /* Version Info */
	public static final int RECORD_FREEZEFRAME = 0x08; /* Freeze Frame */
	public static final int RECORD_READINESS = 0x10; /* Readiness */
	public static final int RECORD_DSBASICS = 0x20; /* Data Stream Basics */

	/* 文件打开操作的错误码 */
	public static final int LSX_ERR_OK = 0; /* 操作成功 */
	public static final int LSX_ERR_LOW_FILEVERSION = -1; /* 文件版本为低版本 */
	public static final int LSX_ERR_HIGH_FILEVERSION = -2; /* 不兼容的文件版本 */
	public static final int LSX_ERR_FILE_NOTFOUND = -3; /* 文件不存在 */
	public static final int LSX_ERR_INCORRECT_FORMAT = -4; /* 文件格式不正确 */
	public static final int LSX_ERR_INVALID_PARAMETER = -5; /* 无效参数 */
	public static final int LSX_ERR_ALLOC_MEMORY = -6; /* 分配内存错误 */

	/* 文件的属性（主版本、可读写属性） */
	public static final int LSX_FILE_READABLE = 0x0001; /* 文件有效并可进行读操作 */
	public static final int LSX_FILE_WRITABLE = 0x0002; /* 文件有效并可进行写操作 */
	public static final int LSX_FILE_V2 = 0x1000; /* 文件主版本为V2 */
	public static final int LSX_FILE_V3 = 0x2000; /* 文件主版本为V3 */

	/* 特定数据流单位 */
	public static final String DSUNIT_TIME = "dstime"; /* 时间 */
	public static final String DSUNIT_DTCS = "dtcs"; /* 故障码个数 */

	/* 最大数据流项数 */
	public static final int MAX_DS_COLNUMBER = 300;

	/* 未定义的数据流类型 */
	public static final int DS_TYPE_UNKNOWN = 0x0000;

	// 对应lsx.h文件中的函数：HLSX lsx_init();
	public native int lsx_init();

	// 对应lsx.h文件中的函数：int lsx_deinit(HLSX hlsx);
	public native int lsx_deinit(int hlsx);

	// 对应lsx.h文件中的函数：
	// LSX_FILE lsx_open(HLSX hlsx, const char *filename, int mode, int *error);
	public native int lsx_open(int hlsx, X431String filename, int mode,
			X431Integer error);

	// 对应lsx.h文件中的函数：
	// int lsx_close(LSX_FILE file);
	public native int lsx_close(int lsx_file);

	// 对应lsx.h文件中的函数：
	// unsigned int lsx_checkfile(const char *filename);
	public native int lsx_checkfile(X431String filepath);

	/*
	 * 读写基本信息
	 */
	// 对应lsx.h文件中的函数：
	// int lsx_read_baseinfo(LSX_FILE file, LSX_BASEINFO *baseinfo);
	public native int lsx_read_baseinfo(int lsx_file, LSX_BASEINFO baseinfo);

	// 对应lsx.h文件中的函数：
	// int lsx_write_baseinfo(LSX_FILE file, const LSX_BASEINFO *baseinfo);
	public native int lsx_write_baseinfo(int lsx_file, LSX_BASEINFO baseinfo);

	/*
	 * 读写车辆信息
	 */
	// 对应lsx.h文件中的函数：
	// int lsx_read_autoinfo(LSX_FILE file, LSX_AUTOINFO *autoinfo);
	public native int lsx_read_autoinfo(int lsx_file, LSX_AUTOINFO autoinfo);

	// 对应lsx.h文件中的函数：
	// int lsx_write_autoinfo(LSX_FILE file, const LSX_AUTOINFO *autoinfo);
	public native int lsx_write_autoinfo(int lsx_file, LSX_AUTOINFO autoinfo);

	/*
	 * 读写经销商信息
	 */
	// 对应lsx.h文件中的函数：
	// int lsx_read_spinfo(LSX_FILE file, LSX_SPINFO *spinfo);
	public native int lsx_read_spinfo(int lsx_file, LSX_SPINFO spinfo);

	// 对应lsx.h文件中的函数：
	// int lsx_write_spinfo(LSX_FILE file, const LSX_SPINFO *spinfo);
	public native int lsx_write_spinfo(int lsx_file, LSX_SPINFO spinfo);

	/*
	 * 读写用户信息
	 */
	// 对应lsx.h文件中的函数：
	// int lsx_read_userinfo(LSX_FILE file, LSX_USERINFO *userinfo);
	public native int lsx_read_userinfo(int lsx_file, LSX_USERINFO userinfo);

	// 对应lsx.h文件中的函数：
	// int lsx_write_userinfo(LSX_FILE file, const LSX_USERINFO *userinfo);
	public native int lsx_write_userinfo(int lsx_file, LSX_USERINFO userinfo);

	/*
	 * 读写记录信息
	 * 
	 * 记录组相关
	 */

	// 对应lsx.h文件中的函数：
	// LSX_RECGROUP lsx_rec_writenewgroup(LSX_FILE file, const char *name, const
	// char *protocol, const char *vin, const char *starttime, int dsinterval);
	public native int lsx_rec_writenewgroup(int file, String name,
			String protocol, String vin, String starttime, int dsinterval);

	// 对应lsx.h文件中的函数：
	// int lsx_rec_finishnewgroup(LSX_RECGROUP grp, const char *endtime);
	public native int lsx_rec_finishnewgroup(int grp, String endtime);
	

	// 对应lsx.h文件中的函数：
	// int lsx_rec_readgroupcount(LSX_FILE file);
	public native int lsx_rec_readgroupcount(int lsx_file);

	// 对应lsx.h文件中的函数：
	// LSX_RECGROUP lsx_rec_readgroupid(LSX_FILE file, int i);
	public native int lsx_rec_readgroupid(int lsx_file, int i);

	// 对应lsx.h文件中的函数：
	// int lsx_rec_readgroupinfo(LSX_RECGROUP grp, char **name, char **protocol,
	// char **vin, char **starttime, char **endtime, int *dsinterval);
    public native int lsx_rec_readgroupinfo(int grp, X431String name, X431String protocol, X431String vin, X431String starttime, X431String endtime, X431Integer dsinterval);

	
	// 对应lsx.h文件中的函数：
	// unsigned int lsx_rec_readalltype(LSX_RECGROUP grp);
	public native int lsx_rec_readalltype(int grp);
	

	// 对应lsx.h文件中的函数：
	// int lsx_rec_modifygroupinfo(LSX_RECGROUP grp, const char *name, const
	// char *vin);

	/*
	 * 其它函数：
	 */

	// 对应lsx.h文件中的函数：
	// unsigned short lsx_read_fileversion(LSX_FILE file);
	public native short lsx_read_fileversion(int lsx_file);

	/*
	 * 语言函数
	 */

	// 对应lsx.h文件中的函数：
	// int lsx_read_langcode(LSX_FILE file, char code[], char code_en[], int
	// size);
	public native int lsx_read_langcode(int lsx_file, X431String code,
			X431String code_en);

	// 对应lsx.h文件中的函数：
	// int lsx_read_langname(LSX_FILE file, char name[], char name_en[], int
	// size);

	// 对应lsx.h文件中的函数：
	// int lsx_selectreadtextlang(LSX_FILE file, const char *langcode);
	public native int lsx_selectreadtextlang(int file, String langcode);

	/* 故障码相关 */

	// 对应lsx.h文件中的函数：
	// int lsx_rec_readdtccount(LSX_RECGROUP grp);
	public native int lsx_rec_readdtccount(int grp);

	// 对应lsx.h文件中的函数：
	// int lsx_rec_readdtc(LSX_RECITEM item, char **dtc, char **state, char
	// **desc, char **time);
	public native int lsx_rec_readdtc(int item, X431String dtc,
			X431String state, X431String desc, X431String time);

	// 对应lsx.h文件中的函数：
	// int lsx_rec_readdtcinfo(LSX_RECGROUP grp, const char *dtc, char **state,
	// char **desc, char **time);
	public native int lsx_rec_readdtcinfo(int grp, String dtc,
			X431String state, X431String desc, X431String time);

	// 对应lsx.h文件中的函数：
	// LSX_RECITEM lsx_rec_readfirstdtcitem(LSX_RECGROUP grp);
	public native int lsx_rec_readfirstdtcitem(int grp);

	// 对应lsx.h文件中的函数：
	// LSX_RECITEM lsx_rec_readlastdtcitem(LSX_RECGROUP grp);
	public native int lsx_rec_readlastdtcitem(int grp);

	// 对应lsx.h文件中的函数：
	// LSX_RECITEM lsx_rec_readnextdtcitem(LSX_RECITEM item);
	public native int lsx_rec_readnextdtcitem(int item);

	// 对应lsx.h文件中的函数：
	// LSX_RECITEM lsx_rec_readprevdtcitem(LSX_RECITEM item);
	public native int lsx_rec_readprevdtcitem(int item);

	// 对应lsx.h文件中的函数：
	// int lsx_rec_writedtc(LSX_RECGROUP grp, const char *dtc, const LSX_STRING
	// *state, const LSX_STRING *desc, const char *time);
	public native int lsx_rec_writedtc(int grp, String dtc, LSX_STRING state,
			LSX_STRING desc, String time);

	/* 动态数据流相关 */

	// 对应lsx.h文件中的函数：
	// int lsx_rec_readdsitemcount(LSX_RECGROUP grp);
	public native int lsx_rec_readdsitemcount(int grp);

	// 对应lsx.h文件中的函数：
	// int lsx_rec_readdscolcount(LSX_RECGROUP grp);
	public native int lsx_rec_readdscolcount(int grp);

	// 对应lsx.h文件中的函数：
	// int lsx_rec_readdsname(LSX_RECGROUP grp, char *textstrs[], int n);
	public native int lsx_rec_readdsname(int grp, String textstrs[], int n);

	// 对应lsx.h文件中的函数：
	// int lsx_rec_readdsunit(LSX_RECGROUP grp, char *textstrs[], int n);
	public native int lsx_rec_readdsunit(int grp, String textstrs[], int n);

	// 对应lsx.h文件中的函数：
	// int lsx_rec_readds(LSX_RECITEM item, char *textstrs[], int n);
	public native int lsx_rec_readds(int item, String textstrs[], int n);

	// 对应lsx.h文件中的函数：
	// int lsx_rec_readdsany(LSX_RECITEM item, char *textstrs[], const int
	// cols[], int n);

	// 对应lsx.h文件中的函数：
	// int lsx_rec_readdstype(LSX_RECGROUP grp, unsigned short type[], int n);
	public native int lsx_rec_readdstype(int grp, short type[], int n);

	// 对应lsx.h文件中的函数：
	// int lsx_rec_readdscolumn(LSX_RECITEM item, char **text, int col);

	// 对应lsx.h文件中的函数：
	// int lsx_rec_readtypedscolindex(LSX_RECGROUP grp, unsigned short type);

	// 对应lsx.h文件中的函数：
	// int lsx_rec_readtypedscolcount(LSX_RECGROUP grp);

	// 对应lsx.h文件中的函数：
	// int lsx_rec_readtypedsname(LSX_RECGROUP grp, char *textstrs[], int n);

	// 对应lsx.h文件中的函数：
	// int lsx_rec_readtypedsunit(LSX_RECGROUP grp, char *textstrs[], int n);

	// 对应lsx.h文件中的函数：
	// int lsx_rec_readtypedstype(LSX_RECGROUP grp, unsigned short type[], int
	// n);

	// 对应lsx.h文件中的函数：
	// int lsx_rec_readtypeds(LSX_RECITEM item, char *textstrs[], int n);

	// 对应lsx.h文件中的函数：
	// int lsx_rec_modifydstype(LSX_RECGROUP grp, int col, unsigned short type);

	// 对应lsx.h文件中的函数：
	// LSX_RECITEM lsx_rec_readfirstdsitem(LSX_RECGROUP grp);
	public native int lsx_rec_readfirstdsitem(int grp);

	// 对应lsx.h文件中的函数：
	// LSX_RECITEM lsx_rec_readlastdsitem(LSX_RECGROUP grp);
	public native int lsx_rec_readlastdsitem(int grp);

	// 对应lsx.h文件中的函数：
	// LSX_RECITEM lsx_rec_readrelndsitem(LSX_RECITEM item, int n);
	public native int lsx_rec_readrelndsitem(int item, int n);

	// 对应lsx.h文件中的函数：
	// int lsx_rec_writedsbasics(LSX_RECGROUP grp, const LSX_STRING *namestrs[],
	// const LSX_STRING *unitstrs[], const unsigned short type[], int n);
	public native int lsx_rec_writedsbasics(int grp, LSX_STRING namestrs[],
			LSX_STRING unitstrs[], int type[], int n);

	// 对应lsx.h文件中的函数：
	// int lsx_rec_writeds(LSX_RECGROUP grp, const LSX_STRING *itemstrs[], int
	// n);
	public native int lsx_rec_writeds(int grp, LSX_STRING itemstrs[], int n);

	/* Readiness相关 */

	// 对应lsx.h文件中的函数：
	// int lsx_rec_readrdncolcount(LSX_RECITEM item);

	// 对应lsx.h文件中的函数：
	// int lsx_rec_readrdnname(LSX_RECITEM item, char *textstrs[], int n);

	// 对应lsx.h文件中的函数：
	// int lsx_rec_readreadiness(LSX_RECITEM item, char *textstrs[], int n);

	// 对应lsx.h文件中的函数：
	// int lsx_rec_writereadiness(LSX_RECGROUP grp, const LSX_STRING
	// *namestrs[], const LSX_STRING *textstrs[], int n);
	public native int lsx_rec_writereadiness(int grp, LSX_STRING namestrs[],
			LSX_STRING textstrs[], int n);

	/* 版本信息相关 */
	// 对应lsx.h文件中的函数：
	// int lsx_rec_readvi(LSX_RECGROUP grp, char **vi);

	// 对应lsx.h文件中的函数：
	// int lsx_rec_writevi(LSX_RECGROUP grp, const LSX_STRING *vi);
	public native int lsx_rec_writevi(int grp, LSX_STRING vi);

	/* 冻结帧相关 */

	// 对应lsx.h文件中的函数：
	// int lsx_rec_readffitemcount(LSX_RECGROUP grp);
	public native int lsx_rec_readffitemcount(int grp);
	
	// 对应lsx.h文件中的函数：
	// int lsx_rec_readffcolcount(LSX_RECITEM item);
	public native int lsx_rec_readffcolcount(int item);
	
	// 对应lsx.h文件中的函数：
	// int lsx_rec_readffname(LSX_RECITEM item, char *textstrs[], int n);
	// 对应lsx.h文件中的函数：
	// int lsx_rec_readffunit(LSX_RECITEM item, char *textstrs[], int n);
	// 对应lsx.h文件中的函数：
	// int lsx_rec_readfftype(LSX_RECITEM item, unsigned short type[], int n);
	
	// 对应lsx.h文件中的函数：
	// int lsx_rec_readfreezeframe(LSX_RECITEM item, char **dtc, char
	// *textstrs[], int n);
	public native int lsx_rec_readfreezeframe(int item, X431String dtc, String textstrs[], int n);

	// 对应lsx.h文件中的函数：
	// LSX_RECITEM lsx_rec_readfirstffitem(LSX_RECGROUP grp);
	public native int lsx_rec_readfirstffitem(int grp);
	
	// 对应lsx.h文件中的函数：
	// LSX_RECITEM lsx_rec_readlastffitem(LSX_RECGROUP grp);
	public native int lsx_rec_readlastffitem(int grp);

	// 对应lsx.h文件中的函数：
	// LSX_RECITEM lsx_rec_readnextffitem(LSX_RECITEM item);
	public native int lsx_rec_readnextffitem(int item);
	
	// 对应lsx.h文件中的函数：
	// LSX_RECITEM lsx_rec_readprevffitem(LSX_RECITEM item);
	public native int lsx_rec_readprevffitem(int item);

	// 对应lsx.h文件中的函数：
	// int lsx_rec_writefreezeframe(LSX_RECGROUP grp, const char *dtc,
	// const LSX_STRING *namestrs[], const LSX_STRING *unitstrs[],
	// const unsigned short type[], const LSX_STRING *textstrs[], int n);
	public native int lsx_rec_writefreezeframe(int grp, String dtc, LSX_STRING namestrs[], LSX_STRING unitstrs[], int type[], LSX_STRING textstrs[], int n);
	

	/* 记录遍历 */

	// 对应lsx.h文件中的函数：
		// unsigned int lsx_rec_readitemtype(LSX_RECITEM item);
	public native int lsx_rec_readitemtype(int item);
	
	// 对应lsx.h文件中的函数：
		// LSX_RECITEM lsx_rec_readfirstitem(LSX_RECGROUP grp);
	public native int lsx_rec_readfirstitem(int grp);
	
	// 对应lsx.h文件中的函数：
		// LSX_RECITEM lsx_rec_readlastitem(LSX_RECGROUP grp);
	public native int lsx_rec_readlastitem(int grp);
	
	// 对应lsx.h文件中的函数：
		// LSX_RECITEM lsx_rec_readnextitem(LSX_RECITEM item);
	public native int lsx_rec_readnextitem(int item);
	
	// 对应lsx.h文件中的函数：
		// LSX_RECITEM lsx_rec_readprevitem(LSX_RECITEM item);
	public native int lsx_rec_readprevitem(int item);
	
}
