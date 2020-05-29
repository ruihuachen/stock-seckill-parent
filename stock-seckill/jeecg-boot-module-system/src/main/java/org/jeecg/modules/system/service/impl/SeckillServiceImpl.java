package org.jeecg.modules.system.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.*;
import org.jeecg.modules.system.controller.LoginController;
import org.jeecg.modules.system.entity.*;
import org.jeecg.modules.system.enums.RedisKeyEnum;
import org.jeecg.modules.system.enums.SeckillStateEnum;
import org.jeecg.modules.system.exception.RepeatKillException;
import org.jeecg.modules.system.exception.SeckillCloseException;
import org.jeecg.modules.system.exception.SeckillException;
import org.jeecg.modules.system.manager.StockByRedis;
import org.jeecg.modules.system.mapper.*;
import org.jeecg.modules.system.rule.OrderNumberRule;
import org.jeecg.modules.system.service.ISeckillService;

import org.jeecg.modules.system.util.RedisPoolUtil;
import org.jeecg.modules.system.vo.Exposer;
import org.jeecg.modules.system.vo.SeckillExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class SeckillServiceImpl implements ISeckillService {

    @Autowired
    private StockGoodsMapper stockGoodsMapper;

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private SysUserGoodMapper userGoodMapper;

    @Autowired
    private StockOrderGoodMapper stockOrderGoodMapper;

    @Autowired
    private StockOrderMapper stockOrderMapper;

    @Autowired
    private OrderCustomerMapper orderCustomerMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Value("SECKILL-GOOD-TOPIC")
    private String kafkaTopic;

    private Gson gson = new GsonBuilder().create();

    //(混淆)md5盐值字符串,用于混淆MD5
    private final String slat = "da>;DS:D@Fa2^2d3Ds#$@%:>>cs:?SAD*&%%$";

    private static final Integer UPDATE_SUCCESS = 1;

    private static final Integer UPDATE_FAIL = 0;

    @Override
    public List<StockGoods> getAdvanceCachingToRedis() {
        Date nowTime = new Date();
        Date endTime = DateUtils.getOneMoreDay(nowTime);
        return stockGoodsMapper.getAdvanceCachingToRedis(nowTime, endTime,0, 10);
    }

    @Override
    public List<StockGoods> getSeckillList() {
        //return stockGoodsMapper.queryAll(0, 10);
        return stockGoodsMapper.queryAll2();
    }

    /**
     * 暴露秒杀地址
     *
     * 用redis做缓存 ——> 减少对数据库的访问量
     * 解决大量用户发送大量请求 ——>获取秒杀地址
     */
    @Override
    public Exposer exportSeckillUrl(String stockGoodId) {
        //优化：缓存优化——> 超时的基础上维护一致性
        //秒杀对象是不可变的 像秒杀记录有出错是直接废弃再新建而不是修改
        //1.访问redis
        StockGoods stockGoods = (StockGoods) redisUtil.get(stockGoodId);
        if (stockGoods == null) {
            //2.访问数据库
            stockGoods = stockGoodsMapper.getStockGoodById(stockGoodId);
            if (stockGoods == null) {
                return new Exposer(false, stockGoodId);
            }else {
                //3.放入redis并设置超时时间
                redisUtil.set(stockGoods.getId(), stockGoods, 60 * 60);
            }
        }

        System.out.println(stockGoods.toString() + "\n");

        //获取票商品开抢时间段
        Date startTime = stockGoods.getStartTime();
        Date endTime = stockGoods.getEndTime();
        //获取到系统当前时间
        Date currentTime = new Date();

        //判断是否符合开抢时间段
        if (currentTime.getTime() < startTime.getTime()
                || currentTime.getTime() > endTime.getTime()) {
            return new Exposer(false, stockGoodId, currentTime.getTime(), startTime.getTime(), endTime.getTime());
        }

        //md5不可逆加密
        String md5 = getMd5(stockGoodId);
            return new Exposer(true, md5, stockGoodId);
    }

    /**
     * 转化成特定的字符串的过程,不可逆
     */
    private String getMd5(String seckillId) {
        String base = seckillId + "/" + slat;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }

    /**
     * 使用注解来控制事务的优点：
     * 1) 开发团队达成一致约定,明确标注事务方法的编程风格
     * 2) 保证事务方法的执行时间尽可能短,不要穿插其他网络操作RPC/HTTP风格 ---> 写入操作/抛出运行期异常则回滚事务 否则commit
     * 3) 不是所有的方法都需要事务,如只有一条修改操作,只读操作不需要事务操作
     */
    @Override
    @Transactional
    public SeckillExecution executeSeckill(StockOrder stockOrder, String md5)
            throws SeckillException, RepeatKillException, SeckillCloseException {

        System.out.println("stockOrder "+ stockOrder.toString() + " "+"md5 " + md5);
        System.out.println();

        //为空或者不等于
//        if (md5 == null || !md5.equals(getMd5(stockOrder.getStockId()))) {
//            throw new SeckillException("seckill data rewrite");
//        }

        //获取服务器当前准确时间
        Date currentTime = new Date();

        /**
         * 秒杀逻辑：减库存 + 记录购买行为
         * 优化：减少行级锁的持有时间
         * 先insert——> update ——> 是否秒杀成功
         */
        try {
            //记录购买行为
            //int insertCount = stockOrderMapper.insertStockerOrder(stockOrder);

            int insertCount = 0;
            if (insertCount <= 0) {
                //重复秒杀
                log.warn("重复秒杀");
                throw new RepeatKillException("seckill repeated");
            } else {
                //减少库存
                //int updateCount = stockGoodsMapper.reduceNumber(stockOrder.getStockId(), currentTime);
                    int updateCount = 1;
                if (updateCount <= 0) {
                    //没有更新到记录,秒杀结束
                    log.warn("没有更新数据库记录,说明抢票结束");
                    throw new SeckillCloseException("seckill is closed");
                } else {
                    //抢票成功
                    //return new SeckillExecution(stockOrder.getStockId(), SeckillStateEnum.SUCCESS, stockOrder);
                    return new SeckillExecution("333333", SeckillStateEnum.SUCCESS, stockOrder);

                }
            }

        } catch (SeckillCloseException e1) {
            //直接抛出
            throw e1;
        } catch (RepeatKillException e2) {
            //直接抛出
            throw e2;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new SeckillException("system inner error:" + e.getMessage());
        }
    }


    @Override
    public StockOrder createSeckillOrder(String stockGoodId, String buyerId) {
        //使用规则值自动生成订单编码
        String proId = (String) FillRuleUtil.executeRule("order_code_num", null);

        return null;
    }

    @Override
    @Transactional
    public SeckillExecution executeSeckill(String stockGoodId, String md5) throws SeckillException, RepeatKillException, SeckillCloseException {

        try {
//            LoginUser sysUser = (LoginUser)SecurityUtils.getSubject().getPrincipal();
//            System.out.println("4" + sysUser.toString());
//            System.out.println();

            //System.out.println("currentUserId: " + currentUserId);

            String currentUserId = "e9ca23d68d884d4ebb19d07889727dae";

            //记录购买行为-完成对三张表的操作
            //订单-票商品表
            StockOrderGood stockOrderGood = new StockOrderGood();
            StockGoods stockGood = stockGoodsMapper.getStockGoodById(stockGoodId);

            System.out.println();
            System.out.println(stockGood.toString());

            //订单表
            Double price = stockGood.getPrice();
            String orderId = (String) FillRuleUtil.executeRule("order_code_num", null);
            StockOrder order = new StockOrder(orderId, new Date(), price);
            order.setUserId(currentUserId);
            order.setGoodsId(stockGoodId);
            String id = orderId.substring(3);
            order.setId(id);

            System.out.println("order " + order.toString());
            System.out.println();

            //忽略主键冲突
            int insertCount = stockOrderMapper.insertIgnore(order);
            if (insertCount <= 0) {
                //重复秒杀
                log.warn("重复秒杀");
                throw new RepeatKillException("重复抢票");
            } else {
                stockOrderGood.setProId(stockGood.getProId());
                stockOrderGood.setTitle(stockGood.getTitle());
                stockOrderGood.setDescription(stockGood.getDescription());
                stockOrderGood.setPrice(stockGood.getPrice());
                stockOrderGood.setTotalMoney(stockGood.getPrice());
                stockOrderGood.setOrderMainId(id);

                System.out.println("stockOrderGood " + stockOrderGood.toString());

                stockOrderGoodMapper.insert(stockOrderGood);

                //给之前订单-票商品更新orderMainId
//            stockOrderGoodMapper.update(new StockOrderGood().setOrderMainId(orderId),
//                    new LambdaQueryWrapper<StockOrderGood>().eq(StockOrderGood::getTitle, stockGood.getTitle()));

                //订单-消费者表
                System.out.println(".....currentUserId " + currentUserId);
                OrderCustomer orderCustomer = userMapper.selectMessageForOrderCustomer(currentUserId);
                orderCustomer.setOrderMainId(id);

                System.out.println("orderCustomer " + orderCustomer);

                orderCustomerMapper.insert(orderCustomer);

                //使用乐观锁插入-减少库存
                int updateCount = stockGoodsMapper.updateByOptimisticLock(stockGood);
                if (updateCount <= 0) {
                    //没有更新到记录,秒杀结束
                    log.warn("没有更新数据库记录,说明抢票结束");
                    throw new SeckillCloseException("抢票已结束");
                } else {
                    //抢票成功
                    new SeckillExecution(stockGoodId, SeckillStateEnum.SUCCESS, order);
                }
            }

        } catch (SeckillCloseException e1) {
            //直接抛出
            throw e1;
        } catch (RepeatKillException e2) {
            //直接抛出
            throw e2;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new SeckillException("系统内部错误:" + e.getMessage());
        }
        return new SeckillExecution(stockGoodId, SeckillStateEnum.SUCCESS);
    }

    /**
     * 获取当前登陆的用户id->即买家id
     * session.getAttribute("userId")->buyerId
     */
    @Override
    public StockOrder getSeckillOrder(String stockGoodId, String buyerId) {

        //数据库id
        String id = UUID.randomUUID().toString().substring(24);
        System.out.println("id " + id);
        //生成唯一的订单编码
        String orderId = OrderNumberRule.getOrderCode();
        System.out.println("orderId " + orderId);
        //通过票商品id查询卖家id
        String sellerId = userGoodMapper.getSellerIdByGoodId(stockGoodId);
        if (oConvertUtils.isEmpty(sellerId)) {
            sellerId = "";
        }
        System.out.println("sellerId " + sellerId);
        //总价
        Double price = Double.valueOf(stockGoodsMapper.getPriceById(stockGoodId));
        System.out.println("price " + price);
        //创建人
        String createBy = userMapper.getUserNameById(buyerId);
        System.out.println("createBy " + createBy);
        //StockOrder stockOrder = new StockOrder(id, orderId, stockGoodId, sellerId, buyerId, price);
        StockOrder stockOrder = new StockOrder();
        return stockOrder;
    }

    //--------------------------------------限时间抢票-------------------------------------------

    @Override
    public int deleteOrderDBBefore() {
        //return stockOrderMapper.deleteOrderDBBefore();
        return 1;
    }

    @Override
    public int createWrongOrder(String stockGoodId) throws Exception {
        //检查库存
        StockGoods stockGood = checkStock(stockGoodId);
        //扣库存
        saleStock(stockGood);
        //创建订单
        return createOrder(stockGood);
    }

    @Override
    public int createOptimisticLockOrder(String stockGoodId) throws Exception {
        //检查库存
        StockGoods stock = checkStock(stockGoodId);
        //乐观锁更新 扣库存
        saleStockOptimsticLock(stock);
        //创建订单
        return createOrder(stock);
    }

    @Override
    public int createOrderWithLimitByRedis(String stockGoodId) throws Exception {
        //检查库存 from redis
        StockGoods stock = checkStockWithRedis(stockGoodId);
        //乐观锁更新db and redis
        saleStockOptimsticLockByRedis(stock);
        //创建订单
        return createOrder(stock);
    }

    @Override
    public SeckillExecution createOrderWithLimitByRedis2(String stockGoodId, String md5) {

        //为空或者不等于
        if (md5 == null || !md5.equals(getMd5(stockGoodId))) {
            throw new SeckillException("seckill stockGood's data rewrite");
        }

        try {
            //从redis中检查库存是否有余 否则直接抢票结束
            StockGoods stockGood = checkStockWithRedis(stockGoodId);
            //乐观锁更新db and redis-存在抢票结束
            saleStockOptimsticLockByRedis(stockGood);
            //创建订单-存在重复抢票
            StockOrder stockOrder = createOrder2(stockGood);
            //抢票成功
            //return new SeckillExecution(stockOrder.getStockId(), SeckillStateEnum.SUCCESS, stockOrder);
            return new SeckillExecution("333", SeckillStateEnum.SUCCESS, stockOrder);
        } catch (SeckillCloseException e1) {
            //直接抛出
            throw e1;
        } catch (RepeatKillException e2) {
            //直接抛出
            throw e2;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new SeckillException("system inner error:" + e.getMessage());
        }
    }


    @Override
    public void createOrderWithLimitByRedisAndKafka(String stockGoodId) throws Exception {
        //检查库存 from redis
        StockGoods stockGood = checkStockWithRedis(stockGoodId);
        //下单请求发送到kafka,需要序列化stock
        kafkaTemplate.send(kafkaTopic, gson.toJson(stockGood));
        log.info("消息发送至 Kafka 成功");
    }

    @Override
    public int consumerNewsToCreateOrderWithLimitByRedisAndKafka(StockGoods stock) throws Exception {
        saleStockOptimsticLock(stock);
        int res = createOrder(stock);
        if (UPDATE_SUCCESS.equals(res)) {
            log.info("Kafka 消费 Topic 创建订单成功");
        }else {
            log.info("Kafka 消费 Topic 创建订单失败");
        }
        return res;
    }

    @Override
    public int testOptimistic(StockGoods stockGoods) {
        return stockGoodsMapper.updateByOptimisticLock(stockGoods);
    }


    /**
     * 检查库存
     * @param stockGoodId
     * @return
     */
    private StockGoods checkStock(String stockGoodId) {
        StockGoods stock = stockGoodsMapper.selectByPrimaryKey(stockGoodId);
        if (stock.getInventory() < 1) {
            throw new RuntimeException("库存不足");
        }
        return stock;
    }

    /**
     * 扣库存
     * @param stockGood
     * @return
     */
    private int saleStock(StockGoods stockGood) {
        stockGood.setInventory(stockGood.getInventory()- 1);
        stockGood.setSale(stockGood.getSale() + 1);
        return stockGoodsMapper.updateByPrimaryKey(stockGood);
    }

    /**
     * 乐观锁扣库存
     * @param stockGood
     */
    private void saleStockOptimsticLock(StockGoods stockGood) {
        if (stockGoodsMapper.updateByOptimisticLock(stockGood) == 0) {
            throw new RuntimeException("并发更新库存失败");
        }

       // return stockGoodsMapper.updateByOptimisticLock(stockGood);
    }

    /**
     * 创建订单(使用联合主键 stockId 和 buyerId)
     * @param stockGood
     * @return
     */
    private StockOrder createOrder2(StockGoods stockGood) {
        //获取票商品id和当前登陆用户即买家id -> 创建订单
        LoginUser currentUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        System.out.println("SecurityUtils.getSubject " + currentUser.toString());

        StockOrder stockOrder = getSeckillOrder(stockGood.getId(), currentUser.getId());
        //int updateRecord = stockOrderMapper.insertStockerOrder(stockOrder);
        if (UPDATE_FAIL.equals(11)) {
            log.warn("重复秒杀");
            throw new RepeatKillException("seckill repeated");
        }
        return stockOrder;
    }


    /**
     * 创建订单
     */
    private int createOrder(StockGoods stockGood) {
        LoginUser currentUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        System.out.println("SecurityUtils.getSubject " + currentUser.toString());

        StockOrder stockOrder = getSeckillOrder(stockGood.getId(), currentUser.getId());
        //int updateRecord = stockOrderMapper.insertStockerOrder(stockOrder);
        if (UPDATE_FAIL.equals(11)) {
            log.warn("重复秒杀");
            throw new RepeatKillException("seckill repeated");
        }
        return 11;
    }

    /**
     * redis 校验库存
     */
    private StockGoods checkStockWithRedis(String stockGoodId) {
        Integer inventory = Integer.parseInt(RedisPoolUtil.get(RedisKeyEnum.STOCK_INVENTORY + stockGoodId));
        Integer sale = Integer.parseInt(RedisPoolUtil.get(RedisKeyEnum.STOCK_SALE + stockGoodId));
        Integer version = Integer.parseInt(RedisPoolUtil.get(RedisKeyEnum.STOCK_VERSION + stockGoodId));
        if (inventory < 1) {
            //库存消耗完-结束抢票
            log.warn("库存不足");
            throw new SeckillCloseException("seckill is closed");
        }
        StockGoods stockGood = new StockGoods(stockGoodId, inventory, sale, version);
        return stockGood;
    }

    /**
     * 更新db and redis
     */
    private void saleStockOptimsticLockByRedis(StockGoods stock) {
        if (UPDATE_FAIL.equals(stockGoodsMapper.updateByOptimisticLock(stock))) {
            log.warn("没有更新数据库记录,说明抢票结束");
            throw new SeckillCloseException("seckill is closed");
        }
        //更新redis
        StockByRedis.updateStockByRedis(stock);
    }

}
