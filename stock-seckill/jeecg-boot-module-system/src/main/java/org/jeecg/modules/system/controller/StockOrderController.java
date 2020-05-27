package org.jeecg.modules.system.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.aspect.annotation.PermissionData;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.FillRuleUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.system.entity.OrderCustomer;
import org.jeecg.modules.system.entity.StockOrder;
import org.jeecg.modules.system.entity.StockOrderGood;
import org.jeecg.modules.system.service.IOrderCustomerService;
import org.jeecg.modules.system.service.IStockOrderGoodService;
import org.jeecg.modules.system.vo.StockOrderPage;
import org.jeecg.modules.system.service.IStockOrderService;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 订单表
 */
@Api(tags = "订单表")
@RestController
@RequestMapping("/stockOrder")
@Slf4j
public class StockOrderController {

    @Autowired
    private IStockOrderService stockOrderService;

    @Autowired
    private IStockOrderGoodService stockOrderGoodService;

    @Autowired
    private IOrderCustomerService orderCustomerService;

    /**
     * 分页列表查询
     *
     * @param stockOrder
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @PermissionData(pageComponent = "modules/stockOrder/StockOrderList")
    @AutoLog(value = "订单表-分页列表查询")
    @ApiOperation(value = "订单表-分页列表查询", notes = "订单表-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(StockOrder stockOrder,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<StockOrder> queryWrapper = QueryGenerator.initQueryWrapper(stockOrder, req.getParameterMap());
        Page<StockOrder> page = new Page<>(pageNo, pageSize);
        IPage<StockOrder> pageList = stockOrderService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    /**
     * 添加
     *
     * @param stockOrderPage
     * @return
     */
    @AutoLog(value = "订单表-添加")
    @ApiOperation(value = "订单表-添加", notes = "订单表-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody StockOrderPage stockOrderPage) {

        StockOrder stockOrder = new StockOrder();
        //将有数值的属性复制在指定的对象
        BeanUtils.copyProperties(stockOrderPage, stockOrder);
        //使用规则值自动生成订单编码
        String orderId = (String)FillRuleUtil.executeRule("order_code_num", null);
        stockOrder.setOrderId(orderId);

        stockOrderService.saveMain(stockOrder, stockOrderPage.getStockOrderGoodList(),
                stockOrderPage.getOrderCustomerList());
        return Result.ok("添加成功！");
    }

    /**
     * 编辑
     *
     * @param stockOrderPage
     * @return
     */
    @AutoLog(value = "订单表-编辑")
    @ApiOperation(value = "订单表-编辑", notes = "订单表-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody StockOrderPage stockOrderPage) {
        StockOrder stockOrder = new StockOrder();
        BeanUtils.copyProperties(stockOrderPage, stockOrder);
        StockOrder stockOrderEntity = stockOrderService.getById(stockOrder.getId());
        if (stockOrderEntity == null) {
            return Result.error("未找到对应数据");
        }
        stockOrderService.updateMain(stockOrder, stockOrderPage.getStockOrderGoodList(), stockOrderPage.getOrderCustomerList());
        return Result.ok("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "订单表-通过id删除")
    @ApiOperation(value = "订单表-通过id删除", notes = "订单表-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        stockOrderService.delMain(id);
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "订单表-批量删除")
    @ApiOperation(value = "订单表-批量删除", notes = "订单表-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.stockOrderService.delBatchMain(Arrays.asList(ids.split(",")));
        return Result.ok("批量删除成功！");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "订单表-通过id查询")
    @ApiOperation(value = "订单表-通过id查询", notes = "订单表-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        StockOrder stockOrder = stockOrderService.getById(id);
        if (stockOrder == null) {
            return Result.error("未找到对应数据");
        }
        return Result.ok(stockOrder);

    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "订单票商品表集合-通过id查询")
    @ApiOperation(value = "订单票商品表集合-通过id查询", notes = "订单票商品表-通过id查询")
    @GetMapping(value = "/queryStockOrderGoodByMainId")
    public Result<?> queryStockOrderGoodListByMainId(@RequestParam(name = "id", required = true) String id) {
        List<StockOrderGood> stockOrderGoodList = stockOrderGoodService.selectByMainId(id);
        return Result.ok(stockOrderGoodList);
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "订单消费者表集合-通过id查询")
    @ApiOperation(value = "订单消费者表集合-通过id查询", notes = "订单消费者表-通过id查询")
    @GetMapping(value = "/queryOrderCustomerByMainId")
    public Result<?> queryOrderCustomerListByMainId(@RequestParam(name = "id", required = true) String id) {
        List<OrderCustomer> orderCustomerList = orderCustomerService.selectByMainId(id);
        return Result.ok(orderCustomerList);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param stockOrder
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, StockOrder stockOrder) {
        // Step.1 组装查询条件查询数据
        QueryWrapper<StockOrder> queryWrapper = QueryGenerator.initQueryWrapper(stockOrder, request.getParameterMap());
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        //Step.2 获取导出数据
        List<StockOrder> queryList = stockOrderService.list(queryWrapper);
        // 过滤选中数据
        String selections = request.getParameter("selections");
        List<StockOrder> stockOrderList = new ArrayList<StockOrder>();
        if (oConvertUtils.isEmpty(selections)) {
            stockOrderList = queryList;
        } else {
            List<String> selectionList = Arrays.asList(selections.split(","));
            stockOrderList = queryList.stream().filter(item -> selectionList.contains(item.getId())).collect(Collectors.toList());
        }

        // Step.3 组装pageList
        List<StockOrderPage> pageList = new ArrayList<StockOrderPage>();
        for (StockOrder main : stockOrderList) {
            StockOrderPage vo = new StockOrderPage();
            BeanUtils.copyProperties(main, vo);
            List<StockOrderGood> stockOrderGoodList = stockOrderGoodService.selectByMainId(main.getId());
            vo.setStockOrderGoodList(stockOrderGoodList);
            List<OrderCustomer> orderCustomerList = orderCustomerService.selectByMainId(main.getId());
            vo.setOrderCustomerList(orderCustomerList);
            pageList.add(vo);
        }

        // Step.4 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, "订单表列表");
        mv.addObject(NormalExcelConstants.CLASS, StockOrderPage.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("订单表数据", "导出人:" + sysUser.getRealname(), "订单表"));
        mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
        return mv;
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();// 获取上传文件对象
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                List<StockOrderPage> list = ExcelImportUtil.importExcel(file.getInputStream(), StockOrderPage.class, params);
                for (StockOrderPage page : list) {
                    StockOrder po = new StockOrder();
                    BeanUtils.copyProperties(page, po);
                    stockOrderService.saveMain(po, page.getStockOrderGoodList(), page.getOrderCustomerList());
                }
                return Result.ok("文件导入成功！数据行数:" + list.size());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return Result.error("文件导入失败:" + e.getMessage());
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return Result.ok("文件导入失败！");
    }

    /**
     * 订单表状态的变更
     * @param jsonObject
     * @return
     */
    @PutMapping(value = "/stateOfOperation")
    public Result<StockOrder> disableBatch(@RequestBody JSONObject jsonObject) {
        Result<StockOrder> result = new Result<>();
        try {
            String ids = jsonObject.getString("ids");
            String status = jsonObject.getString("status");
            String[] arr = ids.split(",");
            for (String id : arr) {
                if (oConvertUtils.isNotEmpty(id)) {
                    stockOrderService.updateStatus(Integer.valueOf(status), id);
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
