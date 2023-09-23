#include<jni.h>
#include<string.h>
#include<stdlib.h>
#include<stdio.h>

jstring Java_com_example_testapplication2_MainActivity_helloWorrrrd(JNIEnv* env, jobject obj){

    return (*env)->NewStringUTF(env,"Hello Worrrrd2333333333!");
}


jstring Java_com_example_testapplication2_RecyclerViewAdapters_songsFragmentRecyclerViewAdapter_helloWorld(JNIEnv* env, jobject obj, jstring string){

    return string;
}

jshort Java_com_example_testapplication2_RecyclerViewAdapters_songsFragmentRecyclerViewAdapter_decode(JNIEnv* env, jobject obj, jshortArray pcm){

    short * cData = (*env)->GetShortArrayElements(env,pcm,NULL);
    short d = cData[5];
    (*env)->ReleaseShortArrayElements(env,pcm, cData, 0);
    return d;
}

jdoubleArray Java_com_example_testapplication2_Miscellaneous_HopeThisWorks_FFT(JNIEnv* env, jobject obj, jdoubleArray jdoubles, jshortArray jpcm){

    short * pcm = (*env)->GetShortArrayElements(env,jpcm,NULL);
    double * doubles = (*env)->GetDoubleArrayElements(env,jdoubles,NULL);
    int N=1000;

    jdoubleArray result;
     result = (*env)->NewDoubleArray(env, 1000);
     if (result == NULL) {
         return NULL; /* out of memory error thrown */
     }

    // fill a temp structure to use to populate the java int array
    jdouble bn[1000];

    for (int n=1;n<=N;++n){
        double bTemp=0;
        for (int neta=1;neta<=N;++neta){
            bTemp+=pcm[neta-1]*doubles[n*neta-1];
        }
        bn[n-1]=(2*bTemp/N);
    }

    (*env)->ReleaseShortArrayElements(env,jpcm, pcm, 0);
    (*env)->ReleaseDoubleArrayElements(env,jdoubles, doubles, 0);

     // move from the temp structure to the java structure
     (*env)->SetDoubleArrayRegion(env, result, 0, 1000, bn);
     return result;
}

jdoubleArray Java_com_example_testapplication2_Miscellaneous_HopeThisWorks_FFTopt(JNIEnv* env, jobject obj, jdoubleArray jsineDoubles, jdoubleArray jcosineDoubles, jshortArray jpcm){

    short * pcm = (*env)->GetShortArrayElements(env,jpcm,NULL);
    double * sineDoubles = (*env)->GetDoubleArrayElements(env,jsineDoubles,NULL);
    double * cosineDoubles = (*env)->GetDoubleArrayElements(env,jcosineDoubles,NULL);
    int N=96;       // 1000

    jdoubleArray result;
    result = (*env)->NewDoubleArray(env, N);
    if (result == NULL) {
        return NULL;
    }

    jdouble bn[N];

    for (int n=1;n<=N/2;++n){
        double bTemp=0;
        double bTempNby2=0;
        double netaR0=0;
        double netaR1=0;
        double netaR2=0;
        double netaR3=0;
        double netaR1Cos=0;
        double netaR3Cos=0;
        int netaR=0;
        for (int neta=1;neta<=N;++neta){
            netaR=neta%4;
            if (netaR==0){
                netaR0+=pcm[neta-1]* sineDoubles[n*neta-1];
            }else if (netaR==1){
                netaR1+=pcm[neta-1]* sineDoubles[n*neta-1];
                netaR1Cos+=pcm[neta-1]* cosineDoubles[n*neta-1];
            }else if (netaR==2){
                netaR2+=pcm[neta-1]* sineDoubles[n*neta-1];
            }else if (netaR==3){
                netaR3+=pcm[neta-1]* sineDoubles[n*neta-1];
                netaR3Cos+=pcm[neta-1]* cosineDoubles[n*neta-1];
            }
        }
        bTemp=netaR0+netaR1+netaR2+netaR3;
        bTempNby2=netaR0+netaR1Cos-netaR2-netaR3Cos;

        bn[n-1]=(2*bTemp/N);
        bn[n-1+N/2]=(2*bTempNby2/N);
    }

    (*env)->ReleaseShortArrayElements(env,jpcm, pcm, 0);
    (*env)->ReleaseDoubleArrayElements(env,jsineDoubles, sineDoubles, 0);
    (*env)->ReleaseDoubleArrayElements(env,jcosineDoubles, cosineDoubles, 0);

     // move from the temp structure to the java structure
     (*env)->SetDoubleArrayRegion(env, result, 0, N, bn);
     return result;
}