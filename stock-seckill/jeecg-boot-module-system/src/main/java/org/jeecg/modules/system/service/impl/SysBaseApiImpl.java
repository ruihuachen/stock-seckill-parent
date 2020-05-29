package org.jeecg.modules.system.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.constant.CacheConstant;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.constant.DataBaseConstant;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.*;
import org.jeecg.common.util.IPUtils;
import org.jeecg.common.util.SpringContextUtils;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.common.util.oss.OssBootUtil;
import org.jeecg.modules.message.entity.SysMessageTemplate;
import org.jeecg.modules.message.service.ISysMessageTemplateService;
import org.jeecg.modules.message.websocket.WebSocket;
import org.jeecg.modules.system.entity.*;
import org.jeecg.modules.system.mapper.*;
import org.jeecg.modules.system.service.ISysDataSourceService;
import org.jeecg.modules.system.service.ISysDictService;
import org.jeecg.modules.system.util.MinioUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 底层共通业务API，提供其他独立模块调用
 */
@Slf4j
@Service
public class SysBaseApiImpl implements ISysBaseAPI {
    /**
     * 当前系统数据库类型
     */
    public static String DB_TYPE = "";

    @Resource
    private SysLogMapper sysLogMapper;

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    private ISysDictService sysDictService;

    @Resource
    private SysRoleMapper roleMapper;

    @Resource
    private SysCategoryMapper categoryMapper;

    @Autowired
    private ISysDataSourceService dataSourceService;

    @Override
    public void addLog(String LogContent, Integer logType, Integer operatetype) {
        SysLog sysLog = new SysLog();
        //注解上的描述,操作日志内容
        sysLog.setLogContent(LogContent);
        sysLog.setLogType(logType);
        sysLog.setOperateType(operatetype);

        //请求的方法名
        //请求的参数
        try {
            //获取request
            HttpServletRequest request = SpringContextUtils.getHttpServletRequest();
            //设置IP地址
            sysLog.setIp(IPUtils.getIpAddr(request));
        } catch (Exception e) {
            sysLog.setIp("127.0.0.1");
        }

        //获取登录用户信息
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (sysUser != null) {
            sysLog.setUserid(sysUser.getUsername());
            sysLog.setUsername(sysUser.getRealname());

        }
        sysLog.setCreateTime(new Date());
        //保存系统日志
        sysLogMapper.insert(sysLog);
    }

    @Override
    @Cacheable(cacheNames = CacheConstant.SYS_USERS_CACHE, key = "#username")
    public LoginUser getUserByName(String username) {
        if (oConvertUtils.isEmpty(username)) {
            return null;
        }
        LoginUser loginUser = new LoginUser();
        SysUser sysUser = userMapper.getUserByName(username);
        if (sysUser == null) {
            return null;
        }
        BeanUtils.copyProperties(sysUser, loginUser);
        return loginUser;
    }

    @Override
    public LoginUser getUserById(String id) {
        if (oConvertUtils.isEmpty(id)) {
            return null;
        }
        LoginUser loginUser = new LoginUser();
        SysUser sysUser = userMapper.selectById(id);
        if (sysUser == null) {
            return null;
        }
        BeanUtils.copyProperties(sysUser, loginUser);
        return loginUser;
    }

    @Override
    public List<String> getRolesByUsername(String username) {
        return sysUserRoleMapper.getRoleByUserName(username);
    }

    @Override
    public String getDatabaseType() throws SQLException {
        DataSource dataSource = SpringContextUtils.getApplicationContext().getBean(DataSource.class);
        return getDatabaseTypeByDataSource(dataSource);
    }

    @Override
    public List<DictModel> queryDictItemsByCode(String code) {
        return sysDictService.queryDictItemsByCode(code);
    }

    @Override
    public List<DictModel> queryTableDictItemsByCode(String table, String text, String code) {
        return sysDictService.queryTableDictItemsByCode(table, text, code);
    }


    /**
     * 获取数据库类型
     *
     * @param dataSource
     * @return
     * @throws SQLException
     */
    private String getDatabaseTypeByDataSource(DataSource dataSource) throws SQLException {
        if ("".equals(DB_TYPE)) {
            Connection connection = dataSource.getConnection();
            try {
                DatabaseMetaData md = connection.getMetaData();
                String dbType = md.getDatabaseProductName().toLowerCase();
                if (dbType.indexOf("mysql") >= 0) {
                    DB_TYPE = DataBaseConstant.DB_TYPE_MYSQL;
                } else if (dbType.indexOf("oracle") >= 0) {
                    DB_TYPE = DataBaseConstant.DB_TYPE_ORACLE;
                } else if (dbType.indexOf("sqlserver") >= 0 || dbType.indexOf("sql server") >= 0) {
                    DB_TYPE = DataBaseConstant.DB_TYPE_SQLSERVER;
                } else if (dbType.indexOf("postgresql") >= 0) {
                    DB_TYPE = DataBaseConstant.DB_TYPE_POSTGRESQL;
                } else {
                    throw new JeecgBootException("数据库类型:[" + dbType + "]不识别!");
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            } finally {
                connection.close();
            }
        }
        return DB_TYPE;

    }

    @Override
    public List<DictModel> queryAllDict() {
        // 查询并排序
        QueryWrapper<SysDict> queryWrapper = new QueryWrapper<SysDict>();
        queryWrapper.orderByAsc("create_time");
        List<SysDict> dicts = sysDictService.list(queryWrapper);
        // 封装成 model
        List<DictModel> list = new ArrayList<DictModel>();
        for (SysDict dict : dicts) {
            list.add(new DictModel(dict.getDictCode(), dict.getDictName()));
        }

        return list;
    }

    @Override
    public List<SysCategoryModel> queryAllDSysCategory() {
        List<SysCategory> ls = categoryMapper.selectList(null);
        List<SysCategoryModel> res = oConvertUtils.entityListToModelList(ls, SysCategoryModel.class);
        return res;
    }


    @Override
    public List<String> queryTableDictByKeys(String table, String text, String code, String[] keyArray) {
        return sysDictService.queryTableDictByKeys(table, text, code, keyArray);
    }

    @Override
    public List<ComboModel> queryAllUser() {
        List<ComboModel> list = new ArrayList<ComboModel>();
        List<SysUser> userList = userMapper.selectList(new QueryWrapper<SysUser>().eq("status", "1").eq("del_flag", "0"));
        for (SysUser user : userList) {
            ComboModel model = new ComboModel();
            model.setTitle(user.getRealname());
            model.setId(user.getId());
            model.setUsername(user.getUsername());
            list.add(model);
        }
        return list;
    }

    @Override
    public JSONObject queryAllUser(String[] userIds, int pageNo, int pageSize) {
        JSONObject json = new JSONObject();
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<SysUser>().eq("status", "1").eq("del_flag", "0");
        List<ComboModel> list = new ArrayList<ComboModel>();
        Page<SysUser> page = new Page<SysUser>(pageNo, pageSize);
        IPage<SysUser> pageList = userMapper.selectPage(page, queryWrapper);
        for (SysUser user : pageList.getRecords()) {
            ComboModel model = new ComboModel();
            model.setUsername(user.getUsername());
            model.setTitle(user.getRealname());
            model.setId(user.getId());
            model.setEmail(user.getEmail());
            if (oConvertUtils.isNotEmpty(userIds)) {
                for (int i = 0; i < userIds.length; i++) {
                    if (userIds[i].equals(user.getId())) {
                        model.setChecked(true);
                    }
                }
            }
            list.add(model);
        }
        json.put("list", list);
        json.put("total", pageList.getTotal());
        return json;
    }

    @Override
    public List<ComboModel> queryAllRole() {
        List<ComboModel> list = new ArrayList<ComboModel>();
        List<SysRole> roleList = roleMapper.selectList(new QueryWrapper<SysRole>());
        for (SysRole role : roleList) {
            ComboModel model = new ComboModel();
            model.setTitle(role.getRoleName());
            model.setId(role.getId());
            list.add(model);
        }
        return list;
    }

    @Override
    public List<ComboModel> queryAllRole(String[] roleIds) {
        List<ComboModel> list = new ArrayList<ComboModel>();
        List<SysRole> roleList = roleMapper.selectList(new QueryWrapper<SysRole>());
        for (SysRole role : roleList) {
            ComboModel model = new ComboModel();
            model.setTitle(role.getRoleName());
            model.setId(role.getId());
            model.setRoleCode(role.getRoleCode());
            if (oConvertUtils.isNotEmpty(roleIds)) {
                for (int i = 0; i < roleIds.length; i++) {
                    if (roleIds[i].equals(role.getId())) {
                        model.setChecked(true);
                    }
                }
            }
            list.add(model);
        }
        return list;
    }

    @Override
    public List<String> getRoleIdsByUsername(String username) {
        return sysUserRoleMapper.getRoleIdByUserName(username);
    }


    @Override
    public DynamicDataSourceModel getDynamicDbSourceByCode(String dbSourceCode) {
        SysDataSource dbSource = dataSourceService.getOne(new LambdaQueryWrapper<SysDataSource>().eq(SysDataSource::getCode, dbSourceCode));
        return new DynamicDataSourceModel(dbSource);
    }


    @Override
    public String upload(MultipartFile file, String bizPath, String uploadType) {
        String url = "";
        if (CommonConstant.UPLOAD_TYPE_MINIO.equals(uploadType)) {
            url = MinioUtil.upload(file, bizPath);
        } else {
            url = OssBootUtil.upload(file, bizPath);
        }
        return url;
    }

}