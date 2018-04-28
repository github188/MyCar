#include <time.h>
#include <string.h>
#include <stdlib.h>
#include <ctype.h>

#include "util.h"

void get_time(char *t, int size)
{ 
	if (size >= 26)
	{
		time_t ti;
		struct tm *tms;
		
		time(&ti);
		tms = localtime(&ti);
		strcpy(t, asctime(tms));
		t[24] = '\0';
	}
	else
	{
		*t = '\0';
	}
}

BOOL str_isiequal(const char *s1, const char *s2)
{
#ifdef WIN32
	return (_stricmp(s1, s2) == 0);
#else
	if (strlen(s1) == strlen(s2))
	{
		int i;
		for (i=0; i<strlen(s1); ++i)
		{
			if (toupper(*(s1+i)) != toupper(*(s2+i)))
			{
				break;
			}
		}

		return (i == strlen(s1));
	}

	return FALSE;
#endif
}
