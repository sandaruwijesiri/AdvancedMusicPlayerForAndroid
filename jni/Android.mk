LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := nativemodule
LOCAL_SRC_FILES := CTest.c

include $(BUILD_SHARED_LIBRARY)