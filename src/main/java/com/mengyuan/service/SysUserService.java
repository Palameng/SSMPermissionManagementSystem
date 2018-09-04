package com.mengyuan.service;

import com.google.common.base.Preconditions;
import com.mengyuan.dao.SysUserMapper;
import com.mengyuan.exception.ParamException;
import com.mengyuan.model.SysUser;
import com.mengyuan.param.UserParam;
import com.mengyuan.util.BeanValidator;
import com.mengyuan.util.MD5Util;
import com.mengyuan.util.PasswordUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class SysUserService {

    @Resource
    private SysUserMapper sysUserMapper;

    /**
     * 新增用户，根据参数校验后，判断手机和邮箱是否被使用，生成密码发送到邮箱，并持久化.
     * @param param 前端需要验证的参数
     */
    public void save(UserParam param) {
        // 校验前端传递过来的参数是否合法
        BeanValidator.check(param);

        // 检查重复
        if (checkEmailExist(param.getMail(), param.getId())) {
            throw new ParamException(("邮箱已被占用"));
        }

        // 检查重复
        if (checkTelephoneExist(param.getTelephone(), param.getId())) {
            throw new ParamException(("电话已被占用"));
        }

        // String password = PasswordUtil.randomPassword();

        String password = "12345678";

        String encryptedPassword = MD5Util.encrypt(password);

        SysUser sysUser = SysUser.builder()
                .username(param.getUsername())
                .telephone(param.getTelephone())
                .mail(param.getMail())
                .password(encryptedPassword)
                .deptId(param.getDeptId())
                .status(param.getStatus())
                .remark(param.getRemark())
                .build();

        // TODO 处理操作人相关信息
        sysUser.setOperator("system");
        sysUser.setOperateIp("127.0.0.1");
        sysUser.setOperateTime(new Date());

        // TODO 发送邮件

        sysUserMapper.insertSelective(sysUser);
    }

    public void update(UserParam param) {
        // 校验前端传递过来的参数是否合法
        BeanValidator.check(param);

        // 检查重复
        if (checkEmailExist(param.getMail(), param.getId())) {
            throw new ParamException(("邮箱已被占用"));
        }

        // 检查重复
        if (checkTelephoneExist(param.getTelephone(), param.getId())) {
            throw new ParamException(("电话已被占用"));
        }

        SysUser before = sysUserMapper.selectByPrimaryKey(param.getId());
        Preconditions.checkNotNull(before, "待更新的用户不存在");
        SysUser after = SysUser.builder()
                .id(param.getId())
                .username(param.getUsername())
                .telephone(param.getTelephone())
                .mail(param.getMail())
                .deptId(param.getDeptId())
                .status(param.getStatus())
                .remark(param.getRemark())
                .build();

        sysUserMapper.updateByPrimaryKeySelective(after);
    }

    public SysUser findByKeyword(String keyword) {
        return sysUserMapper.findByKeyword(keyword);
    }


    private boolean checkEmailExist(String mail, Integer userId) {
        return sysUserMapper.countByMail(mail, userId) > 0;
    }

    private boolean checkTelephoneExist(String telephone, Integer userId) {
        return sysUserMapper.countByTelephone(telephone, userId) > 0;
    }

}
