cmake_minimum_required(VERSION 3.4.1)

# 创建一个变量 source_file 它的值就是src/main/cpp/所有的.cpp文件
file(GLOB source_file src/main/cpp/*.cpp)

add_library( native-lib
             SHARED
             ${source_file} )

find_library( log-lib
              log )

include_directories(src/main/cpp/include)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS}  -D__STDC_CONSTANT_MACROS -L${CMAKE_SOURCE_DIR}/src/main/cpp/libs/${ANDROID_ABI}")

target_link_libraries(native-lib
                       avfilter avformat avcodec   avutil swresample swscale
                       ${log-lib} z android OpenSLES)
#FFmpeg中会动态依赖ndk中的libz库，所以要引入 ；引入的库顺序要注意顺序：avformat 在avcodec之前，静态依赖
# android是为了使用ANativeWindow
