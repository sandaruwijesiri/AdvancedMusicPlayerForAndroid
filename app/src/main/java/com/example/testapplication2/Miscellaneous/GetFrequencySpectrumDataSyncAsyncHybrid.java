package com.example.testapplication2.Miscellaneous;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;

import androidx.annotation.NonNull;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.ArrayList;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.SampleBuffer;

public class GetFrequencySpectrumDataSyncAsyncHybrid {

    MediaExtractor mediaExtractor;
    MediaFormat mediaFormat;
    MediaCodec decoder;

    int channelCount;
    int sampleRate;
    long duration;
    String mimeType;

    //long start=0;

    public float[] floatSounds = new float[7];
    public boolean done=false;

    int subBassN;
    int bassN;
    int lowerMidrangeN;
    int midrangeN;
    int higherMidrangeN;
    int presenceN;
    int brillianceN;

    //long ext=0;
    //long temp;

    final ArrayList<Short> subBassArrayList = new ArrayList<>();
    double subBassTotal =0;
    int subBassCount =0;

    final ArrayList<Short> bassArrayList = new ArrayList<>();
    double bassTotal =0;
    int bassCount =0;

    final ArrayList<Short> lowerMidrangeArrayList = new ArrayList<>();
    double lowerMidrangeTotal =0;
    int lowerMidrangeCount =0;

    final ArrayList<Short> midrangeArrayList = new ArrayList<>();
    double midrangeTotal =0;
    int midrangeCount =0;

    final ArrayList<Short> higherMidrangeArrayList = new ArrayList<>();
    double higherMidrangeTotal =0;
    int higherMidrangeCount =0;

    final ArrayList<Short> presenceArrayList = new ArrayList<>();
    double presenceTotal =0;
    int presenceCount =0;

    final ArrayList<Short> brillianceArrayList = new ArrayList<>();
    double brillianceTotal =0;
    int brillianceCount =0;


    public GetFrequencySpectrumDataSyncAsyncHybrid(String fileName){
        mediaExtractor = new MediaExtractor();
        try {
            mediaExtractor.setDataSource(fileName);
        }catch (Exception e){
            done=true;
            return;
        }

        mediaFormat = null;
        int i;
        int numTracks = mediaExtractor.getTrackCount();
        for (i = 0; i < numTracks; i++)
        {
            mediaFormat = mediaExtractor.getTrackFormat(i);
            if (mediaFormat.getString(MediaFormat.KEY_MIME).startsWith("audio/"))
            {
                mediaExtractor.selectTrack(i);
                break;
            }
        }

        channelCount = mediaFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
        sampleRate = mediaFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE);
        duration = mediaFormat.getLong(MediaFormat.KEY_DURATION);
        mimeType = mediaFormat.getString(MediaFormat.KEY_MIME);

        subBassN=AudioScanner.subBassN(sampleRate);
        bassN=AudioScanner.bassN(sampleRate);
        lowerMidrangeN=AudioScanner.lowerMidrangeN(sampleRate);
        midrangeN=AudioScanner.midrangeN(sampleRate);
        higherMidrangeN=AudioScanner.higherMidrangeN(sampleRate);
        presenceN=AudioScanner.presenceN(sampleRate);
        brillianceN=AudioScanner.brillianceN(sampleRate);

        //start =System.currentTimeMillis();
        if (fileName.endsWith(".mp3")){
            decodeSynchronousJLayer(fileName);
            finalizeExecution();
        }else {
            try {
                decodeAsynchronousMediaDecoder();
            }catch (Exception e){
                done=true;
                //return;
            }
        }
    }

    public void decodeAsynchronousMediaDecoder() throws IOException {
        decoder = MediaCodec.createDecoderByType(mimeType);
        decoder.setCallback(mediaCodecCallback);
        decoder.configure(mediaFormat, null, null, 0);
        decoder.start();
    }

    public void decodeSynchronousJLayer(String path){
        if (path.endsWith(".mp3")) {

            try {
                FileInputStream fileIn = new FileInputStream(path);
                InputStream inputStream = new BufferedInputStream(fileIn, 1024);
                Bitstream bitstream = new Bitstream(inputStream);
                Decoder decoder = new Decoder();
                Header frameHeader = bitstream.readFrame();

                while (frameHeader != null) {
                    try {
                        SampleBuffer output = (SampleBuffer) decoder.decodeFrame(frameHeader, bitstream);
                        short[] pcm = output.getBuffer();
                        processSamples(pcm);

                    } catch (Exception e) {
                        //e.printStackTrace();
                    }
                    bitstream.closeFrame();
                    frameHeader = bitstream.readFrame();
                }
                bitstream.close();

                //System.out.println(path.substring(path.lastIndexOf("/")+1)+"\n" + Arrays.toString(getScaleAndKey.getResults4()));  //getResults4()

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    MediaCodec.Callback mediaCodecCallback = new MediaCodec.Callback() {
        private boolean mOutputEOS = false;
        private boolean mInputEOS = false;
        ByteBuffer inputBuffer;
        ByteBuffer outputBuffer;

        int sec=0;
        @Override
        public void onInputBufferAvailable (@NonNull MediaCodec codec,
                                            int index)
        {
            if (mOutputEOS | mInputEOS) return;
            inputBuffer = codec.getInputBuffer(index);
            if (inputBuffer == null) return;
            long sampleTime = 0;
            int result;
            result = mediaExtractor.readSampleData(inputBuffer, 0);
            if (result >= 0)
            {
                sampleTime = mediaExtractor.getSampleTime();
                /*if (sampleTime/1000000>sec) {
                    sec= (int) (sampleTime/1000000);
                    System.out.println("sampleTime!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! " + sec);
                }*/
                codec.queueInputBuffer(index, 0, result, sampleTime, 0);
                mediaExtractor.advance();
            }
            else
            {
                codec.queueInputBuffer(index, 0, 0, -1, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                mInputEOS = true;
            }
        }

        @Override
        public void onOutputBufferAvailable (@NonNull MediaCodec codec,
                                             int index,
                                             @NonNull MediaCodec.BufferInfo info)
        {
            outputBuffer = codec.getOutputBuffer(index);

            if (outputBuffer != null) {
                outputBuffer.rewind();
                outputBuffer.order(ByteOrder.LITTLE_ENDIAN);

                ShortBuffer shortBuffer = outputBuffer.asShortBuffer();
                short[] shorts = new short[shortBuffer.remaining()];
                shortBuffer.get(shorts);
                processSamples(shorts);
            }

            mOutputEOS |= ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0);
            codec.releaseOutputBuffer(index, false);
            if (mOutputEOS)
            {
                codec.stop();
                codec.release();
                mediaExtractor.release();
                finalizeExecution();
            }
        }

        @Override
        public void onError (@NonNull MediaCodec codec,
                             @NonNull MediaCodec.CodecException e)
        {
            System.out.println("mediacodec collback onError: %s"+e.getMessage());
        }

        @Override
        public void onOutputFormatChanged (@NonNull MediaCodec codec,
                                           @NonNull MediaFormat format)
        {
            System.out.println("onOutputFormatChanged: %s"+ format.toString());
        }

    };

    public void processSamples(short[] shorts){

        for (int i = 0; i < shorts.length; ++i) {
            if (i % channelCount == 0) {
                if (subBassArrayList.size() >= subBassN) {
                    short[] p = new short[subBassArrayList.size()];
                    for (int l = 0; l < subBassArrayList.size(); ++l) {
                        p[l] = subBassArrayList.get(l);
                    }
                    //temp = System.currentTimeMillis();
                    subBassTotal += AudioScanner.getSubBass(p);
                    //ext += (System.currentTimeMillis() - temp);
                    subBassArrayList.clear();
                    ++subBassCount;
                }
                subBassArrayList.add(shorts[i]);
                if (bassArrayList.size() >= bassN) {
                    short[] p = new short[bassArrayList.size()];
                    for (int l = 0; l < bassArrayList.size(); ++l) {
                        p[l] = bassArrayList.get(l);
                    }
                    //temp = System.currentTimeMillis();
                    bassTotal += AudioScanner.getBass(p);
                    //ext += (System.currentTimeMillis() - temp);
                    bassArrayList.clear();
                    ++bassCount;
                }
                bassArrayList.add(shorts[i]);
                if (lowerMidrangeArrayList.size() >= lowerMidrangeN) {
                    short[] p = new short[lowerMidrangeArrayList.size()];
                    for (int l = 0; l < lowerMidrangeArrayList.size(); ++l) {
                        p[l] = lowerMidrangeArrayList.get(l);
                    }
                    //temp = System.currentTimeMillis();
                    lowerMidrangeTotal += AudioScanner.getLowerMidrange(p);
                    //ext += (System.currentTimeMillis() - temp);
                    lowerMidrangeArrayList.clear();
                    ++lowerMidrangeCount;
                }
                lowerMidrangeArrayList.add(shorts[i]);
                if (midrangeArrayList.size() >= midrangeN) {
                    short[] p = new short[midrangeArrayList.size()];
                    for (int l = 0; l < midrangeArrayList.size(); ++l) {
                        p[l] = midrangeArrayList.get(l);
                    }
                    //temp = System.currentTimeMillis();
                    midrangeTotal += AudioScanner.getMidrange(p);
                    //ext += (System.currentTimeMillis() - temp);
                    midrangeArrayList.clear();
                    ++midrangeCount;
                }
                midrangeArrayList.add(shorts[i]);
                if (higherMidrangeArrayList.size() >= higherMidrangeN) {
                    short[] p = new short[higherMidrangeArrayList.size()];
                    for (int l = 0; l < higherMidrangeArrayList.size(); ++l) {
                        p[l] = higherMidrangeArrayList.get(l);
                    }
                    //temp = System.currentTimeMillis();
                    higherMidrangeTotal += AudioScanner.getHigherMidrange(p);
                    //ext += (System.currentTimeMillis() - temp);
                    higherMidrangeArrayList.clear();
                    ++higherMidrangeCount;
                }
                higherMidrangeArrayList.add(shorts[i]);
                if (presenceArrayList.size() >= presenceN) {
                    short[] p = new short[presenceArrayList.size()];
                    for (int l = 0; l < presenceArrayList.size(); ++l) {
                        p[l] = presenceArrayList.get(l);
                    }
                    //temp = System.currentTimeMillis();
                    presenceTotal += AudioScanner.getPresence(p);
                    //ext += (System.currentTimeMillis() - temp);
                    presenceArrayList.clear();
                    ++presenceCount;
                }
                presenceArrayList.add(shorts[i]);
                if (brillianceArrayList.size() >= brillianceN) {
                    short[] p = new short[brillianceArrayList.size()];
                    for (int l = 0; l < brillianceArrayList.size(); ++l) {
                        p[l] = brillianceArrayList.get(l);
                    }
                    //temp = System.currentTimeMillis();
                    brillianceTotal += AudioScanner.getBrilliance(p);
                    //ext += (System.currentTimeMillis() - temp);
                    brillianceArrayList.clear();
                    ++brillianceCount;
                }
                brillianceArrayList.add(shorts[i]);
            }
        }
    }

    public void finalizeExecution(){

        if (subBassCount > 0) {
            floatSounds[0] = (float) (subBassTotal / (subBassCount * 1000));
            floatSounds[1] = (float) (bassTotal / (subBassCount * 1000));
            floatSounds[2] = (float) (lowerMidrangeTotal / (subBassCount * 1000));
            floatSounds[3] = (float) (midrangeTotal / (subBassCount * 1000));
            floatSounds[4] = (float) (higherMidrangeTotal / (subBassCount * 1000));
            floatSounds[5] = (float) (presenceTotal / (subBassCount * 1000));
            floatSounds[6] = (float) (brillianceTotal / (subBassCount * 1000));

            //System.out.println("!!!!!!!!!!!TimeTaken : " + (System.currentTimeMillis() - start) + " : " + ext);
            //System.out.println("!!!!!!!!!!!SampleRate: " + sampleRate);
            //System.out.println("!!!!!!!!!!!Frequencies : " + Arrays.toString(floatSounds));
        }
        done = true;
    }
}
