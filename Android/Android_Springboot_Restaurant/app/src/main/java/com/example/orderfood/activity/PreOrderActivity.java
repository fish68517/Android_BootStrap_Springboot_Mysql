package com.example.orderfood.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderfood.ApiService;
import com.example.orderfood.MyApplication;
import com.example.orderfood.R;
import com.example.orderfood.RetrofitClient;
import com.example.orderfood.adapter.CategoryAdapter;
import com.example.orderfood.adapter.DishAdapter;
import com.example.orderfood.adapter.StoreAdapter;
import com.example.orderfood.fragment.CartDialogFragment;
import com.example.orderfood.fragment.DishDetailDialogFragment;
import com.example.orderfood.model.CartManager;
import com.example.orderfood.model.Dish;
import com.example.orderfood.model.DishCategory;
import com.example.orderfood.model.Store;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PreOrderActivity extends AppCompatActivity implements
        CategoryAdapter.OnCategoryClickListener,
        DishAdapter.OnDishClickListener,
        CartManager.CartChangeListener {

    private AutoCompleteTextView storeSpinner;
    private RecyclerView categoryRecyclerView;
    private RecyclerView dishRecyclerView;
    private CategoryAdapter categoryAdapter;
    private DishAdapter dishAdapter;
    private List<Store> stores = new ArrayList<>();
    private List<DishCategory> categories = new ArrayList<>();
    private List<Dish> dishes = new ArrayList<>();
    private View cartBottomView;
    private TextView textViewTotalPrice;
    private TextView textViewCartCount;
    private CartManager cartManager;

    private ApiService apiService = RetrofitClient.getInstance().getApiService();
    private Boolean isDineIn;
    private Store selectedStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_order);
        isDineIn = getIntent().getBooleanExtra("isDineIn",false);

        cartManager = CartManager.getInstance();
        cartManager.addCartChangeListener(this);

        setupToolbar();
        initViews();
        loadStores();
        loadCategories(1);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("预约点单");
        // 设置返回键监听
        toolbar.setNavigationOnClickListener(v -> {
            onBackPressed();
        });
    }

    private void initViews() {
        storeSpinner = findViewById(R.id.storeSpinner);
        categoryRecyclerView = findViewById(R.id.categoryRecyclerView);
        dishRecyclerView = findViewById(R.id.dishRecyclerView);

        // 设置分类列表
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        categoryAdapter = new CategoryAdapter(categories, this);
        categoryRecyclerView.setAdapter(categoryAdapter);

        // 设置菜品列表
        dishRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        dishAdapter = new DishAdapter(dishes, this,this);
        dishRecyclerView.setAdapter(dishAdapter);

        storeSpinner.setSelection(0);
        // 设置门店选择监听
        storeSpinner.setOnItemClickListener((parent, view, position, id) -> {
            selectedStore = stores.get(position);
            System.out.println("选择门店: " + selectedStore.getStoreName());
        });

/*        // 初始化购物车底部栏
        cartBottomView = findViewById(R.id.cartBottomView);
        textViewTotalPrice = cartBottomView.findViewById(R.id.textViewTotalPrice);
        textViewCartCount = cartBottomView.findViewById(R.id.textViewCartCount);

        cartBottomView.findViewById(R.id.cartIconContainer).setOnClickListener(v -> {
            showCartDialog();
        });

        cartBottomView.findViewById(R.id.buttonSubmit).setOnClickListener(v -> {
            if (cartManager.getTotalCount() > 0) {
               startActivity(new Intent(this, OrderConfirmActivity.class));
            }
        });*/
    }

    private void loadStores() {
        // 从服务器加载门店列表

        apiService.getAllStores().enqueue(new Callback<List<Store>>() {
            @Override
            public void onResponse(Call<List<Store>> call, Response<List<Store>> response) {
                System.out.println("加载门店成功: " + response.body());
                System.out.println("\n加载门店成功: " + response.message());
                System.out.println("\n加载门店成功: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    stores.clear();
                    stores.addAll(response.body());
                    StoreAdapter adapter = new StoreAdapter(PreOrderActivity.this, stores);
                    storeSpinner.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Store>> call, Throwable t) {
                System.out.println("加载门店失败: " + t.getMessage());
                Toast.makeText(PreOrderActivity.this, "加载门店失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCategories(int storeId) {
        System.out.println("加载分类: " + storeId);
        apiService.getAllDishCategories().enqueue(new Callback<List<DishCategory>>() {
            @Override
            public void onResponse(Call<List<DishCategory>> call, Response<List<DishCategory>> response) {
                System.out.println("加载分类成功: " + response.body());
                System.out.println("\n加载分类成功: " + response.message());
                System.out.println("\n加载分类成功: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    categories.clear();
                    categories.addAll(response.body());
                    categoryAdapter.notifyDataSetChanged();

                    if (!categories.isEmpty()) {
                        loadDishes(categories.get(0).getCategoryId());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<DishCategory>> call, Throwable t) {
                System.out.println("加载分类失败: " + t.getMessage());
                Toast.makeText(PreOrderActivity.this, "加载分类失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadDishes(int categoryId) {
        apiService.getDishesByCategory(categoryId).enqueue(new Callback<List<Dish>>() {
            @Override
            public void onResponse(Call<List<Dish>> call, Response<List<Dish>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    dishes.clear();
                    dishes.addAll(response.body());
                    dishAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Dish>> call, Throwable t) {
                System.out.println("加载菜品失败: " + t.getMessage());
                Toast.makeText(PreOrderActivity.this, "加载菜品失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onCategoryClick(DishCategory category) {
        loadDishes(category.getCategoryId());
    }

    @Override
    public void onDishClick(Dish dish) {
        if (selectedStore == null) {
            Toast.makeText(this, "请先选择门店", Toast.LENGTH_SHORT).show();
            return;
        }
        MyApplication.saveStore(selectedStore);
        showDishDetailDialog(dish);
    }

    private void showDishDetailDialog(Dish dish) {
        DishDetailDialogFragment dialogFragment;
        if (isDineIn) {
            dialogFragment = DishDetailDialogFragment.newInstance(dish,"到店消费");
        } else {
             dialogFragment = DishDetailDialogFragment.newInstance(dish,"预约点单");
        }

        dialogFragment.show(getSupportFragmentManager(), "dish_detail");
    }

    private void showCartDialog() {
        CartDialogFragment dialogFragment = new CartDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "cart");
    }

    @Override
    public void onCartChanged() {
        updateCartUI();
    }

    private void updateCartUI() {
        double totalPrice = cartManager.getTotalPrice();
        int totalCount = cartManager.getTotalCount();

        textViewTotalPrice.setText(String.format("¥%.2f", totalPrice));
        if (totalCount > 0) {
            textViewCartCount.setVisibility(View.VISIBLE);
            textViewCartCount.setText(String.valueOf(totalCount));
            cartBottomView.findViewById(R.id.buttonSubmit).setEnabled(true);
        } else {
            textViewCartCount.setVisibility(View.GONE);
            cartBottomView.findViewById(R.id.buttonSubmit).setEnabled(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cartManager.removeCartChangeListener(this);
    }
} 