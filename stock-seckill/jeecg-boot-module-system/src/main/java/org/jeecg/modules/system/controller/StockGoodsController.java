package org.jeecg.modules.system.controller;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.aspect.annotation.PermissionData;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.FillRuleUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.system.entity.StockGoods;
import org.jeecg.modules.system.service.IStockGoodsService;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 票商品表
 */
@Api(tags = "票商品表")
@RestController
@RequestMapping("/goods/stockGoods")
@Slf4j
public class StockGoodsController extends JeecgController<StockGoods, IStockGoodsService> {

    @Autowired
    private IStockGoodsService stockGoodsService;

    /**
     * 分页列表查询
     *
     * @param stockGoods
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "票商品表-分页列表查询")
    @ApiOperation(value = "票商品表-分页列表查询", notes = "票商品表-分页列表查询")
    @GetMapping(value = "/list")
    @PermissionData(pageComponent = "modules/goods/stockGoods")
    public Result<?> queryPageList(StockGoods stockGoods,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<StockGoods> queryWrapper = QueryGenerator.initQueryWrapper(stockGoods, req.getParameterMap());
        Page<StockGoods> page = new Page<>(pageNo, pageSize);
        IPage<StockGoods> pageList = stockGoodsService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    /**
     * 添加
     *
     * @param stockGoods
     * @return
     */
    @AutoLog(value = "票商品表-添加")
    @ApiOperation(value = "票商品表-添加", notes = "票商品表-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody StockGoods stockGoods) {
        //使用规则值自动生成票商品编码
        String proId = (String) FillRuleUtil.executeRule("food_code_num", null);
        stockGoods.setProId(proId);
        stockGoodsService.save(stockGoods);
        return Result.ok("添加成功！");
    }

    /**
     * 编辑
     *
     * @param stockGoods
     * @return
     */
    @AutoLog(value = "票商品表-编辑")
    @ApiOperation(value = "票商品表-编辑", notes = "票商品表-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody StockGoods stockGoods) {
        stockGoodsService.updateById(stockGoods);
        return Result.ok("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "票商品表-通过id删除")
    @ApiOperation(value = "票商品表-通过id删除", notes = "票商品表-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        stockGoodsService.deleteGood(id);
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "票商品表-批量删除")
    @ApiOperation(value = "票商品表-批量删除", notes = "票商品表-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        Result<StockGoods> result = new Result<>();
        if (oConvertUtils.isEmpty(ids)) {
            result.error500("未选中票商品！");
        } else {
            stockGoodsService.deleteBatchGood(ids.split(","));
            result.success("删除票商品成功!");
        }
        return result;
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "票商品表-通过id查询")
    @ApiOperation(value = "票商品表-通过id查询", notes = "票商品表-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        StockGoods stockGoods = stockGoodsService.getById(id);
        if (stockGoods == null) {
            return Result.error("未找到对应数据");
        }
        return Result.ok(stockGoods);
    }

    /**
     * 禁止出售和解禁出售该票商品
     *
     * @param jsonObject
     * @return
     */
    @PutMapping(value = "/disableBatch")
    public Result<StockGoods> disableBatch(@RequestBody JSONObject jsonObject) {
        Result<StockGoods> result = new Result<>();
        try {
            String ids = jsonObject.getString("ids");
            String status = jsonObject.getString("status");
            String[] arr = ids.split(",");
            for (String id : arr) {
                if (oConvertUtils.isNotEmpty(id)) {
                    stockGoodsService.update(new StockGoods().setStatus(Integer.parseInt(status)),
                            new UpdateWrapper<StockGoods>().lambda().eq(StockGoods::getId, id));
                }
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败" + e.getMessage());
        }
        result.success("操作成功!");
        return result;
    }
}
