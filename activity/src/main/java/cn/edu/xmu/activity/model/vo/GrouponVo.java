package cn.edu.xmu.activity.model.vo;

import lombok.Data;

import javax.validation.constraints.Future;

/**
 * description: 用于修改、新建团购活动
 * date: 2020/12/4 17:26
 * author: 杨铭
 * version: 1.0
 */
@Data
public class GrouponVo {
    
    String beginTime;

    String endTime;

    String strategy;
}
