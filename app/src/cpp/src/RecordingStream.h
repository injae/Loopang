//
// Created by nieel on 5/20/19.
//

#ifndef LOOPANG_RECORDINGSTREAM_H
#define LOOPANG_RECORDINGSTREAM_H

#include <CommonStream.h>

class RecordingStream : public CommonStream{
public:
    RecordingStream(int32_t device_id);
};


#endif //LOOPANG_RECORDINGSTREAM_H
