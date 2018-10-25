package com.cdtsp.hmilib.mediaclient.os;

import com.cdtsp.hmilib.mediaclient.os.MediaInfo;

interface IMediaClientController {
    void playVideo(in MediaInfo info, int displayId, boolean asCopyMode);
    void playVideoAsCurrent(in MediaInfo info);
}
