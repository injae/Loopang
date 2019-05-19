cmake_minimum_required(VERSION 3.6)

list(APPEND CMAKE_MODULE_PATH "${CMAKE_CURRENT_SOURCE_DIR}/cmake/")
project(native-audio-lib LANGUAGES C CXX VERSION 0.0.1)

include(cppm_tool)
cppm_load()
cppm_cxx_standard(14)

include(cmake/android-ndk-include.cmake)

include(download/git)
git_clone(oboe URL https://github.com/google/oboe.git PATH ${CPPM_ROOT}/install/oboe/lastest QUIET)

cppm_target_define(native-audio-lib STATIC 
SOURCES 
	src/native-audio-lib.cpp)

cppm_target_dependencies(native-audio-lib
PUBLIC
    log
	oboe
)


