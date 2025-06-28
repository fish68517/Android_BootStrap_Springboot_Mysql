package com.example.orderfood.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.orderfood.ApiService;
import com.example.orderfood.MyApplication;
import com.example.orderfood.R;
import com.example.orderfood.RetrofitClient;
import com.example.orderfood.adapter.SpecGroupAdapter;
import com.example.orderfood.model.CartItem;
import com.example.orderfood.model.CartManager;
import com.example.orderfood.model.Dish;
import com.example.orderfood.model.SpecGroup;
import com.example.orderfood.model.SpecOption;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DishDetailDialogFragment extends BottomSheetDialogFragment {
    private Dish dish;
    private CartManager cartManager;
    private TextView textViewQuantity;
    private int quantity = 1;
    private double totalPrice;
    private ArrayList<SpecGroup> specGroups = new ArrayList<>();
    private Map<String, String> selectedOptions = new HashMap<>();
    private RecyclerView recyclerViewSpecs;
    private SpecGroupAdapter specAdapter;

    private ApiService apiService = RetrofitClient.getInstance().getApiService();
    private String cartType;

    public static DishDetailDialogFragment newInstance(Dish dish,String type) {
        DishDetailDialogFragment fragment = new DishDetailDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable("dish_detail", dish);
        args.putString("cart_type", type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dish = (Dish) getArguments().getSerializable("dish_detail");
        cartType = (String) getArguments().getString("cart_type");
        System.out.println("dish detail: " + dish.toString());
        cartManager = CartManager.getInstance();
        totalPrice = dish.getPrice();
        
        // 解析规格选项
        if (dish.getDishOptions() != null) {
            try {
                JSONObject jsonObject = new JSONObject(dish.getDishOptions());
                Iterator<String> keys = jsonObject.keys();
                
                while (keys.hasNext()) {
                    String groupName = keys.next();
                    JSONArray optionsArray = jsonObject.getJSONArray(groupName);
                    
                    // 创建规格组
                    SpecGroup group = new SpecGroup();
                    group.setName(groupName);
                    group.setRequired(true); // 可以根据需求设置是否必选
                    
                    // 添加选项
                    List<SpecOption> options = new ArrayList<>();
                    for (int i = 0; i < optionsArray.length(); i++) {
                        String optionName = optionsArray.getString(i);
                        SpecOption option = new SpecOption();
                        option.setName(optionName);
                        // 这里可以根据规则设置价格调整
                        if (groupName.equals("容量")) {
                            if (optionName.equals("小份")) option.setPriceDelta(-2);
                            else if (optionName.equals("大份")) option.setPriceDelta(2);
                        }
                        // 这里可以根据规则设置价格调整
                        if (groupName.equals("辅料")) {
                            if (optionName.equals("加珍珠")) option.setPriceDelta(2);
                            else if (optionName.equals("加椰果")) option.setPriceDelta(2);
                        }
                        options.add(option);
                    }
                    group.setOptions(options);
                    specGroups.add(group);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_dish_detail, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        ImageView imageViewDish = view.findViewById(R.id.imageViewDish);
        TextView textViewName = view.findViewById(R.id.textViewName);
        TextView textViewPrice = view.findViewById(R.id.textViewPrice);
        TextView textViewDescription = view.findViewById(R.id.textViewDescription);
        textViewQuantity = view.findViewById(R.id.textViewQuantity);

        int imageResourceId = getActivity().getResources().getIdentifier(dish.getImage(),
                "drawable",getActivity().getPackageName());
        // 加载菜品图片
        Glide.with(this)
                .load(imageResourceId)
                .into(imageViewDish);

        textViewName.setText(dish.getDishName());
        textViewPrice.setText(String.format("¥%.2f", dish.getPrice()));
        textViewDescription.setText(dish.getDescription());
        updateQuantity();

        // 添加按钮点击事件
        view.findViewById(R.id.buttonMinus).setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                updateQuantity();
            }
        });

        view.findViewById(R.id.buttonPlus).setOnClickListener(v -> {
            quantity++;
            updateQuantity();
        });

        // 初始化规格选择列表
        recyclerViewSpecs = view.findViewById(R.id.recyclerViewSpecs);
        recyclerViewSpecs.setLayoutManager(new LinearLayoutManager(getContext()));
        
        specAdapter = new SpecGroupAdapter(specGroups, (group, option) -> {
            // 处理规格选择
            selectedOptions.put(group.getName(), option.getName());
            updateTotalPrice();
        });
        recyclerViewSpecs.setAdapter(specAdapter);
        
        // 修改添加到购物车按钮点击事件
        view.findViewById(R.id.buttonAddToCart).setOnClickListener(v -> {
            // 创建精简版的购物车项
            CartItem cartItem = new CartItem();
            cartItem.setDishId(dish.getDishId());
            cartItem.setUserId(MyApplication.getCurUser().getUserId());
            cartItem.setQuantity(quantity);
            cartItem.setTotalPrice(totalPrice * quantity);
            cartItem.setCartType(cartType);
            cartItem.setSelectedOptions(selectedOptions);
            
            // 不要设置 dish 对象
            // 不要设置时间字段，由后端处理
            
            System.out.println("selected options: " + selectedOptions.toString());
            System.out.println("quantity: " + quantity);
            System.out.println("total price: " + totalPrice);
            
            apiService.addToCart(cartItem).enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getActivity() == null? MyApplication.mContext : getActivity(), "已添加到购物车", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "添加到购物车失败", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                    System.out.println("failure: " + t.getMessage());
                    Toast.makeText(getActivity(), "添加到购物车失败", Toast.LENGTH_SHORT).show();
                }
            });
            dismiss();
        });
    }

    private void updateQuantity() {
        textViewQuantity.setText(String.valueOf(quantity));
    }

    private void updateTotalPrice() {
        double basePrice = dish.getPrice();
        double optionsPrice = 0;
        
        // 计算规格选项的价格调整
        for (SpecGroup group : specGroups) {
            String selectedOption = selectedOptions.get(group.getName());
            if (selectedOption != null) {
                for (SpecOption option : group.getOptions()) {
                    if (option.getName().equals(selectedOption)) {
                        optionsPrice += option.getPriceDelta();
                        break;
                    }
                }
            }
        }
        
        totalPrice = basePrice + optionsPrice;
        TextView textViewPrice = getView().findViewById(R.id.textViewPrice);
        textViewPrice.setText(String.format("¥%.2f", totalPrice));
    }
} 