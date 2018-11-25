//
//  WindowsTypeHelper.h
//  SquareCam-Test
//
//  Created by LiuJiangyu on 3/11/15.
//
//

#ifndef SquareCam_Test_WindowsTypeHelper_h
#define SquareCam_Test_WindowsTypeHelper_h

#include <sys/types.h>
#include <string.h>
#include <stdlib.h>
#include <algorithm>

// std::min/max
using std::min;
using std::max;

//
//BaseTsd.h
//
typedef unsigned long ULONG_PTR;

//
//limits.h
//
#ifndef UINT_MAX
#define UINT_MAX	0xffffffff
#endif
#define INFINITE UINT_MAX

typedef long int		INT32, *PINT32;
typedef unsigned int	DWORD;
#ifndef TARGET_OS_IPHONE
typedef signed char     BOOL;
#endif
typedef unsigned char 	BYTE, BOOLEAN;
typedef unsigned short	WORD;
typedef float			FLOAT;
typedef int 			INT;
typedef unsigned int 	UINT, UINT32;
typedef	unsigned int 	WPARAM;
typedef long			LPARAM;
typedef void*			LPVOID;
typedef void			VOID, *PVOID;
typedef UINT*			UINT_PTR;
typedef DWORD*			DWORD_PTR;
typedef long			LRESULT;
typedef char			CHAR;
typedef short			SHORT;
typedef long			LONG;
typedef unsigned short	USHORT;
typedef unsigned char	UCHAR;

#define CONST const

#define CALLBACK __stdcall
#define far
#define near
#define FAR far
#define NEAR near

typedef unsigned long long ULONGLONG;
typedef wchar_t WCHAR;
typedef CONST CHAR	*LPCSTR;
typedef WCHAR TCHAR, *PTCHAR, *LPWSTR;
typedef CONST WCHAR *LPCWSTR;
typedef unsigned char byte;

#ifndef _HRESULT_DEFINED
#define _HRESULT_DEFINED
typedef long HRESULT;
#endif

#ifndef IN
#define IN
#endif

#ifndef OUT
#define OUT
#endif

#ifndef NULL
#ifdef __cplusplus
#define NULL	0
#endif
#endif

#ifndef TRUE
#define TRUE	1
#endif

#ifndef FALSE
#define FALSE	0
#endif

// WinError.h
//
#define _HRESULT_TYPEDEF_(_sc) ((HRESULT)_sc)
#define S_OK		((HRESULT)0L)
#define S_FALSE		((HRESULT)1L)
#define E_NOTIMPL	((HRESULT)0x80004001L)

#define E_OUTOFMEMORY	_HRESULT_TYPEDEF_(0x8007000EL)
#define E_INVALIDARG	_HRESULT_TYPEDEF_(0x80070057L)
#define E_FAIL			_HRESULT_TYPEDEF_(0x80004005L)

#define FAILED(hr)	(((HRESULT)(hr)) < 0)
#define MAKE_HRESULT(sev, fac, code)	\
((HRESULT)(((unsigned long)(sev)<<31) | ((unsigned long)(fac)<<16) | ((unsigned long)(code))))
#define SUCCEEDED(hr)	(((HRESULT)(hr))>=0)

#define E_UNEXPECTED false

#define UINT32 unsigned int

#define nullptr NULL

#define __min(a, b) (((a) < (b)) ? (a) : (b))

#define __max(a, b) (((a) > (b)) ? (a) : (b))

#define UNREFERENCED_PARAMETER(x) (void)(x)

template <typename T, size_t N>
char ( &_ArraySizeHelper( T (&array)[N] ))[N];

#define ARRAYSIZE( array ) (sizeof( _ArraySizeHelper( array ) ))

//
// stdlib.h
//
#define MAX_PATH 260

// handle
typedef void *HANDLE;
typedef struct {} BITMAP, *HBITMAP;
typedef struct {} ICONIMAGE, *HICON;
typedef struct {} MENU, *HMENU;
typedef struct {} DC, *HDC;
typedef struct {} WND, *HWND;

inline int CloseHandle(HANDLE){
    return 1;
}
inline int DeleteObject(HBITMAP){
    return 1;
}
inline int DestroyIcon(HICON){
    return 1;
}
inline int DestroyMenu(HMENU){
    return 1;
}
inline int ReleaseDC(HWND,HDC){
    return 1;
}
inline int DeleteDC(HDC){
    return 1;
}

struct RECT
{
    int left;
    int top;
    int right;
    int bottom;
    
    RECT() : left(0), top(0), right(0), bottom(0)
    {
    }
    
    RECT(int _left, int _top, int _right, int _bottom)
    {
        left = _left;
        top = _top;
        right = _right;
        bottom = _bottom;
    }
    
    RECT& operator=(const RECT& rhs)
    {
        left = rhs.left;
        top = rhs.top;
        right = rhs.right;
        bottom = rhs.bottom;
        
        return *this;
    }
    
    bool operator==(const RECT& rhs)
    {
        return (top == rhs.top && left == rhs.left
                && right == rhs.right && bottom == rhs.bottom);
    }
    
    bool operator!=(const RECT& rhs)
    {
        return (top != rhs.top || left != rhs.left
                || right != rhs.right || bottom != rhs.bottom);
    }
    
    bool IsEmpty() const
    {
        return (left >= right || top >= bottom);
    }
    
    void Clear()
    {
        left = top = right = bottom = 0;
    }
    
    int Width() const
    {
        return (int)(right - left);
    }
    
    int Height() const
    {
        return (int)(bottom - top);
    }
    
    int Area() const
    {
        return Width() * Height();
    }
    
    int CenterX() const
    {
        return (int)((left + right) / 2);
    }
    
    int CenterY() const
    {
        return (int)((top + bottom) / 2);
    }
    
    void Shift(int dx, int dy)
    {
        left += dx;
        right += dx;
        top += dy;
        bottom += dy;
    }
    
    float GetOverlap(const RECT& rhs) const
    {
        const int l = (int)__max(left, rhs.left);
        const int r = (int)__min(right, rhs.right);
        const int t = (int)__max(top, rhs.top);
        const int b = (int)__min(bottom, rhs.bottom);
        
        if (l >= r|| t >= b)
        {
            return 0.f;
        }
        else
        {
            const int aInter = (r - l) * (b - t);
            const int aUnion = Area() + rhs.Area() - aInter;
            
            return ((float)aInter / aUnion);
        }
    }
    
    static bool IsOverlapped(const RECT& r0,
                             const RECT& r1,
                             const float threshold = 0.3f)
    {
        return (r0.GetOverlap(r1) >= threshold);
    }
};
typedef struct tagPOINT {
    LONG x;
    LONG y;
} POINT, *PPOINT;
#endif
