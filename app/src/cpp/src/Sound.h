//
// Created by nieel on 5/23/19.
//

#ifndef LOOPANG_SOUND_H
#define LOOPANG_SOUND_H

#include "IRenderableAudio.h"
#include <vector>
#include <algorithm>
#include <oboe/Oboe.h>
#include <android_log.h>

using namespace oboe;

class Sound : public IRenderableAudio, oboe::AudioStreamCallback {
public:
    void renderAudio(float *audio_data, int32_t num_frames) override {
        auto iter = std::next(pcm_data.begin(), cur_sample);
        const int32_t num_sample = std::min(num_frames * sample_per_frame, total_sample - cur_sample);
        for(auto i = 0; i < num_sample; ++i, std::advance(iter,1), ++cur_sample) {
            audio_data[i] += *iter;
        }
    }

    DataCallbackResult onAudioReady(AudioStream *stream, void *audio_data, int32_t num_frames) override {
        assert(input_stream == stream);
        auto result = stream->read(audio_data,stream->getBufferSizeInFrames(),0);
        if(result == oboe::Result::OK) {
            if(result.value() < num_frames) {
                LOGD("T:frame read: %d",result.value());
            }
            return DataCallbackResult::Continue;
        }
        return DataCallbackResult::Stop;

        //auto buffer = std::make_unique<float[]>(stream->getBufferCapacityInFrames() * stream->getChannelCount());
        //oboe::convertFloatToPcm16(buffer.get(), static_cast<int16_t *>(audio_data), num_frames * stream->getChannelCount());
        //if(!total_sample) {
        //    do {
        //        auto result = stream->read(buffer.get(),num_frames, 0);
        //        if(result == oboe::Result::OK) break;
        //        frame_read = result.value();
        //    }while(frame_read != 0);
        //}
        //else {
        //    auto result = stream->read(buffer.get(), num_frames, 0);
        //    if(result == oboe::Result::OK) {
        //        LOGD("T:Read %d frames", result.value());
        //    } else {
        //        LOGD("T:Error reading stream: %s", convertToText(result.error()));
        //        return oboe::DataCallbackResult::Stop;
        //    }
        //    frame_read = result.value();
        //}
        //total_sample += frame_read;
        //LOGD("T:frame read: %d",frame_read);
        //cur_sample += frame_read;
        //return DataCallbackResult::Continue;
    }

    AudioStream* build_recorder() {
        AudioStreamBuilder builder;
        builder.setDirection(Direction::Input);
        builder.setCallback(this);
        builder.setFormat(AudioFormat::I16);
        builder.setPerformanceMode(PerformanceMode::LowLatency);
        builder.setSharingMode(SharingMode::Exclusive);

        AudioStream* stream;
        auto result = builder.openStream(&stream);
        input_stream = stream;
        if(result != Result::OK) { LOGD("Error reading stream: ");}
        return stream;
    }

private:
    std::vector<float> pcm_data;
    AudioStream* input_stream;
    int32_t total_sample = 0;
    int32_t cur_sample = 0;
    int sample_per_frame = 2; // default stereo
};

#endif //LOOPANG_SOUND_H
