//
//  wltrace.h
//  DongCi
//
//  Created by lukewcn on 2018/9/13.
//  Copyright © 2018 wmlives. All rights reserved.
//

#ifndef wltrace_h
#define wltrace_h

//#include <stdio.h>
#include <curl/curl.h>

CURLcode trace(const char *url, const char *ua);
char *trace_getres(CURLcode res);
void trace_reset();
char *trace_get_final_ip();

#endif /* wltrace_h */
