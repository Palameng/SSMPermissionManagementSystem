package com.mengyuan.param;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class TestVo {

    @NotBlank
    private String msg;

    @NotNull(message = "id 不能为空")
    @Max(value = 10, message = "id 不能大于10")
    @Min(value = 1, message = "至少为1")
    private Integer id;


    private List<String> str;
}
