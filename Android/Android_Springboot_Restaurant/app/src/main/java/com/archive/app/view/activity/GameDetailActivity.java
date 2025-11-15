// --- START OF REFACTORED FILE GameDetailActivity.java ---

package com.archive.app.view.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair; // 使用 Android 内置的 Pair 类来组合数据
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.archive.app.ApiService;
import com.archive.app.MyApplication;
import com.archive.app.R;
import com.archive.app.RetrofitClient;
import com.archive.app.adapter.CommentAdapter;
import com.archive.app.model.dto.CommentPostDTO;
import com.archive.app.model.dto.FavoriteRequestDTO;
import com.archive.app.model.entity.Comment;
import com.archive.app.model.entity.Game;
import com.archive.app.model.response.ApiResponse;
import com.archive.app.util.ImageLoaderUtil;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.button.MaterialButton;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class GameDetailActivity extends AppCompatActivity {

    // ... UI 控件声明 (与之前相同) ...
    private ImageView ivGameCover;
    private ImageView gameIcon;
    private TextView tvGameName, tvGameCategory, tvGameDescription;
    private RatingBar ratingBar;
    private MaterialButton btnFavorite, btnSubmitComment;
    private EditText etComment;
    private RecyclerView rvComments;
    private LinearLayout layoutEmptyComments;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;


    private int gameId;
    private int userId;
    private ApiService apiService;
    private CommentAdapter commentAdapter;


    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private boolean curIsFavorite;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_detail);

        apiService = RetrofitClient.getMainApiService();
        bindViews();
        setupToolbar();

        gameId = getIntent().getIntExtra("GAME_ID", 0);
        userId = MyApplication.curUser.getUserId();

        getGameFavoriteStatus();

        if (gameId != 0) {
            // 使用 RxJava 加载数据
            loadDataWithRxJava();
        } else {
            Toast.makeText(this, "无效的游戏ID", Toast.LENGTH_SHORT).show();
            finish();
        }

        setupClickListeners();
    }

    private void getGameFavoriteStatus() {
        apiService.checkFavorite(gameId, userId).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                Log.d("RxJava", "Received game favorite status: " + response.code());
                if (response.isSuccessful()) {
                    Boolean isFavorite = response.body();
                    Log.d("RxJava", "Game favorite status: " + isFavorite);
                    if (isFavorite) {
                        btnFavorite.setChecked( true);
                        btnFavorite.setIconResource(R.drawable.ic_favorite_filled);
                        curIsFavorite = true;
                    } else {
                        btnFavorite.setChecked( false);
                        btnFavorite.setIconResource(R.drawable.ic_favorite);
                    }

                } else {
                    btnFavorite.setIconResource(R.drawable.ic_favorite);
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e("RxJavaError", "Failed to get game favorite status: " + t.getMessage());

            }
        });
    }

    /**
     * 新增：使用 RxJava 的 zip 操作符并行请求数据
     */
    private void loadDataWithRxJava() {
        // 1. 创建两个独立的 Single 请求源
        Single<Game> gameDetailSource = apiService.getGameDetail(gameId)
                .onErrorReturn(throwable -> {
                    // 你可以在这里打印日志来追踪错误
                    Log.e("RxJavaError", "Failed to get game details: " + throwable.getMessage());
                    return null; // 返回 null 作为备用值
                });


        Single<List<Comment>> commentsSource = apiService.getComments(gameId)
                // 如果评论请求失败，返回一个空列表
                .onErrorReturn(throwable -> {
                    // 你可以在这里打印日志来追踪错误
                    Log.e("RxJavaError", "Failed to get Comment details: " + throwable.getMessage());
                    return null; // 返回 null 作为备用值
                });

        // 2. 使用 Single.zip 将两个请求组合起来
        compositeDisposable.add( // 将订阅添加到 CompositeDisposable 中进行管理
                Single.zip(gameDetailSource, commentsSource, Pair::new) // 方法引用 Pair::new 等同于 (game, comments) -> new Pair<>(game, comments)
                        .subscribeOn(Schedulers.io()) // 指定网络请求在 IO 线程执行
                        .observeOn(AndroidSchedulers.mainThread()) // 指定结果在主线程（UI线程）处理
                        .subscribe(
                                // OnSuccess: 两个请求都成功（或已处理错误）后，会进入这里
                                gameAndCommentsPair -> {
                                    Game game = gameAndCommentsPair.first;
                                    List<Comment> comments = gameAndCommentsPair.second;

                                    Iterator<Comment> iterator = comments.iterator();
                                    while (iterator.hasNext()) {
                                        Comment comment = (Comment) iterator.next();
                                        if (comment.getStatus() == null || !Objects.equals(comment.getStatus(), "approved")) {
                                            iterator.remove();
                                        }
                                    }

                                    if (game != null) {
                                        updateUi(game, comments);
                                    } else {
                                        Toast.makeText(this, "加载游戏数据失败，请重试", Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                },
                                // OnError: 如果 zip 过程中发生未经处理的错误，会进入这里
                                throwable -> {
                                    Toast.makeText(this, "发生未知错误: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                        )
        );
    }


    /**
     * 修改：updateUi 方法现在接收两个参数
     */
    private void updateUi(Game game, List<Comment> comments) {
        collapsingToolbarLayout.setTitle(game.getTitle());
        ImageLoaderUtil.loadImage(this, game.getCoverImageUrl(), ivGameCover);
        ImageLoaderUtil.loadImage(this, game.getCoverImageUrl(), gameIcon);
        tvGameName.setText(game.getTitle());
        tvGameCategory.setText(game.getCategory());
        float randomRating = (float) (Math.random() * 4 + 1);
        ratingBar.setRating(randomRating);
        tvGameDescription.setText(game.getDescription());

        setupCommentsSection(comments);
    }

    private void setupCommentsSection(List<Comment> comments) {
        if (comments != null && !comments.isEmpty()) {
            rvComments.setVisibility(View.VISIBLE);
            layoutEmptyComments.setVisibility(View.GONE);
            rvComments.setLayoutManager(new LinearLayoutManager(this));
            commentAdapter = new CommentAdapter(comments);
            rvComments.setAdapter(commentAdapter);
        } else {
            rvComments.setVisibility(View.GONE);
            layoutEmptyComments.setVisibility(View.VISIBLE);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 在 Activity 销毁时，取消所有正在进行的网络请求，防止内存泄漏
        compositeDisposable.clear();
    }


    // --- bindViews, setupToolbar, setupClickListeners, onOptionsItemSelected 方法保持不变 ---
    private void bindViews() {
        toolbar = findViewById(R.id.toolbar);
        collapsingToolbarLayout = findViewById(R.id.toolbar_layout);
        ivGameCover = findViewById(R.id.iv_game_cover);
        tvGameName = findViewById(R.id.tv_game_name);
        gameIcon = findViewById(R.id.iv_game_icon);
        ratingBar = findViewById(R.id.rating_bar);
        tvGameCategory = findViewById(R.id.tv_game_category);
        btnFavorite = findViewById(R.id.btn_favorite);
        tvGameDescription = findViewById(R.id.tv_game_description);
        etComment = findViewById(R.id.et_comment);
        btnSubmitComment = findViewById(R.id.btn_submit_comment);
        rvComments = findViewById(R.id.rv_comments);
        layoutEmptyComments = findViewById(R.id.layout_empty_comments);
    }
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }
    private void setupClickListeners() {
        int userId = MyApplication.curUser.getUserId();

        btnFavorite.setOnClickListener(v -> {
            // 获取 icon id
            if ( !curIsFavorite) {
                apiService.addFavorite(new FavoriteRequestDTO(gameId,userId)).enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                        if (response.isSuccessful()) {
                            btnFavorite.setIconResource(R.drawable.ic_favorite_filled);
                            curIsFavorite = true;
                            Toast.makeText(GameDetailActivity.this, "收藏成功", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(GameDetailActivity.this, "收藏失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {

                    }
                });
            } else {
                apiService.removeFavorite(new FavoriteRequestDTO(gameId,userId)).enqueue(new Callback<ApiResponse>() {

                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        if (response.isSuccessful()) {
                            curIsFavorite = false;
                            Toast.makeText(GameDetailActivity.this, "取消收藏成功", Toast.LENGTH_SHORT).show();
                            btnFavorite.setIconResource(R.drawable.ic_favorite);
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {

                    }
                });
            }




        });
        btnSubmitComment.setOnClickListener(v -> {
            String commentText = etComment.getText().toString().trim();
            if (commentText.isEmpty()) {
                Toast.makeText(this, "评论内容不能为空", Toast.LENGTH_SHORT).show();
                return;
            }

            apiService.postComment(new CommentPostDTO(gameId, userId,commentText)).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                    if (response.isSuccessful()) {
                        Toast.makeText(GameDetailActivity.this, "评论成功: "
                                + commentText, Toast.LENGTH_SHORT).show();
                        etComment.setText("");
                    } else {
                        Log.e("GameDetailActivity", "onResponse: " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    Log.e("GameDetailActivity", "onFailure: " + t.getMessage());

                }
            });

        });
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
