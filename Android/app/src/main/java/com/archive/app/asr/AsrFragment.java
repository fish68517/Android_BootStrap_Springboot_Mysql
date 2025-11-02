package com.archive.app.asr;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.archive.app.R;
import com.archive.app.db.ScheduleContract;
import com.archive.app.db.ScheduleDbHelper;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.XXPermissions;
import com.iflytek.sparkchain.core.asr.ASR;
import com.iflytek.sparkchain.core.asr.AsrCallbacks;
import com.iflytek.sparkchain.core.asr.Segment;
import com.iflytek.sparkchain.core.asr.Transcription;
import com.iflytek.sparkchain.core.asr.Vad;

import java.io.FileInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

public class AsrFragment extends Fragment implements View.OnClickListener, AudioRecorderManager.AudioDataCallback {

    private static final String TAG = "AEELog";
    private Spinner sp_language;
    private TextView tv_result;
    private Button btn_audio_start, btn_file_start;
    private String language = "zh_cn";
    private ASR mAsr = null;
    private boolean isrun = false;
    private boolean isdws = false;
    private String startMode = "NONE";
    private String cacheInfo = "";
    private AudioRecorderManager audioRecorderManager;
    private AtomicBoolean isWrite = new AtomicBoolean(false);
    private ScheduleDbHelper dbHelper;

    private final AsrCallbacks mAsrCallbacks = new AsrCallbacks() {
        @Override
        public void onResult(ASR.ASRResult asrResult, Object o) {
            Log.e(TAG, "result:" + asrResult.getStatus());
            int begin = asrResult.getBegin();
            int end = asrResult.getEnd();
            int status = asrResult.getStatus();
            String result = asrResult.getBestMatchText();
            String sid = asrResult.getSid();

            List<Vad> vads = asrResult.getVads();
            List<Transcription> transcriptions = asrResult.getTranscriptions();
            int vad_begin = -1;
            int vad_end = -1;
            String word = null;
            for (Vad vad : vads) {
                vad_begin = vad.getBegin();
                vad_end = vad.getEnd();
                Log.d(TAG, "vad={begin:" + vad_begin + ",end:" + vad_end + "}");
            }
            for (Transcription transcription : transcriptions) {
                List<Segment> segments = transcription.getSegments();
                for (Segment segment : segments) {
                    word = segment.getText();
                }
            }
            String info = "result={begin:" + begin + ",end:" + end + ",status:" + status + ",result:" + result + ",sid:" + sid + "}";
            Log.d(TAG, info);

            if (status == 0) {
                if (isdws) {
                    cacheInfo = tv_result.getText().toString();
                    updateUi(() -> tv_result.setText(cacheInfo + "日程安排：" + result));
                }
            } else if (status == 2) {
                if (isdws) {
                    updateUi(() -> tv_result.setText(cacheInfo + "日程安排：" + result + "\n"));
                } else {
                    showInfo(result + "\n");
                }
                parseAndSaveSchedule(result);
                stopAsr();
            } else {
                if (isdws) {
                    updateUi(() -> tv_result.setText(cacheInfo + "日程安排：" + result));
                }
            }
            toend();
        }

        @Override
        public void onError(ASR.ASRError asrError, Object o) {
            int code = asrError.getCode();
            String msg = asrError.getErrMsg();
            String sid = asrError.getSid();
            String info = "error={code:" + code + ",msg:" + msg + ",sid:" + sid + "}";
            Log.d(TAG, info);
            showInfo("识别出错!错误码：" + code + ",错误信息：" + msg + ",sid:" + sid + "\n");
            updateUi(() -> {
                btn_audio_start.setText("麦克风识别");
                btn_audio_start.setEnabled(true);
                btn_file_start.setEnabled(true);
            });
            isrun = false;
        }

        @Override
        public void onBeginOfSpeech() {}

        @Override
        public void onEndOfSpeech() {}
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ai_asr, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dbHelper = new ScheduleDbHelper(requireContext());
        // 初始化 ASR（可以根据需要决定是否在这里初始化）
        initASR();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        dbHelper.close();
        // 清理资源，避免内存泄漏
        stopAsr();
        if (mAsr != null) {

            mAsr = null;
        }
        if (audioRecorderManager != null) {
            audioRecorderManager.stopRecord();

            audioRecorderManager = null;
        }
    }

    private void initASR() {
        if (mAsr == null) {
            mAsr = new ASR();
            mAsr.registerCallbacks(mAsrCallbacks);
        }
    }

    private void parseAndSaveSchedule(String result) {
        if (result == null || result.isEmpty()) {
            return;
        }

        String title = "";
        String location = null;
        long startTime = 0;

        int timeIndex = result.indexOf("时间");
        int locationIndex = result.indexOf("地点");

        if (timeIndex != -1 && locationIndex != -1) {
            if (timeIndex < locationIndex) {
                title = result.substring(0, timeIndex).trim();
                String timeStr = result.substring(timeIndex + 2, locationIndex).trim();
                startTime = parseTime(timeStr);
                location = result.substring(locationIndex + 2).trim();
            } else { // location comes first
                title = result.substring(0, locationIndex).trim();
                location = result.substring(locationIndex + 2, timeIndex).trim();
                String timeStr = result.substring(timeIndex + 2).trim();
                startTime = parseTime(timeStr);
            }
        } else if (timeIndex != -1) {
            title = result.substring(0, timeIndex).trim();
            String timeStr = result.substring(timeIndex + 2).trim();
            startTime = parseTime(timeStr);
        } else if (locationIndex != -1) {
            title = result.substring(0, locationIndex).trim();
            location = result.substring(locationIndex + 2).trim();
        } else {
            title = result.trim();
        }

        if (title.isEmpty()) {
            showInfo("无法识别日程标题，保存失败。\n");
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ScheduleContract.ScheduleEntry.COLUMN_NAME_TITLE, title);
        if (startTime > 0) {
            values.put(ScheduleContract.ScheduleEntry.COLUMN_NAME_START_TIME, startTime);
        }
        if (location != null && !location.isEmpty()) {
            values.put(ScheduleContract.ScheduleEntry.COLUMN_NAME_LOCATION, location);
        }
        values.put(ScheduleContract.ScheduleEntry.COLUMN_NAME_IS_COMPLETED, 0);
        values.put(ScheduleContract.ScheduleEntry.COLUMN_NAME_IS_SYNCED, 0);


        long newRowId = db.insert(ScheduleContract.ScheduleEntry.TABLE_NAME, null, values);

        if (newRowId != -1) {
            showInfo("日程已保存: " + title + "\n");
        } else {
            showInfo("日程保存失败。\n");
        }
    }

    private long parseTime(String timeStr) {
        // 支持 "yyyy-MM-dd HH:mm" 格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        try {
            Date date = sdf.parse(timeStr);
            return date.getTime();
        } catch (ParseException e) {
            Log.e(TAG, "Time parsing failed for: " + timeStr, e);
            // 您可以在此处添加对其他时间格式的支持，例如 "明天下午3点"
            // 目前，如果解析失败，我们将返回0
            return 0;
        }
    }

    int count = 0;

    private void runAsr_file() {
        if (isrun) {
            showInfo("正在识别中，请勿重复开启。\n");
            return;
        }
        if (mAsr == null) {
            initASR();
        }
        updateUi(() -> {
            btn_audio_start.setText("录音中\n");
            btn_audio_start.setEnabled(false);
            btn_file_start.setEnabled(false);
        });
        isdws = false;
        mAsr.language(language);
        mAsr.domain("iat");
        mAsr.accent("mandarin");
        mAsr.vinfo(true);
        if ("zh_cn".equals(language)) {
            mAsr.dwa("wpgs");
            isdws = true;
        }

        count++;
        int ret = mAsr.start(count + "");
        if (ret == 0) {
            isrun = true;
            write();
        } else {
            updateUi(() -> {
                btn_audio_start.setText("麦克风识别");
                btn_audio_start.setEnabled(true);
                btn_file_start.setEnabled(true);
            });
            isrun = false;
            showInfo("识别开启失败，错误码:" + ret + "\n");
        }
    }

    private void runAsr_Audio() {
        if (isrun) {
            showInfo("正在识别中，请勿重复开启。\n");
            return;
        }
        if (mAsr == null) {
            initASR();
        }
        updateUi(() -> {
          //  showInfo("已开启录音，说完后请点击停止识别，即可获得最终结果\n");

            btn_audio_start.setText("录音中\n");
            btn_audio_start.setEnabled(false);
            btn_file_start.setEnabled(false);
        });
        isdws = false;
        mAsr.language(language);
        mAsr.domain("iat");
        mAsr.accent("mandarin");
        mAsr.vinfo(true);
        if ("zh_cn".equals(language)) {
            mAsr.dwa("wpgs");
            isdws = true;
        }

        count++;
        int ret = mAsr.start(count + "");
        if (ret != 0) {
            updateUi(() -> {
                btn_audio_start.setText("麦克风识别");
                btn_audio_start.setEnabled(true);
                btn_file_start.setEnabled(true);
            });
            isrun = false;
            showInfo("识别开启失败，错误码:" + ret + "\n");
        } else {
            isrun = true;
            isWrite.set(true);
            if (audioRecorderManager == null) {
                audioRecorderManager = AudioRecorderManager.getInstance();
            }
            audioRecorderManager.startRecord();
            audioRecorderManager.registerCallBack(this);
        }
    }

    private String getAudioPath() {
        String path = "";
        showInfo("选择的语种:" + language + "\n");
        switch (language) {
            case "zh_cn":
                path = "/sdcard/iflytek/asr/cn.pcm";
                break;
            case "en_us":
                path = "/sdcard/iflytek/asr/en.pcm";
                break;
        }
        return path;
    }

    private void write() {
        String filePath = getAudioPath();
        showInfo("识别音频路径:" + filePath + "\n");
        showInfo("正在识别中，请稍等...\n");
        try {
            FileInputStream fs = new FileInputStream(filePath);
            byte[] buffer = new byte[1280];
            int len;
            while (-1 != (len = fs.read(buffer))) {
                if (!isrun) {
                    break;
                }
                if (len > 0) {
                    mAsr.write(buffer.clone());
                    Thread.sleep(40);
                }
            }
            fs.close();
            Thread.sleep(10);
            mAsr.stop(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopAsr() {
        if (isrun) {
            if ("AUDIO".equals(startMode)) {
                if (mAsr != null) {
                    if (audioRecorderManager != null) {
                        audioRecorderManager.stopRecord();
                        audioRecorderManager = null;
                    }
                    mAsr.stop(false);
                  //  showInfo("\n已停止录音。\n");
                }
            } else {
                if (mAsr != null) {
                    mAsr.stop(true);
                   // showInfo("\n已停止识别。\n");
                }
            }
            startMode = "NONE";
            updateUi(() -> {
                btn_audio_start.setText("麦克风识别");
                btn_audio_start.setEnabled(true);
                btn_file_start.setEnabled(true);
            });
            isrun = false;
        } else {
          //  showInfo("\n已停止识别。\n");
        }
    }

    private void initView(View view) {
        btn_file_start = view.findViewById(R.id.ai_asr_file_start_btn);
        btn_audio_start = view.findViewById(R.id.ai_asr_audio_start_btn);
        btn_file_start.setOnClickListener(this);
        btn_audio_start.setOnClickListener(this);
        view.findViewById(R.id.ai_asr_stop_btn).setOnClickListener(this);
        sp_language = view.findViewById(R.id.ai_asr_language);
        tv_result = view.findViewById(R.id.ai_asr_result);
        tv_result.setMovementMethod(new ScrollingMovementMethod());
        SpinnerAdapter languageSpinner = new SpinnerAdapter(requireContext(), asrParams.getLanguage());
        sp_language.setAdapter(languageSpinner);
        sp_language.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                language = asrParams.getLanguage().get(position).value;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void getPermission() {
        XXPermissions.with(getActivity()).permission("android.permission.RECORD_AUDIO").request(new OnPermission() {
            @Override
            public void hasPermission(List<String> granted, boolean all) {
                Log.d(TAG, "SDK获取系统权限成功:" + all);
                for (String permission : granted) {
                    Log.d(TAG, "获取到的权限有：" + permission);
                }
                if (all) {
                    runAsr_Audio();
                }
            }

            @Override
            public void noPermission(List<String> denied, boolean quick) {
                if (quick) {
                    Log.e(TAG, "onDenied:被永久拒绝授权，请手动授予权限");
                    XXPermissions.startPermissionActivity(requireContext(), denied);
                } else {
                    Log.e(TAG, "onDenied:权限获取失败");
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        Log.d(TAG, "onClick id:" + id);
        Log.d(TAG, "onClick id 22 :" + R.id.ai_asr_file_start_btn);
        if (id == R.id.ai_asr_audio_start_btn) {
            startMode = "AUDIO";
            getPermission();
        } else if (id == R.id.ai_asr_stop_btn) {
            stopAsr();
        }

    }

    private void showInfo(String text) {
        updateUi(() -> tv_result.append(text));
    }

    private void toend() {
        if (tv_result.getLayout() != null) {
            int scrollAmount = tv_result.getLayout().getLineTop(tv_result.getLineCount()) - tv_result.getHeight();
            if (scrollAmount > 0) {
                tv_result.scrollTo(0, scrollAmount + 10);
            }
        }
    }

    @Override
    public void onAudioData(byte[] data, int size) {
        if (isWrite.get()) {
            int ret = mAsr.write(data);
            if (ret != 0) {
                isWrite.set(false);
            }
        }
    }

    @Override
    public void onAudioVolume(double db, int volume) {}

    private void updateUi(Runnable runnable) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(runnable);
        }
    }
}