//
// Created by nieel on 5/20/19.
//

#include "EchoEngine.h"
#include "android_log.h"

void EchoEngine::echo_on(bool is_on) {
    if(is_on) { open_all(); }
    else      { close_all(); }
}

void EchoEngine::open_all() {
    LOGE("I Log: %d, %d",playback_device_id,recording_device_id);
    playback_  = new PlayBackStream(playback_device_id);
    recording_ = new RecordingStream(recording_device_id);
    playback_->builder.setCallback(this);
    recording_->builder.setCallback(nullptr);
    if(playback_ && recording_) {
        playback_->open();
        recording_->open();
        recording_->start();
        playback_->start();
    }
    else {
        close_all();
    }

}

void EchoEngine::close_all() {
    if(playback_ != nullptr) {
        playback_->close();
        playback_ = nullptr;
    }
    if(recording_ != nullptr) {
        recording_->close();
        recording_ = nullptr;
    }
}

void EchoEngine::set_playback_device_id(int32_t device_id)  { playback_device_id  = device_id; }
void EchoEngine::set_recording_device_id(int32_t device_id) { recording_device_id = device_id; }

oboe::DataCallbackResult EchoEngine::onAudioReady(oboe::AudioStream *oboeStream, void *audioData, int32_t numFrames) {
    using namespace oboe;
    int32_t prev_frame_read = 0, frame_read = 0;
    assert(oboeStream == playback_->stream);
    //LOGI("Call onAudioReady");

    if(playback_->processed_frame_count < playback_->system_startup_frames) {

        do {
            prev_frame_read = frame_read;
            auto status = recording_->stream->read(audioData, numFrames, 0);
            frame_read = (!status) ? 0 : status.value();
            if(frame_read == 0) break;

        }while(frame_read);

        frame_read = prev_frame_read;
    }
    else {
        auto status = recording_->stream->read(audioData, numFrames, 0);
        if(!status) {
            LOGE("input stream read error %s", convertToText(status.error()));
            return DataCallbackResult::Stop;
        }

        frame_read = status.value();
    }
    if(frame_read < numFrames) {
       int32_t byte_per_frame = recording_->stream->getChannelCount() * oboeStream->getBytesPerSample();
       uint8_t* pad_pos = static_cast<uint8_t *>(audioData) + frame_read * byte_per_frame;
       memset(pad_pos, 0, static_cast<size_t>((numFrames - frame_read) * byte_per_frame));
    }

    playback_->processed_frame_count += numFrames;

    return oboe::DataCallbackResult::Continue;
}

EchoEngine::~EchoEngine() {
    //playback_->stop();
    //recording_->stop();
    close_all();
}
