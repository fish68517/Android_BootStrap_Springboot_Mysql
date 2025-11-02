package com.archive.app.view.fragment;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;


import com.archive.app.MyApplication;
import com.archive.app.RetrofitClient;
import com.archive.app.model.UploadAvatarResponse;
import com.archive.app.model.User;

import com.archive.app.view.activity.UserInfoEditActivity;
import com.bumptech.glide.Glide;
import com.archive.app.R;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private CircleImageView profileImage;
    private TextView profileNickname, profileUserId;
    private ProgressBar progressBar;
    private View btnEditProfile, btnMyOrders, btnAbout;

    // Hardcoded user ID for demonstration. In a real app, you'd get this after login.
    private static final String MOCK_USER_ID = String.valueOf(MyApplication.curUser.getId());

    private ActivityResultLauncher<String> galleryLauncher;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Launcher for picking image from gallery
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                uploadImage(uri);
            }
        });

        // Launcher for requesting permission
        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                openGallery();
            } else {
                Toast.makeText(getContext(), "需要权限才能访问相册", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        profileImage = view.findViewById(R.id.profile_image);
        profileNickname = view.findViewById(R.id.profile_nickname);
        profileUserId = view.findViewById(R.id.profile_userid);
        progressBar = view.findViewById(R.id.progressBar);
        btnEditProfile = view.findViewById(R.id.btn_edit_profile);
        btnMyOrders = view.findViewById(R.id.btn_my_orders);
        btnAbout = view.findViewById(R.id.btn_about);

        setupClickListeners();
        fetchUserInfo(MOCK_USER_ID);
    }

    private void setupClickListeners() {
        profileImage.setOnClickListener(v -> {
            checkPermissionAndOpenGallery();
        });
        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), UserInfoEditActivity.class);
            intent.putExtra("USER_ID", MOCK_USER_ID);
            startActivity(intent);
        });



    }

    private void fetchUserInfo(String userId) {
        progressBar.setVisibility(View.VISIBLE);

    }

    private void updateUI(User user) {
        profileNickname.setText("用户名：" + user.getUsername());
        profileUserId.setText("ID: " + user.getId());

        if (getContext() != null) {
            Glide.with(getContext())
                    .load(user.getAvatar())
                    .placeholder(R.drawable.avatar)
                    .error(R.drawable.avatar)
                    .into(profileImage);
        }
    }

    private void checkPermissionAndOpenGallery() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    private void openGallery() {
        galleryLauncher.launch("image/*");
    }

    private void uploadImage(Uri imageUri) {
        String filePath = getPathFromUri(getContext(), imageUri);
        if (filePath == null) {
            Toast.makeText(getContext(), "无法获取图片路径", Toast.LENGTH_SHORT).show();
            return;
        }

        File file = new File(filePath);
        String mimeType = getMimeType(imageUri);
        if (mimeType == null) {
            Toast.makeText(getContext(), "无法确定文件类型", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("avatar", file.getName(), requestFile);
        RequestBody userIdBody = RequestBody.create(MediaType.parse("text/plain"), MOCK_USER_ID);

        progressBar.setVisibility(View.VISIBLE);

    }

    public static String getPathFromUri(final Context context, final Uri uri) {
        if (context == null || uri == null) return null;
        // DocumentProvider
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT && android.provider.DocumentsContract.isDocumentUri(context, uri)) {
            if ("com.android.externalstorage.documents".equals(uri.getAuthority())) {
                final String docId = android.provider.DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return android.os.Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                final String id = android.provider.DocumentsContract.getDocumentId(uri);
                final Uri contentUri = android.content.ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                final String docId = android.provider.DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    private String getMimeType(Uri uri) {
        String mimeType;
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            ContentResolver cr = getContext().getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase());
        }
        return mimeType;
    }
} 