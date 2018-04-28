#include "file.h"
#include "lsxdef.h"

#define swapword(x) (unsigned short)((((x)&0x00ff) << 8) | (((x)&0xff00) >> 8))
#define swapint(x) (unsigned int)((((x)&0x00ff) << 24) | (((x)&0xff00) << 8) | (((x)&0xff0000) >> 8) | (((x)&0xff000000) >> 24))

size_t readbyte(void *buffer, size_t size, size_t count, FILE *stream);
size_t readword(void *buffer, size_t size, size_t count, FILE *stream);
size_t readint(void *buffer, size_t size, size_t count, FILE *stream);
size_t readheader(void *buffer, size_t size, size_t count, FILE *stream);
size_t readbaseinfo(void *buffer, size_t size, size_t count, FILE *stream);
size_t readautoinfo(void *buffer, size_t size, size_t count, FILE *stream);
size_t readspinfo(void *buffer, size_t size, size_t count, FILE *stream);
size_t readuserinfo(void *buffer, size_t size, size_t count, FILE *stream);
size_t readcb(void *buffer, size_t size, size_t count, FILE *stream);
size_t readrgcb(void *buffer, size_t size, size_t count, FILE *stream);
size_t readtcb(void *buffer, size_t size, size_t count, FILE *stream);
size_t readrcb(void *buffer, size_t size, size_t count, FILE *stream);

size_t file_read(void *buffer, size_t size, size_t count, FILE *stream, unsigned char buftype)
{
	switch (buftype)
	{
	case BUFTYPE_BYTE:				
		return readbyte(buffer, size, count, stream);
		break;
	case BUFTYPE_WORD:
		return readword(buffer, size, count, stream);
		break;
	case BUFTYPE_INT:
		return readint(buffer, size, count, stream);
		break;
	case BUFTYPE_LSX_FILE_HEADER:
		return readheader(buffer, size, count, stream);
		break;
	case BUFTYPE_LSX_FILE_BASEINFO:
		return readbaseinfo(buffer, size, count, stream);
		break;
	case BUFTYPE_LSX_FILE_AUTOINFO:
		return readautoinfo(buffer, size, count, stream);
		break;
	case BUFTYPE_LSX_FILE_SPINFO:
		return readspinfo(buffer, size, count, stream);
		break;
	case BUFTYPE_LSX_FILE_USERINFO:
		return readuserinfo(buffer, size, count, stream);
		break;
	case BUFTYPE_LSX_FILE_CB:			
		return readcb(buffer, size, count, stream);
		break;
	case BUFTYPE_LSX_FILE_RGCB:
		return readrgcb(buffer, size, count, stream);
		break;
	case BUFTYPE_LSX_FILE_TCB:
		return readtcb(buffer, size, count, stream);
		break;
	case BUFTYPE_LSX_FILE_RCB:
		return readrcb(buffer, size, count, stream);
		break;
	default:
		break;
	}

	return 0;
}

size_t readbyte(void *buffer, size_t size, size_t count, FILE *stream)
{
	return fread(buffer, size, count, stream);
}

size_t readword(void *buffer, size_t size, size_t count, FILE *stream)
{
	size_t ret = fread(buffer, size, count, stream);
#ifdef HOST_BIGENDIAN
	WORD *v = (WORD *)buffer;
	*v = swapword(*v);
#endif
	return ret;
}

size_t readint(void *buffer, size_t size, size_t count, FILE *stream)
{
	size_t ret = fread(buffer, size, count, stream);
#ifdef HOST_BIGENDIAN
	UINT *v = (UINT *)buffer;
	*v = swapint(*v);
#endif
	return ret;
}

size_t readheader(void *buffer, size_t size, size_t count, FILE *stream)
{
	size_t ret = fread(buffer, size, count, stream);
#ifdef HOST_BIGENDIAN
	LSX_FILE_HEADER *v = (LSX_FILE_HEADER *)buffer;
	v->version = swapword(v->version);
	v->info_offset = swapword(v->info_offset);
	v->firstcb_offset = swapint(v->firstcb_offset);
	v->lastcb_offset = swapint(v->lastcb_offset);
	v->reserved = swapint(v->reserved);
#ifdef _DEBUG
	printf("======= readheader =======\n");
	printf("flag: %s\n", v->flag);
	printf("version: 0x%02x\n", v->version);
	printf("info_offset: 0x%02x\n", v->info_offset);
	printf("firstcb_offset: 0x%04x\n", v->firstcb_offset);
	printf("lastcb_offset: 0x%04x\n", v->lastcb_offset);
	printf("reserved: 0x%04x\n", v->reserved);
#endif
#endif
	return ret;
}

size_t readbaseinfo(void *buffer, size_t size, size_t count, FILE *stream)
{
	size_t ret = fread(buffer, size, count, stream);
#ifdef HOST_BIGENDIAN
	LSX_FILE_BASEINFO *v = (LSX_FILE_BASEINFO *)buffer;
	v->productid = swapword(v->productid);
	v->codepage = swapword(v->codepage);
	v->version = swapword(v->version);
	v->creationtime = swapword(v->creationtime);
	v->recgroups = swapword(v->recgroups);
#endif
	return ret;
}

size_t readautoinfo(void *buffer, size_t size, size_t count, FILE *stream)
{
	size_t ret = fread(buffer, size, count, stream);
#ifdef HOST_BIGENDIAN
	LSX_FILE_AUTOINFO *v = (LSX_FILE_AUTOINFO *)buffer;
	v->model = swapword(v->model);
	v->make = swapword(v->make);
	v->year = swapword(v->year);
	v->madein = swapword(v->madein);
	v->chassis = swapword(v->chassis);
	v->enginemodel = swapword(v->enginemodel);
	v->displacement = swapword(v->displacement);
#endif
	return ret;
}

size_t readspinfo(void *buffer, size_t size, size_t count, FILE *stream)
{
	size_t ret = fread(buffer, size, count, stream);
#ifdef HOST_BIGENDIAN
	LSX_FILE_SPINFO *v = (LSX_FILE_SPINFO *)buffer;
	v->name = swapword(v->name);
	v->phone = swapword(v->phone);
#endif
	return ret;
}

size_t readuserinfo(void *buffer, size_t size, size_t count, FILE *stream)
{
	size_t ret = fread(buffer, size, count, stream);
#ifdef HOST_BIGENDIAN
	LSX_FILE_USERINFO *v = (LSX_FILE_USERINFO *)buffer;
	v->name = swapword(v->name);
	v->phone = swapword(v->phone);
	v->license = swapword(v->license);
#endif
	return ret;
}

size_t readcb(void *buffer, size_t size, size_t count, FILE *stream)
{
	size_t ret = fread(buffer, size, count, stream);
#ifdef HOST_BIGENDIAN
	LSX_FILE_CB *v = (LSX_FILE_CB *)buffer;
	v->size = swapword(v->size);
	v->type = swapword(v->type);
	v->nextcb_offset = swapint(v->nextcb_offset);
	v->rgnsize = swapint(v->rgnsize);
#endif
	return ret;
}

size_t readrgcb(void *buffer, size_t size, size_t count, FILE *stream)
{
	size_t ret = fread(buffer, size, count, stream);
#ifdef HOST_BIGENDIAN
	LSX_FILE_RGCB *v = (LSX_FILE_RGCB *)buffer;
	v->cb.size = swapword(v->cb.size);
	v->cb.type = swapword(v->cb.type);
	v->cb.nextcb_offset = swapint(v->cb.nextcb_offset);
	v->cb.rgnsize = swapint(v->cb.rgnsize);
	v->name = swapword(v->name);
	v->vin = swapword(v->vin);
	v->protocol = swapword(v->protocol);
	v->reserved = swapword(v->reserved);
	v->starttime = swapword(v->starttime);
	v->endtime = swapword(v->endtime);
	v->firstrcb_offset = swapint(v->firstrcb_offset);
	v->lastrcb_offset = swapint(v->lastrcb_offset);
	v->firstdtcrcb_offset = swapint(v->firstdtcrcb_offset);
	v->lastdtcrcb_offset = swapint(v->lastdtcrcb_offset);
#endif
	return ret;
}

size_t readtcb(void *buffer, size_t size, size_t count, FILE *stream)
{
	size_t ret = fread(buffer, size, count, stream);
#ifdef HOST_BIGENDIAN
	LSX_FILE_TCB *v = (LSX_FILE_TCB *)buffer;
	v->cb.size = swapword(v->cb.size);
	v->cb.type = swapword(v->cb.type);
	v->cb.nextcb_offset = swapint(v->cb.nextcb_offset);
	v->cb.rgnsize = swapint(v->cb.rgnsize);
	v->text_count = swapint(v->text_count);
#endif
	return ret;
}

size_t readrcb(void *buffer, size_t size, size_t count, FILE *stream)
{
	size_t ret = fread(buffer, size, count, stream);
#ifdef HOST_BIGENDIAN
	LSX_FILE_RCB *v = (LSX_FILE_RCB *)buffer;
	v->cb.size = swapword(v->cb.size);
	v->cb.type = swapword(v->cb.type);
	v->cb.nextcb_offset = swapint(v->cb.nextcb_offset);
	v->cb.rgnsize = swapint(v->cb.rgnsize);
	v->recordsize = swapword(v->recordsize);
	v->un.reserved = swapword(v->un.reserved);
#endif
	return ret;
}

