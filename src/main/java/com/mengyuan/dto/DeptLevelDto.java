package com.mengyuan.dto;

import com.google.common.collect.Lists;
import com.mengyuan.model.SysDept;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.BeanUtils;

import java.util.List;

/**
 * 部门树DTO类
 */
@Getter
@Setter
@ToString
public class DeptLevelDto extends SysDept {



    //继承sysDept,并增加一个list存储子部门列表
    private List<DeptLevelDto> deptList = Lists.newArrayList();

    /**
     * 适配方法
     * @param dept 传入的部门对象
     * @return dto对象
     */
    public static DeptLevelDto adapt(SysDept dept) {
        DeptLevelDto dto = new DeptLevelDto();
        //拷贝属性
        BeanUtils.copyProperties(dept, dto);
        return dto;
    }

    public void setDeptList(List<DeptLevelDto> deptList) {
        this.deptList = deptList;
    }
}
