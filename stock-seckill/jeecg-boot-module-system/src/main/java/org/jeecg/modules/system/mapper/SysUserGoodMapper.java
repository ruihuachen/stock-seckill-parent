package org.jeecg.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.system.entity.SysUserGood;

/**
 * 票商品-卖家表
 */
public interface SysUserGoodMapper extends BaseMapper<SysUserGood> {

    void deleteBathGoodUserRelation(@Param("goodIds")String[] goodIds);

    @Delete("delete from sys_user_good where good_id = #{goodId}")
    void deleteGoodUserRelation(@Param("goodId") String goodId);

    @Select("select seller_id from sys_user_good where good_id = #{goodId}")
    String getSellerIdByGoodId(@Param("goodId") String goodId);
}
