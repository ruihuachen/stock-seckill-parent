package org.jeecg.modules.system.mapper;

import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.system.entity.SysDictItem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * 字典 key-value
 */
public interface SysDictItemMapper extends BaseMapper<SysDictItem> {

    @Select("SELECT * FROM sys_dict_item WHERE DICT_ID = #{mainId} order by sort_order asc, item_value asc")
    List<SysDictItem> selectItemsByMainId(String mainId);

    @Select("select max(cast(item_value as UNSIGNED)) from sys_dict_item where dict_id = #{dictId}")
    int selectItemTextValueByDictId(String dictId);

    /**
     * 据dictId 查询是否存在 没有新建save 有的话更新updateById
     */
    @Select("select count(1) from sys_dict_item where item_text=#{dictName} and dict_id = '1256036640653074434'")
    int selectCountByDictId(String dictName);

    /**
     * 根据value 获取type字段的字典表中的text值
     */
    @Select("select * from sys_dict_item where item_value = #{itemValue} and dict_id = '1256036640653074434'")
    SysDictItem selectItemByItemTextOfType(String itemValue);
}
