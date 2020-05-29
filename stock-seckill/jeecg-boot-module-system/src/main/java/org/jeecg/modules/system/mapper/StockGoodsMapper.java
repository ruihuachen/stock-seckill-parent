package org.jeecg.modules.system.mapper;

import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.jeecg.modules.system.entity.StockGoods;

import java.util.Date;
import java.util.List;

/**
 * 票商品表
 */
public interface StockGoodsMapper extends BaseMapper<StockGoods> {

    @Select("select * from stock_goods where id = #{id}")
    StockGoods getStockGoodById(@Param("id") String id);

    @Select("select price from stock_goods where pro_id = #{proId}")
    String getPriceByProId(@Param("proId") String proId);

    @Select("select id from stock_goods where pro_id = #{proId}")
    String getGoodIdByProId(@Param("proId") String proId);

    @Select("select price from stock_goods where id = #{id}")
    String getPriceById(@Param("id") String id);

    @Select("select pro_id from stock_goods where title = #{title}")
    String getProIdByTitle(@Param("title") String title);

    //--------------------------------------限时间抢票-------------------------------------------
    // 初始化DB测试
    @Update("update stock_goods set inventory = 50, sale = 0, version = 0")
    int initDBBefore();

    @Select("select * from stock_goods where id = #{id}")
    StockGoods selectByPrimaryKey(@Param("id") String id);

    @Update("update stock_goods set inventory = #{inventory}," +
            "sale = #{sale} where id = #{id}")
    int updateByPrimaryKey(StockGoods stock);

    @Update("update stock_goods set inventory = #{inventory}," +
            "sale = #{sale}, version = #{version}" +
            "where id = #{id}")
    int updateByPrimaryKey2(StockGoods stock);

    /**
     * 乐观锁
     */
    @Update("update stock_goods set inventory = inventory - 1, sale = sale + 1, version = version + 1 where id = #{id}")
    int updateByOptimisticLock(StockGoods stock);

    /**
     * 乐观锁-内部
     */
    @Update("update stock_goods set inventory = inventory - #{num}, sale = sale + #{num}, version = version + 1 where pro_id = #{proId}")
    int updateByOptimisticLockByProId(String proId, int num);


    //-----------------------------------------------------------------------------------------------

    /**
     * 减少库存
     * @param stockGoodId
     * @param killTime
     * @return 影响行数>=1 更新
     */
    int reduceNumber(@Param("stockGoodId") String stockGoodId, @Param("killTime") Date killTime);

    /**
     * 根据偏移量查询秒杀商品列表
     * @param offset
     * @param limit
     * @return
     */
    List<StockGoods> queryAll(@Param("offset") int offset, @Param("limit") int limit);


    List<StockGoods> queryAll2();


    List<StockGoods> getAdvanceCachingToRedis(@Param("now") Date now,@Param("end") Date end, @Param("offset") int offset, @Param("limit") int limit);
}
