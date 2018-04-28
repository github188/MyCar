#ifndef _MEMBLOCK_H
#define _MEMBLOCK_H

#include "lsxdef.h"

#ifdef __cplusplus
extern "C" {
#endif
	
MEM_BLOCK *mem_init(int size);
BOOL mem_deinit(MEM_BLOCK *memblk);

BYTE *mem_alloc(MEM_BLOCK *memblk, int allocsize);
void mem_free(MEM_BLOCK *memblk);

BYTE *mem_getbuffer(MEM_BLOCK *memblk, int n, int blksize);

#ifdef __cplusplus
}
#endif

#endif /* _MEMBLOCK_H */
