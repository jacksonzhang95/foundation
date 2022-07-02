package com.foundation.theory.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : jacksonz
 * @date : 2021/10/18 16:28
 */
public class ObserverDemo {
}

class OrderService {
    private List<BaseOrderEventListener> orderEventListeners = new ArrayList<>();

    public void createOrder() {
        // create order
        // after create order
        for (BaseOrderEventListener orderEventListener : orderEventListeners) {
            orderEventListener.handleEvent();
        }
    }
}

abstract class BaseOrderEventListener {
    abstract void handleEvent();
}

