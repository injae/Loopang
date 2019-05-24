//
// Created by nieel on 5/20/19.
//

#ifndef LOOPANG_OHCASTRAENGINE_H
#define LOOPANG_OHCASTRAENGINE_H

#include <oboe/Oboe.h>
#include <PlayBackStream.h>
#include <RecordingStream.h>

class EchoEngine : public oboe::AudioStreamCallback{
public:
    oboe::DataCallbackResult onAudioReady(oboe::AudioStream *oboeStream, void *audioData, int32_t numFrames);
    ~EchoEngine();
    void echo_on(bool is_on);
    void open_all();
    void close_all();
    void set_playback_device_id(int32_t device_id);
    void set_recording_device_id(int32_t device_id);

private:
    PlayBackStream*  playback_;
    RecordingStream* recording_;
    int32_t playback_device_id{oboe::kUnspecified};
    int32_t recording_device_id{oboe::kUnspecified};

};

#endif //LOOPANG_OHCASTRAENGINE_H
