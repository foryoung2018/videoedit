//
//  string_util.hpp
//  OpencvBench
//
//  Created by Jack Yang on 3/23/16.
//  Copyright © 2016 Jack Yang. All rights reserved.
//

#pragma once
#include <algorithm>
#include <string>
#include <sstream>
#include "utf8.h"

namespace dc {
    namespace platform {
        class StringUtil {
        public:
            StringUtil() = delete;
            StringUtil(const StringUtil&) = delete;
            StringUtil& operator=(const StringUtil&) = delete;
            
        public:
            static bool IsInitialChinese(const std::string& str) {
                // TODO: find a better solution
                // 0xxxxxxx = single-byte ASCII character
                // 1xxxxxxx = part of multi-byte character
                if (str.empty()) {
                    return false;
                }
                
                return int8_t(str[0]) < 0;
            }
            
            static bool ContainsMultibyte(const std::string & str)
            {
                if(str.empty())
                {
                    return false;
                }
                int8_t pre = 0;
                for(int i = 0; i < str.length(); ++i)
                {
                    if(pre < 0 && int8_t(str[i]) < 0)
                    {
                        return true;
                    }
                    pre = int8_t(str[i]);
                }
                return false;
            }
            
            static std::string ToUpper(std::string& str){
                std::string upper_str(str);
                std::transform(upper_str.begin(), upper_str.end(), upper_str.begin(), ::toupper);
                return upper_str;
            }
            
            static std::wstring Str2Wstr(const std::string& str){
                std::wstring utf32result;
                utf8::utf8to32(str.begin(), str.end(), back_inserter(utf32result));
                return utf32result;
            }
            
            static std::string Wstr2Str(const std::wstring& wstr){
                std::string utf8result;
                utf8::utf32to8(wstr.begin(), wstr.end(), back_inserter(utf8result));
                return utf8result;
            }
            
            static std::wstring SubWstr(const std::string &str, const int pos, const int len)
            {
                std::wstring wstr = Str2Wstr(str);
                return wstr.substr(pos, len);
            }
            
            template <typename T>
            static std::string ToString(T value) {
                std::ostringstream os ;
                os << value ;
                return os.str() ;
            }
            
            static inline std::string & TrimL(std::string &s) {
                s.erase(s.begin(), std::find_if(s.begin(), s.end(), std::not1(std::ptr_fun<int, int>(std::isspace))));
                return s;
            }
            
            
            static inline std::string &TrimR(std::string &s) {
                s.erase(std::find_if(s.rbegin(), s.rend(), std::not1(std::ptr_fun<int, int>(std::isspace))).base(), s.end());
                return s;
            }
            
            
            static inline std::string &Trim(std::string &s) {
                return TrimL(TrimR(s));
            }
        };
    }
}
