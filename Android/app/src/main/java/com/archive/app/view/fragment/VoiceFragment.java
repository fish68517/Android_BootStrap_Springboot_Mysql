package com.archive.app.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.archive.app.R;

public class VoiceFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_voice, container, false);

        ImageButton voiceInputButton = view.findViewById(R.id.button_voice_input);
        voiceInputButton.setOnClickListener(v -> {
            Toast.makeText(getContext(), "开始语音识别...", Toast.LENGTH_SHORT).show();
            // 在这里集成语音识别逻辑
        });

        return view;
    }
} 