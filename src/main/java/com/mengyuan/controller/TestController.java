package com.mengyuan.controller;

import com.mengyuan.common.ApplicationContextHelper;
import com.mengyuan.common.JsonData;
import com.mengyuan.dao.SysAclModuleMapper;
import com.mengyuan.exception.ParamException;
import com.mengyuan.exception.PermissionException;
import com.mengyuan.model.SysAcl;
import com.mengyuan.model.SysAclModule;
import com.mengyuan.param.TestVo;
import com.mengyuan.util.BeanValidator;
import com.mengyuan.util.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/test")
@Slf4j
public class TestController {

    @RequestMapping("/hello.json")
    @ResponseBody
    public JsonData hello() {
        log.info("");
        throw new RuntimeException("test exception");
        //return JsonData.success("hello world!");
    }

    @RequestMapping("/validate.json")
    @ResponseBody
    public JsonData validate(TestVo vo) throws ParamException {
        log.info("validate");

        SysAclModuleMapper moduleMapper = ApplicationContextHelper.popBean(SysAclModuleMapper.class);
        SysAclModule module = moduleMapper.selectByPrimaryKey(1);
        log.info(JsonMapper.obj2String(module));
//        try {
//            Map<String, String> map = BeanValidator.validateObject(vo);
//
//            if (MapUtils.isNotEmpty(map)) {
//                for (Map.Entry<String, String> entry: map.entrySet()) {
//                    log.info("{}->{}", entry.getKey(), entry.getValue());
//                }
//            }
//
//        } catch (Exception e) {
//
//        }
        BeanValidator.check(vo);

        return JsonData.success("test validate");
    }

}
