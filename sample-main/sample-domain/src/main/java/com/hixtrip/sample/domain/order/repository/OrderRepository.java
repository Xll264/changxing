package com.hixtrip.sample.domain.order.repository;

import com.hixtrip.sample.domain.order.model.Order;
import com.hixtrip.sample.domain.pay.model.CommandPay;

/**
 *
 */
public interface OrderRepository {
    boolean createOrder(Order order);

    boolean orderPaySuccess(CommandPay commandPay);

    boolean orderPayFail(CommandPay commandPay);
}
