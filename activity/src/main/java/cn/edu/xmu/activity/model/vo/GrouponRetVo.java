package cn.edu.xmu.activity.model.vo;

import cn.edu.xmu.activity.model.bo.Groupon;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * description: GrouponRetVo
 * date: 2020/12/11 10:57
 * author: 杨铭
 * version: 1.0
 */
@Data
public class GrouponRetVo {
    Long id;
    String name;
    LocalDateTime beginTime;
    LocalDateTime endTime;

    public GrouponRetVo(Groupon grouponActivity){
        this.id = grouponActivity.getId();
        this.beginTime=grouponActivity.getBeginTime();
        this.endTime=grouponActivity.getEndTime();
        this.name=grouponActivity.getName();
    }
}
