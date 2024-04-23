package com.hixtrip.sample.infra;

import com.hixtrip.sample.domain.inventory.InventoryDomainService;
import com.hixtrip.sample.domain.inventory.model.Inventory;
import com.hixtrip.sample.domain.order.model.Order;
import com.hixtrip.sample.domain.order.repository.OrderRepository;
import com.hixtrip.sample.domain.pay.model.CommandPay;
import com.hixtrip.sample.infra.db.dataobject.OrderDO;
import com.hixtrip.sample.infra.db.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @Time 2024/4/22 21:29
 * @Description
 */
@Component
public class OrderRepositoryImpl implements OrderRepository {
    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private InventoryDomainService inventoryDomainService;

    /**
     * 创建订单
     *
     * @param order
     */
    @Override
    public boolean createOrder(Order order) {
        // 将订单保存到数据库中
        return orderMapper.insertOrder(order);
    }
    /**
     * 待付款订单支付成功
     */
    @Override
    public boolean orderPaySuccess(CommandPay commandPay) {
        OrderDO orderDO = orderMapper.queryOrderById(commandPay.getOrderId());
        if (Objects.nonNull(orderDO)) {
            //修改订单状态
            orderDO.setPayStatus(commandPay.getPayStatus());
            orderDO.setPayTime(LocalDateTime.now());
            orderMapper.updateOrder(orderDO);

            //扣减预占库存，增加占用库存
            Inventory inventory = inventoryDomainService.getInventory(orderDO.getSkuId());
            inventoryDomainService.changeInventory(orderDO.getSkuId(),(inventory.getSellableQuantity()),
                    (inventory.getWithholdingQuantity() - orderDO.getAmount()),inventory.getOccupiedQuantity()+ orderDO.getAmount());
            return true;
        }
        return false;
    }

    /**
     * 待付款订单支付失败
     */
    @Override
    public boolean orderPayFail(CommandPay commandPay) {
        OrderDO orderDO = orderMapper.queryOrderById(commandPay.getOrderId());
        if (Objects.nonNull(orderDO)) {
            orderDO.setPayStatus(commandPay.getPayStatus());
            return orderMapper.updateOrder(orderDO);
        }
        return false;
    }
}
