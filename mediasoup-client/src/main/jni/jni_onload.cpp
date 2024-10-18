#include "mediasoupclient.hpp"
#include <jni.h>
#include <modules/utility/include/jvm_android.h>
#include <sdk/android/native_api/jni/class_loader.h>
#include <sdk/android/src/jni/jni_helpers.h>

namespace mediasoupclient
{
extern "C" jint JNIEXPORT JNICALL JNI_OnLoad(JavaVM* jvm, void* reserved)
{
	jint ret = webrtc::jni::InitGlobalJniVariables(jvm);
	if (ret < 0)
		return -1;

	mediasoupclient::Initialize();
	// webrtc::jni::LoadGlobalClassReferenceHolder();
	webrtc::InitClassLoader(webrtc::jni::GetEnv());
	return ret;
}

extern "C" void JNIEXPORT JNICALL JNI_OnUnload(JavaVM* jvm, void* reserved)
{
	// webrtc::jni::FreeGlobalClassReferenceHolder();
	mediasoupclient::Cleanup();
}

} // namespace mediasoupclient
