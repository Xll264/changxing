package com.hixtrip.sample.infra;

import com.hixtrip.sample.domain.inventory.model.Inventory;
import com.hixtrip.sample.domain.inventory.repository.InventoryRepository;
import com.hixtrip.sample.infra.db.convertor.InventoryDOConvertor;
import com.hixtrip.sample.infra.db.dataobject.InventoryDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * infra层是domain定义的接口具体的实现
 */
@Component
public class InventoryRepositoryImpl implements InventoryRepository {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    @Override
    public Inventory getInventory(String skuId) {
        //从缓存中获取库存
        Object o = redisTemplate.opsForValue().get(skuId);
        Inventory inventory = InventoryDOConvertor.INSTANCE.doToDomain((InventoryDO) o);
        return inventory;
    }

    @Override
    public void editInventory(String skuId, Long sellableQuantity, Long withholdingQuantity, Long occupiedQuantity) {
        // 将新的库存信息存入缓存
        InventoryDO inventoryDO = new InventoryDO(skuId, sellableQuantity, withholdingQuantity, occupiedQuantity);
        redisTemplate.opsForValue().set(skuId, inventoryDO);
    }
}
