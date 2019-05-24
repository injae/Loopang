
include(cppm_tool)
find_library(log log-lib)
set(OBOE_DIR ${CPPM_ROOT}/install/oboe/lastest)
add_subdirectory(${OBOE_DIR} ./oboe)
include_directories(${OBOE_DIR}/include/oboe)

