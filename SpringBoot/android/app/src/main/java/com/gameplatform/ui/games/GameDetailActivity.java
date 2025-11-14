package com.gameplatform.ui.games;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gameplatform.R;
import com.gameplatform.contract.GameDetailContract;
import com.gameplatform.model.Comment;
import com.gameplatform.model.Game;
import com.gameplatform.presenter.GameDetailPresenter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class GameDetailActivity extends AppCompatActivity implements GameDetailContract.View {
    
    public static final String EXTRA_GAME_ID = "game_id";
    public static final String EXTRA_GAME = "game";
    
    private GameDetailContract.Presenter presenter;
    private CommentAdapter commentAdapter;
    
    // Views
    private ImageView ivGameCover;
    private ImageView ivGameIcon;
    private TextView tvGameName;
    private RatingBar ratingBar;
    private TextView tvGameCategory;
    private MaterialButton btnFavorite;
    private TextView tvGameDescription;
    private TextInputEditText etComment;
    private MaterialButton btnSubmitComment;
    private RecyclerView rvComments;
    private LinearLayout layoutEmptyComments;
    
    private Game currentGame;
    
    public static Intent newIntent(Context context, String gameId) {
        Intent intent = new Intent(context, GameDetailActivity.class);
        intent.putExtra(EXTRA_GAME_ID, gameId);
        return intent;
    }
    
    public static Intent newIntent(Context context, Game game) {
        Intent intent = new Intent(context, GameDetailActivity.class);
        intent.putExtra(EXTRA_GAME, game);
        return intent;
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_detail);
        
        initViews();
        setupToolbar();
        setupRecyclerView();
        setupClickListeners();
        
        presenter = new GameDetailPresenter(this);
        
        // Get game data from intent
        Game game = getIntent().getParcelableExtra(EXTRA_GAME);
        String gameId = getIntent().getStringExtra(EXTRA_GAME_ID);
        
        if (game != null) {
            currentGame = game;
            displayGameInfo(game);
            presenter.loadComments(game.getGameId());
        } else if (!TextUtils.isEmpty(gameId)) {
            presenter.loadGameDetail(gameId);
        } else {
            finish();
        }
    }
    
    private void initViews() {
        ivGameCover = findViewById(R.id.iv_game_cover);
        ivGameIcon = findViewById(R.id.iv_game_icon);
        tvGameName = findViewById(R.id.tv_game_name);
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
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }
    
    private void setupRecyclerView() {
        commentAdapter = new CommentAdapter();
        commentAdapter.setOnCommentLikeListener(new CommentAdapter.OnCommentLikeListener() {
            @Override
            public void onCommentLike(Comment comment, int position, boolean currentlyLiked) {
                presenter.likeCommentWithState(comment.getCommentId(), position, currentlyLiked);
            }
        });
        
        rvComments.setLayoutManager(new LinearLayoutManager(this));
        rvComments.setAdapter(commentAdapter);
    }
    
    private void setupClickListeners() {
        btnFavorite.setOnClickListener(v -> {
            if (currentGame != null) {
                presenter.toggleFavoriteWithCurrentState(currentGame.getGameId(), currentGame.isFavorited());
            }
        });
        
        btnSubmitComment.setOnClickListener(v -> {
            String content = etComment.getText().toString().trim();
            if (TextUtils.isEmpty(content)) {
                showError(getString(R.string.comment_empty));
                etComment.requestFocus();
                return;
            }
            
            if (currentGame != null) {
                // Disable button during submission
                btnSubmitComment.setEnabled(false);
                btnSubmitComment.setText("发表中...");
                presenter.submitComment(currentGame.getGameId(), content);
            }
        });
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void showGameDetail(Game game) {
        currentGame = game;
        displayGameInfo(game);
        presenter.loadComments(game.getGameId());
    }
    
    private void displayGameInfo(Game game) {
        // Load game cover image
        Glide.with(this)
                .load(game.getCoverImage())
                .placeholder(R.drawable.ic_game_placeholder)
                .error(R.drawable.ic_game_placeholder)
                .into(ivGameCover);
        
        // Load game icon
        Glide.with(this)
                .load(game.getIcon())
                .placeholder(R.drawable.ic_game_placeholder)
                .error(R.drawable.ic_game_placeholder)
                .into(ivGameIcon);
        
        tvGameName.setText(game.getName());
        ratingBar.setRating(game.getRating());
        tvGameCategory.setText(game.getCategory());
        tvGameDescription.setText(game.getDescription());
        
        updateFavoriteButton(game.isFavorited());
        
        // Set toolbar title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(game.getName());
        }
    }
    
    @Override
    public void showComments(List<Comment> comments) {
        commentAdapter.setComments(comments);
        
        // Show/hide empty state
        if (comments == null || comments.isEmpty()) {
            rvComments.setVisibility(View.GONE);
            layoutEmptyComments.setVisibility(View.VISIBLE);
        } else {
            rvComments.setVisibility(View.VISIBLE);
            layoutEmptyComments.setVisibility(View.GONE);
        }
    }
    
    @Override
    public void showCommentAdded(Comment comment) {
        commentAdapter.addComment(comment);
        etComment.setText("");
        
        // Re-enable submit button
        btnSubmitComment.setEnabled(true);
        btnSubmitComment.setText(getString(R.string.submit_comment));
        
        // Hide keyboard
        etComment.clearFocus();
        
        // Hide empty state and show comments list
        layoutEmptyComments.setVisibility(View.GONE);
        rvComments.setVisibility(View.VISIBLE);
        
        showSuccess(getString(R.string.comment_success));
        
        // Scroll to top to show the new comment
        rvComments.smoothScrollToPosition(0);
    }
    
    @Override
    public void showCommentLiked(int position, boolean isLiked, int likeCountChange) {
        commentAdapter.updateCommentLike(position, isLiked, likeCountChange);
        String message = isLiked ? getString(R.string.like_success) : getString(R.string.unlike_success);
        showSuccess(message);
    }
    
    @Override
    public void showFavoriteToggled(boolean isFavorited) {
        updateFavoriteButton(isFavorited);
        if (currentGame != null) {
            currentGame.setFavorited(isFavorited);
        }
        String message = isFavorited ? getString(R.string.favorite_success) : getString(R.string.unfavorite_success);
        showSuccess(message);
    }
    
    private void updateFavoriteButton(boolean isFavorited) {
        btnFavorite.setSelected(isFavorited);
        
        if (isFavorited) {
            btnFavorite.setText(getString(R.string.favorited));
            btnFavorite.setIcon(getDrawable(R.drawable.ic_favorite_filled));
            btnFavorite.setIconTint(getColorStateList(R.color.on_secondary));
            btnFavorite.setTextColor(getColor(R.color.on_secondary));
        } else {
            btnFavorite.setText(getString(R.string.add_to_favorites));
            btnFavorite.setIcon(getDrawable(R.drawable.ic_favorite_border));
            btnFavorite.setIconTint(getColorStateList(R.color.primary_color));
            btnFavorite.setTextColor(getColor(R.color.primary_color));
        }
        
        // Add a subtle animation
        btnFavorite.animate()
                .scaleX(0.9f)
                .scaleY(0.9f)
                .setDuration(100)
                .withEndAction(() -> {
                    btnFavorite.animate()
                            .scaleX(1.0f)
                            .scaleY(1.0f)
                            .setDuration(100)
                            .start();
                })
                .start();
    }
    
    @Override
    public void showLoading() {
        // Show loading indicator if needed
    }
    
    @Override
    public void hideLoading() {
        // Hide loading indicator if needed
    }
    
    @Override
    public void showError(String message) {
        // Re-enable submit button if it was disabled
        if (!btnSubmitComment.isEnabled()) {
            btnSubmitComment.setEnabled(true);
            btnSubmitComment.setText(getString(R.string.submit_comment));
        }
        
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void showSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.onDestroy();
        }
    }
}