package org.jeecg.modules.system.service.impl;

import org.jeecg.modules.system.entity.SysDictItem;
import org.jeecg.modules.system.mapper.SysDictItemMapper;
import org.jeecg.modules.system.service.ISysDictItemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 服务实现类
 */
@Service
public class SysDictItemServiceImpl extends ServiceImpl<SysDictItemMapper, SysDictItem> implements ISysDictItemService {

    @Autowired
    private SysDictItemMapper dictItemMapper;

    @Override
    public List<SysDictItem> selectItemsByMainId(String mainId) {
        return dictItemMapper.selectItemsByMainId(mainId);
    }

    @Override
    public void insertRecordsIntoDict(String dictId, String dictText) {
        SysDictItem sysDictItem = new SysDictItem();
        try {
            sysDictItem.setDictId(dictId);
            sysDictItem.setItemText(dictText);

            int res = dictItemMapper.selectItemTextValueByDictId(dictId);
            sysDictItem.setItemValue(String.valueOf(res).equals("null") ? "1" : String.valueOf(res + 1));

            sysDictItem.setStatus(1);
            sysDictItem.setCreateTime(new Date());
            this.save(sysDictItem);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
    }

    @Override
    public int selectCountByDictId(String dictName) {
        return dictItemMapper.selectCountByDictId(dictName);
    }

    @Override
    public SysDictItem selectItemByItemTextOfType(String itemValue) {
        return dictItemMapper.selectItemByItemTextOfType(itemValue);
    }
}
