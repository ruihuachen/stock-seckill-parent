package org.jeecg.modules.system.service;

import org.jeecg.modules.system.entity.SysDictItem;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 *  服务类
 */
public interface ISysDictItemService extends IService<SysDictItem> {
    List<SysDictItem> selectItemsByMainId(String mainId);

    void insertRecordsIntoDict(String dictId, String dictText);

    int selectCountByDictId(String dictName);

    SysDictItem selectItemByItemTextOfType(String itemValue);
}
