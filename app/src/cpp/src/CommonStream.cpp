//
// Created by nieel on 5/20/19.
//

#include "CommonStream.h"
#include "android_log.h"


CommonStream::CommonStream() : builder(), stream(nullptr) {
    using namespace oboe;
    builder.setAudioApi(AudioApi::AAudio)
          ->setFormat(AudioFormat::I16)
          ->setSharingMode(SharingMode::Shared)
          ->setPerformanceMode(PerformanceMode::LowLatency);
}

CommonStream::~CommonStream() {
    delete stream;
    stream = nullptr;
}

oboe::Result CommonStream::open() { return builder.openStream(&stream); }

oboe::Result CommonStream::start() {
    using oboe::Result;
    assert(stream);
    if(stream) {
        auto result = stream->requestStart();
        if(result != Result::OK) { LOGE("Error starting stream. %s", oboe::convertToText(result));}
    }
    return oboe::Result::ErrorNull;
}

oboe::Result CommonStream::stop() {
    using oboe::Result;
    if(stream) {
        auto result = stream->stop(0L);
        if(result != Result::OK) { LOGE("Error stop stream. %s", oboe::convertToText(result));}
    }
    return oboe::Result::ErrorNull;
}

oboe::Result CommonStream::close() {
    using oboe::Result;
    if(stream) {
        auto result = stream->close();
        if(result != Result::OK) { LOGE("Error close stream. %s", oboe::convertToText(result));}
    }
    return oboe::Result::ErrorNull;
}
