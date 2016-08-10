/**
* Copyright (c) 2015-2016 VisionStar Information Technology (Shanghai) Co., Ltd. All Rights Reserved.
* EasyAR is the registered trademark or trademark of VisionStar Information Technology (Shanghai) Co., Ltd in China
* and other countries for the augmented reality technology developed by VisionStar Information Technology (Shanghai) Co., Ltd.
*/

#include "ar.hpp"
#include "renderer.hpp"
#include <jni.h>
#include <GLES2/gl2.h>
#include <android/log.h>
#include <sstream>

#define  LOG_TAG    "HelloJni"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define JNIFUNCTION_NATIVE(sig) Java_com_lptiyu_tanke_activities_imagedistinguish_ImageDistinguishActivity_##sig

extern "C" {
    JNIEXPORT jboolean JNICALL JNIFUNCTION_NATIVE(nativeInit(JNIEnv* env, jobject object,jobjectArray picData,jobjectArray videoData));
    JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(nativeDestory(JNIEnv* env, jobject object));
    JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(nativeInitGL(JNIEnv* env, jobject object));
    JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(nativeStartAr(JNIEnv* env, jobject object));
    JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(nativeStopAr(JNIEnv* env, jobject object));
    JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(nativeResizeGL(JNIEnv* env, jobject object, jint w, jint h));
    JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(nativeRender(JNIEnv* env, jobject obj));
    JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(nativeRotationChange(JNIEnv* env, jobject obj, jboolean portrait));
};

namespace EasyAR {
namespace samples {

class HelloARVideo : public AR
{
public:
    HelloARVideo();
    ~HelloARVideo();
    virtual void initGL();
    virtual void resizeGL(int width, int height);
    virtual void render();
    virtual bool clear();
    jclass myclass;
    jobject myobject;
    jmethodID mymethod;
    JNIEnv* myenv;
    JavaVM* g_jvm;
    int tracks_count;
    //标识是否第一次
    int istargeted;
    std::string *tracks_doing;
private:
    Vec2I view_size;
    VideoRenderer* renderer[10];
    int tracked_target;
    int active_target;
    int texid[10];
    ARVideo* video;
    VideoRenderer* video_renderer;
};

HelloARVideo::HelloARVideo()
{
    istargeted=0;
    tracks_count=3;
    view_size[0] = -1;
    tracked_target = 0;
    active_target = 0;
//    for(int i = 0; i < tracks_count; ++i) {
//        texid[i] = 0;
//        renderer[i] = new VideoRenderer;
//    }
    video = NULL;
    video_renderer = NULL;
}

HelloARVideo::~HelloARVideo()
{
    for(int i = 0; i < tracks_count; ++i) {
        delete renderer[i];
    }
}

void HelloARVideo::initGL()
{
    augmenter_ = Augmenter();
    augmenter_.attachCamera(camera_);
    for(int i = 0; i < tracks_count; ++i) {
        texid[i] = 0;
        renderer[i] = new VideoRenderer;
        renderer[i]->init();
        texid[i] = renderer[i]->texId();
    }
}

void HelloARVideo::resizeGL(int width, int height)
{
    view_size = Vec2I(width, height);
}

void HelloARVideo::render()
{
    glClearColor(0.f, 0.f, 0.f, 1.f);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

    Frame frame = augmenter_.newFrame();
    if(view_size[0] > 0){
        AR::resizeGL(view_size[0], view_size[1]);
        if(camera_ && camera_.isOpened())
            view_size[0] = -1;
    }
    augmenter_.setViewPort(viewport_);
    augmenter_.drawVideoBackground();
    glViewport(viewport_[0], viewport_[1], viewport_[2], viewport_[3]);

    AugmentedTarget::Status status = frame.targets()[0].status();
    if(status == AugmentedTarget::kTargetStatusTracked){
        int id = frame.targets()[0].target().id();
        if(active_target && active_target != id) {
//            video->onLost();
//            delete video;
//            video = NULL;
            istargeted=0;
            tracked_target = 0;
            active_target = 0;
        }
        if (!tracked_target) {
            if (istargeted == 0) {
                // if(frame.targets()[0].target().name() == std::string("argame") && texid[0]) {
                //     video = new ARVideo;
                //     video->openVideoFile("video.mp4", texid[0]);
                //     video_renderer = renderer[0];
                // }
                // else if(frame.targets()[0].target().name() == std::string("namecard") && texid[1]) {
                //     video = new ARVideo;
                //     video->openTransparentVideoFile("transparentvideo.mp4", texid[1]);
                //     video_renderer = renderer[1];
                // }
                // else if(frame.targets()[0].target().name() == std::string("idback") && texid[2]) {
                //     video = new ARVideo;
                //     video->openStreamingVideo("http://7xl1ve.com5.z0.glb.clouddn.com/sdkvideo/EasyARSDKShow201520.mp4", texid[2]);
                //     video_renderer = renderer[2];
                // }
                  // LOGI("%s",frame.targets()[0].target().id()+"d");
                //int a;
                //std::stringstream stream(frame.targets()[0].target().name());
                //stream>>a;
                //myenv->CallStaticVoidMethod(myclass,mymethod);
               // int a=atoi(frame.targets()[0].target().name());

              //  myenv->CallStaticIntMethod(obj,java_method


//            video = new ARVideo;
//            video->openStreamingVideo(tracks_doing[a], texid[a]);
//            video_renderer = renderer[a];
                //调用java
                //(*jvm)->GetEnv(jvm, (void**)&env, JNI_VERSION_1_2);
                g_jvm->AttachCurrentThread(&myenv, NULL);
                myclass= myenv->FindClass("com/lptiyu/tanke/activities/imagedistinguish/MyC2Java");
                mymethod = myenv->GetStaticMethodID(myclass, "show","()V");
                myenv->CallStaticVoidMethod(myclass,mymethod);
                istargeted=1;

            }
            if (istargeted) {
                //video->onFound();
                tracked_target = id;
                active_target = id;
            }
        }
        Matrix44F projectionMatrix = getProjectionGL(camera_.cameraCalibration(), 0.2f, 500.f);
        Matrix44F cameraview = getPoseGL(frame.targets()[0].pose());
        ImageTarget target = frame.targets()[0].target().cast_dynamic<ImageTarget>();
//        if(tracked_target) {
//            video->update();
//            video_renderer->render(projectionMatrix, cameraview, target.size());
//        }
    } else {
        if (tracked_target) {
            //video->onLost();

            g_jvm->AttachCurrentThread(&myenv, NULL);
            myclass= myenv->FindClass("com/lptiyu/tanke/activities/imagedistinguish/MyC2Java");
            mymethod = myenv->GetStaticMethodID(myclass, "unshow","()V");
            myenv->CallStaticVoidMethod(myclass,mymethod);
            tracked_target = 0;
            istargeted=0;
        }
    }
}

bool HelloARVideo::clear()
{
    AR::clear();
    if(video){
        delete video;
        video = NULL;
        tracked_target = 0;
        active_target = 0;
    }
    return true;
}

}
}
EasyAR::samples::HelloARVideo ar;

JNIEXPORT jboolean JNICALL JNIFUNCTION_NATIVE(nativeInit(JNIEnv* env,jobject object,jobjectArray picData,jobjectArray videoData))
{

   // ar.myenv=env;
env->GetJavaVM(&(ar.g_jvm));
    bool status = ar.initCamera();
    //ar.loadAllFromJsonFile("targets.json");
    //ar.loadFromImage("namecard.jpg");
//pyj
//LOGI("call from jni!");
    jint i;
    jstring myarray,doarray;
//LOGI("call from jni!");
    int row =env->GetArrayLength(picData);//获得行数
    ar.tracks_count=row;
ar.tracks_doing=new std::string[row];
    for (i = 0; i < row; i++){
        myarray = (jstring)(env->GetObjectArrayElement(picData,i));
        doarray=(jstring)(env->GetObjectArrayElement(videoData,i));
        char * ok=(char*)(env->GetStringUTFChars(myarray,NULL));
        char * dolist=(char*)(env->GetStringUTFChars(doarray,NULL));
         ar.loadFromImage(ok,i);
        ar.tracks_doing[i]=dolist;
       // ar.tracks_all[i][j]=env->GetStringUTFChars((jstring)(env->GetObjectArrayElement((jobjectArray)myarray , j)),NULL);
      // LOGI("test: %s\n",env->GetStringUTFChars(myarray,NULL) );
}
//pyj

    //status &= ar.start();
    return status;
}

JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(nativeDestory(JNIEnv*, jobject))
{
    ar.clear();
}

JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(nativeInitGL(JNIEnv*, jobject))
{
    ar.initGL();
}
JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(nativeStartAr(JNIEnv*, jobject))
{
 ar.start();
}
JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(nativeStopAr(JNIEnv*, jobject))
{
 ar.stop();
}
JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(nativeResizeGL(JNIEnv*, jobject, jint w, jint h))
{
    ar.resizeGL(w, h);
}

JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(nativeRender(JNIEnv*, jobject))
{
    ar.render();
}

JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(nativeRotationChange(JNIEnv*, jobject, jboolean portrait))
{
    ar.setPortrait(portrait);
}
