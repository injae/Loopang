//
// Created by nieel on 5/20/19.
//

#include "PlayBackStream.h"
#include "android_log.h"


PlayBackStream::PlayBackStream(int32_t device_id) : CommonStream() {
    using namespace oboe;
    builder.setDeviceId(device_id)
          ->setDirection(Direction::Output)
          ->setChannelCount(ChannelCount::Stereo);
}

oboe::Result PlayBackStream::open() {
    auto result = CommonStream::open();
    if(result == oboe::Result::OK && stream) {
        sample_rate = stream->getSampleRate();
        system_startup_frames = static_cast<uint64_t>(sample_rate * 0.5f);
        processed_frame_count = 0;
    }
    else {
        LOGE("Failed to create playback stream. Error: %s", oboe::convertToText(result));
    }
    return result;
}

