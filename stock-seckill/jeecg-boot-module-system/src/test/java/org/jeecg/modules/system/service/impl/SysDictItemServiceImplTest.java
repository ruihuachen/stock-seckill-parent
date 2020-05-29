package org.jeecg.modules.system.service.impl;

import org.jeecg.modules.system.entity.SysDictItem;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

import static com.sun.tools.doclint.Entity.not;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SysDictItemServiceImplTest {

    @Autowired
    private SysDictItemServiceImpl sysDictItemService;

    @Test
    public void insertRecordsIntoDict() {
        SysDictItem sysDictItem = new SysDictItem();
            sysDictItem.setDictId("1256036640653074434");
            sysDictItem.setItemText("交通");
            sysDictItem.setItemValue(String.valueOf(1));
            sysDictItem.setStatus(1);
            sysDictItem.setCreateTime(new Date());
        Assert.assertTrue(sysDictItemService.save(sysDictItem));

    }
}