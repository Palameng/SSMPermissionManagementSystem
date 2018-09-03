package com.mengyuan.service;

import com.google.common.base.Preconditions;
import com.mengyuan.dao.SysDeptMapper;
import com.mengyuan.exception.ParamException;
import com.mengyuan.model.SysDept;
import com.mengyuan.param.DeptParam;
import com.mengyuan.util.BeanValidator;
import com.mengyuan.util.LevelUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class SysDeptService {

    @Resource
    private SysDeptMapper sysDeptMapper;

    /**
     * 新增部门
     * @param param 前端传递过来的需要校验的参数
     */
    public void save(DeptParam param) {
        // 校验前端传递过来的参数是否合法
        BeanValidator.check(param);

        // 检查重复
        if (checkExist(param.getParentId(), param.getName(), param.getId())) {
            throw new ParamException(("同级下存在相同名称的部门"));
        }

        // 处理基本信息
        SysDept dept = SysDept.builder()
                .name(param.getName())
                .parentId(param.getParentId())
                .seq(param.getSeq())
                .remark(param.getRemark())
                .build();

        // 处理层级信息
        dept.setLevel(LevelUtil.calculateLevel(getLevel(param.getParentId()), param.getParentId()));

        // TODO 处理操作人相关信息
        dept.setOperator("system");
        dept.setOperateIp("127.0.0.1");
        dept.setOperateTime(new Date());

        sysDeptMapper.insertSelective(dept);

    }

    /**
     * 检查同级是否存在相同的部门名称
     * @param parentId 父部门ID
     * @param deptName 部门名称
     * @param deptId 部门ID
     * @return true : 检查发现存在相同名称部门
     *          false : 检查通过
     */
    private boolean checkExist(Integer parentId, String deptName, Integer deptId) {
        return sysDeptMapper.countByNameAndParentId(parentId, deptName, deptId) > 0;
    }

    /**
     * 根据ID获取部门level
     * @param deptId
     * @return
     */
    private String getLevel(Integer deptId) {
        SysDept dept = sysDeptMapper.selectByPrimaryKey(deptId);

        if (dept == null) {
            return null;
        }

        return dept.getLevel();

    }

    public void update(DeptParam param) {
        // 校验前端传递过来的参数是否合法
        BeanValidator.check(param);

        // 检查重复
        if (checkExist(param.getParentId(), param.getName(), param.getId())) {
            throw new ParamException(("同级下存在相同名称的部门"));
        }

        SysDept before = sysDeptMapper.selectByPrimaryKey(param.getId());

        Preconditions.checkNotNull(before, "待更新的部门不存在");

        SysDept after = SysDept.builder()
                .id(param.getId())
                .name(param.getName())
                .parentId(param.getParentId())
                .seq(param.getSeq())
                .remark(param.getRemark())
                .build();

        after.setLevel(LevelUtil.calculateLevel(getLevel(param.getParentId()), param.getParentId()));

        // TODO 处理操作人相关信息
        after.setOperator("system-update");
        after.setOperateIp("127.0.0.1");
        after.setOperateTime(new Date());

        updateWithChild(before, after);
    }

    @Transactional
    protected void updateWithChild(SysDept before, SysDept after) {
        // 假设 0.2.4
        String newLevelPrefix = after.getLevel();

        // 假设 0.1.3
        String oldLevelPrefix = before.getLevel();

        // 判断新旧level是否相等，不相等则把旧level下的所有子部门规划到新level下
        if (!after.getLevel().equals(before.getLevel())) {
            // 找到所有旧level下的子部门list
            List<SysDept> deptList = sysDeptMapper.getChildDeptListByLevel(before.getLevel() + "." + before.getId()); // 假设 0.1.3.5
            // 遍历子部门，获取到他们的level
            if (CollectionUtils.isNotEmpty(deptList)) {
                for (SysDept dept : deptList) {
                    String level = dept.getLevel(); // 假设 0.1.3.5
                    if (level.indexOf(oldLevelPrefix) == 0) {
                        level = newLevelPrefix + level.substring(oldLevelPrefix.length()); // 假设 0.2.4 + .5
                        dept.setLevel(level);
                    }
                }

                sysDeptMapper.batchUpdateLevel(deptList);
            }
        }

        sysDeptMapper.updateByPrimaryKey(after);

    }

}
