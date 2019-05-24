//
// Created by nieel on 5/20/19.
//

#ifndef LOOPANG_PLAYBACKSTREAM_H
#define LOOPANG_PLAYBACKSTREAM_H

#include <CommonStream.h>

class PlayBackStream : public CommonStream{
public:
    PlayBackStream(int32_t device_id);
    uint64_t processed_frame_count = 0;
    uint64_t system_startup_frames = 0;
    int32_t  sample_rate = 0;
    oboe::Result open();
};


#endif //LOOPANG_PLAYBACKSTREAM_H
