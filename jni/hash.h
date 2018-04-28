#ifndef _HASH_H
#define _HASH_H

#include "lsxdef.h"
#include "lsx.h"


#ifdef __cplusplus
extern "C" {
#endif
	
HASH_TBL *hash_init(int htbl_size, int nodeblk_size, int textblk_size);
BOOL hash_deinit(HASH_TBL *hashtbl);

int hash_add(FILE_T *fp, const char *str);
void hash_removeall(HASH_TBL *htbl);

char *hash_gettext(HASH_TBL *htbl, WORD number);

BOOL hash_load(FILE_T *fp, CBNODE_LST *cblst);
int hash_savetext(FILE_T *fp, long offset, int *totalsize);

#ifdef __cplusplus
}
#endif

#endif /* _HASH_H */





















