package com.cnlaunch.mycar.jni;

/**********************************************************
 * ��װX431�ļ������java�࣬��Ҫ�ǵ��ñ��ط����е�C/C++�����X431�ļ����д���
 * 
 * @author ��ID:2860��
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

	/* ��Ʒ���� */
	public static final int PRODUCT_UNKNOWN = 0x00; /* Unknown */
	public static final int PRODUCT_X431 = 0x01; /* X431 */
	public static final int PRODUCT_X431INFINITE = 0x02; /* X431 Infinite */
	public static final int PRODUCT_X431TOP = 0x03; /* X431 Top */
	public static final int PRODUCT_X431TOOL = 0x04; /* X431 Tool */
	public static final int PRODUCT_X431PC = 0x05; /* X431 PC */

	public static final int PRODUCT_PCLINK = 0x11; /* PCLink */
	public static final int PRODUCT_PCCENTER = 0x12; /* PCCenter */
	public static final int PRODUCT_ADM = 0x13; /* ADM */
	public static final int PRODUCT_RECORDER = 0x14; /* ������¼�� */
	public static final int PRODUCT_CRECORDER = 0x15; /* CRecorder */

	/* ��¼���� */
	public static final int RECORD_DTC = 0x01; /* DTC */
	public static final int RECORD_DATASTREAM = 0x02; /* Data Stream */
	public static final int RECORD_VERSIONINFO = 0x04; /* Version Info */
	public static final int RECORD_FREEZEFRAME = 0x08; /* Freeze Frame */
	public static final int RECORD_READINESS = 0x10; /* Readiness */
	public static final int RECORD_DSBASICS = 0x20; /* Data Stream Basics */

	/* �ļ��򿪲����Ĵ����� */
	public static final int LSX_ERR_OK = 0; /* �����ɹ� */
	public static final int LSX_ERR_LOW_FILEVERSION = -1; /* �ļ��汾Ϊ�Ͱ汾 */
	public static final int LSX_ERR_HIGH_FILEVERSION = -2; /* �����ݵ��ļ��汾 */
	public static final int LSX_ERR_FILE_NOTFOUND = -3; /* �ļ������� */
	public static final int LSX_ERR_INCORRECT_FORMAT = -4; /* �ļ���ʽ����ȷ */
	public static final int LSX_ERR_INVALID_PARAMETER = -5; /* ��Ч���� */
	public static final int LSX_ERR_ALLOC_MEMORY = -6; /* �����ڴ���� */

	/* �ļ������ԣ����汾���ɶ�д���ԣ� */
	public static final int LSX_FILE_READABLE = 0x0001; /* �ļ���Ч���ɽ��ж����� */
	public static final int LSX_FILE_WRITABLE = 0x0002; /* �ļ���Ч���ɽ���д���� */
	public static final int LSX_FILE_V2 = 0x1000; /* �ļ����汾ΪV2 */
	public static final int LSX_FILE_V3 = 0x2000; /* �ļ����汾ΪV3 */

	/* �ض���������λ */
	public static final String DSUNIT_TIME = "dstime"; /* ʱ�� */
	public static final String DSUNIT_DTCS = "dtcs"; /* ��������� */

	/* ������������� */
	public static final int MAX_DS_COLNUMBER = 300;

	/* δ��������������� */
	public static final int DS_TYPE_UNKNOWN = 0x0000;

	// ��Ӧlsx.h�ļ��еĺ�����HLSX lsx_init();
	public native int lsx_init();

	// ��Ӧlsx.h�ļ��еĺ�����int lsx_deinit(HLSX hlsx);
	public native int lsx_deinit(int hlsx);

	// ��Ӧlsx.h�ļ��еĺ�����
	// LSX_FILE lsx_open(HLSX hlsx, const char *filename, int mode, int *error);
	public native int lsx_open(int hlsx, X431String filename, int mode,
			X431Integer error);

	// ��Ӧlsx.h�ļ��еĺ�����
	// int lsx_close(LSX_FILE file);
	public native int lsx_close(int lsx_file);

	// ��Ӧlsx.h�ļ��еĺ�����
	// unsigned int lsx_checkfile(const char *filename);
	public native int lsx_checkfile(X431String filepath);

	/*
	 * ��д������Ϣ
	 */
	// ��Ӧlsx.h�ļ��еĺ�����
	// int lsx_read_baseinfo(LSX_FILE file, LSX_BASEINFO *baseinfo);
	public native int lsx_read_baseinfo(int lsx_file, LSX_BASEINFO baseinfo);

	// ��Ӧlsx.h�ļ��еĺ�����
	// int lsx_write_baseinfo(LSX_FILE file, const LSX_BASEINFO *baseinfo);
	public native int lsx_write_baseinfo(int lsx_file, LSX_BASEINFO baseinfo);

	/*
	 * ��д������Ϣ
	 */
	// ��Ӧlsx.h�ļ��еĺ�����
	// int lsx_read_autoinfo(LSX_FILE file, LSX_AUTOINFO *autoinfo);
	public native int lsx_read_autoinfo(int lsx_file, LSX_AUTOINFO autoinfo);

	// ��Ӧlsx.h�ļ��еĺ�����
	// int lsx_write_autoinfo(LSX_FILE file, const LSX_AUTOINFO *autoinfo);
	public native int lsx_write_autoinfo(int lsx_file, LSX_AUTOINFO autoinfo);

	/*
	 * ��д��������Ϣ
	 */
	// ��Ӧlsx.h�ļ��еĺ�����
	// int lsx_read_spinfo(LSX_FILE file, LSX_SPINFO *spinfo);
	public native int lsx_read_spinfo(int lsx_file, LSX_SPINFO spinfo);

	// ��Ӧlsx.h�ļ��еĺ�����
	// int lsx_write_spinfo(LSX_FILE file, const LSX_SPINFO *spinfo);
	public native int lsx_write_spinfo(int lsx_file, LSX_SPINFO spinfo);

	/*
	 * ��д�û���Ϣ
	 */
	// ��Ӧlsx.h�ļ��еĺ�����
	// int lsx_read_userinfo(LSX_FILE file, LSX_USERINFO *userinfo);
	public native int lsx_read_userinfo(int lsx_file, LSX_USERINFO userinfo);

	// ��Ӧlsx.h�ļ��еĺ�����
	// int lsx_write_userinfo(LSX_FILE file, const LSX_USERINFO *userinfo);
	public native int lsx_write_userinfo(int lsx_file, LSX_USERINFO userinfo);

	/*
	 * ��д��¼��Ϣ
	 * 
	 * ��¼�����
	 */

	// ��Ӧlsx.h�ļ��еĺ�����
	// LSX_RECGROUP lsx_rec_writenewgroup(LSX_FILE file, const char *name, const
	// char *protocol, const char *vin, const char *starttime, int dsinterval);
	public native int lsx_rec_writenewgroup(int file, String name,
			String protocol, String vin, String starttime, int dsinterval);

	// ��Ӧlsx.h�ļ��еĺ�����
	// int lsx_rec_finishnewgroup(LSX_RECGROUP grp, const char *endtime);
	public native int lsx_rec_finishnewgroup(int grp, String endtime);
	

	// ��Ӧlsx.h�ļ��еĺ�����
	// int lsx_rec_readgroupcount(LSX_FILE file);
	public native int lsx_rec_readgroupcount(int lsx_file);

	// ��Ӧlsx.h�ļ��еĺ�����
	// LSX_RECGROUP lsx_rec_readgroupid(LSX_FILE file, int i);
	public native int lsx_rec_readgroupid(int lsx_file, int i);

	// ��Ӧlsx.h�ļ��еĺ�����
	// int lsx_rec_readgroupinfo(LSX_RECGROUP grp, char **name, char **protocol,
	// char **vin, char **starttime, char **endtime, int *dsinterval);
    public native int lsx_rec_readgroupinfo(int grp, X431String name, X431String protocol, X431String vin, X431String starttime, X431String endtime, X431Integer dsinterval);

	
	// ��Ӧlsx.h�ļ��еĺ�����
	// unsigned int lsx_rec_readalltype(LSX_RECGROUP grp);
	public native int lsx_rec_readalltype(int grp);
	

	// ��Ӧlsx.h�ļ��еĺ�����
	// int lsx_rec_modifygroupinfo(LSX_RECGROUP grp, const char *name, const
	// char *vin);

	/*
	 * ����������
	 */

	// ��Ӧlsx.h�ļ��еĺ�����
	// unsigned short lsx_read_fileversion(LSX_FILE file);
	public native short lsx_read_fileversion(int lsx_file);

	/*
	 * ���Ժ���
	 */

	// ��Ӧlsx.h�ļ��еĺ�����
	// int lsx_read_langcode(LSX_FILE file, char code[], char code_en[], int
	// size);
	public native int lsx_read_langcode(int lsx_file, X431String code,
			X431String code_en);

	// ��Ӧlsx.h�ļ��еĺ�����
	// int lsx_read_langname(LSX_FILE file, char name[], char name_en[], int
	// size);

	// ��Ӧlsx.h�ļ��еĺ�����
	// int lsx_selectreadtextlang(LSX_FILE file, const char *langcode);
	public native int lsx_selectreadtextlang(int file, String langcode);

	/* ��������� */

	// ��Ӧlsx.h�ļ��еĺ�����
	// int lsx_rec_readdtccount(LSX_RECGROUP grp);
	public native int lsx_rec_readdtccount(int grp);

	// ��Ӧlsx.h�ļ��еĺ�����
	// int lsx_rec_readdtc(LSX_RECITEM item, char **dtc, char **state, char
	// **desc, char **time);
	public native int lsx_rec_readdtc(int item, X431String dtc,
			X431String state, X431String desc, X431String time);

	// ��Ӧlsx.h�ļ��еĺ�����
	// int lsx_rec_readdtcinfo(LSX_RECGROUP grp, const char *dtc, char **state,
	// char **desc, char **time);
	public native int lsx_rec_readdtcinfo(int grp, String dtc,
			X431String state, X431String desc, X431String time);

	// ��Ӧlsx.h�ļ��еĺ�����
	// LSX_RECITEM lsx_rec_readfirstdtcitem(LSX_RECGROUP grp);
	public native int lsx_rec_readfirstdtcitem(int grp);

	// ��Ӧlsx.h�ļ��еĺ�����
	// LSX_RECITEM lsx_rec_readlastdtcitem(LSX_RECGROUP grp);
	public native int lsx_rec_readlastdtcitem(int grp);

	// ��Ӧlsx.h�ļ��еĺ�����
	// LSX_RECITEM lsx_rec_readnextdtcitem(LSX_RECITEM item);
	public native int lsx_rec_readnextdtcitem(int item);

	// ��Ӧlsx.h�ļ��еĺ�����
	// LSX_RECITEM lsx_rec_readprevdtcitem(LSX_RECITEM item);
	public native int lsx_rec_readprevdtcitem(int item);

	// ��Ӧlsx.h�ļ��еĺ�����
	// int lsx_rec_writedtc(LSX_RECGROUP grp, const char *dtc, const LSX_STRING
	// *state, const LSX_STRING *desc, const char *time);
	public native int lsx_rec_writedtc(int grp, String dtc, LSX_STRING state,
			LSX_STRING desc, String time);

	/* ��̬��������� */

	// ��Ӧlsx.h�ļ��еĺ�����
	// int lsx_rec_readdsitemcount(LSX_RECGROUP grp);
	public native int lsx_rec_readdsitemcount(int grp);

	// ��Ӧlsx.h�ļ��еĺ�����
	// int lsx_rec_readdscolcount(LSX_RECGROUP grp);
	public native int lsx_rec_readdscolcount(int grp);

	// ��Ӧlsx.h�ļ��еĺ�����
	// int lsx_rec_readdsname(LSX_RECGROUP grp, char *textstrs[], int n);
	public native int lsx_rec_readdsname(int grp, String textstrs[], int n);

	// ��Ӧlsx.h�ļ��еĺ�����
	// int lsx_rec_readdsunit(LSX_RECGROUP grp, char *textstrs[], int n);
	public native int lsx_rec_readdsunit(int grp, String textstrs[], int n);

	// ��Ӧlsx.h�ļ��еĺ�����
	// int lsx_rec_readds(LSX_RECITEM item, char *textstrs[], int n);
	public native int lsx_rec_readds(int item, String textstrs[], int n);

	// ��Ӧlsx.h�ļ��еĺ�����
	// int lsx_rec_readdsany(LSX_RECITEM item, char *textstrs[], const int
	// cols[], int n);

	// ��Ӧlsx.h�ļ��еĺ�����
	// int lsx_rec_readdstype(LSX_RECGROUP grp, unsigned short type[], int n);
	public native int lsx_rec_readdstype(int grp, short type[], int n);

	// ��Ӧlsx.h�ļ��еĺ�����
	// int lsx_rec_readdscolumn(LSX_RECITEM item, char **text, int col);

	// ��Ӧlsx.h�ļ��еĺ�����
	// int lsx_rec_readtypedscolindex(LSX_RECGROUP grp, unsigned short type);

	// ��Ӧlsx.h�ļ��еĺ�����
	// int lsx_rec_readtypedscolcount(LSX_RECGROUP grp);

	// ��Ӧlsx.h�ļ��еĺ�����
	// int lsx_rec_readtypedsname(LSX_RECGROUP grp, char *textstrs[], int n);

	// ��Ӧlsx.h�ļ��еĺ�����
	// int lsx_rec_readtypedsunit(LSX_RECGROUP grp, char *textstrs[], int n);

	// ��Ӧlsx.h�ļ��еĺ�����
	// int lsx_rec_readtypedstype(LSX_RECGROUP grp, unsigned short type[], int
	// n);

	// ��Ӧlsx.h�ļ��еĺ�����
	// int lsx_rec_readtypeds(LSX_RECITEM item, char *textstrs[], int n);

	// ��Ӧlsx.h�ļ��еĺ�����
	// int lsx_rec_modifydstype(LSX_RECGROUP grp, int col, unsigned short type);

	// ��Ӧlsx.h�ļ��еĺ�����
	// LSX_RECITEM lsx_rec_readfirstdsitem(LSX_RECGROUP grp);
	public native int lsx_rec_readfirstdsitem(int grp);

	// ��Ӧlsx.h�ļ��еĺ�����
	// LSX_RECITEM lsx_rec_readlastdsitem(LSX_RECGROUP grp);
	public native int lsx_rec_readlastdsitem(int grp);

	// ��Ӧlsx.h�ļ��еĺ�����
	// LSX_RECITEM lsx_rec_readrelndsitem(LSX_RECITEM item, int n);
	public native int lsx_rec_readrelndsitem(int item, int n);

	// ��Ӧlsx.h�ļ��еĺ�����
	// int lsx_rec_writedsbasics(LSX_RECGROUP grp, const LSX_STRING *namestrs[],
	// const LSX_STRING *unitstrs[], const unsigned short type[], int n);
	public native int lsx_rec_writedsbasics(int grp, LSX_STRING namestrs[],
			LSX_STRING unitstrs[], int type[], int n);

	// ��Ӧlsx.h�ļ��еĺ�����
	// int lsx_rec_writeds(LSX_RECGROUP grp, const LSX_STRING *itemstrs[], int
	// n);
	public native int lsx_rec_writeds(int grp, LSX_STRING itemstrs[], int n);

	/* Readiness��� */

	// ��Ӧlsx.h�ļ��еĺ�����
	// int lsx_rec_readrdncolcount(LSX_RECITEM item);

	// ��Ӧlsx.h�ļ��еĺ�����
	// int lsx_rec_readrdnname(LSX_RECITEM item, char *textstrs[], int n);

	// ��Ӧlsx.h�ļ��еĺ�����
	// int lsx_rec_readreadiness(LSX_RECITEM item, char *textstrs[], int n);

	// ��Ӧlsx.h�ļ��еĺ�����
	// int lsx_rec_writereadiness(LSX_RECGROUP grp, const LSX_STRING
	// *namestrs[], const LSX_STRING *textstrs[], int n);
	public native int lsx_rec_writereadiness(int grp, LSX_STRING namestrs[],
			LSX_STRING textstrs[], int n);

	/* �汾��Ϣ��� */
	// ��Ӧlsx.h�ļ��еĺ�����
	// int lsx_rec_readvi(LSX_RECGROUP grp, char **vi);

	// ��Ӧlsx.h�ļ��еĺ�����
	// int lsx_rec_writevi(LSX_RECGROUP grp, const LSX_STRING *vi);
	public native int lsx_rec_writevi(int grp, LSX_STRING vi);

	/* ����֡��� */

	// ��Ӧlsx.h�ļ��еĺ�����
	// int lsx_rec_readffitemcount(LSX_RECGROUP grp);
	public native int lsx_rec_readffitemcount(int grp);
	
	// ��Ӧlsx.h�ļ��еĺ�����
	// int lsx_rec_readffcolcount(LSX_RECITEM item);
	public native int lsx_rec_readffcolcount(int item);
	
	// ��Ӧlsx.h�ļ��еĺ�����
	// int lsx_rec_readffname(LSX_RECITEM item, char *textstrs[], int n);
	// ��Ӧlsx.h�ļ��еĺ�����
	// int lsx_rec_readffunit(LSX_RECITEM item, char *textstrs[], int n);
	// ��Ӧlsx.h�ļ��еĺ�����
	// int lsx_rec_readfftype(LSX_RECITEM item, unsigned short type[], int n);
	
	// ��Ӧlsx.h�ļ��еĺ�����
	// int lsx_rec_readfreezeframe(LSX_RECITEM item, char **dtc, char
	// *textstrs[], int n);
	public native int lsx_rec_readfreezeframe(int item, X431String dtc, String textstrs[], int n);

	// ��Ӧlsx.h�ļ��еĺ�����
	// LSX_RECITEM lsx_rec_readfirstffitem(LSX_RECGROUP grp);
	public native int lsx_rec_readfirstffitem(int grp);
	
	// ��Ӧlsx.h�ļ��еĺ�����
	// LSX_RECITEM lsx_rec_readlastffitem(LSX_RECGROUP grp);
	public native int lsx_rec_readlastffitem(int grp);

	// ��Ӧlsx.h�ļ��еĺ�����
	// LSX_RECITEM lsx_rec_readnextffitem(LSX_RECITEM item);
	public native int lsx_rec_readnextffitem(int item);
	
	// ��Ӧlsx.h�ļ��еĺ�����
	// LSX_RECITEM lsx_rec_readprevffitem(LSX_RECITEM item);
	public native int lsx_rec_readprevffitem(int item);

	// ��Ӧlsx.h�ļ��еĺ�����
	// int lsx_rec_writefreezeframe(LSX_RECGROUP grp, const char *dtc,
	// const LSX_STRING *namestrs[], const LSX_STRING *unitstrs[],
	// const unsigned short type[], const LSX_STRING *textstrs[], int n);
	public native int lsx_rec_writefreezeframe(int grp, String dtc, LSX_STRING namestrs[], LSX_STRING unitstrs[], int type[], LSX_STRING textstrs[], int n);
	

	/* ��¼���� */

	// ��Ӧlsx.h�ļ��еĺ�����
		// unsigned int lsx_rec_readitemtype(LSX_RECITEM item);
	public native int lsx_rec_readitemtype(int item);
	
	// ��Ӧlsx.h�ļ��еĺ�����
		// LSX_RECITEM lsx_rec_readfirstitem(LSX_RECGROUP grp);
	public native int lsx_rec_readfirstitem(int grp);
	
	// ��Ӧlsx.h�ļ��еĺ�����
		// LSX_RECITEM lsx_rec_readlastitem(LSX_RECGROUP grp);
	public native int lsx_rec_readlastitem(int grp);
	
	// ��Ӧlsx.h�ļ��еĺ�����
		// LSX_RECITEM lsx_rec_readnextitem(LSX_RECITEM item);
	public native int lsx_rec_readnextitem(int item);
	
	// ��Ӧlsx.h�ļ��еĺ�����
		// LSX_RECITEM lsx_rec_readprevitem(LSX_RECITEM item);
	public native int lsx_rec_readprevitem(int item);
	
}
