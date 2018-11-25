//
//  wltrace.m
//  DongCi
//
//  Created by lukewcn on 2018/9/13.
//  Copyright © 2018 wmlives. All rights reserved.
//

#include "wltrace.h"
#include <curl/curl.h>
#include <arpa/inet.h>
#include <stdlib.h>
#include <stdbool.h>
#include <string.h>

char *trace_head = NULL;
char *trace_log = NULL;
char *res_str = NULL;
char *final_server_ip = NULL;
int trace_redirect_count = 0;

size_t curlHeaderCallback(char *buffer, size_t size, size_t nitems, void *userdata);

char *trace_get_final_ip() {
    return final_server_ip;
}

char *trace_getres(CURLcode res) {
    if (res != CURLE_OK) {
        res_str = (char *)malloc(32);
        sprintf(res_str, "CURL failed with error code:%d", res);
        return res_str;
    }

    char *nullstr = "null";
    size_t null_len = strlen(nullstr);
    if (!trace_head) {
        trace_head = (char *)malloc(null_len+1);
        trace_head = strcpy(trace_head, nullstr);
    }
    if (!trace_log) {
        trace_log = (char *)malloc(null_len+1);
        trace_log = strcpy(trace_log, nullstr);
    }

    res_str = (char *)malloc(4+strlen(trace_log)+strlen(trace_head));
    sprintf(res_str, "head:\n%s\njump:\n%s", trace_head, trace_log);
    return res_str;
}

void trace_reset() {
    if (trace_head) {
        free(trace_head);
    }
    trace_head = NULL;

    if (trace_log) {
        free(trace_log);
    }
    trace_log = NULL;

    if (res_str) {
        free(res_str);
    }
    res_str = NULL;

    if (final_server_ip) {
        free(final_server_ip);
    }
    final_server_ip = NULL;

    trace_redirect_count = 0;
}

CURLcode trace(const char *url, const char *ua) {
    char *ip = NULL;
    double connect_time = 0.0;
    int http_statuscode = 0;
    char *red_url = NULL;
    curl_socket_t sockfd;

    CURL *curl = curl_easy_init();
    if (ua) {
        curl_easy_setopt(curl, CURLOPT_USERAGENT, ua);
    }
    curl_easy_setopt(curl, CURLOPT_URL, url);
    curl_easy_setopt(curl, CURLOPT_VERBOSE, 1L);
    curl_easy_setopt(curl, CURLOPT_TCP_NODELAY, 1L);
    curl_easy_setopt(curl, CURLOPT_FOLLOWLOCATION, false);
    curl_easy_setopt(curl, CURLOPT_FAILONERROR, true);
    curl_easy_setopt(curl, CURLOPT_SSL_VERIFYPEER, false);
    curl_easy_setopt(curl, CURLOPT_SSL_VERIFYHOST, false);
    curl_easy_setopt(curl, CURLOPT_NOBODY, 1L);
    curl_easy_setopt(curl, CURLOPT_HEADER, 1L);
    curl_easy_setopt(curl, CURLOPT_SUPPRESS_CONNECT_HEADERS, 0L);
    curl_easy_setopt(curl, CURLOPT_HEADERFUNCTION, &curlHeaderCallback);


    CURLcode res = curl_easy_perform(curl);
    if (res == CURLE_OK) {
        curl_easy_getinfo(curl, CURLINFO_PRIMARY_IP, &ip);
        if (ip) {
            printf("IP: %s\n", ip);
        }

        bool info_res = curl_easy_getinfo(curl, CURLINFO_CONNECT_TIME, &connect_time);
        if(CURLE_OK == info_res) {
            printf("Connect_Time: %.2f\n", connect_time);
        }

        info_res = curl_easy_getinfo(curl, CURLINFO_REDIRECT_COUNT, &trace_redirect_count);
        if(CURLE_OK == info_res) {
            printf("Redirect_Count: %d\n", trace_redirect_count);
        }

        curl_easy_getinfo(curl, CURLINFO_RESPONSE_CODE, &http_statuscode);

        curl_easy_getinfo(curl, CURLINFO_REDIRECT_URL, &red_url);
        if (red_url) {
            printf("Redirect to: %s\n", red_url);
        }
    } else {
        printf("CURL failed with error code: %d", res);
        return res;
    }


    char *tmplog = (char *)malloc(256);
    sprintf(tmplog, "url:%s,ip:%s,connect_time:%.2f\n", url, ip, connect_time);
    size_t tmploglen = strlen(tmplog);
    printf("trace_log_1: %p\n", trace_log);
    if (!trace_log) {
        trace_log = (char *)malloc(tmploglen+1);
        trace_log = strcpy(trace_log, tmplog);
    } else {
        trace_log = (char *)realloc(trace_log, strlen(trace_log)+tmploglen+1);
        trace_log = strcat(trace_log, tmplog);
    }
    printf("trace_log_2: %p\n", trace_log);
    free(tmplog);

    bool needjump = http_statuscode == 301 || http_statuscode == 302 || http_statuscode == 303;
    if (needjump) {
        if (red_url) {
            trace_redirect_count += 1;
            trace(red_url, ua);
        }
    } else {
        printf("Header: \n%s\n", trace_head);
        printf("Trace Log: \n%s", trace_log);

        if (final_server_ip) {
            free(final_server_ip);
        }
        if (ip) {
            final_server_ip = (char *)malloc(strlen(ip));
            final_server_ip = strcpy(final_server_ip, ip);
        }
    }

    curl_easy_cleanup(curl);
    return res;
}


/* header */

size_t curlHeaderCallback(char *buffer, size_t size, size_t nitems, void *userdata) {
    size_t len = nitems * size;
    if (len == 0 || !buffer) {
        return 0;
    }

    if (!trace_head) {
        trace_head = (char *)malloc(len+1);
        trace_head = strncpy(trace_head, buffer, len);
    } else {
        trace_head = (char *)realloc(trace_head, strlen(trace_head)+len+1);
        trace_head = strncat(trace_head, buffer, len);
    }

    return len;
}
