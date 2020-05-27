package org.jeecg.modules.system.controller;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.util.JwtUtil;
import org.jeecg.common.util.PasswordUtil;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.system.entity.StockGoods;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.IStockGoodsService;
import org.jeecg.modules.system.service.ISysLogService;
import org.jeecg.modules.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RequestMapping("/frontDesk")
@Api(tags = "前台展示")
@Slf4j
@Controller
public class FdLoginController {

    @Autowired
    private IStockGoodsService stockGoodsService;

    /**
     * 前台展示票商品列表
     */
    @GetMapping(value = "/list")
    public String showGoodList(Model model) {
        System.out.println("-----------------------showList--------------------");
        System.out.println();
        List<StockGoods> list = stockGoodsService.getStockGoods();

        System.out.println(list.toString());
        System.out.println();
        model.addAttribute("list", list);
        return "list";
    }

    /**
     * 前台票商品详情页
     */
    @GetMapping(value = "/{stockGoodId}/detail")
    public String detail(@PathVariable("stockGoodId") String stockGoodId, Model model) {
        if (stockGoodId == null) {
            return "redirect:/frontDesk/list";
        }

        StockGoods stockGood = stockGoodsService.getById(stockGoodId);
        if (stockGood == null) {
            return "forward:/frontDesk/list";
        }

        System.out.println("-----------------------------详情页---------------------------------");
        System.out.println(stockGood.toString() + "\n\n");

        model.addAttribute("stockGood", stockGood);
        return "detail";
    }
}
