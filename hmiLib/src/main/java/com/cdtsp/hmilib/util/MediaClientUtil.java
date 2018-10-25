package com.cdtsp.hmilib.util;

public class MediaClientUtil {

    /**
     * media的类型,有两种media类型：
     * {@link #EXTRA_VALUE_TYPE_MUSIC} 和 {@link #EXTRA_VALUE_TYPE_VIDEO}
     */
    public static final String EXTRA_KEY_MEDIA_TYPE = "media_type";

    /**
     * Extra key，表示MediaBrowserCompat.MediaItem
     */
    public static final String EXTRA_KEY_MEDIA_ITEM = "media_item";

    /**
     * Extra key，表示MediaInfo
     */
    public static final String EXTRA_KEY_MEDIA_INFO = "media_info";

    /**
     * Extra  value，表示音乐media类型
     */
    public static final String EXTRA_VALUE_TYPE_MUSIC = "music";

    /**
     * Extra  value，表示视频media类型
     */
    public static final String EXTRA_VALUE_TYPE_VIDEO = "video";

    /**
     * Extra key，表示启动MediaClient时的Intent
     */
    public static final String EXTRA_KEY_MEDIA_LAUNCH_INTENT = "media_launch_intent";

    /**
     * Extra key，表示是否以副本模式启动video播放界面
     */
    public static final String EXTRA_KEY_LAUNCH_AS_COPY = "as_copy_mode";

    public static final String ACTION_LAUNCH_PLAY_VIDEO = "com.cdtsp.videodemo.PLAY_VIDEO";
    public static final String ACTION_LAUNCH_PLAY_MUSIC = "com.cdtsp.videodemo.PLAY_MUSIC";
    public static final String ACTION_LAUNCH_PLAY_MUSIC_USB = "com.cdtsp.videodemo.PLAY_MUSIC_USB";
    public static final String ACTION_LAUNCH_PLAY_MUSIC_BT = "com.cdtsp.mediaclient.PLAY_MUSIC_BT";

}
