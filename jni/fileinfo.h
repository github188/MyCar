#ifndef _FILEINFO_H
#define _FILEINFO_H

#include "lsxdef.h"
#include "memblock.h"
#include "lsx.h"


#define FISF_BASEINFO				0x00000001L
#define FISF_AUTOINFO				0x00000002L
#define FISF_SPINFO					0x00000004L
#define FISF_USERINFO				0x00000008L
#define FISF_HEADER					0x00000010L
#define FISF_NEWFILE	(FISF_BASEINFO | FISF_AUTOINFO | FISF_SPINFO | FISF_USERINFO | FISF_HEADER)

#define CBSF_CBONLY					0x00000001L
#define CBSF_TEXT					0x00000002L
#define CBSF_ALL 					(CBSF_CBONLY | CBSF_TEXT)

#define CHECKFILE_OK					0
#define CHECKFILE_ERR_READHEADER		-1
#define CHECKFILE_ERR_FLAG				-2
#define CHECKFILE_ERR_VERSION			-3
#define CHECKFILE_ERR_HIGH_FILEVERSION  -4
#define CHECKFILE_ERR_HEADER			-5
#define CHECKFILE_ERR_IO				-6
#define CHECKFILE_ERR_CBSIZE			-7
#define CHECKFILE_ERR_RGCB				-8
#define CHECKFILE_ERR_LASTCB			-9
#define CHECKFILE_ERR_LOW_FILEVERSION	-10

#ifdef __cplusplus
extern "C" {
#endif

FILE_INFO *fi_init(int cbblk_size);
BOOL fi_deinit(FILE_INFO *fi);

int fi_checkfile(FILE *fp, unsigned short *version);

void fi_clear(FILE_T *fp);	
BOOL fi_newfile(FILE_T *fp);

BOOL fi_load(FILE_T *fp);
BOOL fi_save(FILE_T *fp);

BOOL fi_write_baseinfo(FILE_T *fp, const LSX_BASEINFO *baseinfo);
BOOL fi_write_autoinfo(FILE_T *fp, const LSX_AUTOINFO *autoinfo);
BOOL fi_write_spinfo(FILE_T *fp, const LSX_SPINFO *spinfo);
BOOL fi_write_userinfo(FILE_T *fp, const LSX_USERINFO *userinfo);

BOOL fi_read_baseinfo(FILE_T *fp, LSX_BASEINFO *baseinfo);
BOOL fi_read_autoinfo(FILE_T *fp, LSX_AUTOINFO *autoinfo);
BOOL fi_read_spinfo(FILE_T *fp, LSX_SPINFO *spinfo);
BOOL fi_read_userinfo(FILE_T *fp, LSX_USERINFO *userinfo);

int fi_read_langcode(FILE_T *fp, char code[], char code_en[], int size);
int fi_read_langname(FILE_T *fp, char name[], char name_en[], int size);
BOOL fi_selecttextlang(FILE_T *fp, const char *langcode);

int fi_rec_readgroupcount(FILE_T *fp);
LSX_RECGROUP fi_rec_readgroupid(FILE_T *fp, int i);
BOOL fi_rec_readgroupinfo(LSX_RECGROUP grp, char **name, 
	char **protocol, char **vin, char **starttime, char **endtime, int *dsinterval);
unsigned int fi_rec_readalltype(LSX_RECGROUP grp);
BOOL fi_rec_modifygroupinfo(LSX_RECGROUP grp, const char *name, const char *vin);

LSX_RECGROUP fi_rec_startnewgroup(FILE_T *fp, const char *name, 
	const char *protocol, const char *vin, const char *starttime, int dsinterval);
BOOL fi_rec_endnewgroup(LSX_RECGROUP grp, const char *endtime);

BOOL fi_rec_writedtc(LSX_RECGROUP grp, const char *dtc, 
	const LSX_STRING *state, const LSX_STRING *desc, const char *time);
BOOL fi_rec_writevi(LSX_RECGROUP grp, const LSX_STRING *vi);
BOOL fi_rec_writedsbasics(LSX_RECGROUP grp, 
	const LSX_STRING *namestrs[], const LSX_STRING *unitstrs[], const WORD type[], int n);
BOOL fi_rec_writeds(LSX_RECGROUP grp, const LSX_STRING *itemstrs[], int n);
BOOL fi_rec_writefreezeframe(LSX_RECGROUP grp, const char *dtc, 
	const LSX_STRING *namestrs[], const LSX_STRING *unitstrs[], const WORD type[], const LSX_STRING *itemstrs[], int n);
BOOL fi_rec_writereadiness(LSX_RECGROUP grp, 
	const LSX_STRING *namestrs[], const LSX_STRING *itemstrs[], int n);

int fi_rec_readdtccount(LSX_RECGROUP grp);
BOOL fi_rec_readdtc(LSX_RECITEM item, char **dtc, char **state, char **desc, char **time);
BOOL fi_rec_readdtcinfo(LSX_RECGROUP grp, const char *dtc, char **state, char **desc, char **time);
LSX_RECITEM fi_rec_readfirstdtcitem(LSX_RECGROUP grp);
LSX_RECITEM fi_rec_readlastdtcitem(LSX_RECGROUP grp);
LSX_RECITEM fi_rec_readnextdtcitem(LSX_RECITEM item);
LSX_RECITEM fi_rec_readprevdtcitem(LSX_RECITEM item);

int fi_rec_readdscolcount(LSX_RECGROUP grp);
BOOL fi_rec_readdsname(LSX_RECGROUP grp, char *textstrs[], int n);
BOOL fi_rec_readdsunit(LSX_RECGROUP grp, char *textstrs[], int n);
BOOL fi_rec_readdstype(LSX_RECGROUP grp, WORD type[], int n);

BOOL fi_rec_readds(LSX_RECITEM item, char *textstrs[], int n);
BOOL fi_rec_readdsany(LSX_RECITEM item, char *textstrs[], const int cols[], int n);

BOOL fi_rec_readdscolumn(LSX_RECITEM item, char **text, int col);

int fi_rec_readtypedscolindex(LSX_RECGROUP grp, WORD type);
int fi_rec_readtypedscolcount(LSX_RECGROUP grp);
BOOL fi_rec_readtypedsname(LSX_RECGROUP grp, char *textstrs[], int n);
BOOL fi_rec_readtypedsunit(LSX_RECGROUP grp, char *textstrs[], int n);
BOOL fi_rec_readtypedstype(LSX_RECGROUP grp, WORD type[], int n);
BOOL fi_rec_readtypeds(LSX_RECITEM item, char *textstrs[], int n);
BOOL fi_rec_readtypedsany(LSX_RECITEM item, char *textstrs[], const WORD type[], int n);

BOOL fi_rec_modifydstype(LSX_RECGROUP grp, int col, WORD type);

int fi_rec_readdsitemcount(LSX_RECGROUP grp);
LSX_RECITEM fi_rec_readfirstdsitem(LSX_RECGROUP grp);
LSX_RECITEM fi_rec_readlastdsitem(LSX_RECGROUP grp);
LSX_RECITEM fi_rec_readrelndsitem(LSX_RECITEM item, int n);

int fi_rec_readffcolcount(LSX_RECITEM item);
BOOL fi_rec_readffname(LSX_RECITEM item, char *textstrs[], int n);
BOOL fi_rec_readffunit(LSX_RECITEM item, char *textstrs[], int n);
BOOL fi_rec_readfftype(LSX_RECITEM item, WORD type[], int n);
BOOL fi_rec_readfreezeframe(LSX_RECITEM item, char **dtc, char *textstrs[], int n);

int fi_rec_readffitemcount(LSX_RECGROUP grp);
LSX_RECITEM fi_rec_readfirstffitem(LSX_RECGROUP grp);
LSX_RECITEM fi_rec_readlastffitem(LSX_RECGROUP grp);
LSX_RECITEM fi_rec_readnextffitem(LSX_RECITEM item);
LSX_RECITEM fi_rec_readprevffitem(LSX_RECITEM item);

int fi_rec_readrdncolcount(LSX_RECITEM item);
BOOL fi_rec_readrdnname(LSX_RECITEM item, char *textstrs[], int n);
BOOL fi_rec_readreadiness(LSX_RECITEM item, char *textstrs[], int n);

unsigned int fi_rec_readitemtype(LSX_RECITEM item);
LSX_RECITEM fi_rec_readfirstitem(LSX_RECGROUP grp);
LSX_RECITEM fi_rec_readlastitem(LSX_RECGROUP grp);
LSX_RECITEM fi_rec_readnextitem(LSX_RECITEM item);
LSX_RECITEM fi_rec_readprevitem(LSX_RECITEM item);

BOOL fi_rec_readvi(LSX_RECGROUP grp, char **vi);


#ifdef __cplusplus
}
#endif

#endif /* _FILEINFO_H */
