package com.example.orderfood.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderfood.ApiService;
import com.example.orderfood.R;
import com.example.orderfood.RetrofitClient;
import com.example.orderfood.adapter.CartAdapter;
import com.example.orderfood.adapter.CategoryAdapter;
import com.example.orderfood.adapter.DishAdapter;
import com.example.orderfood.model.DishCategory;
import com.example.orderfood.model.Dish;
import com.example.orderfood.model.CartItem;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DineInActivity extends AppCompatActivity implements CategoryAdapter.OnCategoryClickListener, DishAdapter.OnDishClickListener {
    private RecyclerView rvCategory;
    private RecyclerView rvDishes;
    private TextView tvTotalPrice;
    private View layoutCart;

    private CategoryAdapter categoryAdapter;
    private DishAdapter dishAdapter;
    private List<CartItem> cartItems = new ArrayList<>();
    private ApiService apiService = RetrofitClient.getInstance().getApiService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dine_in);

        setupToolbar();
        initViews();
        loadCategories();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("到店消费");
    }

    private void initViews() {
        rvCategory = findViewById(R.id.rv_category);
        rvDishes = findViewById(R.id.rv_dishes);
        tvTotalPrice = findViewById(R.id.tv_total_price);
        layoutCart = findViewById(R.id.layout_cart);

        rvCategory.setLayoutManager(new LinearLayoutManager(this));
        rvDishes.setLayoutManager(new LinearLayoutManager(this));

        categoryAdapter = new CategoryAdapter(new ArrayList<>(), this);
        dishAdapter = new DishAdapter(new ArrayList<>(),this,this);

        rvCategory.setAdapter(categoryAdapter);
        rvDishes.setAdapter(dishAdapter);




        // 查看购物车
        layoutCart.setOnClickListener(v -> {
            showCartDialog();
        });
    }

    private void loadCategories() {
        apiService.getAllDishCategories().enqueue(new Callback<List<DishCategory>>() {
            @Override
            public void onResponse(Call<List<DishCategory>> call, Response<List<DishCategory>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoryAdapter.setCategories((List<DishCategory>)response.body());
                    // 默认加载第一个分类的菜品
                    if (!response.body().isEmpty()) {
                        loadDishesByCategory(response.body().get(0).getCategoryId());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<DishCategory>> call, Throwable t) {
                // 处理错误
            }
        });
    }

    private void loadDishesByCategory(int categoryId) {
        apiService.getDishesByCategory(categoryId).enqueue(new Callback<List<Dish>>() {
            @Override
            public void onResponse(Call<List<Dish>> call, Response<List<Dish>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    dishAdapter.setDishes((List<Dish>)response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Dish>> call, Throwable t) {
                // 处理错误
            }
        });
    }

    private void addToCart(Dish dish) {
        // 检查是否已在购物车中
        for (CartItem item : cartItems) {
            if (item.getDish().getDishId() == dish.getDishId()) {
                item.setQuantity(item.getQuantity() + 1);
                updateTotalPrice();
                return;
            }
        }

        // 新增到购物车
        CartItem cartItem = new CartItem(dish, 1);
        cartItems.add(cartItem);
        updateTotalPrice();
    }

    private void updateTotalPrice() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getDish().getPrice() * item.getQuantity();
        }
        tvTotalPrice.setText(String.format("￥%.2f", total));
    }

    private void showCartDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_cart, null);
        RecyclerView rvCart = view.findViewById(R.id.rv_cart);
        TextView tvSubmit = view.findViewById(R.id.tv_submit);


        CartAdapter cartAdapter = new CartAdapter(cartItems,this);
        rvCart.setLayoutManager(new LinearLayoutManager(this));
        rvCart.setAdapter(cartAdapter);

        tvSubmit.setOnClickListener(v -> {
            submitOrder();
            dialog.dismiss();
        });

        dialog.setContentView(view);
        dialog.show();
    }

    private void submitOrder() {
        // TODO: 提交订单
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCategoryClick(DishCategory category) {

        loadDishesByCategory(category.getCategoryId());
    }

    @Override
    public void onDishClick(Dish dish) {


        addToCart(dish);
    }
}