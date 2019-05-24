//
// Created by nieel on 5/20/19.
//

#include "RecordingStream.h"

RecordingStream::RecordingStream(int32_t device_id) : CommonStream() {
    using namespace oboe;
    builder.setDeviceId(device_id)
          ->setDirection(Direction::Input)
          ->setSampleRate(kUnspecified)
          ->setChannelCount(ChannelCount::Stereo);
}

