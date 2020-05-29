package org.jeecg.modules.system.service.impl;

import org.jeecg.modules.system.mapper.StockGoodsMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StockGoodsServiceImplTest {

    @Autowired
    private StockGoodsMapper stockGoodsMapper;

    @Test
    public void Test() {
        System.out.println(stockGoodsMapper.updateByOptimisticLockByProId("FNR2020051916131972", 20));

    }

}