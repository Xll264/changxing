package com.hixtrip.sample.infra.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hixtrip.sample.domain.order.model.Order;
import com.hixtrip.sample.infra.db.dataobject.OrderDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Time 2024/4/22 21:31
 * @Description
 */
@Mapper
public interface OrderMapper extends BaseMapper<OrderDO> {
    boolean insertOrder(Order order);

    OrderDO queryOrderById(String id);

    boolean updateOrder(OrderDO orderDO);
}
