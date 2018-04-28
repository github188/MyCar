#ifndef _FILE_H
#define _FILE_H

#include <stdio.h>

#define BUFTYPE_BYTE				0x01
#define BUFTYPE_WORD				0x02
#define BUFTYPE_INT					0x03
#define BUFTYPE_LSX_FILE_HEADER		0x04
#define BUFTYPE_LSX_FILE_BASEINFO	0x05
#define BUFTYPE_LSX_FILE_AUTOINFO	0x06
#define BUFTYPE_LSX_FILE_SPINFO		0x07
#define BUFTYPE_LSX_FILE_USERINFO	0x08
#define BUFTYPE_LSX_FILE_CB			0x09
#define BUFTYPE_LSX_FILE_RGCB		0x0A
#define BUFTYPE_LSX_FILE_TCB		0x0B
#define BUFTYPE_LSX_FILE_RCB		0x0C

#ifdef __cplusplus
extern "C" {
#endif

size_t file_read(void *buffer, size_t size, size_t count, FILE *stream, unsigned char buftype);

#ifdef __cplusplus
}
#endif
		
#endif /* _FILE_H */
