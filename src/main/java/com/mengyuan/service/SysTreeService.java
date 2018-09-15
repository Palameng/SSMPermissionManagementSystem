package com.mengyuan.service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.mengyuan.dao.SysAclModuleMapper;
import com.mengyuan.dao.SysDeptMapper;
import com.mengyuan.dto.AclModuleLevelDto;
import com.mengyuan.dto.DeptLevelDto;
import com.mengyuan.model.SysAclModule;
import com.mengyuan.model.SysDept;
import com.mengyuan.util.LevelUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class SysTreeService {

    @Resource
    private SysDeptMapper sysDeptMapper;

    @Resource
    private SysAclModuleMapper sysAclModuleMapper;

    /********************************************************************************
     *                               处理部门模块树部分
     ********************************************************************************/

    public List<DeptLevelDto> deptTree() {
        // 获取所有的部门信息
        List<SysDept> deptList = sysDeptMapper.getAllDept();
        List<DeptLevelDto> dtoList = Lists.newArrayList();

        for (SysDept dept : deptList) {
            // 适配成dto，即每个实例都有一个自己的deptlist
            DeptLevelDto dto = DeptLevelDto.adapt(dept);
            dtoList.add(dto);
        }

        return deptListToTree(dtoList);
    }

    /**
     * 使用multimap处理list变为树结构
     * level -> [dept1, dept2, ...]
     *
     * @param deptLevelList
     * @return
     */
    public List<DeptLevelDto> deptListToTree(List<DeptLevelDto> deptLevelList) {
        if (CollectionUtils.isEmpty(deptLevelList)) {
            return Lists.newArrayList();
        }

        Multimap<String, DeptLevelDto> levelDeptMap = ArrayListMultimap.create();
        List<DeptLevelDto> rootList = Lists.newArrayList();

        for (DeptLevelDto dto : deptLevelList) {
            levelDeptMap.put(dto.getLevel(), dto);

            // 提取出第一层root层的部门列表
            if (LevelUtil.ROOT.equals(dto.getLevel())) {
                rootList.add(dto);
            }
        }

        //seq从小到大排序
        Collections.sort(rootList, new Comparator<DeptLevelDto>() {
            @Override
            public int compare(DeptLevelDto o1, DeptLevelDto o2) {
                return o1.getSeq() - o2.getSeq();
            }
        });

        //递归生成树
        transformDeptTree(rootList, LevelUtil.ROOT, levelDeptMap);

        return rootList;
    }

    public void transformDeptTree(List<DeptLevelDto> deptLevelList,
                                  String level,
                                  Multimap<String, DeptLevelDto> levelDeptMap) {
        for (int i = 0; i < deptLevelList.size(); i++) {
            // 遍历该层的每一个元素
            DeptLevelDto deptLevelDto = deptLevelList.get(i);

            // 处理当前层级的数据
            String nextLevel = LevelUtil.calculateLevel(level, deptLevelDto.getId());

            //处理下一层
            List<DeptLevelDto> tempDeptList = (List<DeptLevelDto>) levelDeptMap.get(nextLevel);

            //判断从map中读取的数据是否为空
            if (CollectionUtils.isNotEmpty(tempDeptList)) {
                // 排序
                Collections.sort(tempDeptList, deptSeqComparator);

                // 设置下一层部门
                deptLevelDto.setDeptList(tempDeptList);

                // 进入到下一层处理
                transformDeptTree(tempDeptList, nextLevel, levelDeptMap);
            }
        }
    }

    public Comparator<DeptLevelDto> deptSeqComparator = new Comparator<DeptLevelDto>() {
        @Override
        public int compare(DeptLevelDto o1, DeptLevelDto o2) {
            return o1.getSeq() - o2.getSeq();
        }
    };


    /********************************************************************************
     *                               处理权限模块树部分
     ********************************************************************************/


    /**
     * 转换基本的实体类为dto类
     * @return
     */
    public List<AclModuleLevelDto> aclModuleTree() {
        List<SysAclModule> aclModuleList = sysAclModuleMapper.getAllAclModule();
        List<AclModuleLevelDto> dtoList = Lists.newArrayList();

        //获取的所有SysAclModule适配成带有子权限list的AclModuleLevelDto
        for (SysAclModule aclModule : aclModuleList) {
            dtoList.add(AclModuleLevelDto.adapt(aclModule));
        }

        return aclModuleListToTree(dtoList);
    }


    /**
     * 数据准备，Multimap和rootlist
     * @param dtoList
     * @return
     */
    public List<AclModuleLevelDto> aclModuleListToTree(List<AclModuleLevelDto> dtoList) {
        if (CollectionUtils.isEmpty(dtoList)) {
            return Lists.newArrayList();
        }

        Multimap<String, AclModuleLevelDto> levelAclModuleMap = ArrayListMultimap.create();
        List<AclModuleLevelDto> rootList = Lists.newArrayList();

        for (AclModuleLevelDto dto : dtoList) {
            levelAclModuleMap.put(dto.getLevel(), dto);

            // 提取出第一层root层的部门列表
            if (LevelUtil.ROOT.equals(dto.getLevel())) {
                rootList.add(dto);
            }
        }

        //seq从小到大排序
        Collections.sort(rootList, aclModuleSeqComparator);

        //递归生成树
        transformAclModuleTree(rootList, LevelUtil.ROOT, levelAclModuleMap);

        return rootList;

    }


    public void transformAclModuleTree(List<AclModuleLevelDto> aclModuleLevelList,
                                  String level,
                                  Multimap<String, AclModuleLevelDto> levelAclModuleMap) {
        // 这里的aclModuleLevelList第一次进来时为 rootList，即顶层权限模块列表
        for (int i = 0; i < aclModuleLevelList.size(); i++) {

            // 遍历该层的每一个元素
            AclModuleLevelDto aclModuleLevelDto = aclModuleLevelList.get(i);

            // 得到当前层级每个部门对应的下一层level
            String nextLevel = LevelUtil.calculateLevel(level, aclModuleLevelDto.getId());

            //获取当前部门aclModuleLevelDto下的所有子部门list
            List<AclModuleLevelDto> tempAclModuleList = (List<AclModuleLevelDto>) levelAclModuleMap.get(nextLevel);

            //判断从map中读取的list是否为空，为空说明该部门已经是该分支下最后一个部门了,不为空则继续排序设置递归
            if (CollectionUtils.isNotEmpty(tempAclModuleList)) {
                // 排序
                Collections.sort(tempAclModuleList, aclModuleSeqComparator);

                // 设置下一层部门
                aclModuleLevelDto.setAclModuleList(tempAclModuleList);

                // 进入到下一层处理
                transformAclModuleTree(tempAclModuleList, nextLevel, levelAclModuleMap);
            }
        }
    }

    public Comparator<AclModuleLevelDto> aclModuleSeqComparator = new Comparator<AclModuleLevelDto>() {
        @Override
        public int compare(AclModuleLevelDto o1, AclModuleLevelDto o2) {
            return o1.getSeq() - o2.getSeq();
        }
    };


}
