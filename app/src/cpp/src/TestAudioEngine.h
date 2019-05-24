//
// Created by nieel on 5/23/19.
//

#ifndef LOOPANG_TESTAUDIOENGINE_H
#define LOOPANG_TESTAUDIOENGINE_H

#include <atomic>
#include <oboe/Oboe.h>
#include <android_log.h>
#include <thread>

class TestAudioEngine : AudioStreamCallback {
public:
    void tab() {
        is_recording.store(is_recording ? false : true);
        if(is_recording) {
            auto result = stream->requestStart();
            if(result != Result::OK) { LOGE("Error starting stream"); }
        }else {
            stream->requestStop();
        }
    }
    void start() {
        stream =sound.build_recorder();
        //AudioStreamBuilder builder;
        //builder.setCallback(this);
        //builder.setPerformanceMode(PerformanceMode::LowLatency);
        //builder.setSharingMode(SharingMode::Exclusive);
        //builder.setFormat(AudioFormat::I16);
        //builder.openStream(&loop);
        //LOGE("T:%s",convertToText(builder.getDirection()));
        //if (stream->getFormat() == AudioFormat::I16){
        //    buffer = std::make_unique<float[]>( (size_t)loop->getBufferCapacityInFrames() * loop->getChannelCount());
        //}

        //AudioStreamBuilder builder;
        //builder.setDirection(Direction::Input);
        //builder.setFormat(AudioFormat::I16);
        //builder.setPerformanceMode(PerformanceMode::LowLatency);

        //auto result = builder.openStream(&stream);

        //if(result != Result::OK) { LOGD("Error reading stream: ");}
        //constexpr int kMillisecondToRecord = 2;
        //int32_t requestedFrames = (int32_t)(kMillisecondToRecord * (stream->getSampleRate() / kMillisPerSecond));
        //int16_t myBuffer[requestedFrames];

        //constexpr int64_t kTimeoutValue = 3 * kNanosPerMillisecond;
        //int frame_read = 0;
        //do {
        //    auto result = stream->read(myBuffer,stream->getBufferSizeInFrames(), 0);
        //    if(result == Result::OK) break;
        //    frame_read = result.value();
        //}while(frame_read != 0);

        //while(is_recording) {
        //    auto result = stream->read(myBuffer ,requestedFrames, kMillisecondToRecord);
        //    if(result == Result::OK) {
        //        LOGD("Read %d frames", result.value());
        //    } else {
        //        LOGD("Error reading stream: %s", convertToText(result.error()));
        //    }
        //}
        //stream->close();
    }

    DataCallbackResult onAudioReady(AudioStream *oboe_stream, void *audio_data, int32_t num_frames) override {

        //if (is16Bit){
        //    oboe::convertFloatToPcm16(buffer.get(), static_cast<int16_t*>(audio_data), num_frames * oboe_stream->getChannelCount());
        //}

        auto result = stream->read(audio_data,oboe_stream->getBufferSizeInFrames(),0);
        if(result == oboe::Result::OK) {
            if(result.value() < num_frames) {
                LOGD("T:frame read: %d",result.value());
            }
            return DataCallbackResult::Continue;
        }
        return DataCallbackResult::Stop;
    }
private:
    std::atomic<bool> is_recording{false};
    Sound sound;
    AudioStream* stream;
    AudioStream* loop;
    std::unique_ptr<float[]> buffer;
};


#endif //LOOPANG_TESTAUDIOENGINE_H
