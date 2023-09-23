package com.example.testapplication2.Miscellaneous;

import java.util.ArrayList;

public class AudioScanner {
    public static float subBassWeight = (float) (Math.pow(10,-0.4));
    public static float bassWeight = (float) (Math.pow(10,-0.1));

    //public static float subBassAndBassWeight = (float) (Math.pow(10,-0.3));
    public static float lowerMidrangeWeight = (float) (Math.pow(10,-0.15));
    public static float midrangeWeight = (float) (Math.pow(10,0.05));
    public static float higherMidrangeWeight = (float) (Math.pow(10,0.6));
    public static float presenceWeight = (float) (Math.pow(10,0.3));
    public static float brillianceWeight = (float) (Math.pow(10,0.2));

    public static float getLoudnessIndex(float[] floatSounds){
        float loudnessIndex=0;
        loudnessIndex+=floatSounds[0]*subBassWeight;
        loudnessIndex+=floatSounds[1]*bassWeight;
        loudnessIndex+=floatSounds[2]*lowerMidrangeWeight;
        loudnessIndex+=floatSounds[3]*midrangeWeight;
        loudnessIndex+=floatSounds[4]*higherMidrangeWeight;
        loudnessIndex+=floatSounds[5]*presenceWeight;
        loudnessIndex+=floatSounds[6]*brillianceWeight;
        return loudnessIndex;
    }

    public static void normalizeFrequencyAmplitudes(float[] floatSounds){
        float total=0;
        for (float f:floatSounds){
            total+=f;
        }
        if (total!=0) {
            for (int i = 0; i < floatSounds.length; ++i) {
                floatSounds[i] = floatSounds[i] * 100 / total;        // get a percentage.
            }
        }
    }

    public static double getSubBass(short[] pcm){
        int N=pcm.length;
        double ret=0;
        for (int neta=1;neta<=N;++neta){
            ret+=pcm[neta-1]* Math.sin(1*Math.PI*neta/N);
        }
        return Math.abs(ret);
    }
    public static double getBass(short[] pcm){
        int N=pcm.length;
        double ret=0;
        for (int n=2;n<=4;++n){
            double temp = 0;
            for (int neta=1;neta<=N;++neta){
                temp+=pcm[neta-1]* Math.sin(n*Math.PI*neta/N);
            }
            ret+=Math.abs(temp);
        }
        return ret;
    }
    public static double getLowerMidrange(short[] pcm){
        int N=pcm.length;
        double ret=0;
        for (int neta=1;neta<=N;++neta){
            ret+=pcm[neta-1]* Math.sin(2*Math.PI*neta/N);
        }
        return Math.abs(ret);
    }
    public static double getMidrange(short[] pcm){
        int N=pcm.length;
        double ret=0;
        for (int n=2;n<=4;++n){
            double temp = 0;
            for (int neta=1;neta<=N;++neta){
                temp+=pcm[neta-1]* Math.sin(n*Math.PI*neta/N);
            }
            ret+=Math.abs(temp);
        }
        return ret;
    }
    public static double getHigherMidrange(short[] pcm){
        int N=pcm.length;
        double ret=0;
        for (int neta=1;neta<=N;++neta){
            ret+=pcm[neta-1]* Math.sin(2*Math.PI*neta/N);
        }
        return Math.abs(ret);
    }
    public static double getPresence(short[] pcm){
        int N=pcm.length;
        double ret=0;
        for (int neta=1;neta<=N;++neta){
            ret+=pcm[neta-1]* Math.sin(3*Math.PI*neta/N);
        }
        return Math.abs(ret);
    }
    public static double getBrilliance(short[] pcm){
        int N=pcm.length;
        double ret=0;
        for (int n=2;n<=3;++n){
            double temp = 0;
            for (int neta=1;neta<=N;++neta){
                temp+=pcm[neta-1]* Math.sin(n*Math.PI*neta/N);
            }
            ret+=Math.abs(temp);
        }
        return ret;
    }

    public static int subBassN(int sampleRate){
        return (int) (((float)sampleRate)/(2*60)+1);        // n==1
    }
    public static int bassN(int sampleRate){
        return (int) (((float)sampleRate)/(2*60)+1);        // n==2,3,4
    }
    public static int lowerMidrangeN(int sampleRate){
        return (int) (((float)sampleRate)/(2*250)+1);        // n==2
    }
    public static int midrangeN(int sampleRate){
        return (int) (((float)sampleRate)/(2*500)+1);        // n==2,3,4
    }
    public static int higherMidrangeN(int sampleRate){
        return (int) (((float)sampleRate)/(2*2000)+1);        // n==2
    }
    public static int presenceN(int sampleRate){
        return (int) (((float)sampleRate)/(2*2000)+1);        // n==3
    }
    public static int brillianceN(int sampleRate){
        return (int) (((float)sampleRate)/(2*6000)+1);        // n==2,3
    }
}
