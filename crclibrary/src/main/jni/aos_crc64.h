#ifndef LIBAOS_CRC_H
#define LIBAOS_CRC_H

//#include <_types/_uintmax_t.h>
//#include <_types/_uint64_t.h>
#include <stdint.h>
#include <stddef.h>

#ifdef _cplusplus
extern "C"{
#endif

uint64_t aos_crc64(uint64_t crc, void *buf, size_t len);
uint64_t aos_crc64_combine(uint64_t crc1, uint64_t crc2, uintmax_t len2);


#ifdef _cplusplus
}
#endif

#endif
