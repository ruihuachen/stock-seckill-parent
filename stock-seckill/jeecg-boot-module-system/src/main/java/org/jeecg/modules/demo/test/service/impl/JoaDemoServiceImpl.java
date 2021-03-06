package org.jeecg.modules.demo.test.service.impl;

import org.jeecg.modules.demo.test.entity.JoaDemo;
import org.jeecg.modules.demo.test.mapper.JoaDemoMapper;
import org.jeecg.modules.demo.test.service.IJoaDemoService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * 流程测试
 */
@Service
public class JoaDemoServiceImpl extends ServiceImpl<JoaDemoMapper, JoaDemo> implements IJoaDemoService {

}
