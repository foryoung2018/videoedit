//
//  file_manager.h
//  ZPLFFmpeg
//
//  Created by Xiao Zhang on 8/30/16.
//  Copyright © 2016 Zepp Technology Inc. All rights reserved.
//

#pragma once
#include <string>
#include <fstream>
#include <chrono>
#include "string_util.hpp"

namespace dc {
    namespace platform {
        class FileManager
        {
        public:
            static bool IsFileExist(const std::string& file_path)
            {
                std::ifstream file(file_path);
                return file.good();
            }
            
            static void DeleteFile(const std::string &filepath)
            {
                if(IsFileExist(filepath))
                {
                    std::remove(filepath.c_str());
                }
            }
            
            static void DeleteFiles(std::vector<std::string> files)
            {
                for(std::string &path : files)
                {
                    DeleteFile(path);
                }
            }
            
            static std::string GenerateUniquePath(const std::string &directory, const std::string &suffix)
            {
                std::chrono::milliseconds now = std::chrono::duration_cast< std::chrono::milliseconds >(std::chrono::system_clock::now().time_since_epoch());
                return directory + "/" + StringUtil::ToString(now.count()) + suffix;
            }
            
            static std::string CheckFilePath(const std::string &filepath)
            {
                if(IsFileExist(filepath))
                {
                    return filepath;
                }
                return "";
            }
        };
    }}
