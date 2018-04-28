#ifndef _LIST_H
#define _LIST_H

#include "lsxdef.h"

#ifdef __cplusplus
extern "C" {
#endif
	
void list_init_textnode(TEXTNODE_LST *lst);
void list_init_cbnode(CBNODE_LST *lst);

void list_add_textnode(TEXTNODE_LST *lst, TEXT_NODE *node);
void list_add_cbnode(CBNODE_LST *lst, CB_NODE *node);
void list_add_dtccbnode(CBNODE_LST *lst, DTCCB_NODE *node);
	
#ifdef __cplusplus
}
#endif

#endif /* _LIST_H */
