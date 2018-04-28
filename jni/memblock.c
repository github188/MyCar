#include <stdio.h>
#include <string.h>
#include <stdlib.h>

#include "memblock.h"

typedef struct _BLOCK
{
	int freesize;					/* 当前块空闲空间大小 */
	BYTE *buffer;					/* 块缓冲区 */
	struct _BLOCK *next;			/* 下一个块指针 */
} BLOCK;

MEM_BLOCK *mem_init(int size)
{
	BYTE *buffer = NULL;
	BLOCK *block = NULL;
	MEM_BLOCK *memblk = NULL;

	buffer = (BYTE *)malloc(size*sizeof(char));
	if (!buffer) goto error;
	block = (BLOCK *)malloc(sizeof(BLOCK));
	if (!block) goto error;
	memblk = (MEM_BLOCK *)malloc(sizeof(MEM_BLOCK));
	if (!memblk) goto error;

	block->freesize = size;
	block->buffer = buffer;
	block->next = NULL;

	memblk->size = size;
	memblk->head = block;
	memblk->tail = block;
	memblk->blocks = 1;
	return memblk;

error:
	if (buffer) free(buffer);
	if (block) free(block);
	return NULL;
}

BOOL mem_deinit(MEM_BLOCK *memblk)
{
	if (memblk)
	{
		BLOCK *block;
		BLOCK *head = (BLOCK *)memblk->head;
		while (head)
		{
			block = head->next;
			free(head->buffer);
			free(head);
			head = block;
		}

		free(memblk);
	}

	return TRUE;
}

BYTE *mem_alloc(MEM_BLOCK *memblk, int allocsize)
{
	BLOCK *block = (BLOCK *)memblk->head;
	BYTE *alloc_buf = NULL;

	while (block)
	{
		if (block->freesize >= allocsize)
		{
			break;
		}

		block = block->next;
	}

	if (!block)
	{
		BYTE *buffer = (BYTE *)malloc(memblk->size*sizeof(char));
		if (buffer)
		{
			block = (BLOCK *)malloc(sizeof(BLOCK));
			if (block)
			{
				block->buffer = buffer;
				block->freesize = memblk->size;
				block->next = NULL;
				((BLOCK *)memblk->tail)->next = block;
				memblk->tail = block;
				++memblk->blocks;
			}
			else
			{
				free(buffer);
			}
		}
	}

	if (block)
	{
		alloc_buf = block->buffer + (memblk->size - block->freesize);
		block->freesize -= allocsize;
	}
	
	return alloc_buf;
}

void mem_free(MEM_BLOCK *memblk)
{
	BLOCK *block = (BLOCK *)memblk->head;
	
	while (block)
	{
		block->freesize = memblk->size;
		block = block->next;
	}
}

BYTE *mem_getbuffer(MEM_BLOCK *memblk, int n, int blksize)
{
	BLOCK *block = NULL;
	BYTE *buffer = NULL;
	int count = 0;
	int block_number = 0;
	int blk_count = memblk->size / blksize;

	if (blk_count < 1 || n < 1 || n > (blk_count*memblk->blocks))
	{
		return NULL;
	}

	block_number = (n+blk_count-1) / blk_count;
	block = (BLOCK *)memblk->head;
	while (block)
	{
		if (++count == block_number)
		{
			n -= (block_number-1) * blk_count;
			buffer = block->buffer + (n-1) * blksize;
			break;
		}

		block = block->next;
	}

	return buffer;
}
