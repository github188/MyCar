LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_LDLIBS += -L$(SYSROOT)/usr/lib -llog
LOCAL_MODULE := x431file
LOCAL_SRC_FILES := com_cnlaunch_mycar_jni_JniX431File.c file.c fileinfo.c hash.c list.c lsx.c memblock.c util.c 
include $(BUILD_SHARED_LIBRARY)
