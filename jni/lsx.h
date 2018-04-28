#ifndef _LSX_H
#define _LSX_H


#define MODE_READ				1
#define MODE_WRITE				2

/* 产品类型 */
#define PRODUCT_UNKNOWN			0x00		/* Unknown */
#define PRODUCT_X431			0x01		/* X431 */
#define PRODUCT_X431INFINITE	0x02		/* X431 Infinite */
#define PRODUCT_X431TOP			0x03		/* X431 Top */
#define PRODUCT_X431TOOL		0x04		/* X431 Tool */
#define PRODUCT_X431PC			0x05		/* X431 PC */

#define PRODUCT_PCLINK			0x11		/* PCLink */
#define PRODUCT_PCCENTER		0x12        /* PCCenter */
#define PRODUCT_ADM				0x13		/* ADM */
#define PRODUCT_RECORDER		0x14		/* 工况记录仪 */
#define PRODUCT_CRECORDER		0x15		/* CRecorder */

/* 记录类型 */
#define RECORD_DTC				0x01		/* DTC */
#define RECORD_DATASTREAM		0x02		/* Data Stream */
#define RECORD_VERSIONINFO		0x04		/* Version Info */
#define RECORD_FREEZEFRAME		0x08		/* Freeze Frame */
#define RECORD_READINESS		0x10		/* Readiness */
#define RECORD_DSBASICS			0x20		/* Data Stream Basics */

/* 文件打开操作的错误码 */
#define LSX_ERR_OK					0		/* 操作成功 */
#define LSX_ERR_LOW_FILEVERSION		-1		/* 文件版本为低版本 */
#define LSX_ERR_HIGH_FILEVERSION	-2		/* 不兼容的文件版本 */
#define LSX_ERR_FILE_NOTFOUND		-3		/* 文件不存在 */
#define LSX_ERR_INCORRECT_FORMAT	-4		/* 文件格式不正确 */
#define LSX_ERR_INVALID_PARAMETER	-5		/* 无效参数 */
#define LSX_ERR_ALLOC_MEMORY        -6		/* 分配内存错误 */

/* 文件的属性（主版本、可读写属性） */
#define LSX_FILE_READABLE			0x0001	/* 文件有效并可进行读操作 */
#define LSX_FILE_WRITABLE			0x0002	/* 文件有效并可进行写操作 */
#define LSX_FILE_V2					0x1000	/* 文件主版本为V2 */
#define LSX_FILE_V3					0x2000	/* 文件主版本为V3 */

/* 特定数据流单位 */
#define DSUNIT_TIME				"dstime"	/* 时间 */
#define DSUNIT_DTCS				"dtcs"		/* 故障码个数 */

/* 最大数据流项数 */
#define MAX_DS_COLNUMBER		300

/* 未定义的数据流类型 */
#define DS_TYPE_UNKNOWN			0x0000

typedef void *HLSX;
typedef void *LSX_FILE;
typedef void *LSX_RECGROUP;
typedef void *LSX_RECITEM;

typedef struct _LSX_STRING
{
	char *str;								/* 本地语言文本 */
	char *str_en;							/* 英文文本 */
} LSX_STRING;

typedef struct _LSX_BASEINFO
{
	char serialno[20];						/* 产品序列号 */
	unsigned short productid;				/* 产品代码 */
	unsigned short codepage;				/* 本地语言代码页 */
	char langname[30];						/* 本地语言名称字符串 */
	char langcode[4];						/* 本地语言代码串 */
	char langcode_en[4];					/* 英文语言代码串 */
	char *diagversion;						/* 诊断软件版本 */
	char *creationtime;						/* 文件创建时间 */
} LSX_BASEINFO;

typedef struct _LSX_AUTOINFO
{
	char vin[20];							/* VIN 码，仅保留不使用 */
	char *make;								/* 车系名 */
	char *model;							/* 车型名 */
	char *year;								/* 出厂年份 */
	char *madein;							/* 出厂地 */
	char *chassis;							/* 底盘 */
	char *enginemodel;						/* 发动机型号 */
	char *displacement;						/* 排量 */
} LSX_AUTOINFO;

typedef struct _LSX_SPINFO
{
	char *name;								/* 经销商名称 */
	char *phone;							/* 经销商电话 */
} LSX_SPINFO;

typedef struct _LSX_USERINFO
{
	char *name;								/* 用户名称 */
	char *phone;							/* 用户电话 */
	char *license;							/* 许可证号 */
} LSX_USERINFO;

#ifdef __cplusplus
extern "C" {
#endif
	
/*
 *	初始化和清理
 */
	
HLSX lsx_init();
int lsx_deinit(HLSX hlsx);

/*
 *	文件打开和关闭
 */

LSX_FILE lsx_open(HLSX hlsx, const char *filename, int mode, int *error);
int lsx_close(LSX_FILE file);

/* 
 * 检测文件是否有效 
 */

unsigned int lsx_checkfile(const char *filename);

/*
 *	读写基本信息
 */

int lsx_read_baseinfo(LSX_FILE file, LSX_BASEINFO *baseinfo);
int lsx_write_baseinfo(LSX_FILE file, const LSX_BASEINFO *baseinfo);

/*
 *	读写车辆信息
 */

int lsx_read_autoinfo(LSX_FILE file, LSX_AUTOINFO *autoinfo);
int lsx_write_autoinfo(LSX_FILE file, const LSX_AUTOINFO *autoinfo);

/*
 *	读写经销商信息
 */

int lsx_read_spinfo(LSX_FILE file, LSX_SPINFO *spinfo);
int lsx_write_spinfo(LSX_FILE file, const LSX_SPINFO *spinfo);

/*
 *	读写用户信息
 */

int lsx_read_userinfo(LSX_FILE file, LSX_USERINFO *userinfo);
int lsx_write_userinfo(LSX_FILE file, const LSX_USERINFO *userinfo);


/*
 *	读写记录信息
 */

/* 记录组相关 */

LSX_RECGROUP lsx_rec_writenewgroup(LSX_FILE file, const char *name, 
	const char *protocol, const char *vin, const char *starttime, int dsinterval);
int lsx_rec_finishnewgroup(LSX_RECGROUP grp, const char *endtime);

int lsx_rec_readgroupcount(LSX_FILE file);
LSX_RECGROUP lsx_rec_readgroupid(LSX_FILE file, int i);
int lsx_rec_readgroupinfo(LSX_RECGROUP grp, char **name, char **protocol, 
	char **vin, char **starttime, char **endtime, int *dsinterval);
unsigned int lsx_rec_readalltype(LSX_RECGROUP grp);

int lsx_rec_modifygroupinfo(LSX_RECGROUP grp, const char *name, const char *vin);

/* 记录遍历 */

unsigned int lsx_rec_readitemtype(LSX_RECITEM item);
LSX_RECITEM lsx_rec_readfirstitem(LSX_RECGROUP grp);
LSX_RECITEM lsx_rec_readlastitem(LSX_RECGROUP grp);
LSX_RECITEM lsx_rec_readnextitem(LSX_RECITEM item);
LSX_RECITEM lsx_rec_readprevitem(LSX_RECITEM item);


/* 故障码相关 */

int lsx_rec_readdtccount(LSX_RECGROUP grp);
int lsx_rec_readdtc(LSX_RECITEM item, char **dtc, char **state, char **desc, char **time);
int lsx_rec_readdtcinfo(LSX_RECGROUP grp, 
	const char *dtc, char **state, char **desc, char **time);

LSX_RECITEM lsx_rec_readfirstdtcitem(LSX_RECGROUP grp);
LSX_RECITEM lsx_rec_readlastdtcitem(LSX_RECGROUP grp);
LSX_RECITEM lsx_rec_readnextdtcitem(LSX_RECITEM item);
LSX_RECITEM lsx_rec_readprevdtcitem(LSX_RECITEM item);

int lsx_rec_writedtc(LSX_RECGROUP grp, const char *dtc, 
	const LSX_STRING *state, const LSX_STRING *desc, const char *time);

/* 动态数据流相关 */

int lsx_rec_readdsitemcount(LSX_RECGROUP grp);
int lsx_rec_readdscolcount(LSX_RECGROUP grp);
int lsx_rec_readdsname(LSX_RECGROUP grp, char *textstrs[], int n);
int lsx_rec_readdsunit(LSX_RECGROUP grp, char *textstrs[], int n);
int lsx_rec_readds(LSX_RECITEM item, char *textstrs[], int n);
int lsx_rec_readdsany(LSX_RECITEM item, char *textstrs[], const int cols[], int n);

int lsx_rec_readdstype(LSX_RECGROUP grp, unsigned short type[], int n);
int lsx_rec_readdscolumn(LSX_RECITEM item, char **text, int col);

int lsx_rec_readtypedscolindex(LSX_RECGROUP grp, unsigned short type);
int lsx_rec_readtypedscolcount(LSX_RECGROUP grp);
int lsx_rec_readtypedsname(LSX_RECGROUP grp, char *textstrs[], int n);
int lsx_rec_readtypedsunit(LSX_RECGROUP grp, char *textstrs[], int n);
int lsx_rec_readtypedstype(LSX_RECGROUP grp, unsigned short type[], int n);
int lsx_rec_readtypeds(LSX_RECITEM item, char *textstrs[], int n);

int lsx_rec_modifydstype(LSX_RECGROUP grp, int col, unsigned short type);
 
LSX_RECITEM lsx_rec_readfirstdsitem(LSX_RECGROUP grp);
LSX_RECITEM lsx_rec_readlastdsitem(LSX_RECGROUP grp);
LSX_RECITEM lsx_rec_readrelndsitem(LSX_RECITEM item, int n);

int lsx_rec_writedsbasics(LSX_RECGROUP grp, 
	const LSX_STRING *namestrs[], const LSX_STRING *unitstrs[], const unsigned short type[], int n);
int lsx_rec_writeds(LSX_RECGROUP grp, const LSX_STRING *itemstrs[], int n);

/* 冻结帧相关 */

int lsx_rec_readffitemcount(LSX_RECGROUP grp);
int lsx_rec_readffcolcount(LSX_RECITEM item);
int lsx_rec_readffname(LSX_RECITEM item, char *textstrs[], int n);
int lsx_rec_readffunit(LSX_RECITEM item, char *textstrs[], int n);
int lsx_rec_readfftype(LSX_RECITEM item, unsigned short type[], int n);
int lsx_rec_readfreezeframe(LSX_RECITEM item, char **dtc, char *textstrs[], int n);

LSX_RECITEM lsx_rec_readfirstffitem(LSX_RECGROUP grp);
LSX_RECITEM lsx_rec_readlastffitem(LSX_RECGROUP grp);
LSX_RECITEM lsx_rec_readnextffitem(LSX_RECITEM item);
LSX_RECITEM lsx_rec_readprevffitem(LSX_RECITEM item);

int lsx_rec_writefreezeframe(LSX_RECGROUP grp, const char *dtc, 
	const LSX_STRING *namestrs[], const LSX_STRING *unitstrs[], 
	const unsigned short type[], const LSX_STRING *textstrs[], int n);

/* Readiness相关 */

int lsx_rec_readrdncolcount(LSX_RECITEM item);
int lsx_rec_readrdnname(LSX_RECITEM item, char *textstrs[], int n);
int lsx_rec_readreadiness(LSX_RECITEM item, char *textstrs[], int n);

int lsx_rec_writereadiness(LSX_RECGROUP grp, 
   const LSX_STRING *namestrs[], const LSX_STRING *textstrs[], int n);

/* 版本信息相关 */

int lsx_rec_readvi(LSX_RECGROUP grp, char **vi);

int lsx_rec_writevi(LSX_RECGROUP grp, const LSX_STRING *vi);


/*
 *	语言函数
 */

int lsx_read_langcode(LSX_FILE file, char code[], char code_en[], int size);
int lsx_read_langname(LSX_FILE file, char name[], char name_en[], int size);

int lsx_selectreadtextlang(LSX_FILE file, const char *langcode);


/*
 *	其它函数：
 */

unsigned short lsx_read_fileversion(LSX_FILE file);


#ifdef __cplusplus
}
#endif

#endif /* _LSX_H */

