#ifndef _LSXDEF_H
#define _LSXDEF_H

#ifdef WIN32
#include <windows.h>
#endif 

#include <stdio.h>

#ifndef WIN32
#define TRUE					1
#define FALSE					0

typedef int						BOOL;
typedef unsigned char			BYTE;
typedef unsigned short			WORD;
typedef unsigned int			UINT;
#endif /* !WIN32 */

#define LSX_FLAG				"LSX8"

#define LSX_API_MAJOR			3
#define LSX_API_MINOR			0
#define LSX_FILEVERSION			((LSX_API_MAJOR << 8) | (LSX_API_MINOR))
#define LSX_FIRSTVERSION		0x0200		// File Version
#define LSX_SECONDVERSION		0x0201
#define LSX_FILEVERSION_V2		0x0200
#define LSX_FILEVERSION_V3		0x0300

#define FILEVER(v)				((v) & 0xFF00)
#define IS_V2(v)				(FILEVER((v)) == LSX_FILEVERSION_V2)
#define IS_V3(v)				(FILEVER((v)) == LSX_FILEVERSION_V3)

#define LENBYTES_V2				1
#define LENBYTES_V3				2

#define OFFSET_DATA					0x100

#define ID_BASEINFO					1
#define ID_AUTOINFO					2
#define ID_SPINFO					3
#define ID_USERINFO					4

/* ���ƿ����� */

#define CB_TYPE_RECGROUP			1		/* ��¼�� */
#define CB_TYPE_TEXT				2		/* �ı� */
#define CB_TYPE_DTC					3		/* ������ */
#define CB_TYPE_DATASTREAM			4		/* ������ */
#define CB_TYPE_DSBASICS			5		/* ������������Ϣ */
#define CB_TYPE_VERSIONINFO			6		/* �汾��Ϣ */
#define CB_TYPE_FREEZEFRAME			7		/* ����֡ */
#define CB_TYPE_READINIESS			8		/* READINESS */


#define RECORDSIZE_DTC				6
#define RECORDSIZE_VERSIONINFO		2



/* ��Ϣ�ṹ���� */

typedef struct _LSX_FILE_HEADER
{
	unsigned char flag[4];			/* �ļ���ʶ */
	unsigned short version;			/* �ļ��汾 */
	unsigned short info_offset;		/* ��Ϣ��ƫ�Ƶ�ַ */
	unsigned int firstcb_offset;	/* ��һ�����ƿ���ļ�ƫ�Ƶ�ַ */
	unsigned int lastcb_offset;		/* ���һ�����ƿ���ļ�ƫ�Ƶ�ַ */
	unsigned int reserved;			/* ���� */
} LSX_FILE_HEADER;

typedef struct _TEXT_STATS
{
	int htbl_size;					/* ��ϣ���С */
	int node_count;					/* �ı��ڵ��� */
	WORD htbl_elements;				/* ��ϣ�����ʹ���� */
	WORD collision_count;			/* ��ϣ���ͻ���� */
	WORD nodeblk_size;				/* �ı��ڵ���С */
	WORD textblk_size;				/* �ı������С */
	WORD nodeblk_count;				/* �ı��ڵ����� */
	WORD textblk_count;				/* �ı�������� */
	int repeat_count;				/* �ı��ظ����� */
} TEXT_STATS;

typedef struct _LSX_FILE_BASEINFO
{
	unsigned char size;				/* ��Ϣ���С */
	unsigned char id;				/* ��Ϣ���ʶ */
	unsigned short productid;		/* ��Ʒ���� */
	unsigned short codepage;		/* ���Դ���ҳ����Ӣ�����ԣ� */
	union 
	{
		char code[4];				/* ���Դ��봮, ����LSX API V2 */	
		unsigned short namecode[2];	/* LSX API V3 ʹ�ã��ڶ���Ԫ��ΪӢ�� */
	} lang;
	unsigned short version;			/* ��������汾 */
	unsigned short creationtime;	/* �ļ�����ʱ�� */
	unsigned short recgroups;		/* ��¼���� */
	unsigned char serailno[20];		/* ��Ʒ���кŴ� */
	TEXT_STATS stats;				/* �����ֶ� */
} LSX_FILE_BASEINFO;

typedef struct _LSX_FILE_AUTOINFO
{
	unsigned char size;				/* ��Ϣ���С */
	unsigned char id;				/* ��Ϣ���ʶ */
	unsigned short model;			/* ������ */
	unsigned short make;			/* ��ϵ�� */
	unsigned short year;			/* ��� */
	unsigned short madein;			/* ������ */
	unsigned short chassis;			/* ���� */
	unsigned short enginemodel;		/* �������ͺ� */
	unsigned short displacement;	/* ���� */
	unsigned char vin[20];			/* VIN�봮 */
} LSX_FILE_AUTOINFO;

typedef struct _LSX_FILE_SPINFO
{
	unsigned char size;				/* ��Ϣ���С */
	unsigned char id;				/* ��Ϣ���ʶ */
	unsigned short name;			/* ���������� */
	unsigned short phone;			/* �绰���� */
} LSX_FILE_SPINFO;

typedef struct _LSX_FILE_USERINFO
{
	unsigned char size;				/* ��Ϣ���С */
	unsigned char id;				/* ��Ϣ���ʶ */
	unsigned short name;			/* �û��� */
	unsigned short phone;			/* �绰���� */
	unsigned short license;			/* ���֤���� */
} LSX_FILE_USERINFO;


/* ���ƿ�ͽڵ�ṹ���� */

typedef struct _LSX_FILE_CB
{
	unsigned short size;			/* ���ƿ��С */
	unsigned short type;			/* ���ƿ����� */
	unsigned int nextcb_offset;		/* ��һ�����ƿ���ļ�ƫ�Ƶ�ַ */
	unsigned int rgnsize;			/* ���ƿ����������С */
} LSX_FILE_CB;

typedef struct _LSX_FILE_TCB		/* �ı����ƿ� */
{
	LSX_FILE_CB cb;
	unsigned int text_count;		/* �ı��� */
} LSX_FILE_TCB;

typedef struct _LSX_FILE_RGCB		/* ��¼����ƿ� */
{
	LSX_FILE_CB cb;
	unsigned short name;			/* ���ƿ����� */
	unsigned short vin;				/* �������͵�VIN�� */
	unsigned short protocol;		/* ���Э������ */
	unsigned short dsinterval;		/* ���������ʱ�䣨ms����LSX API V3ʹ�� */
	unsigned short starttime;		/* ��¼��ʼʱ�� */
	unsigned short endtime;			/* ��¼����ʱ�� */
	unsigned int firstrcb_offset;	/* ��һ����¼���ƿ���ļ�ƫ�Ƶ�ַ */
	unsigned int lastrcb_offset;	/* ���һ����¼���ƿ���ļ�ƫ�Ƶ�ַ */
	unsigned int firstdtcrcb_offset; /* ��һ���������¼���ƿ���ļ�ƫ�Ƶ�ַ */
	unsigned int lastdtcrcb_offset;	/* ���һ���������¼���ƿ���ļ�ƫ�Ƶ�ַ */
} LSX_FILE_RGCB;

typedef struct _LSX_FILE_RCB		/* ��¼���ƿ� */
{
	LSX_FILE_CB cb;
	unsigned short recordsize;		/* ��¼��С */
	union 
	{
		unsigned short starttime;	/* ��¼��ʼʱ�䣨��������ƿ飩 */
		unsigned short dtc;			/* �����루����֡���ƿ飩 */
		unsigned short reserved;
	} un;
} LSX_FILE_RCB;

typedef struct _CB_NODE
{
	unsigned short size;			/* �ڵ��С */
	unsigned short type;			/* �ڵ����� */
	unsigned int offset;			/* ��ָ��CB����ļ�ƫ�Ƶ�ַ */
	LSX_FILE_CB *cb;				/* CB��ָ�� */
	struct _CB_NODE *next;			/* ��һ��CB�ڵ�ָ�� */
	struct _CB_NODE *prev;			/* ǰһ��CB�ڵ�ָ�� */
} CB_NODE;

typedef struct _CBNODE_LIST
{
	int count;						/* �ڵ��� */
	CB_NODE *head;					/* ͷ�ڵ�ָ�� */
	CB_NODE *tail;					/* β�ڵ�ָ�� */
} CBNODE_LST;

struct _FILE_T;
typedef struct _RGCB_NODE
{
	CB_NODE cbnode;
	struct _FILE_T *file;
	CB_NODE *ds_basics;				/* ������������Ϣ�����ơ���λ�����ͣ����ƿ�ڵ㣬LSX API V3����������Ϣ */
	CBNODE_LST rcblst;				/* ��¼���ƿ����� */
	CBNODE_LST dtclst;				/* DTC��¼���ƿ����� */
} RGCB_NODE;

typedef struct _RCB_NODE
{
	CB_NODE cbnode;
	RGCB_NODE *parent;
} RCB_NODE;

typedef struct _DTCCB_NODE
{
	CB_NODE cbnode;
	RGCB_NODE *parent;
	struct _DTCCB_NODE *dtc_next;
	struct _DTCCB_NODE *dtc_prev;
} DTCCB_NODE;

typedef struct _TEXT_NODE
{
	unsigned int number;			/* �ı���� */
	char *text;						/* �ı��� */
	struct _TEXT_NODE *htbl_next;	/* ��ϣ��ĳ����λ���������һ���ı��ڵ� */
	struct _TEXT_NODE *lst_next;	/* ��������������һ���ı��ڵ� */
} TEXT_NODE;

typedef struct _TEXTNODE_LIST
{
	int count;						/* �ڵ��� */
	unsigned int saved_number;		/* �Ѵ洢�������� */
	TEXT_NODE *head;				/* ͷ�ڵ�ָ�� */
	TEXT_NODE *tail;				/* β�ڵ�ָ�� */
	TEXT_NODE *notsaved;			/* δ�洢���׽ڵ�ָ�� */
} TEXTNODE_LST;


/* ��Ҫ�Ľṹ���� */

typedef struct _MEM_BLOCK
{
	int size;						/* ÿ����Ĵ�С */
	int blocks;						/* ���� */
	void *head;						/* ͷ��ָ�� */
	void *tail;						/* β��ָ�� */
} MEM_BLOCK;

typedef struct _HASH_TABLE
{
	int size;						/* ��ϣ���С */
	int nodes;						/* �洢���ı��ڵ��� */
	int elements;					/* ��ռ�õĹ�ϣ��Ԫ���� */
	int lenbytes;					/* �ı����еĳ����ֽ�����LSX API V3���� */
	TEXT_NODE **startaddr;			/* ��ϣ���ڴ���ʼ��ַ */
	MEM_BLOCK *nodeblock;			/* �ı��ڵ�� */
	MEM_BLOCK *textblock;			/* �ı����� */
	TEXTNODE_LST *numberlist;		/* ���ı����������ı��ڵ����� */
} HASH_TBL;

typedef struct _FILE_INFO
{
	int size;						/* �ļ���С */
	UINT flag;						/* �洢��־ */
	UINT newcb_offset;				/* �������ƿ���ļ�ƫ�Ƶ�ַ */
	LSX_FILE_HEADER *header;		/* HEADERָ�� */
	LSX_FILE_BASEINFO *fbi;			/* BASEINFOָ�� */
	LSX_FILE_AUTOINFO *fai;			/* AUTOINFOָ�� */
	LSX_FILE_SPINFO *fspi;			/* SPINFOָ�� */
	LSX_FILE_USERINFO *fui;			/* USERINFOָ�� */
	CBNODE_LST *cblst;				/* CB�ڵ����� */
	MEM_BLOCK *cbblock;				/* CB�� */
} FILE_INFO;

typedef struct _LSX_T
{
	HASH_TBL *htbl;					/* HASH_TBL�ṹָ�� */
	FILE_INFO *fi;					/* FILE_INFO�ṹָ�� */
} LSX_T;

typedef struct _ITEM_T
{
	int index;						/* ��¼��һ��RCB�е���ţ���0���� */
	RCB_NODE *rcb_node;				/* RCB�ڵ�ָ�� */
} ITEM_T;

typedef struct _LANG_CODE
{
	char code[4];					/* �������Դ��� */
	char code_en[4];				/* Ӣ�����Դ��� */
	int offset;						/* �ı����е�ǰ�����ı���ƫ�ƣ�ȡֵ[0|sizeof(WORD)] */
} LANG_CODE;						/* V3 ���� */

typedef struct _FILE_T
{
	int mode;						/* x431�ļ��Ĵ�ģʽ */
	FILE *fp;						/* �ļ�ָ�� */
	HASH_TBL *htbl;					/* HASH_TBL�ṹָ�� */
	FILE_INFO *fi;					/* FILE_INFO�ṹָ�� */
	LANG_CODE lang;					/* �ļ����ı����Խṹ��LSX API V3���� */
	ITEM_T item;					/* ITEM_T�ṹ */
} FILE_T;


#endif /* _LSXDEF_H */













