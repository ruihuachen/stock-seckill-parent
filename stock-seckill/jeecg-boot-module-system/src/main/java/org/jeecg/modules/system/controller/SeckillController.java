package org.jeecg.modules.system.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.system.entity.StockGoods;
import org.jeecg.modules.system.entity.StockOrder;
import org.jeecg.modules.system.enums.SeckillStateEnum;
import org.jeecg.modules.system.exception.RepeatKillException;
import org.jeecg.modules.system.exception.SeckillCloseException;
import org.jeecg.modules.system.exception.SeckillException;
import org.jeecg.modules.system.manager.CurrentLimitByRedis;
import org.jeecg.modules.system.manager.StockByRedis;
import org.jeecg.modules.system.service.ISeckillService;
import org.jeecg.modules.system.service.IStockGoodsService;
import org.jeecg.modules.system.vo.Exposer;
import org.jeecg.modules.system.vo.SeckillExecution;
import org.jeecg.modules.system.vo.SeckillResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;

/**
 * 限时有效抢票
 */
@Slf4j
@Controller
@Api(tags = "前台用户抢票")
@RequestMapping(value = "/seckill")
public class SeckillController {

    @Autowired
    private ISeckillService seckillService;

    @Autowired
    private IStockGoodsService stockGoodsService;

    /**
     * 前台-限时票商品展示
     *
     * @param model
     * @return
     */
    @GetMapping(value = "/list")
    public String list(Model model) {
        //获取列表页 TODO：分页-查询
        List<StockGoods> list = seckillService.getSeckillList();
        model.addAttribute("list", list);
        return "list";
    }

    /**
     * 前台-票商品详情页
     *
     * @param seckillId
     * @param model
     * @return
     */
    @GetMapping(value = "/{seckillId}/detail")
    public String detail(@PathVariable("seckillId") String seckillId, Model model) {
        if (seckillId == null) {
            return "redirect:/seckill/list";
        }

        StockGoods stockGood = stockGoodsService.getById(seckillId);
        if (stockGood == null) {
            return "forward:/seckill/list";
        }

        System.out.println(stockGood.toString() + "\n");

        model.addAttribute("stockGood", stockGood);
        return "detail";
    }

    /**
     * 暴露隐藏的真实抢票URL接口
     * 根据服务器当前时间判断是否可以暴露
     *
     * @param stockGoodId
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/{stockGoodId}/exposer", produces = {"application/json;charset=UTF-8"})
    public SeckillResult<Exposer> exposer(@PathVariable String stockGoodId) {
        SeckillResult<Exposer> result;

        try {
            Exposer exposer = seckillService.exportSeckillUrl(stockGoodId);
            result = new SeckillResult<>(true, exposer);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result = new SeckillResult<>(false, e.getMessage());
        }
        return result;
    }

    @PostMapping(value = "/{stockGoodId}/{md5}/execute", produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<SeckillExecution> execute(@PathVariable("stockGoodId")String stockGoodId,
                                                   @PathVariable("md5")String md5,
                                                   HttpSession session) {


        System.out.println("-------------------------------进入抢票-----------------------------------------");
        System.out.println();

        //打印前端传入的字段
        System.out.println(stockGoodId + " "+md5+" ");

        try {
            //获取票商品id和当前登陆用户即买家id -> 创建订单
            LoginUser currentUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            System.out.println("SecurityUtils.getSubject " + currentUser.toString());

            //先创建订单
            StockOrder stockOrder = seckillService.getSeckillOrder(stockGoodId, currentUser.getId());
            //在执行抢票
            SeckillExecution execution = seckillService.executeSeckill(stockOrder, md5);
            return new SeckillResult<>(true, execution);
        } catch (RepeatKillException e) {
            SeckillExecution execution = new SeckillExecution(stockGoodId, SeckillStateEnum.REPEAT_KILL);
            return new SeckillResult<>(true, execution);

        } catch (SeckillCloseException e) {
            SeckillExecution execution = new SeckillExecution(stockGoodId, SeckillStateEnum.END);
            return new SeckillResult<>(true, execution);

        } catch (SeckillException e) {
            log.error(e.getMessage(), e);
            SeckillExecution execution = new SeckillExecution(stockGoodId, SeckillStateEnum.INNER_ERROR);
            return new SeckillResult<>(true, execution);
        }
    }

    /**
     * 获取服务器端时间,防止用户篡改客户端时间提前参与秒杀
     *
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/time/now")
    public SeckillResult<Long> time() {
        Date date = new Date();
        return new SeckillResult<>(true, date.getTime());
    }

    //@PostMapping(value = "/{stockGoodId}/{md5}/execute", produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<SeckillExecution> execute(@PathVariable("stockGoodId") String stockGoodId,
                                                   @PathVariable("md5") String md5) {

        //打印前端传入的字段
        System.out.println(stockGoodId + " " + md5 + " ");

        try {
            SeckillExecution execution = seckillService.createOrderWithLimitByRedis2(stockGoodId, md5);
            return new SeckillResult<>(true, execution);
        } catch (RepeatKillException e) {
            SeckillExecution execution = new SeckillExecution(stockGoodId, SeckillStateEnum.REPEAT_KILL);
            return new SeckillResult<>(true, execution);

        } catch (SeckillCloseException e) {
            SeckillExecution execution = new SeckillExecution(stockGoodId, SeckillStateEnum.END);
            return new SeckillResult<>(true, execution);

        } catch (SeckillException e) {
            log.error(e.getMessage(), e);
            SeckillExecution execution = new SeckillExecution(stockGoodId, SeckillStateEnum.INNER_ERROR);
            return new SeckillResult<>(true, execution);
        }
    }


    private static final String success = "SUCCESS";
    private static final String error = "ERROR";
    private static final Integer SUCCESS = 1;


    /**
     * 压测前先请求该方法，初始化数据库缓存
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "initDBAndRedis", method = RequestMethod.GET)
    public String intiDBAndRedisBefore(HttpServletRequest request) {

        System.out.println("-------------------压测before----------------------");
        System.out.println();

        int res = 0;
        try {
            //将所有票商品的库存设置50，sale = 0， versio = 0
            res = stockGoodsService.initDB();
            //测试：将id = 1的票商品缓存在redis
            StockByRedis.initRedisBefore();
        } catch (Exception e) {
            log.error("Exception: ", e);
        }
        if (res == 1) {
            log.info("重置数据库和缓存成功！");
        }
        return res == 1 ? success : error;
    }


    /**
     * 秒杀基础逻辑，存在超卖问题
     *
     * @param sid 票商品唯一标识id
     * @return
     */
    @RequestMapping(value = "createWrongOrder", method = RequestMethod.POST)
    public String createWrongOrder(String sid) {

        System.out.println("createWrongOrder..............");
        System.out.println();

        int res = 0;
        try {
            res = seckillService.createWrongOrder(sid);
        } catch (Exception e) {
            log.error("Exception: " + e);
        }
        return res == 1 ? success : error;
    }

    /**
     * 乐观锁扣库存
     *
     * @param request
     * @param sid
     * @return
     */
    @RequestMapping(value = "createOptimisticOrder", method = RequestMethod.POST)
    public String createOptimisticOrder(HttpServletRequest request, String sid) {

        System.out.println("createOptimisticOrder..................");

        int res = 0;
        try {
            res = seckillService.createOptimisticLockOrder(sid);
        } catch (Exception e) {
            log.error("Exception: " + e);
        }
        return SUCCESS.equals(res) ? success : error;
    }

    /**
     * 限流 + 乐观锁
     *
     * @param request
     * @param sid
     * @return
     */
    @RequestMapping(value = "createOptimisticOrderByRedisLimit", method = RequestMethod.POST)
    public String createOptimisticOrderByRedisLimit(HttpServletRequest request, String sid) {

        System.out.println("CreateOptimisticOrderByRedisLimit....................");

        int res = 0;
        try {
            if (CurrentLimitByRedis.limit()) {
                res = seckillService.createOptimisticLockOrder(sid);
                if (SUCCESS.equals(res)) {
                    log.info("秒杀成功");
                }
            }
        } catch (Exception e) {
            log.error("Exception: " + e);
        }
        return SUCCESS.equals(res) ? success : error;
    }

    /**
     * 限流 + 预先redis缓存库存 -> 每次减库存 访问redis检查库存情况 -> 更新db和redis
     * ps:需要在RedisPreheatRunner 做缓存预热，需要 stock.id = 1
     * 需改 使用定时任务进行缓存预热
     *
     * @param request
     * @param sid
     * @return
     */
    @RequestMapping(value = "createOrderWithLimitByRedis", method = RequestMethod.GET)
    public String createOrderWithLimitByRedis(HttpServletRequest request, String sid) {

        System.out.println("进入 limitByRedis................ ");
        System.out.println(sid);

        int res = 0;
        try {
            if (CurrentLimitByRedis.limit()) {
                res = seckillService.createOrderWithLimitByRedis(sid);
                if (SUCCESS.equals(res)) {
                    log.info("抢票成功");
                }
            }
        } catch (Exception e) {
            log.error("Exception: " + e);
        }
        return SUCCESS.equals(res) ? success : error;
    }

    /**
     * 限流 + redis缓存库存 + kafkaTest 异步下单
     *
     * @param request
     * @param sid
     * @return
     */
    @RequestMapping(value = "createOrderWithLimitByRedisAndKafka", method = RequestMethod.GET)
    public String createOrderWithLimitByRedisAndKafka(HttpServletRequest request, String sid) {
        System.out.println("进入 kafka");

        try {
            if (CurrentLimitByRedis.limit()) {
                seckillService.createOrderWithLimitByRedisAndKafka(sid);
            }
        } catch (Exception e) {
            log.error("Exception: " + e);
        }
        return "秒杀请求正在处理，排队中";
    }

}
