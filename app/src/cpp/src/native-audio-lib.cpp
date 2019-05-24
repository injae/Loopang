#include <jni.h>
#include <EchoEngine.h>
#include <android_log.h>
#include <string>
#include <vector>

#include "AudioEngine.h"
#include "TestAudioEngine.h"

static EchoEngine* _engine;

EchoEngine* get_safe_engine(const char* message) {
    if(_engine == nullptr) { LOGE(message, "method"); }
    return _engine;
}

extern "C" {
    JNIEXPORT bool JNICALL Java_com_treasure_loopang_audio_NativeAudioManager_create( JNIEnv* env, jobject instance) {
        if(_engine == nullptr) _engine = new(std::nothrow) EchoEngine();
        return (_engine != nullptr);
    }

    JNIEXPORT void JNICALL Java_com_treasure_loopang_audio_NativeAudioManager_delete( JNIEnv* env, jobject instance) {
        delete _engine;
        _engine = nullptr;
    }

    JNIEXPORT void JNICALL Java_com_treasure_loopang_audio_NativeAudioManager_EchoOn( JNIEnv* env, jobject instance, jboolean is_on) {
        auto engine = get_safe_engine("Engine is null, you must call createEngine before calling this ");
        engine->echo_on(is_on);
    }

    JNIEXPORT void JNICALL Java_com_treasure_loopang_audio_NativeAudioManager_setRecordingDeviceId( JNIEnv* env, jobject instance, jint device_id) {
        auto engine = get_safe_engine("set Recording Device ID");
        engine->set_recording_device_id(device_id);
    }
    JNIEXPORT void JNICALL Java_com_treasure_loopang_audio_NativeAudioManager_setPlaybackDeviceId( JNIEnv* env, jobject instance, jint device_id) {
        auto engine = get_safe_engine("set PlayBack Device ID");
        engine->set_playback_device_id(device_id);
    }
}


AudioEngine engine;

std::vector<int> convertJavaArrayToVector(JNIEnv *env, jintArray intArray){

    std::vector<int> v;
    jsize length = env->GetArrayLength(intArray);

    if (length > 0) {
        jboolean isCopy;
        jint *elements = env->GetIntArrayElements(intArray, &isCopy);
        for (int i = 0; i < length; i++) {
            v.push_back(elements[i]);
        }
    }
    return v;
}

extern "C" {
    JNIEXPORT void JNICALL Java_com_treasure_loopang_audio_NativeAudioManager_startEngine(JNIEnv *env, jobject instance, jintArray jCpuIds) {
        std::vector<int> cpuIds = convertJavaArrayToVector(env, jCpuIds);
        engine.start(cpuIds);
    }

    JNIEXPORT void JNICALL Java_com_treasure_loopang_audio_NativeAudioManager_stopEngine(JNIEnv *env, jobject instance) {
        engine.stop();
    }

    JNIEXPORT void JNICALL Java_com_treasure_loopang_audio_NativeAudioManager_tab(JNIEnv *env, jobject instance, jboolean b) {
        engine.tap(b);
    }

    TestAudioEngine tengine;
    JNIEXPORT void JNICALL Java_com_treasure_loopang_audio_NativeAudioManager_startTest(JNIEnv *env, jobject instance) {
        tengine.start();
    }

    JNIEXPORT void JNICALL Java_com_treasure_loopang_audio_NativeAudioManager_tabTest(JNIEnv *env, jobject instance) {
        tengine.tab();
    }
}
