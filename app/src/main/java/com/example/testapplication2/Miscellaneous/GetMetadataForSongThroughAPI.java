package com.example.testapplication2.Miscellaneous;

import static android.content.Context.MODE_PRIVATE;
import static com.example.testapplication2.MainActivity.queueObject;
import static com.example.testapplication2.OtherClasses.APIKeys.RapidAPIKey;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.testapplication2.OtherClasses.Methods;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Base64;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.SampleBuffer;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GetMetadataForSongThroughAPI {

    MediaExtractor mediaExtractor;
    MediaFormat mediaFormat;
    MediaCodec decoder;

    int channelCount;
    int sampleRate;
    long duration;
    String mimeType;

    public boolean done=false;
    int startSecond;


    public GetMetadataForSongThroughAPI(String fileName, int startSecond){
        mediaExtractor = new MediaExtractor();
        this.startSecond=startSecond;
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

        System.out.println("Sample Rate!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1 " + sampleRate);

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

                while (frameHeader != null && !done) {
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
            if (done) return;
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
            if (done)
            {
                codec.stop();
                codec.release();
                mediaExtractor.release();
                finalizeExecution();
                return;
            }

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

    int seconds = 5;//8;
    ArrayList<Short> monoAudio = new ArrayList<>(sampleRate*seconds);
    float startCount=0;
    boolean isBeyondStartSecond = false;
    int dif=0;
    public void processSamples(short[] shorts){
        if (startCount<=startSecond) {
            dif = (int) (startSecond - startCount);
            startCount += ((float) shorts.length) / (channelCount * sampleRate);
        }else {
            int i=0;
            if (!isBeyondStartSecond){
                i = dif*channelCount*sampleRate;
            }
            isBeyondStartSecond=true;
            for (; i < shorts.length; ++i) {
                if (i % channelCount == 0) {
                    monoAudio.add(shorts[i]);
                    if (monoAudio.size() >= sampleRate * seconds) {
                        done = true;
                        return;
                    }
                }
            }
        }
    }

    short[] audioShorts = new short[44100*seconds];
    float time=0;
    float adjustedTime=0;
    public void adjustForSampleRate(){
        for (int i=0;i<audioShorts.length;++i){
            time = ((float) i)/44100;
            int o=0;
            if (time*sampleRate==(int) time*sampleRate){
                o=(int) (time*sampleRate);
            }else {
                o=(int) (time*sampleRate +1);
            }
            adjustedTime = time - ((float) (o-1))/sampleRate;
            if (o==0){
                audioShorts[i]=monoAudio.get(o);
            }else if (o>=monoAudio.size()){
                audioShorts[i] = (short) (((-monoAudio.get(o - 1))*sampleRate)*adjustedTime + monoAudio.get(o-1));
            }else {
                audioShorts[i] = (short) (((monoAudio.get(o)-monoAudio.get(o-1))*sampleRate)*adjustedTime + monoAudio.get(o-1));
            }
        }
    }

    public String getMetaData() {


        /*AudioTrack player = new AudioTrack.Builder()
                .setAudioAttributes(new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build())
                .setAudioFormat(new AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(44100)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build())
                .setBufferSizeInBytes(AudioTrack.getMinBufferSize(44100, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT))
                .build();

        player.play();
        player.write(audioShorts,0,audioShorts.length);*/
        byte[] bytesToSend = new byte[audioShorts.length*2];
        short x;
        for (int i=0;i<audioShorts.length;++i){
            x  = audioShorts[i];
            bytesToSend[i*2] = (byte)(x & 0xff);
            bytesToSend[i*2+1] = (byte)((x >> 8) & 0xff);
        }

        String base64Str = Base64.getEncoder().encodeToString(bytesToSend);

        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, base64Str);
        Request request = new Request.Builder()
                .url("https://shazam.p.rapidapi.com/songs/detect")
                .post(body)
                .addHeader("content-type", "text/plain")
                .addHeader("X-RapidAPI-Key", RapidAPIKey)
                .addHeader("X-RapidAPI-Host", "shazam.p.rapidapi.com")
                .build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        //return "";

    }

    public void finalizeExecution(){
        done = true;
    }
}
