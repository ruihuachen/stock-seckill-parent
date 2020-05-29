package org.jeecg.modules.system.service.impl;

import org.jeecg.modules.system.mapper.StockOrderMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.LinkedList;
import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StockOrderServiceImplTest {

    @Autowired
    private StockOrderMapper stockOrderMapper;

    @Test
    public void getStockOrderVOs() {
        List<String> ids = new LinkedList<>();
        ids.add("1257294605624672259");
        ids.add("1257294605624672258");
//
//        System.out.println(stockOrderMapper.getNeedShowMessage(ids));
//
//        System.out.println();
    }
}