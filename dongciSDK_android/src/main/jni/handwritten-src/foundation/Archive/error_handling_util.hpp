#pragma once
#include "zf_log.h"
// exception
inline void MuteAllExceptions(std::function<void()> action,std::string msg="MuteAllExceptions!")
{
	try
	{
		action();
	}
	catch(std::exception& ex)
	{
		ZF_LOGE("Exception happened! %s %s", msg.c_str(), ex.what());
	}
	catch(...)
	{
		ZF_LOGE("Exception happened! %s", msg.c_str());
	}
}