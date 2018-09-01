package com.mengyuan.param;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DeptParam {
    private Integer id;

    private String name;

    private Integer parentId;

    private Integer seq;

    private String remark;
}
