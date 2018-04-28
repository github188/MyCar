#ifdef WIN32
#include <windows.h>
#else
#include <unistd.h>
#endif
#include <string.h>
#include <stdlib.h>

#include "lsx.h"
#include "lsxdef.h"
#include "memblock.h"
#include "hash.h"
#include "fileinfo.h"


/*
#ifdef WIN32
#pragma comment(lib, "COMMODE.OBJ")
#endif
*/

#define SIZE_TEXT_BLOCK		(16*1024) /* V3调整为16k，原为8k */
#define SIZE_TEXTNODE_BLOCK	(8*1024)  /* V3调整为8k，原为4k */
#define SIZE_CB_BLOCK		(4*1024)

#define SIZE_HASH_TABLE		10949 /* V3调整，原为5471 */

static int check_file(const char *filename, unsigned short *version);
static int lsx_save(LSX_FILE file);

HLSX lsx_init()
{
	HASH_TBL *htbl = NULL;
	FILE_INFO *fi = NULL;
	LSX_T *lsx = NULL;

	htbl = hash_init(SIZE_HASH_TABLE, SIZE_TEXTNODE_BLOCK, SIZE_TEXT_BLOCK);
	if (!htbl) goto error;
	fi = fi_init(SIZE_CB_BLOCK);
	if (!fi) goto error;

	lsx = (LSX_T *)malloc(sizeof(LSX_T));
	if (!lsx) goto error;

/*
#ifdef _DEBUG
	printf("header: %d\n"
		"baseinfo: %d\n"
		"autoinfo: %d\n"
		"userinfo: %d\n"
		"spinfo: %d\n",
		sizeof(LSX_FILE_HEADER),
		sizeof(LSX_FILE_BASEINFO),
		sizeof(LSX_FILE_AUTOINFO),
		sizeof(LSX_FILE_USERINFO),
		sizeof(LSX_FILE_SPINFO));
#endif
*/

	lsx->htbl = htbl;
	lsx->fi = fi;
	return (HLSX)lsx;

error:
	if (htbl) hash_deinit(htbl);
	if (fi) fi_deinit(fi);
	return (HLSX)0;
}

int lsx_deinit(HLSX hlsx)
{
	if (hlsx)
	{
		LSX_T *lsx = (LSX_T *)hlsx;
		hash_deinit(lsx->htbl);
		fi_deinit(lsx->fi);

		free(lsx);
	}
	
	return 0;
}

LSX_FILE lsx_open(HLSX hlsx, const char *filename, int mode, int *error)
{
	FILE *fp = NULL;
	FILE_T *file = NULL;
	char open_mode[4] = {0};
	int result;
	*error = LSX_ERR_OK;

	if (hlsx == (HLSX)0 
		|| (mode != MODE_READ && mode != MODE_WRITE)
		|| !filename || !*filename)
	{
		*error = LSX_ERR_INVALID_PARAMETER;
		return NULL;
	}

	result = check_file(filename, NULL);

	switch (result)
	{
	case LSX_ERR_OK:
	case LSX_ERR_LOW_FILEVERSION:
		if (mode == MODE_READ)
		{
			strcpy(open_mode, "rb");
		}
		else //MODE_WRITE
		{
			if (result == LSX_ERR_OK)
			{
				strcpy(open_mode, "r+b");
			}
		}
		break;
	case LSX_ERR_FILE_NOTFOUND:
		if (mode == MODE_WRITE)
		{
			strcpy(open_mode, "w+b");
		}
		break;
	case LSX_ERR_INCORRECT_FORMAT:
	case LSX_ERR_HIGH_FILEVERSION:
		break;
	}

	*error = result;
	if (strlen(open_mode) == 0)
	{
		return NULL;
	}

	fp = fopen(filename, open_mode);
	if (fp != NULL)
	{
		file = (FILE_T *)malloc(sizeof(FILE_T));
		if (file != NULL)
		{
			LSX_T *lsx = (LSX_T *)hlsx;
			file->htbl = lsx->htbl;
			file->fi = lsx->fi;
			file->fp = fp;
			file->mode = mode;
			file->item.index = 0;
			file->item.rcb_node = NULL;
			memset(&file->lang, 0, sizeof(file->lang));

			fi_clear(file);
			if (strcmp(open_mode, "w+b") == 0)
			{
				fi_newfile(file);
			}
			else
			{
				fi_load(file);
			}
		}
		else
		{
			*error = LSX_ERR_ALLOC_MEMORY;
			fclose(fp);
		}
	}

	return (LSX_FILE)file;
}

int check_file(const char *filename, unsigned short *version)
{
	BOOL result;
	
	FILE *fp = fopen(filename, "rb");

	if (fp == NULL)
	{
		return LSX_ERR_FILE_NOTFOUND;
	}
	
	result = fi_checkfile(fp, version);

	fclose(fp);
	
	switch (result)
	{
	case CHECKFILE_OK:
		result = LSX_ERR_OK;
		break;
	case CHECKFILE_ERR_LOW_FILEVERSION:
		result = LSX_ERR_LOW_FILEVERSION;
		break;
	case CHECKFILE_ERR_HIGH_FILEVERSION:
		result = LSX_ERR_HIGH_FILEVERSION;
		break;
	default:
		result = LSX_ERR_INCORRECT_FORMAT;
		break;
	}
	return (result);
}

int lsx_close(LSX_FILE file)
{
	int ret = 0;
	FILE_T *fp = (FILE_T *)file;

	if (fp)
	{
		if (fp->mode == MODE_WRITE)
		{
			ret = fi_save(fp) ? 0 : -1;
		}

		fi_clear(fp);
		fclose(fp->fp);
		free(fp);
	}

	return ret;
}

unsigned int lsx_checkfile(const char *filename)
{
	int result = 0;
	unsigned short version = 0;
	int check = check_file(filename, &version);

	switch (check)
	{
	case LSX_ERR_OK:
		result = LSX_FILE_READABLE | LSX_FILE_WRITABLE;
		break;
	case LSX_ERR_LOW_FILEVERSION:
		result = LSX_FILE_READABLE;
		break;
	case LSX_ERR_HIGH_FILEVERSION:
		result = 0;
		break;
	default:
		result = 0;
		break;
	}

	if (result)
	{
		if (IS_V2(version))
		{
			result |= LSX_FILE_V2;
		}
		else if (IS_V3(version))
		{
			result |= LSX_FILE_V3;
		}
	}

	return result;
}

int lsx_save(LSX_FILE file)
{
	FILE_T *fp = (FILE_T *)file;
	if (fp && fp->mode == MODE_WRITE)
	{
		if (fi_save(fp))
		{
			int ret = fflush(fp->fp);
#ifndef WIN32
			sync();
#endif
			return ret;
		}
	}
	
	return -1;
}



/*
	读写基本信息：
 */

int lsx_read_baseinfo(LSX_FILE file, LSX_BASEINFO *baseinfo)
{
	FILE_T *fp = (FILE_T *)file;
	if (fp)
	{
		return fi_read_baseinfo(fp, baseinfo) ? 0 : -1;
	}

	return -1;
}

int lsx_write_baseinfo(LSX_FILE file, const LSX_BASEINFO *baseinfo)
{
	FILE_T *fp = (FILE_T *)file;
	if (!fp || fp->mode == MODE_READ)
	{
		return -1;
	}

	return (fi_write_baseinfo(fp, baseinfo)) ? 0 : -1;
}


/*
	读写车辆信息：
 */

int lsx_read_autoinfo(LSX_FILE file, LSX_AUTOINFO *autoinfo)
{
	FILE_T *fp = (FILE_T *)file;
	if (fp)
	{
		return fi_read_autoinfo(fp, autoinfo) ? 0 : -1;
	}

	return -1;
}

int lsx_write_autoinfo(LSX_FILE file, const LSX_AUTOINFO *autoinfo)
{
	FILE_T *fp = (FILE_T *)file;
	if (!fp || fp->mode == MODE_READ)
	{
		return -1;
	}
	
	return (fi_write_autoinfo(fp, autoinfo)) ? 0 : -1;
}


/*
	读写经销商信息：
 */

int lsx_read_spinfo(LSX_FILE file, LSX_SPINFO *spinfo)
{
	FILE_T *fp = (FILE_T *)file;
	if (fp)
	{
		return fi_read_spinfo(fp, spinfo) ? 0 : -1;
	}
	
	return -1;
}

int lsx_write_spinfo(LSX_FILE file, const LSX_SPINFO *spinfo)
{
	FILE_T *fp = (FILE_T *)file;
	if (!fp || fp->mode == MODE_READ)
	{
		return -1;
	}
	
	return (fi_write_spinfo(fp, spinfo)) ? 0 : -1;
}


/*
	读写用户信息：
 */

int lsx_read_userinfo(LSX_FILE file, LSX_USERINFO *userinfo)
{
	FILE_T *fp = (FILE_T *)file;
	if (fp)
	{
		return fi_read_userinfo(fp, userinfo) ? 0 : -1;
	}
	
	return -1;
}

int lsx_write_userinfo(LSX_FILE file, const LSX_USERINFO *userinfo)
{
	FILE_T *fp = (FILE_T *)file;
	if (!fp || fp->mode == MODE_READ)
	{
		return -1;
	}
	
	return (fi_write_userinfo(fp, userinfo)) ? 0 : -1;
}



int lsx_rec_readgroupcount(LSX_FILE file)
{
	FILE_T *fp = (FILE_T *)file;
	if (fp)
	{
		return fi_rec_readgroupcount(fp);
	}

	return -1;
}

LSX_RECGROUP lsx_rec_readgroupid(LSX_FILE file, int i)
{
	FILE_T *fp = (FILE_T *)file;
	if (fp && i > 0)
	{
		return fi_rec_readgroupid(fp, i);
	}

	return NULL;
}

int lsx_rec_readgroupinfo(LSX_RECGROUP grp, char **name, 
	char **protocol, char **vin, char **starttime, char **endtime, int *dsinterval)
{
	if (grp)
	{
		return fi_rec_readgroupinfo(grp, name, protocol, vin, starttime, endtime, dsinterval) ? 0 : -1;
	}

	return -1;
}

int lsx_rec_modifygroupinfo(LSX_RECGROUP grp, const char *name, const char *vin)
{
	if (grp && (name || vin))
	{
		return fi_rec_modifygroupinfo(grp, name, vin) ? 0 : -1;
	}

	return -1;
}

unsigned int lsx_rec_readalltype(LSX_RECGROUP grp)
{
	if (grp)
	{
		return fi_rec_readalltype(grp);
	}

	return 0;
}

unsigned int lsx_rec_readitemtype(LSX_RECITEM item)
{
	if (item)
	{
		return fi_rec_readitemtype(item);
	}

	return 0;
}

LSX_RECITEM lsx_rec_readfirstitem(LSX_RECGROUP grp)
{
	if (grp)
	{
		return fi_rec_readfirstitem(grp);
	}
	
	return NULL;
}

LSX_RECITEM lsx_rec_readlastitem(LSX_RECGROUP grp)
{
	if (grp)
	{
		return fi_rec_readlastitem(grp);
	}
	
	return NULL;
}

LSX_RECITEM lsx_rec_readnextitem(LSX_RECITEM item)
{
	if (item)
	{
		return fi_rec_readnextitem(item);
	}
	
	return NULL;
}

LSX_RECITEM lsx_rec_readprevitem(LSX_RECITEM item)
{
	if (item)
	{
		return fi_rec_readprevitem(item);
	}
	
	return NULL;
}

LSX_RECITEM lsx_rec_readfirstdtcitem(LSX_RECGROUP grp)
{
	if (grp)
	{
		return fi_rec_readfirstdtcitem(grp);
	}

	return NULL;
}

LSX_RECITEM lsx_rec_readlastdtcitem(LSX_RECGROUP grp)
{
	if (grp)
	{
		return fi_rec_readlastdtcitem(grp);
	}
	
	return NULL;
}

LSX_RECITEM lsx_rec_readnextdtcitem(LSX_RECITEM item)
{
	if (item)
	{
		return fi_rec_readnextdtcitem(item);
	}
	
	return NULL;
}

LSX_RECITEM lsx_rec_readprevdtcitem(LSX_RECITEM item)
{
	if (item)
	{
		return fi_rec_readprevdtcitem(item);
	}
	
	return NULL;
}

int lsx_rec_readdtccount(LSX_RECGROUP grp)
{
	if (grp)
	{
		return fi_rec_readdtccount(grp);
	}
	
	return -1;
}

int lsx_rec_readdtc(LSX_RECITEM item, char **dtc, char **state, char **desc, char **time)
{
	if (item && dtc)
	{
		ITEM_T *rec_item = (ITEM_T *)item;
		if (rec_item->rcb_node->cbnode.type == CB_TYPE_DTC)
		{
			return fi_rec_readdtc(item, dtc, state, desc, time) ? 0 : -1;
		}
	}

	return -1;
}

int lsx_rec_readvi(LSX_RECGROUP grp, char **vi)
{
	if (grp && vi)
	{
		return fi_rec_readvi(grp, vi) ? 0 : -1;
	}

	return -1;
}

int lsx_rec_readdsitemcount(LSX_RECGROUP grp)
{
	if (grp)
	{
		return fi_rec_readdsitemcount(grp);
	}
	
	return -1;
}

int lsx_rec_readdscolcount(LSX_RECGROUP grp)
{
	if (grp)
	{
		return fi_rec_readdscolcount(grp);
	}

	return -1;
}

int lsx_rec_readdsname(LSX_RECGROUP grp, char *textstrs[], int n)
{
	if (grp && n > 0)
	{
		return fi_rec_readdsname(grp, textstrs, n) ? 0 : -1;
	}

	return -1;
}

int lsx_rec_readdsunit(LSX_RECGROUP grp, char *textstrs[], int n)
{
	if (grp && n > 0)
	{
		return fi_rec_readdsunit(grp, textstrs, n) ? 0 : -1;
	}
	
	return -1;
}

int lsx_rec_readds(LSX_RECITEM item, char *textstrs[], int n)
{
	if (item && n > 0)
	{
		ITEM_T *rec_item = (ITEM_T *)item;
		if (rec_item->rcb_node->cbnode.type == CB_TYPE_DATASTREAM)
		{
			return fi_rec_readds(item, textstrs, n) ? 0 : -1;
		}
	}

	return -1;
}

int lsx_rec_readdsany(LSX_RECITEM item, char *textstrs[], const int cols[], int n)
{
	if (item && n > 0)
	{
		ITEM_T *rec_item = (ITEM_T *)item;
		if (rec_item->rcb_node->cbnode.type == CB_TYPE_DATASTREAM)
		{
			return fi_rec_readdsany(item, textstrs, cols, n) ? 0 : -1;
		}
	}

	return -1;
}

int lsx_rec_readdstype(LSX_RECGROUP grp, unsigned short type[], int n)
{
	if (grp && n > 0)
	{
		RGCB_NODE *rgcb_node = (RGCB_NODE *)grp;
		FILE_T *fp = rgcb_node->file;
		if (!IS_V2(fp->fi->header->version))
		{
			return fi_rec_readdstype(grp, type, n) ? 0 : -1;
		}
	}
	
	return -1;
}

int lsx_rec_readdscolumn(LSX_RECITEM item, char **text, int col)
{
	if (item && text && col >= 0)
	{
		ITEM_T *rec_item = (ITEM_T *)item;
		if (rec_item->rcb_node->cbnode.type == CB_TYPE_DATASTREAM)
		{
			return fi_rec_readdscolumn(item, text, col) ? 0 : -1;
		}
	}
	
	return -1;
}

int lsx_rec_readtypedscolindex(LSX_RECGROUP grp, unsigned short type)
{
	if (grp && type)
	{
		RGCB_NODE *rgcb_node = (RGCB_NODE *)grp;
		FILE_T *fp = rgcb_node->file;
		if (!IS_V2(fp->fi->header->version))
		{
			return fi_rec_readtypedscolindex(grp, type);
		}
	}
	
	return -1;
}

int lsx_rec_readtypedscolcount(LSX_RECGROUP grp)
{
	if (grp)
	{
		RGCB_NODE *rgcb_node = (RGCB_NODE *)grp;
		FILE_T *fp = rgcb_node->file;
		if (!IS_V2(fp->fi->header->version))
		{
			return fi_rec_readtypedscolcount(grp);
		}
	}
	
	return -1;
}

int lsx_rec_readtypedsname(LSX_RECGROUP grp, char *textstrs[], int n)
{
	if (grp && n > 0)
	{
		RGCB_NODE *rgcb_node = (RGCB_NODE *)grp;
		FILE_T *fp = rgcb_node->file;
		if (!IS_V2(fp->fi->header->version))
		{
			return fi_rec_readtypedsname(grp, textstrs, n) ? 0 : -1;
		}
	}
	
	return -1;
}

int lsx_rec_readtypedsunit(LSX_RECGROUP grp, char *textstrs[], int n)
{
	if (grp && n > 0)
	{
		RGCB_NODE *rgcb_node = (RGCB_NODE *)grp;
		FILE_T *fp = rgcb_node->file;
		if (!IS_V2(fp->fi->header->version))
		{
			return fi_rec_readtypedsunit(grp, textstrs, n) ? 0 : -1;
		}
	}

	return -1;
}

int lsx_rec_readtypedstype(LSX_RECGROUP grp, unsigned short type[], int n)
{
	if (grp && n > 0)
	{
		RGCB_NODE *rgcb_node = (RGCB_NODE *)grp;
		FILE_T *fp = rgcb_node->file;
		if (!IS_V2(fp->fi->header->version))
		{
			return fi_rec_readtypedstype(grp, type, n) ? 0 : -1;
		}
	}
	
	return -1;
}

int lsx_rec_readtypeds(LSX_RECITEM item, char *textstrs[], int n)
{
	if (item && n > 0)
	{
		ITEM_T *rec_item = (ITEM_T *)item;
		RGCB_NODE *rgcb_node = rec_item->rcb_node->parent;
		FILE_T *fp = rgcb_node->file;
		if (!IS_V2(fp->fi->header->version))
		{
			return fi_rec_readtypeds(item, textstrs, n) ? 0 : -1;
		}
	}
	
	return -1;
}

int lsx_rec_readtypedsany(LSX_RECITEM item, char *textstrs[], const unsigned short type[], int n)
{
	if (item && n > 0)
	{
		ITEM_T *rec_item = (ITEM_T *)item;
		RGCB_NODE *rgcb_node = rec_item->rcb_node->parent;
		FILE_T *fp = rgcb_node->file;
		if (!IS_V2(fp->fi->header->version))
		{
			return fi_rec_readtypedsany(item, textstrs, type, n) ? 0 : -1;
		}
	}
	
	return -1;
}

int lsx_rec_modifydstype(LSX_RECGROUP grp, int col, unsigned short type)
{
	if (grp && col >= 0)
	{
		RGCB_NODE *rgcb_node = (RGCB_NODE *)grp;
		FILE_T *fp = rgcb_node->file;
		if (fp->mode == MODE_WRITE
			&& !IS_V2(fp->fi->header->version))
		{
			return fi_rec_modifydstype(grp, col, type) ? 0 : -1;
		}
	}
	
	return -1;
}

LSX_RECITEM lsx_rec_readfirstdsitem(LSX_RECGROUP grp)
{
	if (grp)
	{
		return fi_rec_readfirstdsitem(grp);
	}

	return NULL;
}

LSX_RECITEM lsx_rec_readlastdsitem(LSX_RECGROUP grp)
{
	if (grp)
	{
		return fi_rec_readlastdsitem(grp);
	}
	
	return NULL;
}

LSX_RECITEM lsx_rec_readrelndsitem(LSX_RECITEM item, int n)
{
	if (item)
	{
		if (n == 0)
		{
			return item;
		}
		else
		{
			return fi_rec_readrelndsitem(item, n);
		}
	}
	
	return NULL;
}

int lsx_rec_readffitemcount(LSX_RECGROUP grp)
{
	if (grp)
	{
		return fi_rec_readffitemcount(grp);
	}

	return -1;
}

int lsx_rec_readffcolcount(LSX_RECITEM item)
{
	if (item)
	{
		ITEM_T *rec_item = (ITEM_T *)item;
		if (rec_item->rcb_node->cbnode.type == CB_TYPE_FREEZEFRAME)
		{
			return fi_rec_readffcolcount(item);
		}
	}
	
	return -1;
}

int lsx_rec_readffname(LSX_RECITEM item, char *textstrs[], int n)
{
	if (item && n > 0)
	{
		ITEM_T *rec_item = (ITEM_T *)item;
		if (rec_item->rcb_node->cbnode.type == CB_TYPE_FREEZEFRAME)
		{
			return fi_rec_readffname(item, textstrs, n) ? 0 : -1;
		}
	}
	
	return -1;
}

int lsx_rec_readffunit(LSX_RECITEM item, char *textstrs[], int n)
{
	if (item && n > 0)
	{
		ITEM_T *rec_item = (ITEM_T *)item;
		if (rec_item->rcb_node->cbnode.type == CB_TYPE_FREEZEFRAME)
		{
			return fi_rec_readffunit(item, textstrs, n) ? 0 : -1;
		}
	}
	
	return -1;
}

int lsx_rec_readfftype(LSX_RECITEM item, unsigned short type[], int n)
{
	if (item && n > 0)
	{
		ITEM_T *rec_item = (ITEM_T *)item;
		FILE_T *fp = rec_item->rcb_node->parent->file;
		if (rec_item->rcb_node->cbnode.type == CB_TYPE_FREEZEFRAME
			&& !IS_V2(fp->fi->header->version))
		{
			return fi_rec_readfftype(item, type, n) ? 0 : -1;
		}
	}
	
	return -1;
}

int lsx_rec_readfreezeframe(LSX_RECITEM item, char **dtc, char *textstrs[], int n)
{
	if (item && n > 0)
	{
		ITEM_T *rec_item = (ITEM_T *)item;
		if (rec_item->rcb_node->cbnode.type == CB_TYPE_FREEZEFRAME)
		{
			return fi_rec_readfreezeframe(item, dtc, textstrs, n) ? 0 : -1;
		}
	}
	
	return -1;
}

LSX_RECITEM lsx_rec_readfirstffitem(LSX_RECGROUP grp)
{
	if (grp)
	{
		return fi_rec_readfirstffitem(grp);
	}

	return NULL;
}

LSX_RECITEM lsx_rec_readlastffitem(LSX_RECGROUP grp)
{
	if (grp)
	{
		return fi_rec_readlastffitem(grp);
	}
	
	return NULL;
}

LSX_RECITEM lsx_rec_readnextffitem(LSX_RECITEM item)
{
	if (item)
	{
		return fi_rec_readnextffitem(item);
	}
	
	return NULL;
}

LSX_RECITEM lsx_rec_readprevffitem(LSX_RECITEM item)
{
	if (item)
	{
		return fi_rec_readprevffitem(item);
	}
	
	return NULL;
}

int lsx_rec_readrdncolcount(LSX_RECITEM item)
{
	if (item)
	{
		ITEM_T *rec_item = (ITEM_T *)item;
		if (rec_item->rcb_node->cbnode.type == CB_TYPE_READINIESS)
		{
			return fi_rec_readrdncolcount(item);
		}
	}
	
	return -1;
}

int lsx_rec_readrdnname(LSX_RECITEM item, char *textstrs[], int n)
{
	if (item && n > 0)
	{
		ITEM_T *rec_item = (ITEM_T *)item;
		if (rec_item->rcb_node->cbnode.type == CB_TYPE_READINIESS)
		{
			return fi_rec_readrdnname(item, textstrs, n) ? 0 : -1;
		}
	}
	
	return -1;
}

int lsx_rec_readreadiness(LSX_RECITEM item, char *textstrs[], int n)
{
	if (item && n > 0)
	{
		ITEM_T *rec_item = (ITEM_T *)item;
		if (rec_item->rcb_node->cbnode.type == CB_TYPE_READINIESS)
		{
			return fi_rec_readreadiness(item, textstrs, n) ? 0 : -1;
		}
	}
	
	return -1;
}


int lsx_rec_readdtcinfo(LSX_RECGROUP grp, const char *dtc, char **state, char **desc, char **time)
{
	if (grp && dtc && *dtc && state && desc)
	{
		return fi_rec_readdtcinfo(grp, dtc, state, desc, time) ? 0 : -1;
	}

	return -1;
}

LSX_RECGROUP lsx_rec_writenewgroup(LSX_FILE file, const char *name, 
	const char *protocol, const char *vin, const char *starttime, int dsinterval)
{
	FILE_T *fp = (FILE_T *)file;
	if (fp && name && *name && fp->mode == MODE_WRITE)
	{
		return fi_rec_startnewgroup(fp, name, protocol, vin, starttime, dsinterval);
	}

	return NULL;
}

int lsx_rec_finishnewgroup(LSX_RECGROUP grp, const char *endtime)
{
	if (grp && endtime && *endtime)
	{
		RGCB_NODE *rgcb_node = (RGCB_NODE *)grp;
		FILE_T *fp = rgcb_node->file;
		if (fp->mode == MODE_WRITE && !rgcb_node->cbnode.next)
		{
			fi_rec_endnewgroup(grp, endtime);
			return lsx_save(fp);
		}
	}

	return -1;
}

int lsx_rec_writedtc(LSX_RECGROUP grp, const char *dtc, 
	const LSX_STRING *state, const LSX_STRING *desc, const char *time)
{
	if (grp && dtc && *dtc)
	{
		RGCB_NODE *rgcb_node = (RGCB_NODE *)grp;
		FILE_T *fp = rgcb_node->file;
		if (fp->mode == MODE_WRITE && !rgcb_node->cbnode.next)
		{
			return fi_rec_writedtc(grp, dtc, state, desc, time) ? 0 : -1;
		}
	}

	return -1;
}

int lsx_rec_writevi(LSX_RECGROUP grp, const LSX_STRING *vi)
{
	if (grp && vi)
	{
		RGCB_NODE *rgcb_node = (RGCB_NODE *)grp;
		FILE_T *fp = rgcb_node->file;
		if (fp->mode == MODE_WRITE && !rgcb_node->cbnode.next)
		{
			return fi_rec_writevi(grp, vi) ? 0 : -1;
		}
	}
	return -1;
}

int lsx_rec_writedsbasics(LSX_RECGROUP grp, 
	const LSX_STRING *namestrs[], const LSX_STRING *unitstrs[], const unsigned short type[], int n)
{
	if (grp && n > 0)
	{
		RGCB_NODE *rgcb_node = (RGCB_NODE *)grp;
		FILE_T *fp = rgcb_node->file;
		if (fp->mode == MODE_WRITE && !rgcb_node->cbnode.next)
		{
			return fi_rec_writedsbasics(grp, namestrs, unitstrs, type, n) ? 0 : -1;
		}
	}
	return -1;
}

int lsx_rec_writeds(LSX_RECGROUP grp, const LSX_STRING *itemstrs[], int n)
{
	if (grp && n > 0)
	{
		RGCB_NODE *rgcb_node = (RGCB_NODE *)grp;
		FILE_T *fp = rgcb_node->file;
		if (fp->mode == MODE_WRITE && !rgcb_node->cbnode.next)
		{
			return fi_rec_writeds(grp, itemstrs, n) ? 0 : -1;
		}
	}
	return -1;
}

int lsx_rec_writefreezeframe(LSX_RECGROUP grp, const char *dtc, 
	const LSX_STRING *namestrs[], const LSX_STRING *unitstrs[], 
	const unsigned short type[], const LSX_STRING *textstrs[], int n)
{
	if (grp && dtc && *dtc && n > 0)
	{
		RGCB_NODE *rgcb_node = (RGCB_NODE *)grp;
		FILE_T *fp = rgcb_node->file;
		if (fp->mode == MODE_WRITE && !rgcb_node->cbnode.next)
		{
			return fi_rec_writefreezeframe(grp, dtc, namestrs, unitstrs, type, textstrs, n) ? 0 : -1;
		}
	}

	return -1;
}

int lsx_rec_writereadiness(LSX_RECGROUP grp, 
	const LSX_STRING *namestrs[], const LSX_STRING *textstrs[], int n)
{
	if (grp && n > 0)
	{
		RGCB_NODE *rgcb_node = (RGCB_NODE *)grp;
		FILE_T *fp = rgcb_node->file;
		if (fp->mode == MODE_WRITE && !rgcb_node->cbnode.next)
		{
			return fi_rec_writereadiness(grp, namestrs, textstrs, n) ? 0 : -1;
		}
	}
	
	return -1;
}

int lsx_read_langcode(LSX_FILE file, char code[], char code_en[], int size)
{
	memset(code, 0, size);
	memset(code_en, 0, size);
	
	if (file && size >= 4)
	{
		int count = 0;
		FILE_T *fp = (FILE_T *)file;

		if (strlen(fp->lang.code) > 0)
		{
			++count;
			strcpy(code, fp->lang.code);
		}
		if (strlen(fp->lang.code_en) > 0)
		{
			++count;
			strcpy(code_en, fp->lang.code_en);
		}
		
		return count;
	}
	
	return -1;
}

int lsx_read_langname(LSX_FILE file, char name[], char name_en[], int size)
{
	if (file && size >= 30)
	{
		FILE_T *fp = (FILE_T *)file;
		return fi_read_langname(fp, name, name_en, size);
	}
	
	return -1;
}

int lsx_selectreadtextlang(LSX_FILE file, const char *langcode)
{
	if (file && langcode)
	{
		FILE_T *fp = (FILE_T *)file;
		return fi_selecttextlang(fp, langcode) ? 0 : -1;
	}

	return -1;
}

unsigned short lsx_read_fileversion(LSX_FILE file)
{
	if (file)
	{
		FILE_T *fp = (FILE_T *)file;
		return fp->fi->header->version;
	}

	return 0;
}
