#include "list.h"

void list_init_textnode(TEXTNODE_LST *lst)
{
	lst->count = 0;
	lst->saved_number = 0;
	lst->head = NULL;
	lst->tail = NULL;
	lst->notsaved = NULL;
}

void list_add_textnode(TEXTNODE_LST *lst, TEXT_NODE *node)
{
	++lst->count;
	
	if (!lst->head)
	{
		lst->head = node;
		lst->tail = node;
	}
	else
	{
		lst->tail->lst_next = node;
		lst->tail = node;
	}
}

void list_init_cbnode(CBNODE_LST *lst)
{
	lst->count = 0;
	lst->head = lst->tail = NULL;
}

void list_add_cbnode(CBNODE_LST *lst, CB_NODE *node)
{
	++lst->count;
	node->next = NULL;

	if (!lst->head)
	{
		lst->head = node;
		lst->tail = node;
		node->prev = NULL;
	}
	else
	{
		lst->tail->next = node;
		node->prev = lst->tail;
		lst->tail = node;
	}
}

void list_add_dtccbnode(CBNODE_LST *lst, DTCCB_NODE *node)
{
	++lst->count;
	
	if (!lst->head)
	{
		lst->head = (CB_NODE *)node;
		lst->tail = (CB_NODE *)node;
		node->dtc_next = NULL;
		node->dtc_prev = NULL;
	}
	else
	{
		((DTCCB_NODE *)lst->tail)->dtc_next = node;
		node->dtc_prev = (DTCCB_NODE *)lst->tail;
		lst->tail = (CB_NODE *)node;
	}
}
