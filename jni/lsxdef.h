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

/* 控制块类型 */

#define CB_TYPE_RECGROUP			1		/* 记录组 */
#define CB_TYPE_TEXT				2		/* 文本 */
#define CB_TYPE_DTC					3		/* 故障码 */
#define CB_TYPE_DATASTREAM			4		/* 数据流 */
#define CB_TYPE_DSBASICS			5		/* 数据流基本信息 */
#define CB_TYPE_VERSIONINFO			6		/* 版本信息 */
#define CB_TYPE_FREEZEFRAME			7		/* 冻结帧 */
#define CB_TYPE_READINIESS			8		/* READINESS */


#define RECORDSIZE_DTC				6
#define RECORDSIZE_VERSIONINFO		2



/* 信息结构定义 */

typedef struct _LSX_FILE_HEADER
{
	unsigned char flag[4];			/* 文件标识 */
	unsigned short version;			/* 文件版本 */
	unsigned short info_offset;		/* 信息区偏移地址 */
	unsigned int firstcb_offset;	/* 第一个控制块的文件偏移地址 */
	unsigned int lastcb_offset;		/* 最后一个控制块的文件偏移地址 */
	unsigned int reserved;			/* 保留 */
} LSX_FILE_HEADER;

typedef struct _TEXT_STATS
{
	int htbl_size;					/* 哈希表大小 */
	int node_count;					/* 文本节点数 */
	WORD htbl_elements;				/* 哈希表表项使用数 */
	WORD collision_count;			/* 哈希表冲突计数 */
	WORD nodeblk_size;				/* 文本节点块大小 */
	WORD textblk_size;				/* 文本串块大小 */
	WORD nodeblk_count;				/* 文本节点块个数 */
	WORD textblk_count;				/* 文本串块个数 */
	int repeat_count;				/* 文本重复计数 */
} TEXT_STATS;

typedef struct _LSX_FILE_BASEINFO
{
	unsigned char size;				/* 信息块大小 */
	unsigned char id;				/* 信息块标识 */
	unsigned short productid;		/* 产品代码 */
	unsigned short codepage;		/* 语言代码页（非英文语言） */
	union 
	{
		char code[4];				/* 语言代码串, 用于LSX API V2 */	
		unsigned short namecode[2];	/* LSX API V3 使用，第二个元素为英文 */
	} lang;
	unsigned short version;			/* 测试软件版本 */
	unsigned short creationtime;	/* 文件创建时间 */
	unsigned short recgroups;		/* 记录组数 */
	unsigned char serailno[20];		/* 产品序列号串 */
	TEXT_STATS stats;				/* 保留字段 */
} LSX_FILE_BASEINFO;

typedef struct _LSX_FILE_AUTOINFO
{
	unsigned char size;				/* 信息块大小 */
	unsigned char id;				/* 信息块标识 */
	unsigned short model;			/* 车型名 */
	unsigned short make;			/* 车系名 */
	unsigned short year;			/* 年款 */
	unsigned short madein;			/* 出厂地 */
	unsigned short chassis;			/* 底盘 */
	unsigned short enginemodel;		/* 发动机型号 */
	unsigned short displacement;	/* 排量 */
	unsigned char vin[20];			/* VIN码串 */
} LSX_FILE_AUTOINFO;

typedef struct _LSX_FILE_SPINFO
{
	unsigned char size;				/* 信息块大小 */
	unsigned char id;				/* 信息块标识 */
	unsigned short name;			/* 经销商名称 */
	unsigned short phone;			/* 电话号码 */
} LSX_FILE_SPINFO;

typedef struct _LSX_FILE_USERINFO
{
	unsigned char size;				/* 信息块大小 */
	unsigned char id;				/* 信息块标识 */
	unsigned short name;			/* 用户名 */
	unsigned short phone;			/* 电话号码 */
	unsigned short license;			/* 许可证号码 */
} LSX_FILE_USERINFO;


/* 控制块和节点结构定义 */

typedef struct _LSX_FILE_CB
{
	unsigned short size;			/* 控制块大小 */
	unsigned short type;			/* 控制块类型 */
	unsigned int nextcb_offset;		/* 下一个控制块的文件偏移地址 */
	unsigned int rgnsize;			/* 控制块管理的区域大小 */
} LSX_FILE_CB;

typedef struct _LSX_FILE_TCB		/* 文本控制块 */
{
	LSX_FILE_CB cb;
	unsigned int text_count;		/* 文本数 */
} LSX_FILE_TCB;

typedef struct _LSX_FILE_RGCB		/* 记录组控制块 */
{
	LSX_FILE_CB cb;
	unsigned short name;			/* 控制块名称 */
	unsigned short vin;				/* 所属车型的VIN码 */
	unsigned short protocol;		/* 诊断协议名称 */
	unsigned short dsinterval;		/* 数据流间隔时间（ms），LSX API V3使用 */
	unsigned short starttime;		/* 记录起始时间 */
	unsigned short endtime;			/* 记录结束时间 */
	unsigned int firstrcb_offset;	/* 第一个记录控制块的文件偏移地址 */
	unsigned int lastrcb_offset;	/* 最后一个记录控制块的文件偏移地址 */
	unsigned int firstdtcrcb_offset; /* 第一个故障码记录控制块的文件偏移地址 */
	unsigned int lastdtcrcb_offset;	/* 最后一个故障码记录控制块的文件偏移地址 */
} LSX_FILE_RGCB;

typedef struct _LSX_FILE_RCB		/* 记录控制块 */
{
	LSX_FILE_CB cb;
	unsigned short recordsize;		/* 记录大小 */
	union 
	{
		unsigned short starttime;	/* 记录起始时间（故障码控制块） */
		unsigned short dtc;			/* 故障码（冻结帧控制块） */
		unsigned short reserved;
	} un;
} LSX_FILE_RCB;

typedef struct _CB_NODE
{
	unsigned short size;			/* 节点大小 */
	unsigned short type;			/* 节点类型 */
	unsigned int offset;			/* 所指向CB块的文件偏移地址 */
	LSX_FILE_CB *cb;				/* CB块指针 */
	struct _CB_NODE *next;			/* 下一个CB节点指针 */
	struct _CB_NODE *prev;			/* 前一个CB节点指针 */
} CB_NODE;

typedef struct _CBNODE_LIST
{
	int count;						/* 节点数 */
	CB_NODE *head;					/* 头节点指针 */
	CB_NODE *tail;					/* 尾节点指针 */
} CBNODE_LST;

struct _FILE_T;
typedef struct _RGCB_NODE
{
	CB_NODE cbnode;
	struct _FILE_T *file;
	CB_NODE *ds_basics;				/* 数据流基本信息（名称、单位、类型）控制块节点，LSX API V3增加类型信息 */
	CBNODE_LST rcblst;				/* 记录控制块链表 */
	CBNODE_LST dtclst;				/* DTC记录控制块链表 */
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
	unsigned int number;			/* 文本序号 */
	char *text;						/* 文本串 */
	struct _TEXT_NODE *htbl_next;	/* 哈希表某表项位置链表的下一个文本节点 */
	struct _TEXT_NODE *lst_next;	/* 序号排序链表的下一个文本节点 */
} TEXT_NODE;

typedef struct _TEXTNODE_LIST
{
	int count;						/* 节点数 */
	unsigned int saved_number;		/* 已存储的最大序号 */
	TEXT_NODE *head;				/* 头节点指针 */
	TEXT_NODE *tail;				/* 尾节点指针 */
	TEXT_NODE *notsaved;			/* 未存储的首节点指针 */
} TEXTNODE_LST;


/* 主要的结构定义 */

typedef struct _MEM_BLOCK
{
	int size;						/* 每个块的大小 */
	int blocks;						/* 块数 */
	void *head;						/* 头块指针 */
	void *tail;						/* 尾块指针 */
} MEM_BLOCK;

typedef struct _HASH_TABLE
{
	int size;						/* 哈希表大小 */
	int nodes;						/* 存储的文本节点数 */
	int elements;					/* 已占用的哈希表元素数 */
	int lenbytes;					/* 文本串中的长度字节数，LSX API V3增加 */
	TEXT_NODE **startaddr;			/* 哈希表内存起始地址 */
	MEM_BLOCK *nodeblock;			/* 文本节点块 */
	MEM_BLOCK *textblock;			/* 文本串块 */
	TEXTNODE_LST *numberlist;		/* 按文本序号排序的文本节点链表 */
} HASH_TBL;

typedef struct _FILE_INFO
{
	int size;						/* 文件大小 */
	UINT flag;						/* 存储标志 */
	UINT newcb_offset;				/* 新增控制块的文件偏移地址 */
	LSX_FILE_HEADER *header;		/* HEADER指针 */
	LSX_FILE_BASEINFO *fbi;			/* BASEINFO指针 */
	LSX_FILE_AUTOINFO *fai;			/* AUTOINFO指针 */
	LSX_FILE_SPINFO *fspi;			/* SPINFO指针 */
	LSX_FILE_USERINFO *fui;			/* USERINFO指针 */
	CBNODE_LST *cblst;				/* CB节点链表 */
	MEM_BLOCK *cbblock;				/* CB块 */
} FILE_INFO;

typedef struct _LSX_T
{
	HASH_TBL *htbl;					/* HASH_TBL结构指针 */
	FILE_INFO *fi;					/* FILE_INFO结构指针 */
} LSX_T;

typedef struct _ITEM_T
{
	int index;						/* 记录在一个RCB中的序号，从0算起 */
	RCB_NODE *rcb_node;				/* RCB节点指针 */
} ITEM_T;

typedef struct _LANG_CODE
{
	char code[4];					/* 本地语言代码 */
	char code_en[4];				/* 英文语言代码 */
	int offset;						/* 文本块中当前语言文本的偏移，取值[0|sizeof(WORD)] */
} LANG_CODE;						/* V3 增加 */

typedef struct _FILE_T
{
	int mode;						/* x431文件的打开模式 */
	FILE *fp;						/* 文件指针 */
	HASH_TBL *htbl;					/* HASH_TBL结构指针 */
	FILE_INFO *fi;					/* FILE_INFO结构指针 */
	LANG_CODE lang;					/* 文件的文本语言结构，LSX API V3增加 */
	ITEM_T item;					/* ITEM_T结构 */
} FILE_T;


#endif /* _LSXDEF_H */













