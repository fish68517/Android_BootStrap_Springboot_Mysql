package com.example.orderfood.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartManager {
    private static CartManager instance;
    private final Map<Integer, CartItem> cartItems = new HashMap<>();
    private final List<CartChangeListener> listeners = new ArrayList<>();

    public static CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public void addToCart(Dish dish) {
        CartItem item = cartItems.get(dish.getDishId());
        if (item == null) {
            item = new CartItem(dish, 1);
            cartItems.put(dish.getDishId(), item);
        } else {
            item.setQuantity(item.getQuantity() + 1);
        }
        notifyCartChanged();
    }

    public void removeFromCart(Dish dish) {
        CartItem item = cartItems.get(dish.getDishId());
        if (item != null) {
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
            } else {
                cartItems.remove(dish.getDishId());
            }
            notifyCartChanged();
        }
    }

    public void clearCart() {
        cartItems.clear();
        notifyCartChanged();
    }

    public double getTotalPrice() {
        double total = 0;
        for (CartItem item : cartItems.values()) {
            total = total + ((item.getDish().getPrice()) * (item.getQuantity()));
        }
        return total;
    }

    public int getTotalCount() {
        int count = 0;
        for (CartItem item : cartItems.values()) {
            count += item.getQuantity();
        }
        return count;
    }

    public List<CartItem> getCartItems() {
        return new ArrayList<>(cartItems.values());
    }

    public void addCartChangeListener(CartChangeListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeCartChangeListener(CartChangeListener listener) {
        listeners.remove(listener);
    }

    private void notifyCartChanged() {
        for (CartChangeListener listener : listeners) {
            listener.onCartChanged();
        }
    }

    public interface CartChangeListener {
        void onCartChanged();
    }
} 