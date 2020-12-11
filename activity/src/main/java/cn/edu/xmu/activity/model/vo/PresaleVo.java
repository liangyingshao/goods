package cn.edu.xmu.activity.model.vo;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

/**
 * description: 修改，新增vo
 * date: 2020/12/11 12:40
 * author: 杨铭
 * version: 1.0
 */
@Data
public class PresaleVo {
    String name;

    @Min(0)
    Long advancePayPrice;
    @Min(0)
    Long restPayPrice;
    @Min(0)
    Integer quantity;

    @Pattern(regexp = "^[1-9]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]) (20|21|22|23|[0-1]\\d):[0-5]\\d:[0-5]\\d$",message = "datetime格式不正确")
    String beginTime;
    @Pattern(regexp = "^[1-9]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]) (20|21|22|23|[0-1]\\d):[0-5]\\d:[0-5]\\d$",message = "datetime格式不正确")
    String endTime;
    @Pattern(regexp = "^[1-9]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]) (20|21|22|23|[0-1]\\d):[0-5]\\d:[0-5]\\d$",message = "datetime格式不正确")
    String payTime;
}
