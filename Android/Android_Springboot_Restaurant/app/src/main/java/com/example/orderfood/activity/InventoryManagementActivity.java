package com.example.orderfood.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.orderfood.ApiService;
import com.example.orderfood.R;
import com.example.orderfood.RetrofitClient;
import com.example.orderfood.model.Order;
import com.example.orderfood.model.Supplier;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InventoryManagementActivity extends AppCompatActivity {

    private static final String TAG = "库存管理";
    private static final int INVENTORY_CAPACITY = 10;
    private static final double WARNING_THRESHOLD = 0.75;

    private ApiService apiService;
    private Map<Integer, Supplier> supplierMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_management);

        Toolbar toolbar = findViewById(R.id.toolbar_inventory);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        apiService = RetrofitClient.getInstance().getApiService();
        mockSupplierData();

        loadStoreData(1, findViewById(R.id.chart_store1), findViewById(R.id.tv_suggestion1), findViewById(R.id.tv_supplier1));
        loadStoreData(2, findViewById(R.id.chart_store2), findViewById(R.id.tv_suggestion2), findViewById(R.id.tv_supplier2));
        loadStoreData(3, findViewById(R.id.chart_store3), findViewById(R.id.tv_suggestion3), findViewById(R.id.tv_supplier3));
    }

    private void mockSupplierData() {
        supplierMap = new HashMap<>();
        supplierMap.put(1, new Supplier("北京生鲜供应中心", "王经理", "13800101234", "北京市朝阳区农产品批发市场A区"));
        supplierMap.put(2, new Supplier("上海食品集团", "李总", "13900215678", "上海市浦东新区外高桥保税区"));
        supplierMap.put(3, new Supplier("广州佳肴配送", "陈小姐", "13700209876", "广州市白云区江南水果批发市场"));
    }

    private void loadStoreData(int storeId, BarChart chart, TextView suggestionView, TextView supplierView) {
        Log.d(TAG, "开始加载店铺 " + storeId + " 的数据...");
        apiService.getOrdersByStore(storeId).enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Order> orders = response.body();
                    Log.d(TAG, "店铺 " + storeId + " 成功获取 " + orders.size() + " 条订单");
                    int totalItemsSold = calculateTotalItemsSold(orders);
                    float usagePercentage = (float) totalItemsSold / INVENTORY_CAPACITY;
                    
                    updateChart(chart, totalItemsSold, usagePercentage);
                    updateInfo(suggestionView, supplierView, storeId, usagePercentage);
                } else {
                    Log.e(TAG, "加载店铺 " + storeId + " 数据失败，响应码: " + response.code());
                    Toast.makeText(InventoryManagementActivity.this, "加载店铺 " + storeId + " 数据失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                Log.e(TAG, "加载店铺 " + storeId + " 数据时网络错误", t);
                Toast.makeText(InventoryManagementActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int calculateTotalItemsSold(List<Order> orders) {
        int total = 0;
        for (Order order : orders) {
            if (order.getCartItems() == null || order.getCartItems().isEmpty()) continue;
            try {
                String[] items = order.getCartItems().split(";");
                for (String item : items) {
                    String[] parts = item.split(":");
                    if (parts.length > 1) {
                        total += Integer.parseInt(parts[1]);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "解析cartItems失败: " + order.getCartItems(), e);
            }
        }
        Log.d(TAG, "计算出总销售量为: " + total);
        return total;
    }

    private void updateChart(BarChart chart, int totalSold, float usagePercentage) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, totalSold));

        BarDataSet dataSet = new BarDataSet(entries, "已售出数量");
        
        if (usagePercentage >= WARNING_THRESHOLD) {
            dataSet.setColor(Color.RED);
        } else {
            dataSet.setColor(Color.rgb(60, 220, 78));
        }
        dataSet.setValueTextSize(12f);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.5f);

        chart.setData(barData);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.setFitBars(true);
        chart.animateY(1000);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return "总销量";
            }
        });

        chart.getAxisLeft().setAxisMinimum(0);
        chart.getAxisLeft().setAxisMaximum(INVENTORY_CAPACITY);
        chart.getAxisRight().setEnabled(false);
        chart.invalidate();
    }

    private void updateInfo(TextView suggestionView, TextView supplierView, int storeId, float usagePercentage) {
        if (usagePercentage >= WARNING_THRESHOLD) {
            suggestionView.setText("库存严重不足 (已消耗" + String.format("%.0f%%", usagePercentage * 100) + ")！建议立即联系供应商补货。");
            suggestionView.setTextColor(Color.RED);
        } else {
            suggestionView.setText("库存状态正常 (已消耗" + String.format("%.0f%%", usagePercentage * 100) + ")。");
            suggestionView.setTextColor(Color.DKGRAY);
        }
        supplierView.setText(supplierMap.get(storeId).toString());
    }
} 