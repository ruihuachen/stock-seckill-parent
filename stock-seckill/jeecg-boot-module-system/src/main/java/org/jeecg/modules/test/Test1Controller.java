package org.jeecg.modules.test;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * create by Ernest on 2020/4/16.
 */
@RestController
@Api(tags="新建module--------------")
@RequestMapping("/test/jeecgDemo")
@Slf4j
public class Test1Controller {
    @ApiOperation("测试hello方法")
    @GetMapping(value = "/hello")
    public Result<String> hello() {
        Result<String> result = new Result<String>();
        result.setResult("Hello World!");
        result.setSuccess(true);
        return result;
    }
}
