#include <string.h>
#include <stdlib.h>

#include "hash.h"
#include "list.h"
#include "fileinfo.h"
#include "file.h"

#define MAX_NUMBER		0xFFFFFFFF

static int toint(const BYTE *buf, int size);

static UINT calc_hashnr(const BYTE *key, int length);
static TEXT_NODE *create_node(HASH_TBL *htbl, const char *str, int len, int lenbytes);

HASH_TBL *hash_init(int htbl_size, int nodeblk_size, int textblk_size)
{
	MEM_BLOCK *nodeblock = NULL;
	MEM_BLOCK *textblock = NULL;
	TEXTNODE_LST *nodelst = NULL;
	HASH_TBL *htbl = NULL;
	TEXT_NODE **startaddr = NULL;
	
	nodeblock = mem_init(nodeblk_size);
	if (!nodeblock) goto error;
	textblock = mem_init(textblk_size);
	if (!textblock) goto error;

	startaddr = (TEXT_NODE **)malloc(htbl_size*sizeof(TEXT_NODE *));
	if (!startaddr) goto error;

	nodelst = (TEXTNODE_LST *)malloc(sizeof(TEXTNODE_LST));
	if (!nodelst) goto error;

	htbl = (HASH_TBL *)malloc(sizeof(HASH_TBL));
	if (!htbl) goto error;

	memset(startaddr, 0, htbl_size*sizeof(TEXT_NODE *));
	list_init_textnode(nodelst);

	htbl->size = htbl_size;
	htbl->startaddr = startaddr;
	htbl->nodeblock = nodeblock;
	htbl->textblock = textblock;
	htbl->numberlist = nodelst;
	htbl->nodes = 0;
	htbl->elements = 0;
	return htbl;

error:
	if (nodeblock) mem_deinit(nodeblock);
	if (textblock) mem_deinit(textblock);
	if (startaddr) free(startaddr);
	if (nodelst) free(nodelst);
	return NULL;
}

BOOL hash_deinit(HASH_TBL *hashtbl)
{
	if (hashtbl)
	{
		free(hashtbl->startaddr);
		free(hashtbl->numberlist);
		mem_deinit(hashtbl->nodeblock);
		mem_deinit(hashtbl->textblock);
		free(hashtbl);
	}

	return TRUE;
}

int toint(const BYTE *buf, int size)
{
	int ret = 0;
	int i = 0;

	for (i=size-1; i>=0; --i)
	{
		ret = ret*16 + *(buf+i);
	}

	return ret;
}

int hash_add(FILE_T *fp, const char *str)
{
	HASH_TBL *htbl = fp->htbl;
	FILE_INFO *fi = fp->fi;
	UINT number = 0;
	BOOL isnewtext = FALSE;
	int lenbytes = 2;

	if (IS_V2(fp->fi->header->version))
	{
		lenbytes = 1;
	}

	if (str && *str != '\0') 
	{
		BOOL isfound = FALSE;
		int str_len = strlen(str);
		TEXT_NODE *current_node = NULL;
		UINT hash_value = calc_hashnr((BYTE *)str, str_len);
		hash_value %= htbl->size;

		current_node = htbl->startaddr[hash_value];
		if (current_node)
		{
			TEXT_NODE *node = current_node;
			while (node)
			{
				if (toint((BYTE *)node->text, 2) == (str_len+1+lenbytes)
					&& strcmp(node->text+2, str) == 0)
				{
					isfound = TRUE;
					break;
				}


				if (!node->htbl_next)
				{
					break;
				}
				else
				{
					node = node->htbl_next;
				}
			}

			current_node = node;
		}

		if (isfound)
		{
			++fi->fbi->stats.repeat_count;
			isnewtext = FALSE;
			number = current_node->number;
		}
		else
		{
			TEXT_NODE *new_node = create_node(htbl, str, str_len, 2);
			if (!new_node) return -1;
			list_add_textnode(htbl->numberlist, new_node);

			if (new_node->number == (htbl->numberlist->saved_number+1))
			{
				htbl->numberlist->notsaved = new_node;
			}
			
			++htbl->nodes;
			if (current_node)
			{
				++fi->fbi->stats.collision_count;
				current_node->htbl_next = new_node;
			}
			else
			{
				++htbl->elements;
				htbl->startaddr[hash_value] = new_node;
			}

			isnewtext = TRUE;
			number = new_node->number;
		}
	}

	return number;
}

void hash_removeall(HASH_TBL *htbl)
{
	htbl->nodes = 0;
	htbl->elements = 0;
	list_init_textnode(htbl->numberlist);
	memset(htbl->startaddr, 0, htbl->size*sizeof(TEXT_NODE *));
	mem_free(htbl->nodeblock);
	mem_free(htbl->textblock);
}

/* copy from MySql */
UINT calc_hashnr(const BYTE *key, int length)
{ 
	register UINT nr=1, nr2=4; 
	while (length--) 
	{ 
		nr ^= (((nr & 63)+nr2)*((UINT)(BYTE)*key++)) + (nr << 8); 
		nr2 += 3; 
	} 
	return ((UINT)nr); 
} 

TEXT_NODE *create_node(HASH_TBL *htbl, const char *str, int len, int lenbytes)
{
	char *text = NULL;
	TEXT_NODE *node = NULL;
	WORD totallen = (WORD)(len+1+lenbytes);
	BYTE *p;
	
	text = (char *)mem_alloc(htbl->textblock, len+1+lenbytes);
	if (!text) goto exit;
	
	strcpy(text+lenbytes, str);
	p = (BYTE *)text;
	*p = (BYTE)(totallen & 0x00FF);
	*(p+1) = (BYTE)((totallen & 0xFF00) >> 8);
	
	node = (TEXT_NODE *)mem_alloc(htbl->nodeblock, sizeof(TEXT_NODE));
	if (!node) goto exit;

	node->text = text;
	node->htbl_next = NULL;
	node->lst_next = NULL;
	node->number = htbl->nodes + 1;

exit:
	return node;
}

BOOL hash_load(FILE_T *fp, CBNODE_LST *cblst)
{
	HASH_TBL *htbl = fp->htbl;
	CB_NODE *cbnode = cblst->head;
	LSX_FILE_TCB *tcb = NULL;
	UINT text_count = 0;
	int lenbytes = 2;
	
	htbl->numberlist->saved_number = MAX_NUMBER;
	if (IS_V2(fp->fi->header->version))
	{
		lenbytes = 1;
	}

	while (cbnode)
	{
		if (cbnode->cb->type == CB_TYPE_TEXT)
		{
			BYTE buf[255];
			WORD len = 0;
			unsigned int count = 0;

			fseek(fp->fp, cbnode->offset+cbnode->cb->size, SEEK_SET);
			
			while (count < cbnode->cb->rgnsize)
			{
				file_read(&len, lenbytes, 1, fp->fp, BUFTYPE_BYTE);
				file_read(buf, len-lenbytes, 1, fp->fp, BUFTYPE_BYTE);
				hash_add(fp, (char *)buf);
				count += len;
			}
			
			tcb = (LSX_FILE_TCB *)cbnode->cb;
			text_count += tcb->text_count;
		}
		
		cbnode = cbnode->next;
	}
	
	htbl->numberlist->saved_number = text_count;
	return TRUE;
}

int hash_savetext(FILE_T *fp, long offset, int *totalsize)
{
	int count = 0;
	HASH_TBL *htbl = fp->htbl;
	TEXT_NODE *node = htbl->numberlist->notsaved;

	*totalsize = 0;
	if (node)
	{
		int length = 0;
		fseek(fp->fp, offset, SEEK_SET);

		while (node)
		{
			length = toint((BYTE *)node->text, 2);
			fwrite(node->text, length, 1, fp->fp);

			++count;
			*totalsize += length;
			node = node->lst_next;
		}

		htbl->numberlist->notsaved = NULL;
		htbl->numberlist->saved_number = htbl->numberlist->tail->number;
	}

	return count;
}

char *hash_gettext(HASH_TBL *htbl, WORD number)
{
	TEXT_NODE *node = NULL;

	if (number < 1 || (int)number > htbl->nodes)
	{
		return NULL;
	}

	node = (TEXT_NODE *)mem_getbuffer(htbl->nodeblock, number, sizeof(TEXT_NODE));
	return (node != NULL) ? node->text + htbl->lenbytes : NULL;
}
