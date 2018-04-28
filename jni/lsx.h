#ifndef _LSX_H
#define _LSX_H


#define MODE_READ				1
#define MODE_WRITE				2

/* ��Ʒ���� */
#define PRODUCT_UNKNOWN			0x00		/* Unknown */
#define PRODUCT_X431			0x01		/* X431 */
#define PRODUCT_X431INFINITE	0x02		/* X431 Infinite */
#define PRODUCT_X431TOP			0x03		/* X431 Top */
#define PRODUCT_X431TOOL		0x04		/* X431 Tool */
#define PRODUCT_X431PC			0x05		/* X431 PC */

#define PRODUCT_PCLINK			0x11		/* PCLink */
#define PRODUCT_PCCENTER		0x12        /* PCCenter */
#define PRODUCT_ADM				0x13		/* ADM */
#define PRODUCT_RECORDER		0x14		/* ������¼�� */
#define PRODUCT_CRECORDER		0x15		/* CRecorder */

/* ��¼���� */
#define RECORD_DTC				0x01		/* DTC */
#define RECORD_DATASTREAM		0x02		/* Data Stream */
#define RECORD_VERSIONINFO		0x04		/* Version Info */
#define RECORD_FREEZEFRAME		0x08		/* Freeze Frame */
#define RECORD_READINESS		0x10		/* Readiness */
#define RECORD_DSBASICS			0x20		/* Data Stream Basics */

/* �ļ��򿪲����Ĵ����� */
#define LSX_ERR_OK					0		/* �����ɹ� */
#define LSX_ERR_LOW_FILEVERSION		-1		/* �ļ��汾Ϊ�Ͱ汾 */
#define LSX_ERR_HIGH_FILEVERSION	-2		/* �����ݵ��ļ��汾 */
#define LSX_ERR_FILE_NOTFOUND		-3		/* �ļ������� */
#define LSX_ERR_INCORRECT_FORMAT	-4		/* �ļ���ʽ����ȷ */
#define LSX_ERR_INVALID_PARAMETER	-5		/* ��Ч���� */
#define LSX_ERR_ALLOC_MEMORY        -6		/* �����ڴ���� */

/* �ļ������ԣ����汾���ɶ�д���ԣ� */
#define LSX_FILE_READABLE			0x0001	/* �ļ���Ч���ɽ��ж����� */
#define LSX_FILE_WRITABLE			0x0002	/* �ļ���Ч���ɽ���д���� */
#define LSX_FILE_V2					0x1000	/* �ļ����汾ΪV2 */
#define LSX_FILE_V3					0x2000	/* �ļ����汾ΪV3 */

/* �ض���������λ */
#define DSUNIT_TIME				"dstime"	/* ʱ�� */
#define DSUNIT_DTCS				"dtcs"		/* ��������� */

/* ������������� */
#define MAX_DS_COLNUMBER		300

/* δ��������������� */
#define DS_TYPE_UNKNOWN			0x0000

typedef void *HLSX;
typedef void *LSX_FILE;
typedef void *LSX_RECGROUP;
typedef void *LSX_RECITEM;

typedef struct _LSX_STRING
{
	char *str;								/* ���������ı� */
	char *str_en;							/* Ӣ���ı� */
} LSX_STRING;

typedef struct _LSX_BASEINFO
{
	char serialno[20];						/* ��Ʒ���к� */
	unsigned short productid;				/* ��Ʒ���� */
	unsigned short codepage;				/* �������Դ���ҳ */
	char langname[30];						/* �������������ַ��� */
	char langcode[4];						/* �������Դ��봮 */
	char langcode_en[4];					/* Ӣ�����Դ��봮 */
	char *diagversion;						/* �������汾 */
	char *creationtime;						/* �ļ�����ʱ�� */
} LSX_BASEINFO;

typedef struct _LSX_AUTOINFO
{
	char vin[20];							/* VIN �룬��������ʹ�� */
	char *make;								/* ��ϵ�� */
	char *model;							/* ������ */
	char *year;								/* ������� */
	char *madein;							/* ������ */
	char *chassis;							/* ���� */
	char *enginemodel;						/* �������ͺ� */
	char *displacement;						/* ���� */
} LSX_AUTOINFO;

typedef struct _LSX_SPINFO
{
	char *name;								/* ���������� */
	char *phone;							/* �����̵绰 */
} LSX_SPINFO;

typedef struct _LSX_USERINFO
{
	char *name;								/* �û����� */
	char *phone;							/* �û��绰 */
	char *license;							/* ���֤�� */
} LSX_USERINFO;

#ifdef __cplusplus
extern "C" {
#endif
	
/*
 *	��ʼ��������
 */
	
HLSX lsx_init();
int lsx_deinit(HLSX hlsx);

/*
 *	�ļ��򿪺͹ر�
 */

LSX_FILE lsx_open(HLSX hlsx, const char *filename, int mode, int *error);
int lsx_close(LSX_FILE file);

/* 
 * ����ļ��Ƿ���Ч 
 */

unsigned int lsx_checkfile(const char *filename);

/*
 *	��д������Ϣ
 */

int lsx_read_baseinfo(LSX_FILE file, LSX_BASEINFO *baseinfo);
int lsx_write_baseinfo(LSX_FILE file, const LSX_BASEINFO *baseinfo);

/*
 *	��д������Ϣ
 */

int lsx_read_autoinfo(LSX_FILE file, LSX_AUTOINFO *autoinfo);
int lsx_write_autoinfo(LSX_FILE file, const LSX_AUTOINFO *autoinfo);

/*
 *	��д��������Ϣ
 */

int lsx_read_spinfo(LSX_FILE file, LSX_SPINFO *spinfo);
int lsx_write_spinfo(LSX_FILE file, const LSX_SPINFO *spinfo);

/*
 *	��д�û���Ϣ
 */

int lsx_read_userinfo(LSX_FILE file, LSX_USERINFO *userinfo);
int lsx_write_userinfo(LSX_FILE file, const LSX_USERINFO *userinfo);


/*
 *	��д��¼��Ϣ
 */

/* ��¼����� */

LSX_RECGROUP lsx_rec_writenewgroup(LSX_FILE file, const char *name, 
	const char *protocol, const char *vin, const char *starttime, int dsinterval);
int lsx_rec_finishnewgroup(LSX_RECGROUP grp, const char *endtime);

int lsx_rec_readgroupcount(LSX_FILE file);
LSX_RECGROUP lsx_rec_readgroupid(LSX_FILE file, int i);
int lsx_rec_readgroupinfo(LSX_RECGROUP grp, char **name, char **protocol, 
	char **vin, char **starttime, char **endtime, int *dsinterval);
unsigned int lsx_rec_readalltype(LSX_RECGROUP grp);

int lsx_rec_modifygroupinfo(LSX_RECGROUP grp, const char *name, const char *vin);

/* ��¼���� */

unsigned int lsx_rec_readitemtype(LSX_RECITEM item);
LSX_RECITEM lsx_rec_readfirstitem(LSX_RECGROUP grp);
LSX_RECITEM lsx_rec_readlastitem(LSX_RECGROUP grp);
LSX_RECITEM lsx_rec_readnextitem(LSX_RECITEM item);
LSX_RECITEM lsx_rec_readprevitem(LSX_RECITEM item);


/* ��������� */

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

/* ��̬��������� */

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

/* ����֡��� */

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

/* Readiness��� */

int lsx_rec_readrdncolcount(LSX_RECITEM item);
int lsx_rec_readrdnname(LSX_RECITEM item, char *textstrs[], int n);
int lsx_rec_readreadiness(LSX_RECITEM item, char *textstrs[], int n);

int lsx_rec_writereadiness(LSX_RECGROUP grp, 
   const LSX_STRING *namestrs[], const LSX_STRING *textstrs[], int n);

/* �汾��Ϣ��� */

int lsx_rec_readvi(LSX_RECGROUP grp, char **vi);

int lsx_rec_writevi(LSX_RECGROUP grp, const LSX_STRING *vi);


/*
 *	���Ժ���
 */

int lsx_read_langcode(LSX_FILE file, char code[], char code_en[], int size);
int lsx_read_langname(LSX_FILE file, char name[], char name_en[], int size);

int lsx_selectreadtextlang(LSX_FILE file, const char *langcode);


/*
 *	����������
 */

unsigned short lsx_read_fileversion(LSX_FILE file);


#ifdef __cplusplus
}
#endif

#endif /* _LSX_H */

