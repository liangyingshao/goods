package cn.edu.xmu.activity.model.vo;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

/**
 * description: 用于修改、新建团购活动
 * date: 2020/12/4 17:26
 * author: 杨铭
 * version: 1.0
 */
@Data
public class GrouponVo {

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime beginTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime endTime;

    String strategy;
}
