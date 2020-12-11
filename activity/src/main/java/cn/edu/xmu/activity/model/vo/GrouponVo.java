package cn.edu.xmu.activity.model.vo;

import lombok.Data;

import javax.validation.constraints.Future;
import javax.validation.constraints.Pattern;

/**
 * description: 用于修改、新建团购活动
 * date: 2020/12/4 17:26
 * author: 杨铭
 * version: 1.0
 */
@Data
public class GrouponVo {

    @Pattern(regexp = "^[1-9]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]) (20|21|22|23|[0-1]\\d):[0-5]\\d:[0-5]\\d$",message = "datetime格式不正确")
    String beginTime;
    @Pattern(regexp = "^[1-9]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]) (20|21|22|23|[0-1]\\d):[0-5]\\d:[0-5]\\d$",message = "datetime格式不正确")
    String endTime;
    @Pattern(regexp = "^[1-9]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]) (20|21|22|23|[0-1]\\d):[0-5]\\d:[0-5]\\d$",message = "datetime格式不正确")
    String strategy;
}
