//
// Created by nieel on 5/20/19.
//

#ifndef LOOPANG_COMMONSTREAM_H
#define LOOPANG_COMMONSTREAM_H


#include <oboe/Oboe.h>

class CommonStream {
public:
    CommonStream();
    virtual oboe::Result open();
    virtual oboe::Result start();
    virtual oboe::Result stop();
    virtual oboe::Result close();
    virtual ~CommonStream();
public:
    oboe::AudioStreamBuilder builder;
    oboe::AudioStream *stream = nullptr;
};


#endif //LOOPANG_COMMONSTREAM_H
