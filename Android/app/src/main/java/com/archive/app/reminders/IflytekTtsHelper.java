package com.archive.app.reminders;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import androidx.annotation.NonNull;

import com.iflytek.sparkchain.core.tts.OnlineTTS;
import com.iflytek.sparkchain.core.tts.TTS;
import com.iflytek.sparkchain.core.tts.TTSCallbacks;

public class IflytekTtsHelper {

    private static final String TAG = "IflytekTtsHelper yhw";
    private OnlineTTS mOnlineTTS;
    private AudioTrack audioTrack;
    private Handler mAudioPlayHandler;
    private boolean isPlaying = false;
    private Thread mAudioPlayThread;
    private TtsCompletionListener listener;

    public interface TtsCompletionListener {
        void onComplete();
        void onError(String error);
    }

    public IflytekTtsHelper() {
        Log.d(TAG, "Creating AudioPlayer thread.");
        startAudioThread();
    }

    public void speak(String text, TtsCompletionListener listener) {
        this.listener = listener;
        if (mAudioPlayHandler != null) {
            Log.d(TAG, "Sending message to AudioPlayer thread.");
            Message msg = mAudioPlayHandler.obtainMessage(AUDIOPLAYER_INIT);
            msg.obj = text;
            mAudioPlayHandler.sendMessage(msg);
        } else {
            // 如果Handler尚未初始化，则等待初始化完成后再发送消息
            Log.d(TAG, "Waiting for AudioPlayer thread to initialize.");
            waitForHandlerInitialization();
            if (mAudioPlayHandler != null) {
                Log.d(TAG, "Sending message to AudioPlayer thread.");
                Message msg = mAudioPlayHandler.obtainMessage(AUDIOPLAYER_INIT);
                msg.obj = text;
                mAudioPlayHandler.sendMessage(msg);
            } else {

                if (listener != null) {
                    listener.onError("Audio player thread not running.");
                }
            }
        }
    }

    private void waitForHandlerInitialization() {
        int waitCount = 0;
        // 等待最多1000毫秒*5，每次检查间隔10毫秒
        while (mAudioPlayHandler == null && waitCount < 100*5) {
            try {
                Log.d(TAG, "Waiting for AudioPlayer thread to initialize.： " + waitCount);
                Thread.sleep(10);
                waitCount++;
            } catch (InterruptedException e) {
                Log.e(TAG, "Waiting for handler initialization interrupted", e);
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void shutdown() {
        Log.d(TAG, "Shutting down AudioPlayer.");
        if (mOnlineTTS != null) {
            mOnlineTTS = null;
        }
        if (mAudioPlayThread != null) {
            // A Looper thread cannot be stopped traditionally.
            // Sending a final message to clean up and then interrupting might be an option,
            // but for a short-lived service, relying on process termination is often sufficient.
            mAudioPlayHandler.sendEmptyMessage(AUDIOPLAYER_END);
            mAudioPlayThread.interrupt();
            mAudioPlayThread = null;
        }
    }

    private void startiFlytekTts(String text) {
        Log.d(TAG, "Starting iFlytek TTS synthesis for text: " + text);

        Log.d(TAG, "isPlaying: " + isPlaying + ", audioTrack: " + audioTrack);
        if (audioTrack != null && isPlaying) {
            stopPlayback();
        }
        Log.d(TAG, "Sending AUDIOPLAYER_START message");
        mAudioPlayHandler.sendEmptyMessage(AUDIOPLAYER_START);

        mOnlineTTS = new OnlineTTS("x4_xiaoyan");
        mOnlineTTS.speed(50);
        mOnlineTTS.pitch(50);
        mOnlineTTS.volume(80);
        Log.d(TAG, "Registering TTS callbacks");
        mOnlineTTS.registerCallbacks(mTTSCallback);
        Log.d(TAG, "Running TTS synthesis");
        int ret = mOnlineTTS.aRun(text);
        Log.d(TAG, "TTS synthesis result: " + ret);
        if (ret != 0) {
            String errorMsg = "iFlytek TTS synthesis failed! ret=" + ret;
            Log.e(TAG, errorMsg);
            if (listener != null) {
                listener.onError(errorMsg);
            }
        } else {
            Log.d(TAG, "TTS synthesis started successfully");
        }
    }

    private void stopPlayback() {
        Log.d(TAG, "Stopping playback.");
        if (mAudioPlayHandler != null) {
            mAudioPlayHandler.removeCallbacksAndMessages(null);
            mAudioPlayHandler.sendEmptyMessage(AUDIOPLAYER_END);
        }
    }

    private final TTSCallbacks mTTSCallback = new TTSCallbacks() {
        @Override
        public void onResult(TTS.TTSResult result, Object o) {
            byte[] audio = result.getData();
            int status = result.getStatus();

            Log.d(TAG, "onResult called with status: " + status + ", audio data length: " + (audio != null ? audio.length : "null"));

            Bundle bundle = new Bundle();
            bundle.putByteArray("audio", audio);
            Message msg = mAudioPlayHandler.obtainMessage(AUDIOPLAYER_WRITE, bundle);
            mAudioPlayHandler.sendMessage(msg);

            if (status == 2) { // End of synthesis
                Log.d(TAG, "Synthesis end detected, sending AUDIOPLAYER_SYNTHESIS_END message");
                mAudioPlayHandler.sendEmptyMessage(AUDIOPLAYER_SYNTHESIS_END);
            }
        }

        @Override
        public void onError(TTS.TTSError ttsError, Object o) {
            String msg = "iFlytek TTS Error! code:" + ttsError.getCode() + ", msg:" + ttsError.getErrMsg();
            Log.e(TAG, msg);
            if (isPlaying) {
                stopPlayback();
            }
            if (listener != null) {
                listener.onError(msg);
            }
        }
    };

    private static final int AUDIOPLAYER_INIT = 0;
    private static final int AUDIOPLAYER_START = 1;
    private static final int AUDIOPLAYER_WRITE = 2;
    private static final int AUDIOPLAYER_END = 3;
    private static final int AUDIOPLAYER_SYNTHESIS_END = 4;
    private static final int SAMPLE_RATE = 16000;
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_OUT_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    private void startAudioThread() {
        Log.d(TAG, "Creating audio play thread");
        mAudioPlayThread = new Thread(() -> {
            Log.d(TAG, "Audio play thread started, preparing Looper");
            Looper.prepare();
            Log.d(TAG, "Creating Handler with thread Looper");
            mAudioPlayHandler = new Handler(Looper.myLooper()) {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    switch (msg.what) {
                        case AUDIOPLAYER_INIT:
                            Log.d(TAG, "AudioPlayer: INIT");
                            int minBufferSize = AudioTrack.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);
                            audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, minBufferSize, AudioTrack.MODE_STREAM);
                            startiFlytekTts((String) msg.obj);
                            break;
                        case AUDIOPLAYER_START:
                            Log.d(TAG, "AudioPlayer: START");
                            if (audioTrack != null) {
                                isPlaying = true;
                                audioTrack.play();
                            }
                            break;
                        case AUDIOPLAYER_WRITE:
                            Log.d(TAG, "AudioPlayer: WRITE");
                            Bundle bundle = (Bundle) msg.obj;
                            byte[] audioData = bundle.getByteArray("audio");
                            if (audioTrack != null && audioData != null && audioData.length > 0) {
                                audioTrack.write(audioData, 0, audioData.length);
                            }
                            break;
                        case AUDIOPLAYER_SYNTHESIS_END:
                            Log.d(TAG, "AudioPlayer: SYNTHESIS_END");
                            this.sendEmptyMessageDelayed(AUDIOPLAYER_END, 500); // Delay to allow buffer to play
                            break;
                        case AUDIOPLAYER_END:
                            Log.d(TAG, "AudioPlayer: END");
                            if (audioTrack != null) {
                                if (audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
                                    audioTrack.stop();
                                }
                                audioTrack.release();
                                audioTrack = null;
                            }
                            isPlaying = false;
                            if (listener != null) {
                                listener.onComplete();
                            }
                            Looper.myLooper().quit();
                            break;
                    }
                }
            };
            Log.d(TAG, "Starting Looper loop");
            Looper.loop();
            Log.d(TAG, "Looper loop ended");
        });
        mAudioPlayThread.start();
    }
} 