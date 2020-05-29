package org.jeecg.modules.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.PermissionData;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.system.entity.GoodType;
import org.jeecg.modules.system.service.IGoodTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

/**
 * 票商品分类管理
 */
@Slf4j
@RestController
@RequestMapping("/goodType")
public class GoodTypeController extends JeecgController<GoodType, IGoodTypeService> {

    @Autowired
    private IGoodTypeService goodTypeService;

    /**
     * 分页列表查询
     *
     * @param goodType
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @GetMapping(value = "/rootList")
    @PermissionData(pageComponent = "modules/goodType/GoodTypeList")
    public Result<?> queryPageList(GoodType goodType,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        String parentId = goodType.getPid();
        if (oConvertUtils.isEmpty(parentId)) {
            parentId = "0";
        }
        goodType.setPid(null);
        QueryWrapper<GoodType> queryWrapper = QueryGenerator.initQueryWrapper(goodType, req.getParameterMap());
        // 使用 eq 防止模糊查询
        queryWrapper.eq("pid", parentId);
        Page<GoodType> page = new Page<>(pageNo, pageSize);
        IPage<GoodType> pageList = goodTypeService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    /**
     * 获取子数据
     */
    @GetMapping(value = "/childList")
    public Result<?> queryPageList(GoodType goodType, HttpServletRequest req) {
        QueryWrapper<GoodType> queryWrapper = QueryGenerator.initQueryWrapper(goodType, req.getParameterMap());
        List<GoodType> list = goodTypeService.list(queryWrapper);
        return Result.ok(list);
    }


    /**
     * 添加
     */
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody GoodType goodType) {
        goodTypeService.addGoodType(goodType);
        return Result.ok("添加成功！");
    }

    /**
     * 编辑
     */
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody GoodType goodType) {
        goodTypeService.updateGoodType(goodType);
        return Result.ok("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        goodTypeService.deleteGoodType(id);
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.goodTypeService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok("批量删除成功！");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        GoodType goodType = goodTypeService.getById(id);
        if (goodType == null) {
            return Result.error("未找到对应数据");
        }
        return Result.ok(goodType);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param goodType
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, GoodType goodType) {
        return super.exportXls(request, goodType, GoodType.class, "票商品分类");
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
        return super.importExcel(request, response, GoodType.class);
    }

}
