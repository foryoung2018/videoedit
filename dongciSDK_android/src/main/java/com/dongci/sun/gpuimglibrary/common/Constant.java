package com.dongci.sun.gpuimglibrary.common;

public class Constant {


    //infos
    public static final int TCP_SERVER_NEW_SERVER_SUCCEED = 1001;
    public static final int TCP_SERVER_DISCONNECT_CLIENT_SUCCEED = 1002;
    public static final int TCP_SERVER_CONNECT_CLIENT_SUCCEED = 1003;
    public static final int TCP_SERVER_START_SERVER_SUCCEED = 1004;
    public static final int TCP_SERVER_CLOSE_SERVER_SUCCEED = 1005;
    public static final int TCP_SERVER_DROP_FRAME = 1006;

    //errors
    public static final int TCP_SERVER_NEW_SERVER_FAILED = -1001;
    public static final int TCP_SERVER_DISCONNECT_CLIENT_FAILED = -1002;
    public static final int TCP_SERVER_CONNECT_CLIENT_FAILED = -1003;
    public static final int TCP_SERVER_START_SERVER_FAILED = -1004;
    public static final int TCP_SERVER_CLOSE_SERVER_FAILED = -1005;
    public static final int TCP_SERVER_RECEIVE_FRAME_FAILED = -1006;


    //infos
    public static final int STREAM_PUBLISHER_START_PUBLISH_SUCCEED = 2001;

    //errors
    public static final int STREAM_PUBLISHER_AUDIO_RECORDER_PERMISSION_DENIED = -2001;
    public static final int STREAM_PUBLISHER_CAMERA_PERMISSION_DENIED = -2002;
    public static final int STREAM_PUBLISHER_SCREEN_PERMISSION_DENIED = -2003;
    public static final int STREAM_PUBLISHER_START_PUBLISH_FAILED = -2004;


    //infos

    //errors
    public static final int AUDIO_RECORDER_CONFIGURE_FAILED = -3001;


    //infos

    //errors
    public static final int CAMERA_CAPTURE_VIEW_SET_CAMERA_FACING_FAILED = -4001;
    public static final int CAMERA_CAPTURE_VIEW_OPEN_CAMERA_FAILED = -4002;
    public static final int CAMERA_CAPTURE_VIEW_SET_PREVIEW_TEXTURE_FAILED = -4003;
    public static final int CAMERA_CAPTURE_VIEW_CAMERA_EGL_RENDER_UNAVAILABLE = -4004;


    //infos

    //errors
    public static final int STREAM_ENCODER_CONFIGURE_VIDEO_HARD_ENCODER_FAILED = -5001;
    public static final int STREAM_ENCODER_CONFIGURE_AUDIO_HARD_ENCODER_FAILED = -5002;
    public static final int STREAM_ENCODER_GET_SPSPPS_FAILED = -5003;
    public static final int STREAM_ENCODER_UNSUPPORTED_COLOR_FORMAT = -5004;


    //infos
    public static final int TCP_PUBLISHER_CONNECT_SUCCEED = 6001;
//    public static final int TCP_PUBLISHER_DROP_FRAME = 6004;

    //errors
    public static final int TCP_PUBLISHER_CONNECT_FAILED = -6001;
    public static final int TCP_PUBLISHER_SEND_FRAME_FAILED = -6002;


    //infos
    public static final int VIDEO_PLAYER_START_PLAY_SUCCEED = 7001;
    public static final int VIDEO_PLAYER_STOP_PLAY_SUCCEED = 7002;

    //errors
    public static final int VIDEO_PLAYER_START_PLAY_FAILED = -7001;


    //infos
//    public static final int RTMP_PUBLISHER_START_PUBLISH_SUCCEED = 8001;

    //errors
    public static final int RTMP_PUBLISHER_RTMP_SOCKET_ERROR = -8001;
    public static final int RTMP_PUBLISHER_RTMP_IO_ERROR = -8002;
    public static final int RTMP_PUBLISHER_ILLEGAL_ARGUMENT = -8003;
    public static final int RTMP_PUBLISHER_ILLEGAL_STATE = -8004;
    public static final int RTMP_PUBLISHER_CONNECT_FAILED = -8005;


    //infos
    public static final int CAMERA_EGL_RENDER_INITIALIZE_SUCCEED = 9001;

    //errors
    public static final int CAMERA_EGL_RENDER_INITIALIZE_FAILED = -9001;


    //infos

    //errors


    //infos
    public static final int SCREEN_EGL_RENDER_INITIALIZE_SUCCEED = 11001;


    //errors
    public static final int SCREEN_EGL_RENDER_SETUP_EGL_FAILED = -11001;
    public static final int SCREEN_EGL_RENDER_INIT_RENDER_FAILED = -11002;

}
