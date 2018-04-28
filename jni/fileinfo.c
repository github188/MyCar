#include <string.h>
#include <stdlib.h>

#include "fileinfo.h"
#include "util.h"
#include "hash.h"
#include "list.h"
#include "file.h"

/* 语言代码，旧定义的 */
#define LANGUAGE_SIMPLIFIED_CHINESE			"chs"		/* 简体中文 */
#define LANGUAGE_TRADITIONAL_CHINESE		"cht"		/* 繁体中文 */
#define LANGUAGE_JAPANESE					"ja"		/* 日语 */
#define LANGUAGE_GREEK						"el"		/* 希腊语 */

/* 新定义的语言代码，拷贝自 x431lang.h */
#define XLANG_CHINESE_SIMPLIFIED            "cn"		/* 简体中文 */
#define XLANG_CHINESE_TRADITIONAL           "hk"		/* 繁体中文 */
#define XLANG_JAPANESE                      "jp"		/* 日语 */
#define XLANG_GREEK                         "gr"		/* 希腊语 */
#define XLANG_ENGLISH                       "en"		/* 英语 */

#define LANGNAME_ENGLISH					"English"
#define CODEPAGE_ENGLISH					1252

static BOOL check_rgcb(FILE *fp, LSX_FILE_RGCB *rgcb, long base);
static int get_recgroupcount(CBNODE_LST *lst);

static void save_newtcb(FILE_T *fp);
static void rgcb_loadrcblist(FILE_T *fp, const LSX_FILE_RGCB *rgcb, RGCB_NODE *rgcb_node);

static RCB_NODE *create_rcbnode(MEM_BLOCK *cbblk, UINT type, long cb_offset);
static RGCB_NODE *create_rgcbnode(FILE_T *fp, MEM_BLOCK *cbblk, long cb_offset);

static int rec_readcolcount(LSX_RECITEM item);
static BOOL rec_readitem(LSX_RECITEM item, int index, char *textstrs[], int n);

static LSX_RECITEM rec_readnextntypeitem(LSX_RECITEM item, UINT itemtype, int n);
static LSX_RECITEM rec_readprevntypeitem(LSX_RECITEM item, UINT itemtype, int n);

static void translatecode(char code[], int size);

FILE_INFO *fi_init(int cbblk_size)
{
	FILE_INFO *fi = NULL;
	CBNODE_LST *cblst = NULL;
	MEM_BLOCK *cbblock = NULL;
	LSX_FILE_HEADER *header = NULL;
	LSX_FILE_BASEINFO *fbi = NULL;
	LSX_FILE_AUTOINFO *fai = NULL;
	LSX_FILE_SPINFO *fspi = NULL;
	LSX_FILE_USERINFO *fui = NULL;
	
	cbblock = mem_init(cbblk_size);
	if (!cbblock) goto error;
	
	cblst = (CBNODE_LST *)malloc(sizeof(CBNODE_LST));
	if (!cblst) goto error;
	
	header = (LSX_FILE_HEADER *)malloc(sizeof(LSX_FILE_HEADER));
	if (!header) goto error;
	fbi = (LSX_FILE_BASEINFO *)malloc(sizeof(LSX_FILE_BASEINFO));
	if (!fbi) goto error;
	fai = (LSX_FILE_AUTOINFO *)malloc(sizeof(LSX_FILE_AUTOINFO));
	if (!fai) goto error;
	fspi = (LSX_FILE_SPINFO *)malloc(sizeof(LSX_FILE_SPINFO));
	if (!fspi) goto error;
	fui = (LSX_FILE_USERINFO *)malloc(sizeof(LSX_FILE_USERINFO));
	if (!fui) goto error;

	fi = (FILE_INFO *)malloc(sizeof(FILE_INFO));
	if (!fi) goto error;

	memset(header, 0, sizeof(LSX_FILE_HEADER));
	memset(fbi, 0, sizeof(LSX_FILE_BASEINFO));
	memset(fai, 0, sizeof(LSX_FILE_AUTOINFO));
	memset(fspi, 0, sizeof(LSX_FILE_SPINFO));
	memset(fui, 0, sizeof(LSX_FILE_USERINFO));

	fbi->size = sizeof(LSX_FILE_BASEINFO);
	fbi->id = ID_BASEINFO;
	fai->size = sizeof(LSX_FILE_AUTOINFO);
	fai->id = ID_AUTOINFO;
	fspi->size = sizeof(LSX_FILE_SPINFO);
	fspi->id = ID_SPINFO;
	fui->size = sizeof(LSX_FILE_USERINFO);
	fui->id = ID_USERINFO;
	
	fi->header = header;
	fi->fbi = fbi;
	fi->fai = fai;
	fi->fspi = fspi;
	fi->fui = fui;

	fi->cbblock = cbblock;
	fi->size = 0;
	fi->flag = 0;
	fi->newcb_offset = OFFSET_DATA;
	
	list_init_cbnode(cblst);
	fi->cblst = cblst;

	return fi;
	
error:
	if (cbblock) mem_deinit(cbblock);
	if (cblst) free(cblst);
	if (header) free(header);
	if (fbi) free(fbi);
	if (fai) free(fai);
	if (fspi) free(fspi);
	if (fui) free(fui);
	return NULL;
}

BOOL fi_deinit(FILE_INFO *fi)
{
	if (fi)
	{
		free(fi->header);
		free(fi->fbi);
		free(fi->fai);
		free(fi->fspi);
		free(fi->fui);

		free(fi->cblst);
		mem_deinit(fi->cbblock);

		free(fi);
	}
	
	return TRUE;
}

int fi_checkfile(FILE *fp, unsigned short *version)
{
	long offset;
	unsigned int size;
	unsigned int cbsize;
	unsigned int lastcb_offset;
	LSX_FILE_HEADER head;
	LSX_FILE_CB cb;
	int result = CHECKFILE_OK;
	
	fseek(fp, 0, SEEK_SET);
	if (file_read(&head, sizeof(head), 1, fp, BUFTYPE_LSX_FILE_HEADER) != 1)
	{
		result = CHECKFILE_ERR_READHEADER;
		goto exit;
	}

	if (memcmp(head.flag, LSX_FLAG, sizeof(head.flag)) != 0)
	{
		result = CHECKFILE_ERR_FLAG;
		goto exit;
	}
	
	if (head.version < LSX_FIRSTVERSION)
	{
		result = CHECKFILE_ERR_VERSION;
		goto exit;
	}

	if (version)
	{
		*version = head.version;
	}
	
	if (((head.version >> 8) & 0xFF) > LSX_API_MAJOR)
	{
		result = CHECKFILE_ERR_HIGH_FILEVERSION;
		goto exit;
	}

	if (((head.version >> 8) & 0xFF) < LSX_API_MAJOR)
	{
		result = CHECKFILE_ERR_LOW_FILEVERSION;
	}

	if (head.info_offset != sizeof(LSX_FILE_HEADER)
		|| head.firstcb_offset != OFFSET_DATA
		|| head.lastcb_offset == 0
		|| head.firstcb_offset > head.lastcb_offset)
	{
		result = CHECKFILE_ERR_HEADER;
		goto exit;
	}
	
	fseek(fp, 0, SEEK_END);
	size = (unsigned int)ftell(fp);
	if (head.lastcb_offset >= size)
	{
		result = CHECKFILE_ERR_HEADER;
		goto exit;
	}
	
	offset = head.firstcb_offset;
	while (1)
	{
		fseek(fp, offset, SEEK_SET);
		if (file_read(&cb, sizeof(cb), 1, fp, BUFTYPE_LSX_FILE_CB) != 1)
		{
			result = CHECKFILE_ERR_IO;
			goto exit;
		}

		switch (cb.type)
		{
		case CB_TYPE_RECGROUP:
			cbsize = sizeof(LSX_FILE_RGCB);
			break;
		case CB_TYPE_TEXT:
			cbsize = sizeof(LSX_FILE_TCB);
			break;
		default:
			cbsize = 0;
			break;
		}

		if (cbsize == 0 || cb.size != cbsize)
		{
			result = CHECKFILE_ERR_CBSIZE;
			goto exit;
		}

		if (cb.type == CB_TYPE_RECGROUP)
		{
			LSX_FILE_RGCB rgcb;

			fseek(fp, offset, SEEK_SET);
			if (file_read(&rgcb, sizeof(rgcb), 1, fp, BUFTYPE_LSX_FILE_RGCB) != 1
				|| !check_rgcb(fp, &rgcb, 0))
			{
				result = CHECKFILE_ERR_RGCB;
				goto exit;
			}
		}

		if (cb.nextcb_offset == 0)
		{
			lastcb_offset = offset;
			break;
		}
		else
		{
			offset = cb.nextcb_offset;
		}
	}

	if (head.lastcb_offset != lastcb_offset)
	{
		result = CHECKFILE_ERR_LASTCB;
	}

exit:
	return result;
}

BOOL check_rgcb(FILE *fp, LSX_FILE_RGCB *rgcb, long base)
{
	long offset;
	LSX_FILE_CB cb;
	unsigned int cbsize;
	unsigned int lastcb_offset;
	
	if (rgcb->firstdtcrcb_offset > rgcb->lastdtcrcb_offset
		|| rgcb->firstrcb_offset > rgcb->lastrcb_offset
		|| (rgcb->firstdtcrcb_offset == 0 && rgcb->lastdtcrcb_offset > 0)
		|| (rgcb->firstrcb_offset == 0 && rgcb->lastrcb_offset > 0))
	{
		return FALSE;
	}

	if (rgcb->firstrcb_offset == 0)
	{
		return TRUE;
	}

	offset = base + rgcb->firstrcb_offset;
	while (1)
	{
		fseek(fp, offset, SEEK_SET);
		if (file_read(&cb, sizeof(cb), 1, fp, BUFTYPE_LSX_FILE_CB) != 1)
		{
			goto exit;
		}
		
		switch (cb.type)
		{
		case CB_TYPE_DTC:
		case CB_TYPE_DATASTREAM:
		case CB_TYPE_DSBASICS:
		case CB_TYPE_VERSIONINFO:
		case CB_TYPE_FREEZEFRAME:
		case CB_TYPE_READINIESS:
			cbsize = sizeof(LSX_FILE_RCB);
			break;
		default:
			cbsize = 0;
			break;
		}
		
		if (cbsize == 0 || cb.size != cbsize)
		{
			goto exit;
		}
		
		if (cb.nextcb_offset == 0)
		{
			lastcb_offset = offset;
			break;
		}
		else
		{
			offset = base + cb.nextcb_offset;
		}
	}

	if (lastcb_offset == base + rgcb->lastrcb_offset)
	{
		if (rgcb->firstdtcrcb_offset == 0)
		{
			return TRUE;
		}

		offset = base + rgcb->firstdtcrcb_offset;
		fseek(fp, offset, SEEK_SET);
		if (file_read(&cb, sizeof(cb), 1, fp, BUFTYPE_LSX_FILE_CB) != 1
			|| cb.type != CB_TYPE_DTC)
		{
			goto exit;
		}

		offset = base + rgcb->lastdtcrcb_offset;
		fseek(fp, offset, SEEK_SET);
		if (file_read(&cb, sizeof(cb), 1, fp, BUFTYPE_LSX_FILE_CB) != 1
			|| cb.type != CB_TYPE_DTC)
		{
			goto exit;
		}

		return TRUE;
	}

exit:
	return FALSE;
}

void fi_clear(FILE_T *fp)
{
	FILE_INFO *fi = fp->fi;
	HASH_TBL *htbl = fp->htbl;
				
	fi->flag = 0;
	fi->size = 0;
	fi->newcb_offset = 0;
	list_init_cbnode(fi->cblst);
	mem_free(fi->cbblock);
	
	memset(fi->header, 0, sizeof(LSX_FILE_HEADER));
	memset(fi->fbi, 0, sizeof(LSX_FILE_BASEINFO));
	memset(fi->fai, 0, sizeof(LSX_FILE_AUTOINFO));
	memset(fi->fspi, 0, sizeof(LSX_FILE_SPINFO));
	memset(fi->fui, 0, sizeof(LSX_FILE_USERINFO));

	hash_removeall(htbl);
}

BOOL fi_write_baseinfo(FILE_T *fp, const LSX_BASEINFO *baseinfo)
{
	FILE_INFO *fi = fp->fi;
	HASH_TBL *htbl = fp->htbl;
	int snlen = strlen(baseinfo->serialno);
	long offset = fi->header->info_offset;
	char buf[100] = {0};
	char langname[30] = {0};
	fi->flag &= ~FISF_BASEINFO;

	fi->fbi->codepage = baseinfo->codepage;
	memset(fi->fbi->lang.namecode, 0, sizeof(fi->fbi->lang.namecode));
	if (strlen(baseinfo->langcode) > 0)
	{
		if (strlen(baseinfo->langname) > 0)
		{
			strcpy(langname, baseinfo->langname);
		}
		sprintf(buf, "%s.%s", baseinfo->langcode, langname);
		fi->fbi->lang.namecode[0] = hash_add(fp, buf);
	}
	if (strlen(baseinfo->langcode_en) > 0)
	{
		sprintf(buf, "%s.%s", baseinfo->langcode_en, LANGNAME_ENGLISH);
		fi->fbi->lang.namecode[1] = hash_add(fp, buf);
	}

	fi->fbi->productid = baseinfo->productid;
	fi->fbi->version = hash_add(fp, baseinfo->diagversion);
	if (snlen >= sizeof(fi->fbi->serailno)) 
		snlen = sizeof(fi->fbi->serailno) - 1;
	memset(fi->fbi->serailno, 0, sizeof(fi->fbi->serailno));
	memcpy(fi->fbi->serailno, baseinfo->serialno, snlen);
	
	fseek(fp->fp, offset, SEEK_SET);
	return (fwrite(fi->fbi, sizeof(LSX_FILE_BASEINFO), 1, fp->fp) == 1);
}

BOOL fi_write_autoinfo(FILE_T *fp, const LSX_AUTOINFO *autoinfo)
{
	FILE_INFO *fi = fp->fi;
	HASH_TBL *htbl = fp->htbl;
	int vinlen = strlen(autoinfo->vin);
	long offset = fi->header->info_offset + sizeof(LSX_FILE_BASEINFO);
	fi->flag &= ~FISF_AUTOINFO;
	
	fi->fai->model = hash_add(fp, autoinfo->model);
	fi->fai->make = hash_add(fp, autoinfo->make);
	if (vinlen >= sizeof(fi->fai->vin)) 
		vinlen = sizeof(fi->fai->vin) - 1;
	memset(fi->fai->vin, 0, sizeof(fi->fai->vin));
	memcpy(fi->fai->vin, autoinfo->vin, vinlen);
	fi->fai->year = hash_add(fp, autoinfo->year);
	fi->fai->madein = hash_add(fp, autoinfo->madein);
	fi->fai->enginemodel = hash_add(fp, autoinfo->enginemodel);
	fi->fai->displacement = hash_add(fp, autoinfo->displacement);
	fi->fai->chassis = hash_add(fp, autoinfo->chassis);
	
	fseek(fp->fp, offset, SEEK_SET);
	return (fwrite(fi->fai, sizeof(LSX_FILE_AUTOINFO), 1, fp->fp) == 1);
}

BOOL fi_write_spinfo(FILE_T *fp, const LSX_SPINFO *spinfo)
{
	FILE_INFO *fi = fp->fi;
	HASH_TBL *htbl = fp->htbl;
	long offset = fi->header->info_offset +
		sizeof(LSX_FILE_BASEINFO) + sizeof(LSX_FILE_AUTOINFO);
	fi->flag &= ~FISF_SPINFO;
	
	fi->fspi->name = hash_add(fp, spinfo->name);
	fi->fspi->phone = hash_add(fp, spinfo->phone);

	fseek(fp->fp, offset, SEEK_SET);
	return (fwrite(fi->fspi, sizeof(LSX_FILE_SPINFO), 1, fp->fp) == 1);
}

BOOL fi_write_userinfo(FILE_T *fp, const LSX_USERINFO *userinfo)
{
	FILE_INFO *fi = fp->fi;
	HASH_TBL *htbl = fp->htbl;
	long offset = fi->header->info_offset +	sizeof(LSX_FILE_BASEINFO) + 
		sizeof(LSX_FILE_AUTOINFO) + sizeof(LSX_FILE_SPINFO);
	fi->flag &= ~FISF_USERINFO;
	
	fi->fui->name = hash_add(fp, userinfo->name);
	fi->fui->phone = hash_add(fp, userinfo->phone);
	fi->fui->license = hash_add(fp, userinfo->license);
	
	fseek(fp->fp, offset, SEEK_SET);
	return (fwrite(fi->fui, sizeof(LSX_FILE_USERINFO), 1, fp->fp) == 1);
}

BOOL fi_read_baseinfo(FILE_T *fp, LSX_BASEINFO *baseinfo)
{
	FILE_INFO *fi = fp->fi;
	HASH_TBL *htbl = fp->htbl;
	BOOL is_v2 = FALSE;

	if (IS_V2(fi->header->version))
	{
		is_v2 = TRUE;
	}

	baseinfo->codepage = fi->fbi->codepage;
	memset(baseinfo->langcode, 0, sizeof(baseinfo->langcode));
	memset(baseinfo->langcode_en, 0, sizeof(baseinfo->langcode_en));
	memset(baseinfo->langname, 0, sizeof(baseinfo->langname));
	if (is_v2)
	{
		strcpy(baseinfo->langcode, fi->fbi->lang.code);
	}
	else
	{
		char *namecode = hash_gettext(htbl, fi->fbi->lang.namecode[0]);

		strcpy(baseinfo->langcode, fp->lang.code);
		strcpy(baseinfo->langcode_en, fp->lang.code_en);

		if (namecode)
		{
			char *p = strchr(namecode, '.');
			if (p) 
			{
				strncpy(baseinfo->langname, p+1, sizeof(baseinfo->langname)-1);
			}
		}
	}

	baseinfo->productid = fi->fbi->productid;
	strcpy(baseinfo->serialno, (char *)fi->fbi->serailno);
	baseinfo->diagversion = hash_gettext(htbl, fi->fbi->version);
	baseinfo->creationtime = hash_gettext(htbl, fi->fbi->creationtime);

	return TRUE;
}

BOOL fi_read_autoinfo(FILE_T *fp, LSX_AUTOINFO *autoinfo)
{
	FILE_INFO *fi = fp->fi;
	HASH_TBL *htbl = fp->htbl;

	strcpy(autoinfo->vin, (char *)fi->fai->vin);
	autoinfo->make = hash_gettext(htbl, fi->fai->make);
	autoinfo->model = hash_gettext(htbl, fi->fai->model);
	autoinfo->year = hash_gettext(htbl, fi->fai->year);
	autoinfo->madein = hash_gettext(htbl, fi->fai->madein);
	autoinfo->chassis = hash_gettext(htbl, fi->fai->chassis);
	autoinfo->enginemodel = hash_gettext(htbl, fi->fai->enginemodel);
	autoinfo->displacement = hash_gettext(htbl, fi->fai->displacement);
	return TRUE;
}

BOOL fi_read_spinfo(FILE_T *fp, LSX_SPINFO *spinfo)
{
	FILE_INFO *fi = fp->fi;
	HASH_TBL *htbl = fp->htbl;

	spinfo->name = hash_gettext(htbl, fi->fspi->name);
	spinfo->phone = hash_gettext(htbl, fi->fspi->phone);
	return TRUE;
}

BOOL fi_read_userinfo(FILE_T *fp, LSX_USERINFO *userinfo)
{
	FILE_INFO *fi = fp->fi;
	HASH_TBL *htbl = fp->htbl;

	userinfo->name = hash_gettext(htbl, fi->fui->name);
	userinfo->phone = hash_gettext(htbl, fi->fui->phone);
	userinfo->license = hash_gettext(htbl, fi->fui->license);
	return TRUE;
}

BOOL fi_newfile(FILE_T *fp)
{
	FILE_INFO *fi = fp->fi;
	HASH_TBL *htbl = fp->htbl;
	LSX_FILE_TCB *tcb = NULL;
	CB_NODE *cbnode = NULL;
	char t[26] = {0};
	char buf[50] = {0};
	char langname[30] = {0};
	
	fi->flag = FISF_NEWFILE;
	fi->newcb_offset = OFFSET_DATA;
	htbl->lenbytes = LENBYTES_V3;
	
	memset(fi->header, 0, sizeof(LSX_FILE_HEADER));
	memset(fi->fbi, 0, sizeof(LSX_FILE_BASEINFO));
	memset(fi->fai, 0, sizeof(LSX_FILE_AUTOINFO));
	memset(fi->fspi, 0, sizeof(LSX_FILE_SPINFO));
	memset(fi->fui, 0, sizeof(LSX_FILE_USERINFO));
	
	fi->fbi->size = sizeof(LSX_FILE_BASEINFO);
	fi->fbi->id = ID_BASEINFO;
	fi->fai->size = sizeof(LSX_FILE_AUTOINFO);
	fi->fai->id = ID_AUTOINFO;
	fi->fspi->size = sizeof(LSX_FILE_SPINFO);
	fi->fspi->id = ID_SPINFO;
	fi->fui->size = sizeof(LSX_FILE_USERINFO);
	fi->fui->id = ID_USERINFO;
	
	fi->header->version = LSX_FILEVERSION;
	fi->header->info_offset = (WORD)sizeof(LSX_FILE_HEADER);
	fi->header->firstcb_offset = OFFSET_DATA;
	
	fi->fbi->codepage = CODEPAGE_ENGLISH;
	sprintf(buf, "%s.%s", XLANG_ENGLISH, LANGNAME_ENGLISH);
	fi->fbi->lang.namecode[0] = hash_add(fp, buf);

	strcpy(fp->lang.code, XLANG_ENGLISH);
	memset(fp->lang.code_en, 0, sizeof(fp->lang.code_en));
	fp->lang.offset = 0;
	
	fi->fbi->productid = PRODUCT_UNKNOWN;
	get_time(t, sizeof(t));
	fi->fbi->creationtime = hash_add(fp, t);
	
	return TRUE;
}

BOOL fi_save(FILE_T *fp)
{
	FILE_INFO *fi = fp->fi;
	HASH_TBL *htbl = fp->htbl;
	long offset = 0;
	long info_offset = fi->header->info_offset;
	
	if (fi->flag & FISF_AUTOINFO)
	{
		offset = info_offset + sizeof(LSX_FILE_BASEINFO);
		fseek(fp->fp, offset, SEEK_SET);
		fwrite(fi->fai, sizeof(LSX_FILE_AUTOINFO), 1, fp->fp);
		fi->flag &= ~FISF_AUTOINFO;
	}
	
	if (fi->flag & FISF_SPINFO)
	{
		offset = info_offset +
			sizeof(LSX_FILE_BASEINFO) + sizeof(LSX_FILE_AUTOINFO);
		fseek(fp->fp, offset, SEEK_SET);
		fwrite(fi->fspi, sizeof(LSX_FILE_SPINFO), 1, fp->fp);
		fi->flag &= ~FISF_SPINFO;
	}
	
	if (fi->flag & FISF_USERINFO)
	{
		offset = info_offset +	sizeof(LSX_FILE_BASEINFO) + 
			sizeof(LSX_FILE_AUTOINFO) + sizeof(LSX_FILE_SPINFO);
		fseek(fp->fp, offset, SEEK_SET);
		fwrite(fi->fui, sizeof(LSX_FILE_USERINFO), 1, fp->fp);
		fi->flag &= ~FISF_USERINFO;
	}
	
	save_newtcb(fp);
	
	if (fi->flag & FISF_HEADER)
	{
		fi->fbi->stats.htbl_size = htbl->size;
		fi->fbi->stats.htbl_elements = htbl->elements;
		fi->fbi->stats.node_count = htbl->nodes;
		fi->fbi->stats.nodeblk_size = htbl->nodeblock->size;
		fi->fbi->stats.textblk_size = htbl->textblock->size;
		fi->fbi->stats.nodeblk_count = htbl->nodeblock->blocks;
		fi->fbi->stats.textblk_count = htbl->textblock->blocks;
		
		fi->fbi->recgroups = (WORD)get_recgroupcount(fi->cblst);
		
		offset = info_offset;
		fseek(fp->fp, offset, SEEK_SET);
		fwrite(fi->fbi, sizeof(LSX_FILE_BASEINFO), 1, fp->fp);
		
		fi->header->lastcb_offset = fi->cblst->tail->offset;
		memcpy(fi->header->flag, LSX_FLAG, sizeof(fi->header->flag));
		fseek(fp->fp, 0, SEEK_SET);
		fwrite(fi->header, sizeof(LSX_FILE_HEADER), 1, fp->fp);
	}
	
	fi->flag = 0;
	return TRUE;
}

int get_recgroupcount(CBNODE_LST *lst)
{
	int count = 0;
	CB_NODE *node = lst->head;

	while (node)
	{
		if (node->cb->type == CB_TYPE_RECGROUP)
		{
			++count;
		}

		node = node->next;
	}

	return count;
}

void save_newtcb(FILE_T *fp)
{
	FILE_INFO *fi = fp->fi;
	HASH_TBL *htbl = fp->htbl;
	LSX_FILE_TCB *tcb = NULL;
	CB_NODE *cbnode = NULL;
	
	if (htbl->numberlist->notsaved)
	{
		int rgnsize = 0;

		cbnode = (CB_NODE *)mem_alloc(fi->cbblock, sizeof(CB_NODE));
		tcb = (LSX_FILE_TCB *)mem_alloc(fi->cbblock, sizeof(LSX_FILE_TCB));
		
		memset(tcb, 0, sizeof(LSX_FILE_TCB));
		tcb->cb.size = sizeof(LSX_FILE_TCB);
		tcb->cb.type = CB_TYPE_TEXT;
		
		cbnode->cb = (LSX_FILE_CB *)tcb;
		cbnode->next = NULL;
		cbnode->offset = fi->newcb_offset;
		
		if (fi->cblst->tail)
		{
			CB_NODE *tail = fi->cblst->tail;
			tail->cb->nextcb_offset = cbnode->offset;
			fseek(fp->fp, tail->offset, SEEK_SET);
			fwrite(tail->cb, tail->cb->size, 1, fp->fp);
		}
		
		list_add_cbnode(fi->cblst, cbnode);

		tcb->text_count = hash_savetext(fp, cbnode->offset+sizeof(LSX_FILE_TCB), &rgnsize);
		tcb->cb.rgnsize = rgnsize;
		
		fseek(fp->fp, cbnode->offset, SEEK_SET);
		fwrite(tcb, sizeof(LSX_FILE_TCB), 1, fp->fp);
		
		fi->newcb_offset += tcb->cb.size + tcb->cb.rgnsize;
		fi->flag |= FISF_HEADER;
	}
}

BOOL fi_load(FILE_T *fp)
{
	FILE_INFO *fi = fp->fi;
	LSX_FILE_CB cb = {0};
	LSX_FILE_RGCB *rgcb = NULL;
	LSX_FILE_TCB *tcb = NULL;
	CB_NODE *cbnode = NULL;
	RGCB_NODE *rgcb_node = NULL;
	long offset = 0;
	
	/* HEADER */
	fseek(fp->fp, 0, SEEK_SET);
	file_read(fi->header, sizeof(LSX_FILE_HEADER), 1, fp->fp, BUFTYPE_LSX_FILE_HEADER);
	
	fp->htbl->lenbytes = LENBYTES_V3;

	/* BASEINFO */
	offset = fi->header->info_offset;
	fseek(fp->fp, offset, SEEK_SET);
	file_read(fi->fbi, sizeof(LSX_FILE_BASEINFO), 1, fp->fp, BUFTYPE_LSX_FILE_BASEINFO);
	
	/* AUTOINFO */
	offset = fi->header->info_offset + sizeof(LSX_FILE_BASEINFO);
	fseek(fp->fp, offset, SEEK_SET);
	file_read(fi->fai, sizeof(LSX_FILE_AUTOINFO), 1, fp->fp, BUFTYPE_LSX_FILE_AUTOINFO);
	
	/* SPINFO */
	offset = fi->header->info_offset +
		sizeof(LSX_FILE_BASEINFO) + sizeof(LSX_FILE_AUTOINFO);
	fseek(fp->fp, offset, SEEK_SET);
	file_read(fi->fspi, sizeof(LSX_FILE_SPINFO), 1, fp->fp, BUFTYPE_LSX_FILE_SPINFO);
	
	/* USERINFO */
	offset = fi->header->info_offset + sizeof(LSX_FILE_BASEINFO) + 
		sizeof(LSX_FILE_AUTOINFO) + sizeof(LSX_FILE_SPINFO);
	fseek(fp->fp, offset, SEEK_SET);
	file_read(fi->fui, sizeof(LSX_FILE_USERINFO), 1, fp->fp, BUFTYPE_LSX_FILE_USERINFO);
	
	fi->flag = 0;
	
	/* CB LIST */
	offset = fi->header->firstcb_offset;
	while (offset > 0)
	{
		fseek(fp->fp, offset, SEEK_SET);
		file_read(&cb, sizeof(LSX_FILE_CB), 1, fp->fp, BUFTYPE_LSX_FILE_CB);

		if (cb.type == CB_TYPE_TEXT)
		{
			cbnode = (CB_NODE *)mem_alloc(fi->cbblock, sizeof(CB_NODE));
			cbnode->size = sizeof(CB_NODE);
			cbnode->type = CB_TYPE_TEXT;
			cbnode->offset = offset;
			cbnode->next = NULL;
			
			tcb = (LSX_FILE_TCB *)mem_alloc(fi->cbblock, sizeof(LSX_FILE_TCB));
			fseek(fp->fp, offset, SEEK_SET);
			file_read(tcb, sizeof(LSX_FILE_TCB), 1, fp->fp, BUFTYPE_LSX_FILE_TCB);
			cbnode->cb = (LSX_FILE_CB *)tcb;
		}
		else
		{
			rgcb_node = create_rgcbnode(fp, fp->fi->cbblock, offset);
			cbnode = &rgcb_node->cbnode;

			rgcb = (LSX_FILE_RGCB *)mem_alloc(fi->cbblock, sizeof(LSX_FILE_RGCB));
			fseek(fp->fp, offset, SEEK_SET);
			file_read(rgcb, sizeof(LSX_FILE_RGCB), 1, fp->fp, BUFTYPE_LSX_FILE_RGCB);

			rgcb_node->cbnode.cb = (LSX_FILE_CB *)rgcb;

			rgcb_loadrcblist(fp, rgcb, rgcb_node);
		}
		
		list_add_cbnode(fi->cblst, cbnode);
		
		offset = cbnode->cb->nextcb_offset;
	}
	
	fi->newcb_offset = fi->header->lastcb_offset + 
		(fi->cblst->tail->cb->size + fi->cblst->tail->cb->rgnsize);
	
	/* TEXT */
	if (hash_load(fp, fi->cblst))
	{
		char langcode[4] = {0};
		char langcode_en[4] = {0};

		fi_read_langcode(fp, langcode, langcode_en, 4);

		if (strlen(langcode) > 0)
		{
			strcpy(fp->lang.code, langcode);
		}
		if (strlen(langcode_en) > 0)
		{
			strcpy(fp->lang.code_en, langcode_en);
		}
		
		fp->lang.offset = 0;
		return TRUE;
	}
	return FALSE;
}

void rgcb_loadrcblist(FILE_T *fp, const LSX_FILE_RGCB *rgcb, RGCB_NODE *rgcb_node)
{
	LSX_FILE_RCB rcb_f = {0};
	LSX_FILE_RCB *rcb = NULL;
	RCB_NODE *rcb_node = NULL;
	DTCCB_NODE *dtccb_node = NULL;
	long offset = rgcb->firstrcb_offset;

	while (offset > 0)
	{
		fseek(fp->fp, offset, SEEK_SET);
		file_read(&rcb_f, sizeof(LSX_FILE_RCB), 1, fp->fp, BUFTYPE_LSX_FILE_RCB);

		rcb = (LSX_FILE_RCB *)mem_alloc(fp->fi->cbblock, sizeof(LSX_FILE_RCB));
		memcpy(rcb, &rcb_f, sizeof(rcb_f));
		
		rcb_node = (RCB_NODE *)create_rcbnode(fp->fi->cbblock, rcb_f.cb.type, offset);
		rcb_node->cbnode.cb = (LSX_FILE_CB *)rcb;
		rcb_node->parent = rgcb_node;

		switch (rcb_f.cb.type)
		{
		case CB_TYPE_DTC:
			list_add_dtccbnode(&rgcb_node->dtclst, (DTCCB_NODE *)rcb_node);
			break;
		case CB_TYPE_DSBASICS:
			rgcb_node->ds_basics = (CB_NODE *)rcb_node;
			break;
		case CB_TYPE_DATASTREAM:
		case CB_TYPE_VERSIONINFO:
		case CB_TYPE_FREEZEFRAME:
		case CB_TYPE_READINIESS:
			break;
		}
		
		list_add_cbnode(&rgcb_node->rcblst, (CB_NODE *)rcb_node);

		offset = rcb_f.cb.nextcb_offset;
	}
}

void translatecode(char code[], int size)
{
	int i;
	char *oldcode[] = 
	{
		LANGUAGE_SIMPLIFIED_CHINESE,
		LANGUAGE_TRADITIONAL_CHINESE,
		LANGUAGE_JAPANESE,
		LANGUAGE_GREEK,
	};

	for (i=0; i<4; ++i)
	{
		if (str_isiequal(code, oldcode[i]))
		{
			switch (i)
			{
			case 0:
				strcpy(code, XLANG_CHINESE_SIMPLIFIED);
				break;
			case 1:
				strcpy(code, XLANG_CHINESE_TRADITIONAL);
				break;
			case 2:
				strcpy(code, XLANG_JAPANESE);
				break;
			case 3:
				strcpy(code, XLANG_GREEK);
				break;
			}
			break;
		}
	}
}

int fi_read_langcode(FILE_T *fp, char code[], char code_en[], int size)
{
	HASH_TBL *htbl = fp->htbl;
	FILE_INFO *fi = fp->fi;
	int count = 0;
	
	memset(code, 0, size);
	memset(code_en, 0, size);
	
	if (IS_V2(fi->header->version))
	{
		strcpy(code, fi->fbi->lang.code);
		translatecode(code, size);
		++count;
	}
	else
	{
		char *p;
		int n = 2;
		
		char *namecode = hash_gettext(htbl, fi->fbi->lang.namecode[0]);
		if (namecode)
		{
			p = strchr(namecode, '.');
			if (p) n = (p - namecode) <= 3 ? (p - namecode) : 3;
			strncpy(code, namecode, n);
			translatecode(code, size);
			++count;
		}
		
		namecode = hash_gettext(htbl, fi->fbi->lang.namecode[1]);
		if (namecode)
		{
			p = strchr(namecode, '.');
			if (p) n = (p - namecode) <= 3 ? (p - namecode) : 3;
			strncpy(code_en, namecode, n);
			translatecode(code_en, size);
			++count;
		}
	}
	
	return count;
}

int fi_read_langname(FILE_T *fp, char name[], char name_en[], int size)
{
	HASH_TBL *htbl = fp->htbl;
	FILE_INFO *fi = fp->fi;
	int count = 0;
	
	memset(name, 0, size);
	memset(name_en, 0, size);
	
	if (IS_V2(fi->header->version))
	{
		++count;
	}
	else
	{
		char *p;
		
		char *namecode = hash_gettext(htbl, fi->fbi->lang.namecode[0]);
		if (namecode)
		{
			p = strchr(namecode, '.');
			if (p) 
			{
				strcpy(name, p+1);
				++count;
			}
		}
		
		namecode = hash_gettext(htbl, fi->fbi->lang.namecode[1]);
		if (namecode)
		{
			p = strchr(namecode, '.');
			if (p) 
			{
				strcpy(name_en, p+1);
				++count;
			}
		}
	}
	
	return count;
}

BOOL fi_selecttextlang(FILE_T *fp, const char *langcode)
{
	if (strcmp(fp->lang.code, langcode) == 0)
	{
		fp->lang.offset = 0;
		return TRUE;
	}
	else if (strcmp(fp->lang.code_en, langcode) == 0)
	{
		fp->lang.offset = sizeof(WORD);
		return TRUE;
	}

	return FALSE;
}

int fi_rec_readgroupcount(FILE_T *fp)
{
	return get_recgroupcount(fp->fi->cblst);
}

LSX_RECGROUP fi_rec_readgroupid(FILE_T *fp, int i)
{
	int count = 0;
	CB_NODE *node = fp->fi->cblst->head;
	
	while (node)
	{
		if (node->cb->type == CB_TYPE_RECGROUP)
		{
			if (++count == i) break;
		}
		
		node = node->next;
	}
	
	if (node)
	{
		return (LSX_RECGROUP)node;
	}

	return NULL;
}

BOOL fi_rec_readgroupinfo(LSX_RECGROUP grp, char **name, 
	char **protocol, char **vin, char **starttime, char **endtime, int *dsinterval)
{
	RGCB_NODE *rgcb_node = (RGCB_NODE *)grp;
	HASH_TBL *htbl = rgcb_node->file->htbl;
	LSX_FILE_RGCB *rgcb = (LSX_FILE_RGCB *)rgcb_node->cbnode.cb;
	
	if (name)
	{
		*name = hash_gettext(htbl, rgcb->name);
	}

	if (vin)
	{
		*vin = hash_gettext(htbl, rgcb->vin);
	}

	if (protocol)
	{
		*protocol = hash_gettext(htbl, rgcb->protocol);
	}

	if (starttime)
	{
		*starttime = hash_gettext(htbl, rgcb->starttime);
	}

	if (endtime)
	{
		*endtime = hash_gettext(htbl, rgcb->endtime);
	}

	if (dsinterval)
	{
		*dsinterval = rgcb->dsinterval;
	}

	return TRUE;
}

BOOL fi_rec_modifygroupinfo(LSX_RECGROUP grp, const char *name, const char *vin)
{
	RGCB_NODE *rgcb_node = (RGCB_NODE *)grp;
	HASH_TBL *htbl = rgcb_node->file->htbl;
	LSX_FILE_RGCB *rgcb = (LSX_FILE_RGCB *)rgcb_node->cbnode.cb;
	FILE_T *fp = rgcb_node->file;

	if (name)
	{
		rgcb->name = hash_add(fp, name);
	}
	if (vin)
	{
		rgcb->vin = hash_add(fp, vin);
	}
	
	fseek(fp->fp, rgcb_node->cbnode.offset, SEEK_SET);
	return (fwrite(rgcb, rgcb->cb.size, 1, fp->fp) == 1);
}

unsigned int fi_rec_readalltype(LSX_RECGROUP grp)
{
	RGCB_NODE *rgcb_node = (RGCB_NODE *)grp;
	RCB_NODE *rcb_node = (RCB_NODE *)rgcb_node->rcblst.head;
	unsigned int type = 0;
	
	while (rcb_node)
	{
		switch (rcb_node->cbnode.type)
		{
		case CB_TYPE_DTC:
			type |= RECORD_DTC;
			break;
		case CB_TYPE_DATASTREAM:
			type |= RECORD_DATASTREAM;
			break;
		case CB_TYPE_VERSIONINFO:
			type |= RECORD_VERSIONINFO;
			break;
		case CB_TYPE_FREEZEFRAME:
			type |= RECORD_FREEZEFRAME;
			break;
		case CB_TYPE_READINIESS:
			type |= RECORD_READINESS;
			break;
		}

		rcb_node = (RCB_NODE *)rcb_node->cbnode.next;
	}
	
	return type;
}

LSX_RECGROUP fi_rec_startnewgroup(FILE_T *fp, const char *name, 
	const char *protocol, const char *vin, const char *starttime, int dsinterval)
{
	FILE_INFO *fi = fp->fi;
	LSX_FILE_RGCB *rgcb = NULL;
	RGCB_NODE *rgcb_node = NULL;
	
	rgcb = (LSX_FILE_RGCB *)mem_alloc(fi->cbblock, sizeof(LSX_FILE_RGCB));
	rgcb_node = create_rgcbnode(fp, fi->cbblock, fi->newcb_offset);
	rgcb_node->cbnode.cb = (LSX_FILE_CB *)rgcb;
	
	memset(rgcb, 0, sizeof(LSX_FILE_RGCB));
	rgcb->cb.size = sizeof(LSX_FILE_RGCB);
	rgcb->cb.type = CB_TYPE_RECGROUP;
	rgcb->name = hash_add(fp, name);
	rgcb->vin = hash_add(fp, vin);
	rgcb->protocol = hash_add(fp, protocol);
	rgcb->starttime = hash_add(fp, starttime);
	rgcb->dsinterval = dsinterval;
	
	if (fp->fi->cblst->tail)
	{
		CB_NODE *tail = fp->fi->cblst->tail;
		tail->cb->nextcb_offset = rgcb_node->cbnode.offset;
		fseek(fp->fp, tail->offset, SEEK_SET);
		fwrite(tail->cb, tail->cb->size, 1, fp->fp);
	}
	
	fseek(fp->fp, rgcb_node->cbnode.offset, SEEK_SET);
	fwrite(rgcb, rgcb->cb.size, 1, fp->fp);
	
	list_add_cbnode(fi->cblst, (CB_NODE *)rgcb_node);
	
	fi->newcb_offset += rgcb->cb.size;
	fp->item.rcb_node = NULL;
	return (LSX_RECGROUP)rgcb_node;
}

BOOL fi_rec_endnewgroup(LSX_RECGROUP grp, const char *endtime)
{
	RGCB_NODE *rgcb_node = (RGCB_NODE *)grp;
	LSX_FILE_RGCB *rgcb = (LSX_FILE_RGCB *)rgcb_node->cbnode.cb;
	FILE_T *fp = rgcb_node->file;
	long offset = rgcb_node->cbnode.offset;
	
	rgcb->endtime = hash_add(fp, endtime);
	fseek(fp->fp, offset, SEEK_SET);
	return (fwrite(rgcb, rgcb->cb.size, 1, fp->fp) == 1);
}

/* fill node members except cb */
RGCB_NODE *create_rgcbnode(FILE_T *fp, MEM_BLOCK *cbblk, long cb_offset)
{
	RGCB_NODE *node = NULL;
	
	node = (RGCB_NODE *)mem_alloc(cbblk, sizeof(RGCB_NODE));
	node->cbnode.size = sizeof(RGCB_NODE);
	node->cbnode.type = CB_TYPE_RECGROUP;
	node->cbnode.offset = cb_offset;
	node->cbnode.next = NULL;
	
	node->file = fp;
	
	list_init_cbnode(&node->rcblst);
	list_init_cbnode(&node->dtclst);

	node->ds_basics = NULL;

	return node;
}

/* fill node members except cb */
RCB_NODE *create_rcbnode(MEM_BLOCK *cbblk, UINT type, long cb_offset)
{
	RCB_NODE *node = NULL;
	int allocsize = 0;

	switch (type)
	{
	case CB_TYPE_DTC:
		allocsize = sizeof(DTCCB_NODE);
		break;
	case CB_TYPE_DATASTREAM:
	case CB_TYPE_DSBASICS:
	case CB_TYPE_VERSIONINFO:
	case CB_TYPE_FREEZEFRAME:
	case CB_TYPE_READINIESS:
		allocsize = sizeof(RCB_NODE);
		break;
	default:
		allocsize = 0;
		break;
	}

	if (allocsize > 0)
	{
		node = (RCB_NODE *)mem_alloc(cbblk, allocsize);
		memset(node, 0, allocsize);
		node->cbnode.size = allocsize;
		node->cbnode.type = type;
		node->cbnode.next = NULL;
		node->cbnode.offset = cb_offset;
	}

	return node;
}

BOOL fi_rec_writedtc(LSX_RECGROUP grp, const char *dtc, 
	const LSX_STRING *state, const LSX_STRING *desc, const char *time)
{
	RGCB_NODE *rgcb_node = (RGCB_NODE *)grp;
	LSX_FILE_RGCB *rgcb = (LSX_FILE_RGCB *)rgcb_node->cbnode.cb;
	FILE_T *fp = rgcb_node->file;
	int add_size = 0;
	UINT rec_type = CB_TYPE_DTC;
	LSX_FILE_RCB *rcb = NULL;
	DTCCB_NODE *rcb_node = NULL;

	WORD number[5] = {0};
	number[0] = hash_add(fp, dtc);
	if (state)
	{
		if (state->str) number[1] = hash_add(fp, state->str);
		if (state->str_en) number[2] = hash_add(fp, state->str_en);
	}
	if (desc)
	{
		if (desc->str) number[3] = hash_add(fp, desc->str);
		if (desc->str_en) number[4] = hash_add(fp, desc->str_en);
	}

	/* add new rcb and node */

	rcb = (LSX_FILE_RCB *)mem_alloc(fp->fi->cbblock, sizeof(LSX_FILE_RCB));
	rcb_node = (DTCCB_NODE *)create_rcbnode(fp->fi->cbblock, rec_type, fp->fi->newcb_offset);
	
	rcb_node->parent = rgcb_node;
	rcb_node->dtc_next = NULL;
	rcb_node->dtc_prev = NULL;
	rcb_node->cbnode.cb = (LSX_FILE_CB *)rcb;

	rcb->cb.size = sizeof(LSX_FILE_RCB);
	rcb->cb.type = rcb_node->cbnode.type;
	rcb->cb.rgnsize = sizeof(number);
	rcb->cb.nextcb_offset = 0;
	rcb->recordsize = sizeof(number);
	rcb->un.starttime = hash_add(fp, time);

	if (rgcb_node->rcblst.count > 0)
	{
		CB_NODE *tail = rgcb_node->rcblst.tail;
		tail->cb->nextcb_offset = rcb_node->cbnode.offset;
		fseek(fp->fp, tail->offset, SEEK_SET);
		fwrite(tail->cb, tail->cb->size, 1, fp->fp);
	}
	else
	{
		rgcb->firstrcb_offset = rcb_node->cbnode.offset;
	}

	if (rgcb_node->dtclst.count == 0)
	{
		rgcb->firstdtcrcb_offset = rcb_node->cbnode.offset;
	}

	fseek(fp->fp, rcb_node->cbnode.offset, SEEK_SET);
	fwrite(&rcb->cb, rcb->cb.size, 1, fp->fp);
	fwrite(number, sizeof(number), 1, fp->fp);

	list_add_cbnode(&rgcb_node->rcblst, (CB_NODE *)rcb_node);
	list_add_dtccbnode(&rgcb_node->dtclst, rcb_node);

	add_size = rcb->cb.size + rcb->cb.rgnsize;
	rgcb->cb.rgnsize += add_size;
	rgcb->lastrcb_offset = rcb_node->cbnode.offset;
	rgcb->lastdtcrcb_offset = rgcb->lastrcb_offset;

	fseek(fp->fp, rgcb_node->cbnode.offset, SEEK_SET);
	fwrite(rgcb, rgcb->cb.size, 1, fp->fp);

	fp->fi->newcb_offset += add_size;
	return TRUE;
}

BOOL fi_rec_writevi(LSX_RECGROUP grp, const LSX_STRING *vi)
{
	RGCB_NODE *rgcb_node = (RGCB_NODE *)grp;
	LSX_FILE_RGCB *rgcb = (LSX_FILE_RGCB *)rgcb_node->cbnode.cb;
	FILE_T *fp = rgcb_node->file;
	int add_size = 0;
	UINT rec_type = CB_TYPE_VERSIONINFO;
	
	WORD number[2] = {0};
	if (vi->str)
	{
		number[0] = hash_add(fp, vi->str);
	}
	if (vi->str_en)
	{
		number[1] = hash_add(fp, vi->str_en);
	}
	
	if (rgcb_node->rcblst.count == 0
		|| rgcb_node->rcblst.tail->cb->type != rec_type)
	{
		/* add new rcb and node */
		
		LSX_FILE_RCB *rcb = (LSX_FILE_RCB *)mem_alloc(fp->fi->cbblock, sizeof(LSX_FILE_RCB));
		RCB_NODE *rcb_node = (RCB_NODE *)create_rcbnode(fp->fi->cbblock, rec_type, fp->fi->newcb_offset);
		
		rcb_node->parent = rgcb_node;
		rcb_node->cbnode.cb = (LSX_FILE_CB *)rcb;
		
		rcb->cb.size = sizeof(LSX_FILE_RCB);
		rcb->cb.type = rcb_node->cbnode.type;
		rcb->cb.rgnsize = sizeof(number);
		rcb->cb.nextcb_offset = 0;
		rcb->recordsize = sizeof(number);
		rcb->un.reserved = 0;
		
		if (rgcb_node->rcblst.count > 0)
		{
			CB_NODE *tail = rgcb_node->rcblst.tail;
			tail->cb->nextcb_offset = rcb_node->cbnode.offset;
			fseek(fp->fp, tail->offset, SEEK_SET);
			fwrite(tail->cb, tail->cb->size, 1, fp->fp);
		}
		else
		{
			rgcb->firstrcb_offset = rcb_node->cbnode.offset;
		}
		
		fseek(fp->fp, rcb_node->cbnode.offset, SEEK_SET);
		fwrite(&rcb->cb, rcb->cb.size, 1, fp->fp);
		fwrite(&number, sizeof(number), 1, fp->fp);
		
		list_add_cbnode(&rgcb_node->rcblst, (CB_NODE *)rcb_node);
		
		add_size = rcb->cb.size + rcb->cb.rgnsize;
		rgcb->cb.rgnsize += add_size;
		rgcb->lastrcb_offset = rcb_node->cbnode.offset;
	}
	else
	{
		/* add new item */
		
		LSX_FILE_RCB *rcb = (LSX_FILE_RCB *)rgcb_node->rcblst.tail->cb;
		RCB_NODE *rcb_node = (RCB_NODE *)rgcb_node->rcblst.tail;
		long offset = rcb_node->cbnode.offset + rcb->cb.size + rcb->cb.rgnsize;
		
		add_size = sizeof(number);
		rcb->cb.rgnsize += add_size;
		fseek(fp->fp, rcb_node->cbnode.offset, SEEK_SET);
		fwrite(&rcb->cb, rcb->cb.size, 1, fp->fp);
		
		fseek(fp->fp, offset, SEEK_SET);
		fwrite(&number, sizeof(number), 1, fp->fp);
		
		rgcb->cb.rgnsize += add_size;
	}
	
	fseek(fp->fp, rgcb_node->cbnode.offset, SEEK_SET);
	fwrite(rgcb, rgcb->cb.size, 1, fp->fp);
	
	fp->fi->newcb_offset += add_size;
	return TRUE;
}

BOOL fi_rec_writedsbasics(LSX_RECGROUP grp, 
	const LSX_STRING *namestrs[], const LSX_STRING *unitstrs[], const WORD type[], int n)
{
	RGCB_NODE *rgcb_node = (RGCB_NODE *)grp;
	LSX_FILE_RGCB *rgcb = (LSX_FILE_RGCB *)rgcb_node->cbnode.cb;
	FILE_T *fp = rgcb_node->file;
	int add_size = 0;
	UINT rec_type = CB_TYPE_DSBASICS;
	
	if (rgcb_node->ds_basics == NULL)
	{
		/* add new rcb and node */
		
		LSX_FILE_RCB *rcb = (LSX_FILE_RCB *)mem_alloc(fp->fi->cbblock, sizeof(LSX_FILE_RCB));
		RCB_NODE *rcb_node = (RCB_NODE *)create_rcbnode(fp->fi->cbblock, rec_type, fp->fi->newcb_offset);
		WORD number[MAX_DS_COLNUMBER*2] = {0};
		int i = 0;

		n = (n <= MAX_DS_COLNUMBER) ? n : MAX_DS_COLNUMBER;
		
		rcb_node->parent = rgcb_node;
		rcb_node->cbnode.cb = (LSX_FILE_CB *)rcb;
		
		rcb->cb.size = sizeof(LSX_FILE_RCB);
		rcb->cb.type = rcb_node->cbnode.type;
		rcb->cb.rgnsize = n * sizeof(WORD) * 6;
		rcb->cb.nextcb_offset = 0;
		rcb->recordsize = n * sizeof(WORD) * 2;
		rcb->un.reserved = 0;
		
		if (rgcb_node->rcblst.count > 0)
		{
			CB_NODE *tail = rgcb_node->rcblst.tail;
			tail->cb->nextcb_offset = rcb_node->cbnode.offset;
			fseek(fp->fp, tail->offset, SEEK_SET);
			fwrite(tail->cb, tail->cb->size, 1, fp->fp);
		}
		else
		{
			rgcb->firstrcb_offset = rcb_node->cbnode.offset;
		}
		
		fseek(fp->fp, rcb_node->cbnode.offset, SEEK_SET);
		fwrite(&rcb->cb, rcb->cb.size, 1, fp->fp);

		for (i=0; i<n; ++i)
		{
			if (namestrs[i]->str)
			{
				number[i*2] = hash_add(fp, namestrs[i]->str);
			}
			if (namestrs[i]->str_en)
			{
				number[i*2+1] = hash_add(fp, namestrs[i]->str_en);
			}
		}
		fwrite(number, n * sizeof(WORD) * 2, 1, fp->fp);
		memset(number, 0, sizeof(number));
		
		for (i=0; i<n; ++i)
		{
			if (unitstrs[i]->str)
			{
				number[i*2] = hash_add(fp, unitstrs[i]->str);
			}
			if (unitstrs[i]->str_en)
			{
				number[i*2+1] = hash_add(fp, unitstrs[i]->str_en);
			}
		}
		fwrite(number, n * sizeof(WORD) * 2, 1, fp->fp);
		memset(number, 0, sizeof(number));
		
		for (i=0; i<n; ++i)
		{
			number[i*2] = type[i];
			number[i*2+1] = type[i];
		}
		fwrite(number, n * sizeof(WORD) * 2, 1, fp->fp);
		
		list_add_cbnode(&rgcb_node->rcblst, (CB_NODE *)rcb_node);
		
		add_size = rcb->cb.size + rcb->cb.rgnsize;
		rgcb->cb.rgnsize += add_size;
		rgcb->lastrcb_offset = rcb_node->cbnode.offset;
		rgcb_node->ds_basics = (CB_NODE *)rcb_node;

		fseek(fp->fp, rgcb_node->cbnode.offset, SEEK_SET);
		fwrite(rgcb, rgcb->cb.size, 1, fp->fp);
		
		fp->fi->newcb_offset += add_size;
		return TRUE;
	}

	return FALSE;
}

BOOL fi_rec_writeds(LSX_RECGROUP grp, const LSX_STRING *itemstrs[], int n)
{
	RGCB_NODE *rgcb_node = (RGCB_NODE *)grp;
	LSX_FILE_RGCB *rgcb = (LSX_FILE_RGCB *)rgcb_node->cbnode.cb;
	FILE_T *fp = rgcb_node->file;
	int add_size = 0;
	UINT rec_type = CB_TYPE_DATASTREAM;
	int colcount = 0;
	WORD number[MAX_DS_COLNUMBER*2] = {0};
	int i = 0;
	
	if (rgcb_node->ds_basics)
	{
		RCB_NODE *node = (RCB_NODE *)rgcb_node->ds_basics;
		LSX_FILE_RCB *rcb = (LSX_FILE_RCB *)rgcb_node->ds_basics->cb;
		colcount = rcb->recordsize / (sizeof(WORD)*2);
		n = (n > colcount) ? colcount : n;
	}
	else
	{
		return FALSE;
	}

	for (i=0; i<n; ++i)
	{
		if (itemstrs[i]->str)
		{
			number[i*2] = hash_add(fp, itemstrs[i]->str);
		}
		if (itemstrs[i]->str_en)
		{
			number[i*2+1] = hash_add(fp, itemstrs[i]->str_en);
		}
	}
	
	if (rgcb_node->rcblst.count == 0
		|| rgcb_node->rcblst.tail->cb->type != rec_type)
	{
		/* add new rcb and node */
		
		LSX_FILE_RCB *rcb = (LSX_FILE_RCB *)mem_alloc(fp->fi->cbblock, sizeof(LSX_FILE_RCB));
		RCB_NODE *rcb_node = (RCB_NODE *)create_rcbnode(fp->fi->cbblock, rec_type, fp->fi->newcb_offset);
		
		rcb_node->parent = rgcb_node;
		rcb_node->cbnode.cb = (LSX_FILE_CB *)rcb;
		
		rcb->cb.size = sizeof(LSX_FILE_RCB);
		rcb->cb.type = rcb_node->cbnode.type;
		rcb->cb.rgnsize = n * sizeof(WORD) * 2;
		rcb->cb.nextcb_offset = 0;
		rcb->recordsize = n * sizeof(WORD) * 2;
		rcb->un.reserved = 0;
		
		if (rgcb_node->rcblst.count > 0)
		{
			CB_NODE *tail = rgcb_node->rcblst.tail;
			tail->cb->nextcb_offset = rcb_node->cbnode.offset;
			fseek(fp->fp, tail->offset, SEEK_SET);
			fwrite(tail->cb, tail->cb->size, 1, fp->fp);
		}
		else
		{
			rgcb->firstrcb_offset = rcb_node->cbnode.offset;
		}
		
		fseek(fp->fp, rcb_node->cbnode.offset, SEEK_SET);
		fwrite(&rcb->cb, rcb->cb.size, 1, fp->fp);

		fwrite(number, n*sizeof(WORD)*2, 1, fp->fp);
		
		list_add_cbnode(&rgcb_node->rcblst, (CB_NODE *)rcb_node);
		
		add_size = rcb->cb.size + rcb->cb.rgnsize;
		rgcb->cb.rgnsize += add_size;
		rgcb->lastrcb_offset = rcb_node->cbnode.offset;
	}
	else
	{
		/* add new item */
		
		LSX_FILE_RCB *rcb = (LSX_FILE_RCB *)rgcb_node->rcblst.tail->cb;
		RCB_NODE *rcb_node = (RCB_NODE *)rgcb_node->rcblst.tail;
		long offset = rcb_node->cbnode.offset + rcb->cb.size + rcb->cb.rgnsize;
		
		add_size = n * sizeof(WORD) * 2;
		rcb->cb.rgnsize += add_size;
		fseek(fp->fp, rcb_node->cbnode.offset, SEEK_SET);
		fwrite(&rcb->cb, rcb->cb.size, 1, fp->fp);
		
		fseek(fp->fp, offset, SEEK_SET);
		fwrite(number, n*sizeof(WORD)*2, 1, fp->fp);
		
		rgcb->cb.rgnsize += add_size;
	}
	
	fseek(fp->fp, rgcb_node->cbnode.offset, SEEK_SET);
	fwrite(rgcb, rgcb->cb.size, 1, fp->fp);
	
	fp->fi->newcb_offset += add_size;
	return TRUE;
}

BOOL fi_rec_writefreezeframe(LSX_RECGROUP grp, const char *dtc, 
	const LSX_STRING *namestrs[], const LSX_STRING *unitstrs[], const WORD type[], const LSX_STRING *itemstrs[], int n)
{
	RGCB_NODE *rgcb_node = (RGCB_NODE *)grp;
	LSX_FILE_RGCB *rgcb = (LSX_FILE_RGCB *)rgcb_node->cbnode.cb;
	FILE_T *fp = rgcb_node->file;
	int add_size = 0;
	UINT rec_type = CB_TYPE_FREEZEFRAME;
	int colcount = 0;
	WORD number[2*MAX_DS_COLNUMBER] = {0};
	int i = 0;
	
	/* add new rcb and node */
		
	LSX_FILE_RCB *rcb = (LSX_FILE_RCB *)mem_alloc(fp->fi->cbblock, sizeof(LSX_FILE_RCB));
	RCB_NODE *rcb_node = (RCB_NODE *)create_rcbnode(fp->fi->cbblock, rec_type, fp->fi->newcb_offset);
	
	n = (n <= MAX_DS_COLNUMBER) ? n : MAX_DS_COLNUMBER;

	rcb_node->parent = rgcb_node;
	rcb_node->cbnode.cb = (LSX_FILE_CB *)rcb;
	
	rcb->cb.size = sizeof(LSX_FILE_RCB);
	rcb->cb.type = rcb_node->cbnode.type;
	rcb->cb.rgnsize = n * sizeof(WORD) * 8;
	rcb->cb.nextcb_offset = 0;
	rcb->recordsize = n * sizeof(WORD) * 2;
	rcb->un.dtc = hash_add(fp, dtc);
	
	if (rgcb_node->rcblst.count > 0)
	{
		CB_NODE *tail = rgcb_node->rcblst.tail;
		tail->cb->nextcb_offset = rcb_node->cbnode.offset;
		fseek(fp->fp, tail->offset, SEEK_SET);
		fwrite(tail->cb, tail->cb->size, 1, fp->fp);
	}
	else
	{
		rgcb->firstrcb_offset = rcb_node->cbnode.offset;
	}
	
	fseek(fp->fp, rcb_node->cbnode.offset, SEEK_SET);
	fwrite(&rcb->cb, rcb->cb.size, 1, fp->fp);
	
	for (i=0; i<n; ++i)
	{
		if (namestrs[i]->str)
		{
			number[i*2] = hash_add(fp, namestrs[i]->str);
		}
		if (namestrs[i]->str_en)
		{
			number[i*2+1] = hash_add(fp, namestrs[i]->str_en);
		}
	}
	fwrite(number, n*sizeof(WORD)*2, 1, fp->fp);
	memset(number, 0, sizeof(number));
	
	for (i=0; i<n; ++i)
	{
		if (unitstrs[i]->str)
		{
			number[i*2] = hash_add(fp, unitstrs[i]->str);
		}
		if (unitstrs[i]->str_en)
		{
			number[i*2+1] = hash_add(fp, unitstrs[i]->str_en);
		}
	}
	fwrite(number, n*sizeof(WORD)*2, 1, fp->fp);
	memset(number, 0, sizeof(number));
	
	for (i=0; i<n; ++i)
	{
		number[i*2] = type[i];
		number[i*2+1] = type[i];
	}
	fwrite(number, n*sizeof(WORD)*2, 1, fp->fp);
	memset(number, 0, sizeof(number));
	
	for (i=0; i<n; ++i)
	{
		if (itemstrs[i]->str)
		{
			number[i*2] = hash_add(fp, itemstrs[i]->str);
		}
		if (itemstrs[i]->str_en)
		{
			number[i*2+1] = hash_add(fp, itemstrs[i]->str_en);
		}
	}
	fwrite(number, n*sizeof(WORD)*2, 1, fp->fp);
	
	list_add_cbnode(&rgcb_node->rcblst, (CB_NODE *)rcb_node);
	
	add_size = rcb->cb.size + rcb->cb.rgnsize;
	rgcb->cb.rgnsize += add_size;
	rgcb->lastrcb_offset = rcb_node->cbnode.offset;
	
	fseek(fp->fp, rgcb_node->cbnode.offset, SEEK_SET);
	fwrite(rgcb, rgcb->cb.size, 1, fp->fp);
	
	fp->fi->newcb_offset += add_size;
	return TRUE;
}

BOOL fi_rec_writereadiness(LSX_RECGROUP grp, 
    const LSX_STRING *namestrs[], const LSX_STRING *itemstrs[], int n)
{
	RGCB_NODE *rgcb_node = (RGCB_NODE *)grp;
	LSX_FILE_RGCB *rgcb = (LSX_FILE_RGCB *)rgcb_node->cbnode.cb;
	FILE_T *fp = rgcb_node->file;
	int add_size = 0;
	UINT rec_type = CB_TYPE_READINIESS;
	int colcount = 0;
	WORD number[2*MAX_DS_COLNUMBER] = {0};
	int i = 0;
	
	/* add new rcb and node */
	
	LSX_FILE_RCB *rcb = (LSX_FILE_RCB *)mem_alloc(fp->fi->cbblock, sizeof(LSX_FILE_RCB));
	RCB_NODE *rcb_node = (RCB_NODE *)create_rcbnode(fp->fi->cbblock, rec_type, fp->fi->newcb_offset);

	n = (n <= MAX_DS_COLNUMBER) ? n : MAX_DS_COLNUMBER;
	
	rcb_node->parent = rgcb_node;
	rcb_node->cbnode.cb = (LSX_FILE_CB *)rcb;
	
	rcb->cb.size = sizeof(LSX_FILE_RCB);
	rcb->cb.type = rcb_node->cbnode.type;
	rcb->cb.rgnsize = n * sizeof(WORD) * 4;
	rcb->cb.nextcb_offset = 0;
	rcb->recordsize = n * sizeof(WORD) * 2;
	rcb->un.reserved = 0;
	
	if (rgcb_node->rcblst.count > 0)
	{
		CB_NODE *tail = rgcb_node->rcblst.tail;
		tail->cb->nextcb_offset = rcb_node->cbnode.offset;
		fseek(fp->fp, tail->offset, SEEK_SET);
		fwrite(tail->cb, tail->cb->size, 1, fp->fp);
	}
	else
	{
		rgcb->firstrcb_offset = rcb_node->cbnode.offset;
	}
	
	fseek(fp->fp, rcb_node->cbnode.offset, SEEK_SET);
	fwrite(&rcb->cb, rcb->cb.size, 1, fp->fp);
	
	for (i=0; i<n; ++i)
	{
		if (namestrs[i]->str)
		{
			number[i*2] = hash_add(fp, namestrs[i]->str);
		}
		if (namestrs[i]->str_en)
		{
			number[i*2+1] = hash_add(fp, namestrs[i]->str_en);
		}
	}
	fwrite(number, n*sizeof(WORD)*2, 1, fp->fp);
	memset(number, 0, sizeof(number));
	
	for (i=0; i<n; ++i)
	{
		if (itemstrs[i]->str)
		{
			number[i*2] = hash_add(fp, itemstrs[i]->str);
		}
		if (itemstrs[i]->str_en)
		{
			number[i*2+1] = hash_add(fp, itemstrs[i]->str_en);
		}
	}
	fwrite(number, n*sizeof(WORD)*2, 1, fp->fp);
	
	list_add_cbnode(&rgcb_node->rcblst, (CB_NODE *)rcb_node);
	
	add_size = rcb->cb.size + rcb->cb.rgnsize;
	rgcb->cb.rgnsize += add_size;
	rgcb->lastrcb_offset = rcb_node->cbnode.offset;
	
	fseek(fp->fp, rgcb_node->cbnode.offset, SEEK_SET);
	fwrite(rgcb, rgcb->cb.size, 1, fp->fp);
	
	fp->fi->newcb_offset += add_size;
	return TRUE;
}

unsigned int fi_rec_readitemtype(LSX_RECITEM item)
{
	ITEM_T *rec_item = (ITEM_T *)item;
	RCB_NODE *rcb_node = rec_item->rcb_node;
	unsigned int type = 0;
	
	switch (rcb_node->cbnode.type)
	{
	case CB_TYPE_DTC:
		type = RECORD_DTC;
		break;
	case CB_TYPE_DATASTREAM:
		type = RECORD_DATASTREAM;
		break;
	case CB_TYPE_DSBASICS:
		type = RECORD_DSBASICS;
		break;
	case CB_TYPE_VERSIONINFO:
		type = RECORD_VERSIONINFO;
		break;
	case CB_TYPE_FREEZEFRAME:
		type = RECORD_FREEZEFRAME;
		break;
	case CB_TYPE_READINIESS:
		type = RECORD_READINESS;
		break;
	}
	
	return type;
}

LSX_RECITEM fi_rec_readfirstitem(LSX_RECGROUP grp)
{
	RGCB_NODE *rgcb_node = (RGCB_NODE *)grp;
	RCB_NODE *rcb_node = (RCB_NODE *)rgcb_node->rcblst.head;
	LSX_FILE_RCB *rcb = NULL;

	while (rcb_node)
	{
		rcb = (LSX_FILE_RCB *)rcb_node->cbnode.cb;
		if (rcb->cb.rgnsize > 0) break;
		rcb_node = (RCB_NODE *)rcb_node->cbnode.next;
	}
	
	if (rcb_node)
	{
		rgcb_node->file->item.index = 0;
		rgcb_node->file->item.rcb_node = rcb_node;
		return (LSX_RECITEM)&rgcb_node->file->item;
	}
	
	return NULL;
}

LSX_RECITEM fi_rec_readlastitem(LSX_RECGROUP grp)
{
	RGCB_NODE *rgcb_node = (RGCB_NODE *)grp;
	RCB_NODE *rcb_node = (RCB_NODE *)rgcb_node->rcblst.tail;
	LSX_FILE_RCB *rcb = NULL;
	
	while (rcb_node)
	{
		rcb = (LSX_FILE_RCB *)rcb_node->cbnode.cb;
		if (rcb->cb.rgnsize > 0) break;
		rcb_node = (RCB_NODE *)rcb_node->cbnode.prev;
	}
	
	if (rcb_node)
	{
		rgcb_node->file->item.index = rcb->cb.rgnsize/rcb->recordsize - 1;
		rgcb_node->file->item.rcb_node = rcb_node;
		return (LSX_RECITEM)&rgcb_node->file->item;
	}
	
	return NULL;
}

LSX_RECITEM fi_rec_readnextitem(LSX_RECITEM item)
{
	ITEM_T *rec_item = (ITEM_T *)item;
	RCB_NODE *rcb_node = rec_item->rcb_node;
	LSX_FILE_RCB *rcb = (LSX_FILE_RCB *)rcb_node->cbnode.cb;
	int rec_count = rcb->cb.rgnsize / rcb->recordsize;
	UINT type = rcb_node->cbnode.type;
	
	if (rec_item->index >= rec_count - 1
		|| type == CB_TYPE_VERSIONINFO
		|| type == CB_TYPE_FREEZEFRAME
		|| type == CB_TYPE_READINIESS
		|| type == CB_TYPE_DSBASICS)
	{
		rcb_node = (RCB_NODE *)rcb_node->cbnode.next;
		while (rcb_node)
		{
			rcb = (LSX_FILE_RCB *)rcb_node->cbnode.cb;
			if (rcb->cb.rgnsize > 0) break;
			rcb_node = (RCB_NODE *)rcb_node->cbnode.next;
		}
		
		if (rcb_node)
		{
			rec_item->index = 0;
			rec_item->rcb_node = rcb_node;
			return (LSX_RECITEM)rec_item;
		}
	}
	else
	{
		++rec_item->index;
		return (LSX_RECITEM)rec_item;
	}
	
	return NULL;
}

LSX_RECITEM fi_rec_readprevitem(LSX_RECITEM item)
{
	ITEM_T *rec_item = (ITEM_T *)item;
	RCB_NODE *rcb_node = rec_item->rcb_node;
	LSX_FILE_RCB *rcb = (LSX_FILE_RCB *)rcb_node->cbnode.cb;
	int rec_count = rcb->cb.rgnsize / rcb->recordsize;
	UINT type = rcb_node->cbnode.type;
	
	if (rec_item->index <= 0
		|| type == CB_TYPE_VERSIONINFO
		|| type == CB_TYPE_FREEZEFRAME
		|| type == CB_TYPE_READINIESS
		|| type == CB_TYPE_DSBASICS)
	{
		rcb_node = (RCB_NODE *)rcb_node->cbnode.prev;
		while (rcb_node)
		{
			rcb = (LSX_FILE_RCB *)rcb_node->cbnode.cb;
			if (rcb->cb.rgnsize > 0) break;
			rcb_node = (RCB_NODE *)rcb_node->cbnode.prev;
		}
		
		if (rcb_node)
		{
			rec_item->index = rcb->cb.rgnsize/rcb->recordsize - 1;
			rec_item->rcb_node = rcb_node;
			return (LSX_RECITEM)rec_item;
		}
	}
	else
	{
		--rec_item->index;
		return (LSX_RECITEM)rec_item;
	}
	
	return NULL;
}

int fi_rec_readdtccount(LSX_RECGROUP grp)
{
	int count = 0;
	RGCB_NODE *rgcb_node = (RGCB_NODE *)grp;
	DTCCB_NODE *dtccb_node = (DTCCB_NODE *)rgcb_node->dtclst.head;
	LSX_FILE_RCB *rcb = NULL;

	while (dtccb_node)
	{
		rcb = (LSX_FILE_RCB *)dtccb_node->cbnode.cb;
		count += rcb->cb.rgnsize / rcb->recordsize;
		dtccb_node = dtccb_node->dtc_next;
	}

	return count;
}

LSX_RECITEM fi_rec_readfirstdtcitem(LSX_RECGROUP grp)
{
	RGCB_NODE *rgcb_node = (RGCB_NODE *)grp;
	DTCCB_NODE *dtccb_node = (DTCCB_NODE *)rgcb_node->dtclst.head;
	if (dtccb_node)
	{
		rgcb_node->file->item.index = 0;
		rgcb_node->file->item.rcb_node = (RCB_NODE *)&dtccb_node->cbnode;
		return (LSX_RECITEM)&rgcb_node->file->item;
	}
	
	return NULL;
}

LSX_RECITEM fi_rec_readlastdtcitem(LSX_RECGROUP grp)
{
	RGCB_NODE *rgcb_node = (RGCB_NODE *)grp;
	DTCCB_NODE *dtccb_node = (DTCCB_NODE *)rgcb_node->dtclst.tail;
	if (dtccb_node)
	{
		LSX_FILE_RCB *rcb = (LSX_FILE_RCB *)dtccb_node->cbnode.cb;
		rgcb_node->file->item.index = rcb->cb.rgnsize/rcb->recordsize - 1;
		rgcb_node->file->item.rcb_node = (RCB_NODE *)&dtccb_node->cbnode;
		return (LSX_RECITEM)&rgcb_node->file->item;
	}
	
	return NULL;
}

LSX_RECITEM fi_rec_readnextdtcitem(LSX_RECITEM item)
{
	return rec_readnextntypeitem(item, CB_TYPE_DTC, 1);
}

LSX_RECITEM fi_rec_readprevdtcitem(LSX_RECITEM item)
{
	return rec_readprevntypeitem(item, CB_TYPE_DTC, 1);
}

BOOL fi_rec_readdtc(LSX_RECITEM item, char **dtc, char **state, char **desc, char **time)
{
	ITEM_T *rec_item = (ITEM_T *)item;
	FILE_T *fp = rec_item->rcb_node->parent->file;
	DTCCB_NODE *dtccb_node = (DTCCB_NODE *)(rec_item->rcb_node);
	LSX_FILE_RCB *rcb = (LSX_FILE_RCB *)dtccb_node->cbnode.cb;
	long offset = dtccb_node->cbnode.offset + rcb->cb.size + rec_item->index*rcb->recordsize;
	WORD number[3] = {0};

	fseek(fp->fp, offset, SEEK_SET);
	file_read(&number[0], sizeof(WORD), 1, fp->fp, BUFTYPE_WORD);

	if (IS_V2(fp->fi->header->version))
	{
		file_read(&number[1], sizeof(WORD), 1, fp->fp, BUFTYPE_WORD);
		file_read(&number[2], sizeof(WORD), 1, fp->fp, BUFTYPE_WORD);
	}
	else
	{
		fseek(fp->fp, fp->lang.offset, SEEK_CUR);
		file_read(&number[1], sizeof(WORD), 1, fp->fp, BUFTYPE_WORD);
		fseek(fp->fp, sizeof(WORD), SEEK_CUR);
		file_read(&number[2], sizeof(WORD), 1, fp->fp, BUFTYPE_WORD);
	}

	*dtc = hash_gettext(fp->htbl, number[0]);
	if (state) *state = hash_gettext(fp->htbl, number[1]);
	if (desc) *desc = hash_gettext(fp->htbl, number[2]);
	
	if (time) *time = hash_gettext(fp->htbl, rcb->un.starttime);
	return TRUE;
}

BOOL fi_rec_readdtcinfo(LSX_RECGROUP grp, const char *dtc, char **state, char **desc, char **time)
{
	RGCB_NODE *rgcb_node = (RGCB_NODE *)grp;
	FILE_T *fp = rgcb_node->file;
	DTCCB_NODE *dtccb_node = (DTCCB_NODE *)rgcb_node->dtclst.head;
	LSX_FILE_RCB *rcb = NULL;
	int rec_count = 0;
	long offset = 0;
	WORD number[3] = {0};
	int i = 0;

	while (dtccb_node)
	{
		i = 0;
		rcb = (LSX_FILE_RCB *)dtccb_node->cbnode.cb;
		rec_count = rcb->cb.rgnsize / rcb->recordsize;

		while (++i <= rec_count)
		{
			offset = dtccb_node->cbnode.offset + rcb->cb.size + (i-1)*rcb->recordsize;

			fseek(fp->fp, offset, SEEK_SET);
			file_read(&number[0], sizeof(WORD), 1, fp->fp, BUFTYPE_WORD);

			if (str_isiequal(dtc, hash_gettext(fp->htbl, number[0])))
			{
				if (IS_V2(fp->fi->header->version))
				{
					file_read(&number[1], sizeof(WORD), 1, fp->fp, BUFTYPE_WORD);
					file_read(&number[2], sizeof(WORD), 1, fp->fp, BUFTYPE_WORD);
				}
				else
				{
					fseek(fp->fp, fp->lang.offset, SEEK_CUR);
					file_read(&number[1], sizeof(WORD), 1, fp->fp, BUFTYPE_WORD);
					fseek(fp->fp, sizeof(WORD), SEEK_CUR);
					file_read(&number[2], sizeof(WORD), 1, fp->fp, BUFTYPE_WORD);
				}

				*state = hash_gettext(fp->htbl, number[1]);
				*desc = hash_gettext(fp->htbl, number[2]);
				if (time) *time = hash_gettext(fp->htbl, rcb->un.starttime);
				return TRUE;
			}
		}

		dtccb_node = dtccb_node->dtc_next;
	}

	return FALSE;
}

BOOL fi_rec_readvi(LSX_RECGROUP grp, char **vi)
{
	RGCB_NODE *rgcb_node = (RGCB_NODE *)grp;
	FILE_T *fp = rgcb_node->file;
	RCB_NODE *rcb_node = (RCB_NODE *)rgcb_node->rcblst.head;

	while (rcb_node)
	{
		if (rcb_node->cbnode.type == CB_TYPE_VERSIONINFO)
		{
			break;
		}
		rcb_node = (RCB_NODE *)rcb_node->cbnode.next;
	}

	if (rcb_node)
	{
		WORD number = 0;
		long offset = rcb_node->cbnode.offset + rcb_node->cbnode.cb->size;

		if (IS_V2(fp->fi->header->version))
		{
			fseek(fp->fp, offset, SEEK_SET);
			file_read(&number, sizeof(WORD), 1, fp->fp, BUFTYPE_WORD);
		}
		else
		{
			fseek(fp->fp, offset+fp->lang.offset, SEEK_SET);
			file_read(&number, sizeof(WORD), 1, fp->fp, BUFTYPE_WORD);
		}
		
		*vi = hash_gettext(fp->htbl, number);
		return TRUE;
	}

	return FALSE;
}

int fi_rec_readdscolcount(LSX_RECGROUP grp)
{
	RGCB_NODE *rgcb_node = (RGCB_NODE *)grp;
	RCB_NODE *rcb_node = (RCB_NODE *)rgcb_node->ds_basics;

	if (rcb_node)
	{
		LSX_FILE_RCB *rcb = (LSX_FILE_RCB *)rcb_node->cbnode.cb;
		FILE_T *fp = rgcb_node->file;

		if (IS_V2(fp->fi->header->version))
		{
			return (rcb->recordsize / sizeof(WORD));
		}
		return (rcb->recordsize / (sizeof(WORD)*2));
	}

	return 0;
}

BOOL fi_rec_readdsname(LSX_RECGROUP grp, char *textstrs[], int n)
{
	RGCB_NODE *rgcb_node = (RGCB_NODE *)grp;
	FILE_T *fp = rgcb_node->file;
	RCB_NODE *rcb_node = (RCB_NODE *)rgcb_node->ds_basics;
	
	if (rcb_node)
	{
		LSX_FILE_RCB *rcb = (LSX_FILE_RCB *)rcb_node->cbnode.cb;
		long offset = rcb_node->cbnode.offset + rcb->cb.size;
		WORD number[MAX_DS_COLNUMBER] = {0};
		int colcount = 0;
		int i = 0;
		BOOL is_v2 = FALSE;

		if (IS_V2(fp->fi->header->version))
		{
			is_v2 = TRUE;
			colcount = rcb->recordsize / sizeof(WORD);
		}
		else
		{
			colcount = rcb->recordsize / (sizeof(WORD)*2);
		}

		n = (n > colcount) ? colcount : n;

		if (is_v2)
		{
			fseek(fp->fp, offset, SEEK_SET);
			for (i=0; i<n; ++i)
			{
				file_read(&number[i], sizeof(WORD), 1, fp->fp, BUFTYPE_WORD);
			}
		}
		else
		{
			for (i=0; i<n; ++i)
			{
				fseek(fp->fp, offset+sizeof(WORD)*2*i+fp->lang.offset, SEEK_SET);
				file_read(&number[i], sizeof(WORD), 1, fp->fp, BUFTYPE_WORD);
			}
		}

		for (i=0; i<n; ++i)
		{
			textstrs[i] = hash_gettext(fp->htbl, number[i]);
		}

		return TRUE;
	}
	
	return FALSE;
}

BOOL fi_rec_readdsunit(LSX_RECGROUP grp, char *textstrs[], int n)
{
	RGCB_NODE *rgcb_node = (RGCB_NODE *)grp;
	FILE_T *fp = rgcb_node->file;
	RCB_NODE *rcb_node = (RCB_NODE *)rgcb_node->ds_basics;
	
	if (rcb_node)
	{
		LSX_FILE_RCB *rcb = (LSX_FILE_RCB *)rcb_node->cbnode.cb;
		long offset = rcb_node->cbnode.offset + rcb->cb.size + rcb->recordsize;
		WORD number[MAX_DS_COLNUMBER] = {0};
		int colcount = 0;
		int i = 0;
		BOOL is_v2 = FALSE;

		if (IS_V2(fp->fi->header->version))
		{
			is_v2 = TRUE;
			colcount = rcb->recordsize / (sizeof(WORD));
		}
		else
		{
			colcount = rcb->recordsize / (sizeof(WORD)*2);
		}
		
		n = (n > colcount) ? colcount : n;

		if (is_v2)
		{
			fseek(fp->fp, offset, SEEK_SET);
			for (i=0; i<n; ++i)
			{
				file_read(&number[i], sizeof(WORD), 1, fp->fp, BUFTYPE_WORD);
			}
		}
		else
		{
			for (i=0; i<n; ++i)
			{
				fseek(fp->fp, offset+sizeof(WORD)*2*i+fp->lang.offset, SEEK_SET);
				file_read(&number[i], sizeof(WORD), 1, fp->fp, BUFTYPE_WORD);
			}
		}
		
		for (i=0; i<n; ++i)
		{
			textstrs[i] = hash_gettext(fp->htbl, number[i]);
		}
		
		return TRUE;
	}
	
	return FALSE;
}

BOOL fi_rec_readdstype(LSX_RECGROUP grp, WORD type[], int n)
{
	RGCB_NODE *rgcb_node = (RGCB_NODE *)grp;
	FILE_T *fp = rgcb_node->file;
	RCB_NODE *rcb_node = (RCB_NODE *)rgcb_node->ds_basics;
	
	if (rcb_node)
	{
		LSX_FILE_RCB *rcb = (LSX_FILE_RCB *)rcb_node->cbnode.cb;
		int colcount = rcb->recordsize / (sizeof(WORD)*2);
		long offset = rcb_node->cbnode.offset + rcb->cb.size + 2*rcb->recordsize;
		int i = 0;
		
		n = (n > colcount) ? colcount : n;
		fseek(fp->fp, offset, SEEK_SET);
		
		for (i=0; i<n; ++i)
		{
			file_read(&type[i], sizeof(WORD), 1, fp->fp, BUFTYPE_WORD);
			fseek(fp->fp, sizeof(WORD), SEEK_CUR);
		}
		
		return TRUE;
	}
	
	return FALSE;
}

BOOL fi_rec_readdscolumn(LSX_RECITEM item, char **text, int col)
{
	ITEM_T *rec_item = (ITEM_T *)item;
	RGCB_NODE *rgcb_node = (RGCB_NODE *)rec_item->rcb_node->parent;
	FILE_T *fp = rgcb_node->file;
	RCB_NODE *ds_basics = (RCB_NODE *)rgcb_node->ds_basics;
	
	if (ds_basics)
	{
		RCB_NODE *rcb_node = rec_item->rcb_node;
		LSX_FILE_RCB *rcb = (LSX_FILE_RCB *)ds_basics->cbnode.cb;
		int colcount = 0;
		long offset = 0;
		WORD number = 0;
		int i = 0;
		BOOL is_v2 = FALSE;
		
		if (IS_V2(fp->fi->header->version))
		{
			is_v2 = TRUE;
			colcount = rcb->recordsize / (sizeof(WORD));
		}
		else
		{
			colcount = rcb->recordsize / (sizeof(WORD)*2);
		}
		
		if (col >= colcount)
		{
			return FALSE;
		}
		
		rcb = (LSX_FILE_RCB *)rcb_node->cbnode.cb;
		offset = rcb_node->cbnode.offset + 
			rcb_node->cbnode.cb->size + (rec_item->index)*rcb->recordsize;
		
		if (is_v2)
		{
			fseek(fp->fp, offset+col*sizeof(WORD), SEEK_SET);
			file_read(&number, sizeof(WORD), 1, fp->fp, BUFTYPE_WORD);
		}
		else
		{
			fseek(fp->fp, offset+col*sizeof(WORD)*2+fp->lang.offset, SEEK_SET);
			file_read(&number, sizeof(WORD), 1, fp->fp, BUFTYPE_WORD);
		}
		
		*text = hash_gettext(fp->htbl, number);
		
		return TRUE;
	}
	
	return FALSE;
}

BOOL fi_rec_readds(LSX_RECITEM item, char *textstrs[], int n)
{
	ITEM_T *rec_item = (ITEM_T *)item;
	RGCB_NODE *rgcb_node = (RGCB_NODE *)rec_item->rcb_node->parent;
	FILE_T *fp = rgcb_node->file;
	RCB_NODE *ds_basics = (RCB_NODE *)rgcb_node->ds_basics;
	
	if (ds_basics)
	{
		RCB_NODE *rcb_node = rec_item->rcb_node;
		LSX_FILE_RCB *rcb = (LSX_FILE_RCB *)ds_basics->cbnode.cb;
		int colcount = 0;
		long offset = 0;
		WORD number[MAX_DS_COLNUMBER] = {0};
		int i = 0;
		BOOL is_v2 = FALSE;
		
		if (IS_V2(fp->fi->header->version))
		{
			is_v2 = TRUE;
			colcount = rcb->recordsize / (sizeof(WORD));
		}
		else
		{
			colcount = rcb->recordsize / (sizeof(WORD)*2);
		}
		
		n = (n > colcount) ? colcount : n;
		rcb = (LSX_FILE_RCB *)rcb_node->cbnode.cb;
		offset = rcb_node->cbnode.offset + 
			rcb_node->cbnode.cb->size + (rec_item->index)*rcb->recordsize;

		if (is_v2)
		{
			fseek(fp->fp, offset, SEEK_SET);
			for (i=0; i<n; ++i)
			{
				file_read(&number[i], sizeof(WORD), 1, fp->fp, BUFTYPE_WORD);
			}
		}
		else
		{
			for (i=0; i<n; ++i)
			{
				fseek(fp->fp, offset+sizeof(WORD)*2*i+fp->lang.offset, SEEK_SET);
				file_read(&number[i], sizeof(WORD), 1, fp->fp, BUFTYPE_WORD);
			}
		}
		
		for (i=0; i<n; ++i)
		{
			textstrs[i] = hash_gettext(fp->htbl, number[i]);
		}
		
		return TRUE;
	}
	
	return FALSE;
}

BOOL fi_rec_readdsany(LSX_RECITEM item, char *textstrs[], const int cols[], int n)
{
	ITEM_T *rec_item = (ITEM_T *)item;
	RGCB_NODE *rgcb_node = (RGCB_NODE *)rec_item->rcb_node->parent;
	FILE_T *fp = rgcb_node->file;
	RCB_NODE *ds_basics = (RCB_NODE *)rgcb_node->ds_basics;
	
	if (ds_basics)
	{
		RCB_NODE *rcb_node = rec_item->rcb_node;
		LSX_FILE_RCB *rcb = (LSX_FILE_RCB *)ds_basics->cbnode.cb;
		int colcount = 0;
		long offset = 0;
		WORD number = 0;
		int i = 0;
		BOOL is_v2 = FALSE;
		
		if (IS_V2(fp->fi->header->version))
		{
			is_v2 = TRUE;
			colcount = rcb->recordsize / (sizeof(WORD));
		}
		else
		{
			colcount = rcb->recordsize / (sizeof(WORD)*2);
		}
		
		rcb = (LSX_FILE_RCB *)rcb_node->cbnode.cb;
		offset = rcb_node->cbnode.offset + 
			rcb_node->cbnode.cb->size + (rec_item->index)*rcb->recordsize;
		
		if (is_v2)
		{
			for (i=0; i<n; ++i)
			{
				if (cols[i] < 0 || cols[i] >= colcount) 
				{
					textstrs[i] = NULL;
				}
				else
				{
					fseek(fp->fp, offset+sizeof(WORD)*cols[i], SEEK_SET);
					file_read(&number, sizeof(WORD), 1, fp->fp, BUFTYPE_WORD);
					textstrs[i] = hash_gettext(fp->htbl, number);
				}
			}
		}
		else
		{
			for (i=0; i<n; ++i)
			{
				if (cols[i] < 0 || cols[i] >= colcount) 
				{
					textstrs[i] = NULL;
				}
				else
				{
					fseek(fp->fp, offset+sizeof(WORD)*cols[i]*2+fp->lang.offset, SEEK_SET);
					file_read(&number, sizeof(WORD), 1, fp->fp, BUFTYPE_WORD);
					textstrs[i] = hash_gettext(fp->htbl, number);
				}
			}
		}
		
		return TRUE;
	}
	
	return FALSE;
}

int fi_rec_readdsitemcount(LSX_RECGROUP grp)
{
	RGCB_NODE *rgcb_node = (RGCB_NODE *)grp;
	RCB_NODE *rcb_node = (RCB_NODE *)rgcb_node->rcblst.head;
	LSX_FILE_RCB *rcb = NULL;
	int count = 0;
	
	while (rcb_node)
	{
		if (rcb_node->cbnode.type == CB_TYPE_DATASTREAM)
		{
			rcb = (LSX_FILE_RCB *)rcb_node->cbnode.cb;
			count += rcb->cb.rgnsize / rcb->recordsize;
		}

		rcb_node = (RCB_NODE *)rcb_node->cbnode.next;
	}

	return count;
}

int fi_rec_readtypedscolindex(LSX_RECGROUP grp, WORD type)
{
	RGCB_NODE *rgcb_node = (RGCB_NODE *)grp;
	RCB_NODE *ds_basics = (RCB_NODE *)rgcb_node->ds_basics;
	int index = -1;
	
	if (ds_basics)
	{
		FILE_T *fp = rgcb_node->file;
		LSX_FILE_RCB *rcb = (LSX_FILE_RCB *)ds_basics->cbnode.cb;
		int colcount = rcb->recordsize / (sizeof(WORD)*2);
		long offset = ds_basics->cbnode.offset + rcb->cb.size + 2*rcb->recordsize;
		int i = 0;
		WORD val;
		
		fseek(fp->fp, offset, SEEK_SET);
		
		for (i=0; i<colcount; ++i)
		{
			file_read(&val, sizeof(WORD), 1, fp->fp, BUFTYPE_WORD);
			if (val == type)
			{
				index = i;
				break;
			}

			fseek(fp->fp, sizeof(WORD), SEEK_CUR);
		}
	}

	return index;
}

int fi_rec_readtypedscolcount(LSX_RECGROUP grp)
{
	RGCB_NODE *rgcb_node = (RGCB_NODE *)grp;
	RCB_NODE *ds_basics = (RCB_NODE *)rgcb_node->ds_basics;
	int count = 0;
	
	if (ds_basics)
	{
		FILE_T *fp = rgcb_node->file;
		LSX_FILE_RCB *rcb = (LSX_FILE_RCB *)ds_basics->cbnode.cb;
		int colcount = rcb->recordsize / (sizeof(WORD)*2);
		long offset = ds_basics->cbnode.offset + rcb->cb.size + 2*rcb->recordsize;
		int i = 0;
		WORD type;
		
		fseek(fp->fp, offset, SEEK_SET);
		
		for (i=0; i<colcount; ++i)
		{
			file_read(&type, sizeof(WORD), 1, fp->fp, BUFTYPE_WORD);
			if (type)
			{
				++count;
			}
			
			fseek(fp->fp, sizeof(WORD), SEEK_CUR);
		}
	}
	
	return count;
}

BOOL fi_rec_readtypedsname(LSX_RECGROUP grp, char *textstrs[], int n)
{
	RGCB_NODE *rgcb_node = (RGCB_NODE *)grp;
	RCB_NODE *ds_basics = (RCB_NODE *)rgcb_node->ds_basics;
	
	if (ds_basics)
	{
		FILE_T *fp = rgcb_node->file;
		LSX_FILE_RCB *rcb = (LSX_FILE_RCB *)ds_basics->cbnode.cb;
		int colcount = rcb->recordsize / (sizeof(WORD)*2);
		long offset = ds_basics->cbnode.offset + rcb->cb.size + 2*rcb->recordsize;
		WORD number[MAX_DS_COLNUMBER] = {0};
		int index[MAX_DS_COLNUMBER] = {0};
		int i, k;
		WORD type;
		
		fseek(fp->fp, offset, SEEK_SET);
		
		for (i=0,k=0; i<colcount; ++i)
		{
			file_read(&type, sizeof(WORD), 1, fp->fp, BUFTYPE_WORD);
			if (type)
			{
				index[k++] = i;
			}
			
			if (k >= n) break;
			fseek(fp->fp, sizeof(WORD), SEEK_CUR);
		}

		offset = ds_basics->cbnode.offset + rcb->cb.size;

		for (i=0; i<k; ++i)
		{
			fseek(fp->fp, offset+sizeof(WORD)*2*index[i]+fp->lang.offset, SEEK_SET);
			file_read(&number[i], sizeof(WORD), 1, fp->fp, BUFTYPE_WORD);
		}

		for (i=0; i<k; ++i)
		{
			textstrs[i] = hash_gettext(fp->htbl, number[i]);
		}

		return TRUE;
	}
	
	return FALSE;
}

BOOL fi_rec_readtypedsunit(LSX_RECGROUP grp, char *textstrs[], int n)
{
	RGCB_NODE *rgcb_node = (RGCB_NODE *)grp;
	RCB_NODE *ds_basics = (RCB_NODE *)rgcb_node->ds_basics;
	
	if (ds_basics)
	{
		FILE_T *fp = rgcb_node->file;
		LSX_FILE_RCB *rcb = (LSX_FILE_RCB *)ds_basics->cbnode.cb;
		int colcount = rcb->recordsize / (sizeof(WORD)*2);
		long offset = ds_basics->cbnode.offset + rcb->cb.size + 2*rcb->recordsize;
		WORD number[MAX_DS_COLNUMBER] = {0};
		int index[MAX_DS_COLNUMBER] = {0};
		int i, k;
		WORD type;
		
		fseek(fp->fp, offset, SEEK_SET);
		
		for (i=0,k=0; i<colcount; ++i)
		{
			file_read(&type, sizeof(WORD), 1, fp->fp, BUFTYPE_WORD);
			if (type)
			{
				index[k++] = i;
			}
			
			if (k >= n) break;
			fseek(fp->fp, sizeof(WORD), SEEK_CUR);
		}
		
		offset = ds_basics->cbnode.offset + rcb->cb.size + rcb->recordsize;
		
		for (i=0; i<k; ++i)
		{
			fseek(fp->fp, offset+sizeof(WORD)*2*index[i]+fp->lang.offset, SEEK_SET);
			file_read(&number[i], sizeof(WORD), 1, fp->fp, BUFTYPE_WORD);
		}
		
		for (i=0; i<k; ++i)
		{
			textstrs[i] = hash_gettext(fp->htbl, number[i]);
		}
		
		return TRUE;
	}
	
	return FALSE;
}

BOOL fi_rec_readtypedstype(LSX_RECGROUP grp, WORD type[], int n)
{
	RGCB_NODE *rgcb_node = (RGCB_NODE *)grp;
	RCB_NODE *ds_basics = (RCB_NODE *)rgcb_node->ds_basics;
	
	if (ds_basics)
	{
		FILE_T *fp = rgcb_node->file;
		LSX_FILE_RCB *rcb = (LSX_FILE_RCB *)ds_basics->cbnode.cb;
		int colcount = rcb->recordsize / (sizeof(WORD)*2);
		long offset = ds_basics->cbnode.offset + rcb->cb.size + 2*rcb->recordsize;
		int i, k;
		WORD val;
		
		fseek(fp->fp, offset, SEEK_SET);
		
		for (i=0,k=0; i<colcount; ++i)
		{
			file_read(&val, sizeof(WORD), 1, fp->fp, BUFTYPE_WORD);
			if (val)
			{
				type[k++] = val;
			}

			if (k >= n) break;
			
			fseek(fp->fp, sizeof(WORD), SEEK_CUR);
		}
		
		return TRUE;
	}
	
	return FALSE;
}

BOOL fi_rec_readtypeds(LSX_RECITEM item, char *textstrs[], int n)
{
	ITEM_T *rec_item = (ITEM_T *)item;
	RGCB_NODE *rgcb_node = (RGCB_NODE *)rec_item->rcb_node->parent;
	RCB_NODE *ds_basics = (RCB_NODE *)rgcb_node->ds_basics;
	
	if (ds_basics)
	{
		FILE_T *fp = rgcb_node->file;
		RCB_NODE *rcb_node = rec_item->rcb_node;
		LSX_FILE_RCB *rcb = (LSX_FILE_RCB *)ds_basics->cbnode.cb;
		int colcount = rcb->recordsize / (sizeof(WORD)*2);
		long offset = ds_basics->cbnode.offset + rcb->cb.size + 2*rcb->recordsize;
		WORD number[MAX_DS_COLNUMBER] = {0};
		int index[MAX_DS_COLNUMBER] = {0};
		int i, k;
		WORD type;
		
		fseek(fp->fp, offset, SEEK_SET);
		
		for (i=0,k=0; i<colcount; ++i)
		{
			file_read(&type, sizeof(WORD), 1, fp->fp, BUFTYPE_WORD);
			if (type)
			{
				index[k++] = i;
			}
			
			if (k >= n) break;
			fseek(fp->fp, sizeof(WORD), SEEK_CUR);
		}
		
		rcb = (LSX_FILE_RCB *)rcb_node->cbnode.cb;
		offset = rcb_node->cbnode.offset + 
			rcb_node->cbnode.cb->size + (rec_item->index)*rcb->recordsize;
		
		for (i=0; i<k; ++i)
		{
			fseek(fp->fp, offset+sizeof(WORD)*2*index[i]+fp->lang.offset, SEEK_SET);
			file_read(&number[i], sizeof(WORD), 1, fp->fp, BUFTYPE_WORD);
		}
		
		for (i=0; i<k; ++i)
		{
			textstrs[i] = hash_gettext(fp->htbl, number[i]);
		}
		
		return TRUE;
	}
	
	return FALSE;
}

BOOL fi_rec_readtypedsany(LSX_RECITEM item, char *textstrs[], const WORD type[], int n)
{
	ITEM_T *rec_item = (ITEM_T *)item;
	RGCB_NODE *rgcb_node = (RGCB_NODE *)rec_item->rcb_node->parent;
	RCB_NODE *ds_basics = (RCB_NODE *)rgcb_node->ds_basics;
	
	if (ds_basics)
	{
		FILE_T *fp = rgcb_node->file;
		LSX_FILE_RCB *rcb = (LSX_FILE_RCB *)ds_basics->cbnode.cb;
		int colcount = rcb->recordsize / (sizeof(WORD)*2);
		long offset = ds_basics->cbnode.offset + rcb->cb.size + 2*rcb->recordsize;
		int cols[MAX_DS_COLNUMBER];
		int i, k;
		WORD typeval;
		
		memset(cols, 0xFF, sizeof(cols));
		for (i=0; i<n; ++i)
		{
			fseek(fp->fp, offset, SEEK_SET);
			for (k=0; k<colcount; ++k)
			{
				file_read(&typeval, sizeof(WORD), 1, fp->fp, BUFTYPE_WORD);
				if (type[i] == typeval)
				{
					cols[i] = k;
					break;
				}
				
				fseek(fp->fp, sizeof(WORD), SEEK_CUR);
			}
		}
		
		return fi_rec_readdsany(item, textstrs, cols, n);
	}
	
	return FALSE;
}

BOOL fi_rec_modifydstype(LSX_RECGROUP grp, int col, WORD type)
{
	RGCB_NODE *rgcb_node = (RGCB_NODE *)grp;
	RCB_NODE *ds_basics = (RCB_NODE *)rgcb_node->ds_basics;
	
	if (ds_basics)
	{
		FILE_T *fp = rgcb_node->file;
		LSX_FILE_RCB *rcb = (LSX_FILE_RCB *)ds_basics->cbnode.cb;
		int colcount = rcb->recordsize / (sizeof(WORD)*2);
		long offset = ds_basics->cbnode.offset + rcb->cb.size + 2*rcb->recordsize;
		
		if (col > colcount)
		{
			return FALSE;
		}

		fseek(fp->fp, offset+col*sizeof(WORD)*2, SEEK_SET);
		fwrite(&type, sizeof(WORD), 1, fp->fp);
		fwrite(&type, sizeof(WORD), 1, fp->fp);
		
		return TRUE;
	}
	
	return FALSE;
}

LSX_RECITEM fi_rec_readfirstdsitem(LSX_RECGROUP grp)
{
	RGCB_NODE *rgcb_node = (RGCB_NODE *)grp;
	RCB_NODE *rcb_node = (RCB_NODE *)rgcb_node->rcblst.head;

	while (rcb_node)
	{
		if (rcb_node->cbnode.type == CB_TYPE_DATASTREAM)
			break;
		rcb_node = (RCB_NODE *)rcb_node->cbnode.next;
	}

	if (rcb_node)
	{
		rgcb_node->file->item.index = 0;
		rgcb_node->file->item.rcb_node = (RCB_NODE *)&rcb_node->cbnode;
		return (LSX_RECITEM)&rgcb_node->file->item;
	}
	
	return NULL;
}

LSX_RECITEM fi_rec_readlastdsitem(LSX_RECGROUP grp)
{
	RGCB_NODE *rgcb_node = (RGCB_NODE *)grp;
	RCB_NODE *rcb_node = (RCB_NODE *)rgcb_node->rcblst.tail;
	
	while (rcb_node)
	{
		if (rcb_node->cbnode.type == CB_TYPE_DATASTREAM)
			break;
		rcb_node = (RCB_NODE *)rcb_node->cbnode.prev;
	}
	
	if (rcb_node)
	{
		LSX_FILE_RCB *rcb = (LSX_FILE_RCB *)rcb_node->cbnode.cb;
		rgcb_node->file->item.index = rcb->cb.rgnsize/rcb->recordsize - 1;
		rgcb_node->file->item.rcb_node = (RCB_NODE *)&rcb_node->cbnode;
		return (LSX_RECITEM)&rgcb_node->file->item;
	}
	
	return NULL;
}

LSX_RECITEM rec_readnextntypeitem(LSX_RECITEM item, UINT itemtype, int n)
{
	ITEM_T *rec_item = (ITEM_T *)item;
	RCB_NODE *rcb_node = rec_item->rcb_node;
	RCB_NODE *prev_node = NULL;
	LSX_FILE_RCB *rcb = NULL;
	int rec_count = 0;
	int count = 0;
	int index = 0;
	
	if (rcb_node->cbnode.type == itemtype)
	{
		rcb = (LSX_FILE_RCB *)rcb_node->cbnode.cb;

		switch (itemtype)
		{
		case CB_TYPE_DTC:
		case CB_TYPE_DATASTREAM:
			rec_count = rcb->cb.rgnsize / rcb->recordsize;
			count = rec_count - rec_item->index - 1;
			break;
		case CB_TYPE_FREEZEFRAME:
		case CB_TYPE_READINIESS:
		case CB_TYPE_VERSIONINFO:
		case CB_TYPE_DSBASICS:
			rec_count = 1;
			count = 0;
			break;
		}

		prev_node = rcb_node;

		if (n <= count)
		{
			rec_item->index += n;
			return (LSX_RECITEM)rec_item;
		}
	}
	else
	{
		count = 0;
		prev_node = NULL;
	}

	while ((rcb_node = (RCB_NODE *)rcb_node->cbnode.next) != NULL)
	{
		if (rcb_node->cbnode.type == itemtype)
		{
			rcb = (LSX_FILE_RCB *)rcb_node->cbnode.cb;

			switch (itemtype)
			{
			case CB_TYPE_DTC:
			case CB_TYPE_DATASTREAM:
				rec_count = rcb->cb.rgnsize / rcb->recordsize;
				break;
			case CB_TYPE_FREEZEFRAME:
			case CB_TYPE_READINIESS:
			case CB_TYPE_VERSIONINFO:
			case CB_TYPE_DSBASICS:
				rec_count = 1;
				break;
			}

			if (rec_count >= n - count)
			{
				index = (n - count) - 1;
				break;
			}
			else
			{
				prev_node = rcb_node;
				count += rec_count;
			}
		}
	}

	if (rcb_node)
	{
		rec_item->rcb_node = rcb_node;
		rec_item->index = index;
	}
	else if (count > 0 && prev_node)
	{
		rec_item->rcb_node = prev_node;
		rec_item->index = rec_count - 1;
	}
	else
	{
		rec_item = NULL;
	}
	
	return (LSX_RECITEM)rec_item;
}

LSX_RECITEM rec_readprevntypeitem(LSX_RECITEM item, UINT itemtype, int n)
{
	ITEM_T *rec_item = (ITEM_T *)item;
	RCB_NODE *rcb_node = rec_item->rcb_node;
	RCB_NODE *prev_node = NULL;
	LSX_FILE_RCB *rcb = NULL;
	int rec_count = 0;
	int count = 0;
	int index = 0;
	
	if (rcb_node->cbnode.type == itemtype)
	{
		rcb = (LSX_FILE_RCB *)rcb_node->cbnode.cb;

		switch (itemtype)
		{
		case CB_TYPE_DTC:
		case CB_TYPE_DATASTREAM:
			rec_count = rcb->cb.rgnsize / rcb->recordsize;
			count = rec_item->index;
			break;
		case CB_TYPE_FREEZEFRAME:
		case CB_TYPE_READINIESS:
		case CB_TYPE_VERSIONINFO:
		case CB_TYPE_DSBASICS:
			rec_count = 1;
			count = 0;
			break;
		}

		prev_node = rcb_node;
		
		if (n <= count)
		{
			rec_item->index -= n;
			return (LSX_RECITEM)rec_item;
		}
	}
	else
	{
		count = 0;
		prev_node = NULL;
	}
	
	while ((rcb_node = (RCB_NODE *)rcb_node->cbnode.prev))
	{
		if (rcb_node->cbnode.type == itemtype)
		{
			rcb = (LSX_FILE_RCB *)rcb_node->cbnode.cb;

			switch (itemtype)
			{
			case CB_TYPE_DTC:
			case CB_TYPE_DATASTREAM:
				rec_count = rcb->cb.rgnsize / rcb->recordsize;
				break;
			case CB_TYPE_FREEZEFRAME:
			case CB_TYPE_READINIESS:
			case CB_TYPE_VERSIONINFO:
			case CB_TYPE_DSBASICS:
				rec_count = 1;
				break;
			}

			if (rec_count >= n - count)
			{
				index = rec_count - (n - count);
				break;
			}
			else
			{
				prev_node = rcb_node;
				count += rec_count;
			}
		}
	}
	
	if (rcb_node)
	{
		rec_item->rcb_node = rcb_node;
		rec_item->index = index;
	}
	else if (count > 0 && prev_node)
	{
		rec_item->rcb_node = prev_node;
		rec_item->index = 0;
	}
	else
	{
		rec_item = NULL;
	}
	
	return (LSX_RECITEM)rec_item;
}

LSX_RECITEM fi_rec_readrelndsitem(LSX_RECITEM item, int n)
{
	if (n >= 0)
	{
		return rec_readnextntypeitem(item, CB_TYPE_DATASTREAM, n);
	}

	return rec_readprevntypeitem(item, CB_TYPE_DATASTREAM, -n);
}

int rec_readcolcount(LSX_RECITEM item)
{
	ITEM_T *rec_item = (ITEM_T *)item;
	LSX_FILE_RCB *rcb = (LSX_FILE_RCB *)rec_item->rcb_node->cbnode.cb;
	FILE_INFO *fi = rec_item->rcb_node->parent->file->fi;

	if (IS_V2(fi->header->version))
	{
		return (rcb->recordsize / sizeof(WORD));
	}
	
	return (rcb->recordsize / (sizeof(WORD) * 2));
}

BOOL rec_readitem(LSX_RECITEM item, int index, char *textstrs[], int n)
{
	ITEM_T *rec_item = (ITEM_T *)item;
	RGCB_NODE *rgcb_node = (RGCB_NODE *)rec_item->rcb_node->parent;
	FILE_T *fp = rgcb_node->file;
	RCB_NODE *rcb_node = rec_item->rcb_node;
	LSX_FILE_RCB *rcb = (LSX_FILE_RCB *)rcb_node->cbnode.cb;
	int colcount = 0;
	long offset = 0;
	WORD number[MAX_DS_COLNUMBER] = {0};
	int i = 0;
	BOOL is_v2 = FALSE;

	if (IS_V2(fp->fi->header->version))
	{
		is_v2 = TRUE;
		colcount = rcb->recordsize / (sizeof(WORD));
	}
	else
	{
		colcount = rcb->recordsize / (sizeof(WORD)*2);
	}
	
	n = (n > colcount) ? colcount : n;
	offset = rcb_node->cbnode.offset + rcb_node->cbnode.cb->size + index*rcb->recordsize;

	if (is_v2)
	{
		fseek(fp->fp, offset, SEEK_SET);
		for (i=0; i<n; ++i)
		{
			file_read(&number[i], sizeof(WORD), 1, fp->fp, BUFTYPE_WORD);
		}
	}
	else
	{
		for (i=0; i<n; ++i)
		{
			fseek(fp->fp, offset+i*sizeof(WORD)*2+fp->lang.offset, SEEK_SET);
			file_read(&number[i], sizeof(WORD), 1, fp->fp, BUFTYPE_WORD);
		}
	}
	
	for (i=0; i<n; ++i)
	{
		textstrs[i] = hash_gettext(fp->htbl, number[i]);
	}
	
	return TRUE;
}

int fi_rec_readffcolcount(LSX_RECITEM item)
{
	return rec_readcolcount(item);
}

BOOL fi_rec_readffname(LSX_RECITEM item, char *textstrs[], int n)
{
	return rec_readitem(item, 0, textstrs, n);
}

BOOL fi_rec_readffunit(LSX_RECITEM item, char *textstrs[], int n)
{
	return rec_readitem(item, 1, textstrs, n);
}

BOOL fi_rec_readfftype(LSX_RECITEM item, WORD type[], int n)
{
	ITEM_T *rec_item = (ITEM_T *)item;
	RGCB_NODE *rgcb_node = (RGCB_NODE *)rec_item->rcb_node->parent;
	FILE_T *fp = rgcb_node->file;
	RCB_NODE *rcb_node = rec_item->rcb_node;
	LSX_FILE_RCB *rcb = (LSX_FILE_RCB *)rcb_node->cbnode.cb;
	int colcount = rcb->recordsize / (sizeof(WORD) * 2);
	long offset = 0;
	int i = 0;
	const index = 2;
	
	n = (n > colcount) ? colcount : n;
	offset = rcb_node->cbnode.offset + rcb_node->cbnode.cb->size + index*rcb->recordsize;
	fseek(fp->fp, offset, SEEK_SET);
	
	for (i=0; i<n; ++i)
	{
		file_read(&type[i], sizeof(WORD), 1, fp->fp, BUFTYPE_WORD);
		fseek(fp->fp, sizeof(WORD), SEEK_CUR);
	}
	
	return TRUE;
}

BOOL fi_rec_readfreezeframe(LSX_RECITEM item, char **dtc, char *textstrs[], int n)
{
	ITEM_T *rec_item = (ITEM_T *)item;
	RGCB_NODE *rgcb_node = (RGCB_NODE *)rec_item->rcb_node->parent;
	FILE_T *fp = rgcb_node->file;
	RCB_NODE *rcb_node = rec_item->rcb_node;
	LSX_FILE_RCB *rcb = (LSX_FILE_RCB *)rcb_node->cbnode.cb;

	*dtc = hash_gettext(fp->htbl, rcb->un.dtc);

	if (IS_V2(fp->fi->header->version))
	{
		return rec_readitem(item, 2, textstrs, n);
	}
	return rec_readitem(item, 3, textstrs, n);
}

int fi_rec_readffitemcount(LSX_RECGROUP grp)
{
	RGCB_NODE *rgcb_node = (RGCB_NODE *)grp;
	RCB_NODE *rcb_node = (RCB_NODE *)rgcb_node->rcblst.head;
	LSX_FILE_RCB *rcb = NULL;
	int count = 0;
	
	while (rcb_node)
	{
		if (rcb_node->cbnode.type == CB_TYPE_FREEZEFRAME)
		{
			rcb = (LSX_FILE_RCB *)rcb_node->cbnode.cb;
			++count;
		}
		
		rcb_node = (RCB_NODE *)rcb_node->cbnode.next;
	}
	
	return count;
}

LSX_RECITEM fi_rec_readfirstffitem(LSX_RECGROUP grp)
{
	RGCB_NODE *rgcb_node = (RGCB_NODE *)grp;
	RCB_NODE *rcb_node = (RCB_NODE *)rgcb_node->rcblst.head;
	
	while (rcb_node)
	{
		if (rcb_node->cbnode.type == CB_TYPE_FREEZEFRAME)
			break;
		rcb_node = (RCB_NODE *)rcb_node->cbnode.next;
	}
	
	if (rcb_node)
	{
		rgcb_node->file->item.index = 0;
		rgcb_node->file->item.rcb_node = (RCB_NODE *)&rcb_node->cbnode;
		return (LSX_RECITEM)&rgcb_node->file->item;
	}
	
	return NULL;
}

LSX_RECITEM fi_rec_readlastffitem(LSX_RECGROUP grp)
{
	RGCB_NODE *rgcb_node = (RGCB_NODE *)grp;
	RCB_NODE *rcb_node = (RCB_NODE *)rgcb_node->rcblst.tail;
	
	while (rcb_node)
	{
		if (rcb_node->cbnode.type == CB_TYPE_FREEZEFRAME)
			break;
		rcb_node = (RCB_NODE *)rcb_node->cbnode.prev;
	}
	
	if (rcb_node)
	{
		rgcb_node->file->item.index = 0;
		rgcb_node->file->item.rcb_node = (RCB_NODE *)&rcb_node->cbnode;
		return (LSX_RECITEM)&rgcb_node->file->item;
	}
	
	return NULL;
}

LSX_RECITEM fi_rec_readnextffitem(LSX_RECITEM item)
{
	return rec_readnextntypeitem(item, CB_TYPE_FREEZEFRAME, 1);
}

LSX_RECITEM fi_rec_readprevffitem(LSX_RECITEM item)
{
	return rec_readprevntypeitem(item, CB_TYPE_FREEZEFRAME, 1);
}

int fi_rec_readrdncolcount(LSX_RECITEM item)
{
	return rec_readcolcount(item);
}

BOOL fi_rec_readrdnname(LSX_RECITEM item, char *textstrs[], int n)
{
	return rec_readitem(item, 0, textstrs, n);
}

BOOL fi_rec_readreadiness(LSX_RECITEM item, char *textstrs[], int n)
{
	return rec_readitem(item, 1, textstrs, n);
}



