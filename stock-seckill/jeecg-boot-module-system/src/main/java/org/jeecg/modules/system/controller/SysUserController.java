package org.jeecg.modules.system.controller;


import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.PermissionData;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.util.JwtUtil;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.PasswordUtil;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.entity.SysUserGood;
import org.jeecg.modules.system.entity.SysUserRole;
import org.jeecg.modules.system.service.ISysUserGoodService;
import org.jeecg.modules.system.service.ISysUserRoleService;
import org.jeecg.modules.system.service.ISysUserService;
import org.jeecg.modules.system.vo.SysUserGoodVO;
import org.jeecg.modules.system.vo.SysUserRoleVO;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * 用户表
 */
@Slf4j
@RestController
@RequestMapping("/sys/user")
public class SysUserController {
    @Autowired
    private ISysBaseAPI sysBaseAPI;

    @Autowired
    private ISysUserService sysUserService;

    @Autowired
    private ISysUserRoleService sysUserRoleService;

    @Autowired
    private ISysUserGoodService sysUserGoodService;

    @Autowired
    private RedisUtil redisUtil;

    //查询（所有用户以及条件查询）
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @PermissionData(pageComponent = "system/UserList")
    public Result<IPage<SysUser>> queryPageList(SysUser user,
                                                @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                HttpServletRequest req) {

        System.out.println("-------------------查询测试----------------------");
        System.out.println(user.toString());
        System.out.println();

        Result<IPage<SysUser>> result = new Result<>();
        QueryWrapper<SysUser> queryWrapper = QueryGenerator.initQueryWrapper(user, req.getParameterMap());
        Page<SysUser> page = new Page<>(pageNo, pageSize);
        IPage<SysUser> pageList = sysUserService.page(page, queryWrapper);

        result.setSuccess(true);
        result.setResult(pageList);
        log.info(pageList.toString());
        return result;
    }

    /**
     * 新增用户
     *
     * @param jsonObject
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @RequiresPermissions("user:add")
    public Result<SysUser> add(@RequestBody JSONObject jsonObject) {
        Result<SysUser> result = new Result<>();
        String selectedRoles = jsonObject.getString("selectedroles");
        String currentGoodId = jsonObject.getString("currentGoodId");
        try {
            SysUser user = JSON.parseObject(jsonObject.toJSONString(), SysUser.class);
            //设置创建时间
            user.setCreateTime(new Date());
            String salt = oConvertUtils.randomGen(8);
            user.setSalt(salt);
            String passwordEncode = PasswordUtil.encrypt(user.getUsername(), user.getPassword(), salt);
            user.setPassword(passwordEncode);
            //用户状态：1-正常 2-冻结
            user.setStatus(1);
            user.setDelFlag("0");
            sysUserService.addUserWithRole(user, selectedRoles);
            if (oConvertUtils.isNotEmpty(currentGoodId)) {
                sysUserGoodService.save(new SysUserGood(user.getId(), currentGoodId));
            }
            result.success("添加成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     * 删除用户
     */
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        sysBaseAPI.addLog("删除用户，id： " + id, CommonConstant.LOG_TYPE_2, 3);
        sysUserService.deleteUser(id);
        return Result.ok("删除用户成功");
    }

    /**
     * 批量删除用户
     */
    @RequestMapping(value = "/deleteBatch", method = RequestMethod.DELETE)
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        sysBaseAPI.addLog("批量删除用户， ids： " + ids, CommonConstant.LOG_TYPE_2, 3);
        sysUserService.deleteBatchUsers(ids);
        return Result.ok("批量删除用户成功");
    }

    /**
     * 获取被逻辑删除的用户列表，无分页
     *
     * @return logicDeletedUserList
     */
    @GetMapping("/recycleBin")
    public Result getRecycleBin() {
        List<SysUser> logicDeletedUserList = sysUserService.queryLogicDeleted();
        return Result.ok(logicDeletedUserList);
    }

    /**
     * 还原被逻辑删除的用户
     *
     * @param userIds 被还原的用户ID，是个 list 集合
     * @return
     */
    @PutMapping("/recycleBin")
    public Result putRecycleBin(@RequestBody List<String> userIds, HttpServletRequest request) {
        if (userIds != null && userIds.size() > 0) {
            SysUser updateUser = new SysUser();
            updateUser.setUpdateBy(JwtUtil.getUserNameByToken(request));
            updateUser.setUpdateTime(new Date());
            sysUserService.revertLogicDeleted(userIds, updateUser);
        }
        return Result.ok("还原成功");
    }

    /**
     * 彻底删除用户
     *
     * @param userIds 被删除的用户ID，多个id用半角逗号分割
     * @return
     */
    @DeleteMapping("/recycleBin")
    public Result deleteRecycleBin(@RequestParam("userIds") String userIds) {
        if (StringUtils.isNotBlank(userIds)) {
            sysUserService.removeLogicDeleted(Arrays.asList(userIds.split(",")));
        }
        return Result.ok("删除成功");
    }


    @RequestMapping(value = "/edit", method = RequestMethod.PUT)
    public Result<SysUser> edit(@RequestBody JSONObject jsonObject) {

        System.out.println("-------------------编辑测试----------------------");
        System.out.println(jsonObject);
        System.out.println();

        Result<SysUser> result = new Result<>();
        try {
            //通过用户id查询
            SysUser sysUser = sysUserService.getById(jsonObject.getString("id"));
            sysBaseAPI.addLog("编辑用户，id： " + jsonObject.getString("id"), CommonConstant.LOG_TYPE_2, 2);
            if (sysUser == null) {
                result.error500("未找到对应实体");
            } else {
                //转化成User对象
                SysUser user = JSON.parseObject(jsonObject.toJSONString(), SysUser.class);
                user.setUpdateTime(new Date());
                //String passwordEncode = PasswordUtil.encrypt(user.getUsername(), user.getPassword(), sysUser.getSalt());
                user.setPassword(sysUser.getPassword());
                String roles = jsonObject.getString("selectedroles");
                sysUserService.editUserWithRole(user, roles);
                result.success("修改成功!");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     * 冻结和解冻用户
     *
     * @param jsonObject
     * @return
     */
    @RequestMapping(value = "/frozenBatch", method = RequestMethod.PUT)
    public Result<SysUser> frozenBatch(@RequestBody JSONObject jsonObject) {
        Result<SysUser> result = new Result<>();
        try {
            String ids = jsonObject.getString("ids");
            String status = jsonObject.getString("status");
            String[] arr = ids.split(",");
            for (String id : arr) {
                if (oConvertUtils.isNotEmpty(id)) {
                    this.sysUserService.update(new SysUser().setStatus(Integer.parseInt(status)),
                            new UpdateWrapper<SysUser>().lambda().eq(SysUser::getId, id));
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败" + e.getMessage());
        }
        result.success("操作成功!");
        return result;

    }

//    @RequestMapping(value = "/queryById", method = RequestMethod.GET)
//    public Result<SysUser> queryById(@RequestParam(name = "id", required = true) String id) {
//        Result<SysUser> result = new Result<>();
//        SysUser sysUser = sysUserService.getById(id);
//        if (sysUser == null) {
//            result.error500("未找到对应实体");
//        } else {
//            result.setResult(sysUser);
//            result.setSuccess(true);
//        }
//        return result;
//    }

    /**
     * 查询对应用户有哪些角色
     *
     * @param userid
     * @return
     */
    @RequestMapping(value = "/queryUserRole", method = RequestMethod.GET)
    public Result<List<String>> queryUserRole(@RequestParam(name = "userid", required = true) String userid) {
        Result<List<String>> result = new Result<>();
        List<String> list = new ArrayList<>();
        List<SysUserRole> userRole = sysUserRoleService.list(new QueryWrapper<SysUserRole>().lambda().eq(SysUserRole::getUserId, userid));
        if (userRole == null || userRole.size() <= 0) {
            result.error500("未找到用户相关角色信息");
        } else {
            for (SysUserRole sysUserRole : userRole) {
                list.add(sysUserRole.getRoleId());
            }
            result.setSuccess(true);
            result.setResult(list);
        }
        return result;
    }


    /**
     * 校验用户账号是否唯一
     * 可以校验其他 需要检验什么就传什么
     *
     * @param sysUser
     * @return
     */
    @RequestMapping(value = "/checkOnlyUser", method = RequestMethod.GET)
    public Result<Boolean> checkOnlyUser(SysUser sysUser) {
        Result<Boolean> result = new Result<>();
        //如果此参数为false则程序发生异常
        result.setResult(true);
        try {
            //通过传入信息查询新的用户信息
            SysUser user = sysUserService.getOne(new QueryWrapper<>(sysUser));
            if (user != null) {
                result.setSuccess(false);
                result.setMessage("用户账号已存在");
                return result;
            }

        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            return result;
        }
        result.setSuccess(true);
        return result;
    }

    /**
     * 修改密码
     */
    @RequiresRoles({"admin"})
    @RequestMapping(value = "/changePassword", method = RequestMethod.PUT)
    public Result<?> changePassword(@RequestBody SysUser sysUser) {
        SysUser u = this.sysUserService.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, sysUser.getUsername()));
        if (u == null) {
            return Result.error("用户不存在！");
        }
        sysUser.setId(u.getId());
        return sysUserService.changePassword(sysUser);
    }

    @GetMapping(value = "/currentUserInfo")
    public Result<?> getCurrentUserInfo() {
        Result<SysUser> res = new Result<>();
        String currentUserId = ((LoginUser)(SecurityUtils.getSubject().getPrincipal())).getId();
        SysUser currentUser = sysUserService.getById(currentUserId);
        res.setSuccess(true);
        res.setResult(currentUser);
        return res;
    }

    @PostMapping(value = "/updateMessage")
    public Result<?> updateMessage(@RequestBody SysUser sysUser) {
        Result<SysUser> res = new Result<>();
        String currentUserId = ((LoginUser)(SecurityUtils.getSubject().getPrincipal())).getId();
        sysUserService.updatePersonalData(sysUser, currentUserId);
        res.setSuccess(true);
        return res;
    }

    /**
     * 查询所有用户所对应的角色信息
     *
     * @return
     */
    @RequestMapping(value = "/queryUserRoleMap", method = RequestMethod.GET)
    public Result<Map<String, String>> queryUserRole() {
        Result<Map<String, String>> result = new Result<>();
        Map<String, String> map = sysUserRoleService.queryUserRole();
        result.setResult(map);
        result.setSuccess(true);
        return result;
    }

    /**
     * 导出excel
     *
     * @param sysUser
     * @param request
     * @return
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(SysUser sysUser, HttpServletRequest request) {
        // Step.1 组装查询条件
        QueryWrapper<SysUser> queryWrapper = QueryGenerator.initQueryWrapper(sysUser, request.getParameterMap());
        //Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        //update-begin--Author:kangxiaolin  Date:20180825 for：[03]用户导出，如果选择数据则只导出相关数据--------------------
        String selections = request.getParameter("selections");
        if (!oConvertUtils.isEmpty(selections)) {
            queryWrapper.in("id", selections.split(","));
        }
        //update-end--Author:kangxiaolin  Date:20180825 for：[03]用户导出，如果选择数据则只导出相关数据----------------------
        List<SysUser> pageList = sysUserService.list(queryWrapper);

        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "用户列表");
        mv.addObject(NormalExcelConstants.CLASS, SysUser.class);
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("用户列表数据", "导出人:" + user.getRealname(), "导出信息"));
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
    //@RequiresPermissions("user:import")
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
                List<SysUser> listSysUsers = ExcelImportUtil.importExcel(file.getInputStream(), SysUser.class, params);
                for (SysUser sysUserExcel : listSysUsers) {
                    if (sysUserExcel.getPassword() == null) {
                        // 密码默认为“123456”
                        sysUserExcel.setPassword("123456");
                    }
                    sysUserService.save(sysUserExcel);
                }
                return Result.ok("文件导入成功！数据行数：" + listSysUsers.size());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return Result.error("抱歉! 您导入的数据中用户名已经存在.");
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        return Result.error("文件导入失败！");
    }

    /**
     * @功能：根据id 批量查询
     * @param userIds
     * @return
     */
//	@RequestMapping(value = "/queryByIds", method = RequestMethod.GET)
//	public Result<Collection<SysUser>> queryByIds(@RequestParam String userIds) {
//		Result<Collection<SysUser>> result = new Result<>();
//		String[] userId = userIds.split(",");
//		Collection<String> idList = Arrays.asList(userId);
//		Collection<SysUser> userRole = sysUserService.listByIds(idList);
//		result.setSuccess(true);
//		result.setResult(userRole);
//		return result;
//	}

    /**
     * 首页用户重置密码
     */
    @RequestMapping(value = "/updatePassword", method = RequestMethod.PUT)
    public Result<?> changPassword(@RequestBody JSONObject json) {
        String username = json.getString("username");
        String oldpassword = json.getString("oldpassword");
        String password = json.getString("password");
        String confirmpassword = json.getString("confirmpassword");
        SysUser user = this.sysUserService.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
        if (user == null) {
            return Result.error("用户不存在！");
        }
        return sysUserService.resetPassword(username, oldpassword, password, confirmpassword);
    }

    @RequestMapping(value = "/userGoodList", method = RequestMethod.GET)
    public Result<IPage<SysUser>> userGoodList(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                               @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<SysUser>> result = new Result<>();
        Page<SysUser> page = new Page<>(pageNo, pageSize);
        String goodId = req.getParameter("goodId");
        String username = req.getParameter("username");
        IPage<SysUser> pageList = sysUserService.getUserByGoodId(page, goodId, username);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    @RequestMapping(value = "/userRoleList", method = RequestMethod.GET)
    public Result<IPage<SysUser>> userRoleList(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                               @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<SysUser>> result = new Result<>();
        Page<SysUser> page = new Page<>(pageNo, pageSize);
        String roleId = req.getParameter("roleId");
        String username = req.getParameter("username");
        IPage<SysUser> pageList = sysUserService.getUserByRoleId(page, roleId, username);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * 给指定角色添加用户
     */
    @RequestMapping(value = "/addSysUserRole", method = RequestMethod.POST)
    public Result<String> addSysUserRole(@RequestBody SysUserRoleVO sysUserRoleVO) {
        Result<String> result = new Result<>();
        try {
            String sysRoleId = sysUserRoleVO.getRoleId();
            for (String sysUserId : sysUserRoleVO.getUserIdList()) {
                SysUserRole sysUserRole = new SysUserRole(sysUserId, sysRoleId);
                QueryWrapper<SysUserRole> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("role_id", sysRoleId).eq("user_id", sysUserId);
                SysUserRole one = sysUserRoleService.getOne(queryWrapper);
                if (one == null) {
                    sysUserRoleService.save(sysUserRole);
                }

            }
            result.setMessage("添加成功!");
            result.setSuccess(true);
            return result;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setMessage("出错了: " + e.getMessage());
            return result;
        }
    }

    /**
     * 给指定商品添加卖家
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/addSysUserGood", method = RequestMethod.POST)
    public Result<String> addSysUserGood(@RequestBody SysUserGoodVO SysUserGoodVO) {
        Result<String> result = new Result<>();
        try {
            String goodId = SysUserGoodVO.getGoodId();
            for (String sellerId : SysUserGoodVO.getUserIdList()) {
                SysUserGood sysUserGood = new SysUserGood(sellerId, goodId);
                QueryWrapper<SysUserGood> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("good_id", goodId).eq("seller_id", sellerId);
                SysUserGood one = sysUserGoodService.getOne(queryWrapper);
                if (one == null) {
                    sysUserGoodService.save(sysUserGood);
                }
            }
            result.setMessage("添加成功!");
            result.setSuccess(true);
            return result;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setMessage("出错了: " + e.getMessage());
            return result;
        }
    }

    /**
     * 删除指定票商品的用户关系
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/deleteUserGood", method = RequestMethod.DELETE)
    public Result<SysUserGood> deleteUserGood(@RequestParam(name = "goodId") String goodId,
                                              @RequestParam(name = "userId", required = true) String userId) {
        Result<SysUserGood> result = new Result<>();
        try {
            QueryWrapper<SysUserGood> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("good_id", goodId).eq("seller_id", userId);
            sysUserGoodService.remove(queryWrapper);
            result.success("删除成功!");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("删除失败！");
        }
        return result;
    }

    /**
     * 删除指定角色的用户关系
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/deleteUserRole", method = RequestMethod.DELETE)
    public Result<SysUserRole> deleteUserRole(@RequestParam(name = "roleId") String roleId,
                                              @RequestParam(name = "userId", required = true) String userId
    ) {
        Result<SysUserRole> result = new Result<>();
        try {
            QueryWrapper<SysUserRole> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("role_id", roleId).eq("user_id", userId);
            sysUserRoleService.remove(queryWrapper);
            result.success("删除成功!");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("删除失败！");
        }
        return result;
    }

    /**
     * 批量删除指定角色的用户关系
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/deleteUserRoleBatch", method = RequestMethod.DELETE)
    public Result<SysUserRole> deleteUserRoleBatch(
            @RequestParam(name = "roleId") String roleId,
            @RequestParam(name = "userIds", required = true) String userIds) {

        Result<SysUserRole> result = new Result<>();
        try {
            QueryWrapper<SysUserRole> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("role_id", roleId).in("user_id", Arrays.asList(userIds.split(",")));
            sysUserRoleService.remove(queryWrapper);
            result.success("删除成功!");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("删除失败！");
        }
        return result;
    }

    /**
     * 批量删除指定票商品的用户关系
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/deleteUserGoodBatch", method = RequestMethod.DELETE)
    public Result<SysUserGood> deleteUserGoodBatch(
            @RequestParam(name = "goodId") String goodId,
            @RequestParam(name = "userIds", required = true) String userIds) {
        Result<SysUserGood> result = new Result<>();
        try {
            QueryWrapper<SysUserGood> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("good_id", goodId).in("seller_id", Arrays.asList(userIds.split(",")));
            sysUserGoodService.remove(queryWrapper);
            result.success("删除成功!");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("删除失败！");
        }
        return result;
    }

    /**
     * 用户注册接口
     *
     * @param jsonObject
     * @param user
     * @return
     */
    @PostMapping("/register")
    public Result<JSONObject> userRegister(@RequestBody JSONObject jsonObject, SysUser user) {
        Result<JSONObject> result = new Result<>();
        String phone = jsonObject.getString("phone");

        String username = jsonObject.getString("username");
        //未设置用户名，则用手机号作为用户名
        if (oConvertUtils.isEmpty(username)) {
            username = phone;
        }
        //未设置密码，则随机生成一个密码
        String password = jsonObject.getString("password");
        if (oConvertUtils.isEmpty(password)) {
            password = RandomUtil.randomString(8);
        }
        String email = jsonObject.getString("email");
        SysUser sysUser1 = sysUserService.getUserByName(username);
        if (sysUser1 != null) {
            result.setMessage("用户名已注册");
            result.setSuccess(false);
            return result;
        }
        SysUser sysUser2 = sysUserService.getUserByPhone(phone);
        if (sysUser2 != null) {
            result.setMessage("该手机号已注册");
            result.setSuccess(false);
            return result;
        }

        if (oConvertUtils.isNotEmpty(email)) {
            SysUser sysUser3 = sysUserService.getUserByEmail(email);
            if (sysUser3 != null) {
                result.setMessage("邮箱已被注册");
                result.setSuccess(false);
                return result;
            }
        }


        try {
            user.setCreateTime(new Date());// 设置创建时间
            String salt = oConvertUtils.randomGen(8);
            String passwordEncode = PasswordUtil.encrypt(username, password, salt);
            user.setSalt(salt);
            user.setUsername(username);
            user.setRealname(username);
            user.setPassword(passwordEncode);
            user.setEmail(email);
            user.setPhone(phone);
            user.setStatus(1);
            user.setDelFlag(CommonConstant.DEL_FLAG_0.toString());
            user.setActivitiSync(CommonConstant.ACT_SYNC_1);

            user.setReceivedAddress(jsonObject.getString("receivedAddress"));
            user.setSendAddress(jsonObject.getString("sendAddress"));

            System.out.println("USER:  " + user.toString());

            //添加用户并分配基本角色
            sysUserService.addUserWithRole(user, "ee8626f80f7c2619917b6236f3a7f02b");//默认临时角色 消费者
            result.success("注册成功");
        } catch (Exception e) {
            result.error500("注册失败");
        }
        return result;
    }

    /**
     * 登陆查询
     *
     * @param sysUser
     * @return
     */
    @GetMapping("/querySysUser")
    public Result<Map<String, Object>> querySysUser(SysUser sysUser) {
        String phone = sysUser.getPhone();
        String username = sysUser.getUsername();
        Result<Map<String, Object>> result = new Result<>();
        Map<String, Object> map = new HashMap<>();
        if (oConvertUtils.isNotEmpty(phone)) {
            SysUser user = sysUserService.getUserByPhone(phone);
            if (user != null) {
                map.put("username", user.getUsername());
                map.put("phone", user.getPhone());
                result.setSuccess(true);
                result.setResult(map);
                return result;
            }
        }
        if (oConvertUtils.isNotEmpty(username)) {
            SysUser user = sysUserService.getUserByName(username);
            if (user != null) {
                map.put("username", user.getUsername());
                map.put("phone", user.getPhone());
                result.setSuccess(true);
                result.setResult(map);
                return result;
            }
        }
        result.setSuccess(false);
        result.setMessage("验证失败");
        return result;
    }

    /**
     * 用户手机号验证
     */
    @PostMapping("/phoneVerification")
    public Result<String> phoneVerification(@RequestBody JSONObject jsonObject) {
        Result<String> result = new Result<>();
        String phone = jsonObject.getString("phone");
        String smscode = jsonObject.getString("smscode");
        Object code = redisUtil.get(phone);
        if (!smscode.equals(code)) {
            result.setMessage("手机验证码错误");
            result.setSuccess(false);
            return result;
        }
        redisUtil.set(phone, smscode);
        result.setResult(smscode);
        result.setSuccess(true);
        return result;
    }

    /**
     * 用户更改密码
     */
    @GetMapping("/passwordChange")
    public Result<SysUser> passwordChange(@RequestParam(name = "username") String username,
                                          @RequestParam(name = "password") String password,
                                          @RequestParam(name = "smscode") String smscode,
                                          @RequestParam(name = "phone") String phone) {
        Result<SysUser> result = new Result<>();
        if (oConvertUtils.isEmpty(username) || oConvertUtils.isEmpty(password) || oConvertUtils.isEmpty(smscode) || oConvertUtils.isEmpty(phone)) {
            result.setMessage("重置密码失败！");
            result.setSuccess(false);
            return result;
        }

        SysUser sysUser = new SysUser();
        Object object = redisUtil.get(phone);
        if (null == object) {
            result.setMessage("短信验证码失效！");
            result.setSuccess(false);
            return result;
        }
        if (!smscode.equals(object)) {
            result.setMessage("短信验证码不匹配！");
            result.setSuccess(false);
            return result;
        }
        sysUser = this.sysUserService.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username).eq(SysUser::getPhone, phone));
        if (sysUser == null) {
            result.setMessage("未找到用户！");
            result.setSuccess(false);
            return result;
        } else {
            String salt = oConvertUtils.randomGen(8);
            sysUser.setSalt(salt);
            String passwordEncode = PasswordUtil.encrypt(sysUser.getUsername(), password, salt);
            sysUser.setPassword(passwordEncode);
            this.sysUserService.updateById(sysUser);
            result.setSuccess(true);
            result.setMessage("密码重置完成！");
            return result;
        }
    }


    /**
     * 根据TOKEN获取用户的部分信息（返回的数据是可供表单设计器使用的数据）
     *
     * @return
     */
    @GetMapping("/getUserSectionInfoByToken")
    public Result<?> getUserSectionInfoByToken(HttpServletRequest request, @RequestParam(name = "token", required = false) String token) {
        try {
            String username = null;
            // 如果没有传递token，就从header中获取token并获取用户信息
            if (oConvertUtils.isEmpty(token)) {
                username = JwtUtil.getUserNameByToken(request);
            } else {
                username = JwtUtil.getUsername(token);
            }

            log.info(" ------ 通过令牌获取部分用户信息，当前用户： " + username);

            // 根据用户名查询用户信息
            SysUser sysUser = sysUserService.getUserByName(username);
            Map<String, Object> map = new HashMap<>();
            map.put("sysUserId", sysUser.getId());
            map.put("sysUserCode", sysUser.getUsername()); // 当前登录用户登录账号
            map.put("sysUserName", sysUser.getRealname()); // 当前登录用户真实名称
            //map.put("sysOrgCode", sysUser.getOrgCode()); // 当前登录用户部门编号

            log.info(" ------ 通过令牌获取部分用户信息，已获取的用户信息： " + map);

            return Result.ok(map);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(500, "查询失败:" + e.getMessage());
        }
    }

    /**
     * 获取用户列表  根据用户名和真实名 模糊匹配
     * @param keyword
     * @param pageNo
     * @param pageSize
     * @return
     */
//	@GetMapping("/appUserList")
//	public Result<?> appUserList(@RequestParam(name = "keyword", required = false) String keyword,
//			@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
//			@RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
//		try {
//			//TODO 从查询效率上将不要用mp的封装的page分页查询 建议自己写分页语句
//			LambdaQueryWrapper<SysUser> query = new LambdaQueryWrapper<SysUser>();
//			query.eq(SysUser::getActivitiSync, "1");
//			query.eq(SysUser::getDelFlag,"0");
//			query.and(i -> i.like(SysUser::getUsername, keyword).or().like(SysUser::getRealname, keyword));
//
//			Page<SysUser> page = new Page<>(pageNo, pageSize);
//			IPage<SysUser> res = this.sysUserService.page(page, query);
//			return Result.ok(res);
//		} catch (Exception e) {
//			log.error(e.getMessage(), e);
//			return Result.error(500, "查询失败:" + e.getMessage());
//		}
//	}
}
