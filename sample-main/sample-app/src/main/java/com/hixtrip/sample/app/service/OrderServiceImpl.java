package com.hixtrip.sample.app.service;

import com.hixtrip.sample.app.api.OrderService;
import com.hixtrip.sample.app.convertor.OrderConvertor;
import com.hixtrip.sample.client.order.dto.CommandOderCreateDTO;
import com.hixtrip.sample.client.order.dto.CommandPayDTO;
import com.hixtrip.sample.domain.inventory.InventoryDomainService;
import com.hixtrip.sample.domain.inventory.model.Inventory;
import com.hixtrip.sample.domain.order.OrderDomainService;
import com.hixtrip.sample.domain.order.model.Order;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * app层负责处理request请求，调用领域服务
 */
@Component
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderDomainService orderDomainService;

    @Autowired
    private InventoryDomainService inventoryDomainService;

    @Override
    public String order(CommandOderCreateDTO commandOderCreateDTO) {
        Order order = new Order();
        BeanUtils.copyProperties(commandOderCreateDTO,order);
        order.setId(generateOrderId());
        order.setPayTime(LocalDateTime.now());
        order.setCreateTime(LocalDateTime.now());
        boolean flag = orderDomainService.createOrder(order);
        if (flag){
            Inventory inventory = inventoryDomainService.getInventory(order.getSkuId());
            if (Objects.nonNull(inventory)) {
                //创建订单成功后，扣减可售库存，增加预占库存
                inventoryDomainService.changeInventory(order.getSkuId(), (inventory.getSellableQuantity() - order.getAmount()),
                        (inventory.getWithholdingQuantity() + order.getAmount()), inventory.getOccupiedQuantity());
                return "创建订单成功";
            }
        }
        return null;
    }

    /**
     * 生成订单号
     * @return
     */
    private String generateOrderId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }
    @Override
    public String payCallback(CommandPayDTO commandPayDTO) {
        boolean flag = orderDomainService.orderPaySuccess(OrderConvertor.INSTANCE.commandPayDTOToCommandPay(commandPayDTO));
        if (flag){
            return "支付成功";
        }
        return "支付失败";
    }
}
