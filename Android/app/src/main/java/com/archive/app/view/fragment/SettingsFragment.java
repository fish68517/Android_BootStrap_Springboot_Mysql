package com.archive.app.view.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.archive.app.R;

public class SettingsFragment extends Fragment {

    public static final String PREFS_NAME = "VoiceSettings";
    public static final String KEY_VOICE_THEME_POSITION = "voice_theme_position";
    private static final String KEY_VOLUME = "volume";
    private static final String KEY_SPEED = "speed";

    private Spinner spinnerVoiceTheme;
    private SeekBar seekbarVolume;
    private SeekBar seekbarSpeed;

    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        spinnerVoiceTheme = view.findViewById(R.id.spinner_voice_theme);
        seekbarVolume = view.findViewById(R.id.seekbar_volume);
        seekbarSpeed = view.findViewById(R.id.seekbar_speed);

        setupSpinner();
        setupSeekBars();

        loadSettings();
    }

    private void setupSpinner() {
        // ArrayAdapter a = new ArrayAdapter(this, R.layout.simple_spinner_item, spinneryear);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.voice_themes));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVoiceTheme.setAdapter(adapter);

        spinnerVoiceTheme.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                saveInt(KEY_VOICE_THEME_POSITION, position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setupSeekBars() {
        seekbarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                saveInt(KEY_VOLUME, seekBar.getProgress());
            }
        });

        seekbarSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                saveInt(KEY_SPEED, seekBar.getProgress());
            }
        });
    }

    private void loadSettings() {
        int voiceThemePosition = sharedPreferences.getInt(KEY_VOICE_THEME_POSITION, 0);
        int volume = sharedPreferences.getInt(KEY_VOLUME, 80);
        int speed = sharedPreferences.getInt(KEY_SPEED, 50);

        spinnerVoiceTheme.setSelection(voiceThemePosition);
        seekbarVolume.setProgress(volume);
        seekbarSpeed.setProgress(speed);
    }

    private void saveInt(String key, int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }
} 