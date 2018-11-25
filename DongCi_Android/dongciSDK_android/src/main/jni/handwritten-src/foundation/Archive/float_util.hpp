//
//  float_util.h
//  ZPSoccerLib
//
//  Created by Jack Yang on 8/7/15.
//  Copyright © 2015 Zepp US Inc. All rights reserved.
//

#pragma once

#include <cmath>
#include <limits>

namespace dc {
    template <typename T>
    inline bool fequal(T lhs, T rhs, T epsilon = std::numeric_limits<T>::epsilon()) // operator==
    {
        return fabs(lhs - rhs) < epsilon;
    }
    
    template <typename T>
    inline bool fless(T lhs, T rhs, T epsilon = std::numeric_limits<T>::epsilon()) // operator<
    {
        return rhs - lhs >= epsilon;
    }

    template <typename T>
    inline bool fgreater(T lhs, T rhs, T epsilon = std::numeric_limits<T>::epsilon()) // operator>
    {
        return lhs - rhs >= epsilon;
    }
    
    template <typename T>
    inline bool flessequal(T lhs, T rhs, T epsilon = std::numeric_limits<T>::epsilon()) // operator<=
    {
        return rhs - lhs > -epsilon;
    }

    template <typename T>
    inline bool fgreaterequal(T lhs, T rhs, T epsilon = std::numeric_limits<T>::epsilon()) // operator>=
    {
        return lhs - rhs > -epsilon;
    }
} // namespace
