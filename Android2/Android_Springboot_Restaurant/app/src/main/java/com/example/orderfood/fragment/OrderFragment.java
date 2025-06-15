package com.example.orderfood.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.orderfood.ApiService;
import com.example.orderfood.MyApplication;
import com.example.orderfood.R;
import com.example.orderfood.RetrofitClient;
import com.example.orderfood.activity.PaymentActivity;
import com.example.orderfood.adapter.OrderAdapter;
import com.example.orderfood.model.Coupon;
import com.example.orderfood.model.Order;
import com.example.orderfood.model.User;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderFragment extends Fragment {
    private RecyclerView recyclerView;

    private List<Order> ordersList = new ArrayList<>();

    private ApiService apiService = RetrofitClient.getInstance().getApiService();
    private OrderAdapter adapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);


        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


    }

    private void loadOrders() {

        apiService.getUserOrders(MyApplication.getCurUser().getUserId()).enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                if (response.isSuccessful()) {
                    ordersList.clear();
                    List<Order> orders = response.body();
                    Iterator<Order> iterator = orders.iterator();
                  /*  while (iterator.hasNext()) {
                        Order order = iterator.next();
                        if (order.getStatus() != 0) {
                            iterator.remove();
                        }
                    }*/
                    ordersList.addAll(orders);
                    adapter = new OrderAdapter(getActivity(), ordersList);
                    recyclerView.setAdapter(adapter);
                    adapter.setOnPayClickListener(new OrderAdapter.OnPayClickListener() {
                        @Override
                        public void onPayClick(Order order) {
                            handlePayment(order);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {

                System.out.println("onFailure: " + t.getMessage());
            }
        });
    }


    /**
     * 处理支付流程，弹出自定义对话框
     */
    private void handlePaymentaa(Order order) {
        // 1. 创建 AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // 2. 加载自定义布局
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_payment, null);
        builder.setView(dialogView);

        // 3. 获取自定义布局中的控件
        ImageView imageViewWeChatQR = dialogView.findViewById(R.id.imageViewWeChatQR);
        ImageView imageViewAlipayQR = dialogView.findViewById(R.id.imageViewAlipayQR);
        Button buttonConfirmPayment = dialogView.findViewById(R.id.buttonConfirmPayment);

        // 4. 生成并设置二维码
        // 模拟的微信支付URL
        String wechatQrUrl = "weixin://wxpay/bizpayurl?pr=SAMPLE123456789";
        Bitmap wechatBitmap = generateQRCode(wechatQrUrl);
        if (wechatBitmap != null) {
            imageViewWeChatQR.setImageBitmap(wechatBitmap);
        }

        // 模拟的支付宝支付URL
        String alipayQrUrl = "https://qr.alipay.com/fkx99999xxxxxx";
        Bitmap alipayBitmap = generateQRCode(alipayQrUrl);
        if (alipayBitmap != null) {
            imageViewAlipayQR.setImageBitmap(alipayBitmap);
        }

        // 5. 创建并显示对话框
        AlertDialog dialog = builder.create();
        dialog.show();

        // 6. 设置“支付成功”按钮的点击事件
        buttonConfirmPayment.setOnClickListener(v -> {
            dialog.dismiss(); // 关闭对话框
            saveScore(order);

        });
    }

    /**
     * 生成二维码的辅助方法
     * @param content 二维码内容URL
     * @return Bitmap 类型的二维码图像，失败则返回 null
     */
    private Bitmap generateQRCode(String content) {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            // 直接指定尺寸，避免在布局中硬编码
            return barcodeEncoder.encodeBitmap(content, BarcodeFormat.QR_CODE, 400, 400);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "生成二维码失败", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private void handlePayment(Order order) {

        if (true) {
            handlePaymentaa(order);
            return;
        }

        // 先获取优惠券信息
        apiService.getAllCoupons().enqueue(new Callback<List<Coupon>>() {
            @Override
            public void onResponse(Call<List<Coupon>> call, Response<List<Coupon>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Coupon> coupons = response.body();
                    
                    // 将优惠券名称转换为字符串数组用于显示
                    String[] couponNames = new String[coupons.size()];
                    for (int i = 0; i < coupons.size(); i++) {
                        couponNames[i] = coupons.get(i).getCouponName();
                    }

                    // 创建并显示优惠券选择对话框
                    new AlertDialog.Builder(requireContext())
                        .setTitle("选择优惠券")
                        .setItems(couponNames, (dialog, which) -> {
                            // 用户选择优惠券后
                            Coupon selectedCoupon = coupons.get(which);
                            String couponName = selectedCoupon.getCouponName();
                            // 计算优惠券金额

                            double totalAmount = order.getTotalAmount();
                            double amount = getAmount(couponName,totalAmount);


                            if (amount == totalAmount) {
                                Toast.makeText(requireContext(), "该优惠券不适用于此订单", Toast.LENGTH_SHORT).show();
                                return;
                            } else {
                                Toast.makeText(requireContext(), "订单总价为：" + amount + "元" + "，已减去" + (totalAmount-amount) + "元", Toast.LENGTH_SHORT).show();
                            }
                            order.setTotalAmount(amount);
                             new Handler().postDelayed(new Runnable() {

                                 @Override
                                 public void run() {
                                     order.setCoupon(selectedCoupon);
                                     if (true) {
                                         Toast.makeText(requireContext(), "已成功下单", Toast.LENGTH_SHORT).show();
                                         saveScore(order);

                                         // 优惠券减少
                                         apiService.deleteCoupon(selectedCoupon.getCouponId()).enqueue(new Callback<Boolean>() {
                                             @Override
                                             public void onResponse(Call<Boolean> call, Response<Boolean> response) {

                                             }

                                             @Override
                                             public void onFailure(Call<Boolean> call, Throwable t) {

                                             }
                                         });
                                     }
                                 }
                             },2000);
                           /* // 跳转到支付界面
                            Intent intent = new Intent(getActivity(), PaymentActivity.class);
                            intent.putExtra("order", order);
                            startActivity(intent);*/
                        })
                        .setNegativeButton("不使用优惠券", (dialog, which) -> {
                            // 用户选择不使用优惠券
                            if (true) {
                                Toast.makeText(requireContext(), "已成功下单", Toast.LENGTH_SHORT).show();
                                saveScore(order);
                                return;
                            }
                            Intent intent = new Intent(getActivity(), PaymentActivity.class);
                            intent.putExtra("order", order);
                            startActivity(intent);
                        })
                        .show();
                } else {
                    // 获取优惠券失败时直接跳转支付
                    if (true) {
                        Toast.makeText(requireContext(), "已成功下单", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Intent intent = new Intent(getActivity(), PaymentActivity.class);
                    intent.putExtra("order", order);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<List<Coupon>> call, Throwable t) {
                // 网络请求失败时直接跳转支付
                if (true) {
                    Toast.makeText(requireContext(), "已成功下单", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(getActivity(), PaymentActivity.class);
                intent.putExtra("order", order);
                startActivity(intent);
            }
        });
    }

    private double getAmount(String couponName,double totalAmount) {
        DecimalFormat df = new DecimalFormat("0.00");
        System.out.println("优惠券名称：" + couponName);
        System.out.println("订单总价：" + totalAmount);
        if (couponName.contains("满20减10")) {
            if (totalAmount >= 20) {
                return totalAmount-10;
            } else {
                return totalAmount;
            }
        } else if (couponName.contains("85折优惠")) {
            return Double.parseDouble(df.format(totalAmount * 0.85));

        } else if (couponName. contains("满100减50")) {
            if (totalAmount >= 100) {
                return totalAmount-50;
            } else {
                return totalAmount;
            }
        } else if (couponName.contains("满500减100")) {

        }else if (couponName. contains("满100减20券")) {
            if (totalAmount >= 100) {
                return totalAmount-20;
            } else {
                return totalAmount;
            }

        } else if (couponName.contains("满50减10")) {
                    if (totalAmount >= 50) {
                        return totalAmount-10;
                    } else {
                        return totalAmount;
                    }

        }else if (couponName.contains("满10减2")) {
                if (totalAmount >= 10) {
                    return totalAmount-2;
                } else {
                    return totalAmount;
                }

        }
        return totalAmount;
    }

    private void saveScore(Order order) {
        User user = MyApplication.getCurUser();
        if (user == null) {
            Toast.makeText(requireContext(), "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }
        user.setPoints(1+user.getPoints());
        apiService.updateUserInfo(MyApplication.getCurUser()).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && response.body() != null && response.body()) {
                 //   Toast.makeText(requireContext(), "下单成功请店内扫码支付", Toast.LENGTH_SHORT).show();
                } else {
                   // Toast.makeText(requireContext(), "下单成功请店内扫码支付", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                System.out.println("onFailure: " + t.getMessage());

              //  Toast.makeText(requireContext(), "下单成功请店内扫码支付", Toast.LENGTH_SHORT).show();
            }
        });

        order.setStatus(1);
        apiService.update(order).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && response.body() != null && response.body()) {

                } else {

                }

                apiService.getUserOrders(MyApplication.getCurUser().getUserId()).enqueue(new Callback<List<Order>>() {
                    @Override
                    public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                        if (response.isSuccessful()) {
                            ordersList.clear();
                            List<Order> orders = response.body();
                            Iterator<Order> iterator = orders.iterator();
                        /*    while (iterator.hasNext()) {
                                Order order = iterator.next();
                                if (order.getStatus() != 0) {
                                    iterator.remove();
                                }
                            }*/
                            ordersList.addAll(orders);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(getActivity(), "支付成功", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onFailure(Call<List<Order>> call, Throwable t) {

                        System.out.println("onFailure: " + t.getMessage());
                    }
                });
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {

                System.out.println("订单状态更新失败: " + t.getMessage());
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        loadOrders();
    }
}
