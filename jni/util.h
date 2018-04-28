#ifndef _UTIL_H
#define _UTIL_H

#include "lsxdef.h"

#ifdef __cplusplus
extern "C" {
#endif
	
void get_time(char *t, int size);
BOOL str_isiequal(const char *s1, const char *s2);
	
#ifdef __cplusplus
}
#endif

#endif /* _UTIL_H */
