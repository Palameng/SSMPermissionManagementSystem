package com.mengyuan.service;

import com.google.common.collect.Lists;
import com.mengyuan.common.RequestHolder;
import com.mengyuan.dao.SysAclMapper;
import com.mengyuan.dao.SysRoleAclMapper;
import com.mengyuan.dao.SysRoleUserMapper;
import com.mengyuan.model.SysAcl;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SysCoreService {
    /**
     * 获取当前用户
     * @return
     */

    @Resource
    private SysAclMapper sysAclMapper;

    @Resource
    private SysRoleUserMapper sysRoleUserMapper;

    @Resource
    private SysRoleAclMapper sysRoleAclMapper;

    /**
     * 获取当前用户的权限列表
     * @return
     */
    public List<SysAcl> getCurrentUserAclList() {
        int userId = RequestHolder.getCurrentUser().getId();
        return getUserAclList(userId);
    }

    /**
     * 获取某个角色对应的权限列表
     * @param roleId
     * @return
     */
    public List<SysAcl> getRoleAclList(int roleId) {
        //插入roleId 只有一条数据而已，复用这个DAO操作
        List<Integer> aclIdList = sysRoleAclMapper.getAclIdListByRoleIdList(Lists.<Integer>newArrayList(roleId));

        if (CollectionUtils.isEmpty(aclIdList)) {
            return Lists.newArrayList();
        }

        return sysAclMapper.getByIdList(aclIdList);

    }

    /**
     * 获取某个用户的权限列表
     * @param userId
     * @return
     */
    public List<SysAcl> getUserAclList(int userId) {
        //如果是超级用户，默认拥有所有权限
        if (isSuperAdmin()) {
            return sysAclMapper.getAll();
        }

        //根据userId找到对应的角色
        List<Integer> userRoleIdList = sysRoleUserMapper.getRoleIdListByUserId(userId);

        if (CollectionUtils.isEmpty(userRoleIdList)) {
            return Lists.newArrayList();
        }

        //用户分饰几个角色，角色之间有相同的权限点，这里归纳权限点总和，这里搜索出所有ID
        List<Integer> userAclIdList = sysRoleAclMapper.getAclIdListByRoleIdList(userRoleIdList);

        if (CollectionUtils.isEmpty(userAclIdList)) {
            return Lists.newArrayList();
        }

        //查找出所有的权限内容
        return sysAclMapper.getByIdList(userAclIdList);


    }

    public boolean isSuperAdmin() {
        return true;
    }
}
