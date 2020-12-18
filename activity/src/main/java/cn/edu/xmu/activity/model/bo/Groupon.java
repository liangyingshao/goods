package cn.edu.xmu.activity.model.bo;

import cn.edu.xmu.activity.model.po.GrouponActivityPo;
import cn.edu.xmu.activity.model.vo.GrouponRetVo;
import cn.edu.xmu.ooad.model.VoObject;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * description: Groupon
 * date: 2020/12/11 10:56
 * author: 杨铭
 * version: 1.0
 */
@Data
public class Groupon implements VoObject {
    Long id;
    String name;
    LocalDateTime beginTime;
    LocalDateTime endTime;

    @Override
    public GrouponRetVo createVo() {
        return new GrouponRetVo(this);
    }

    @Override
    public Object createSimpleVo() {
        return new GrouponRetVo(this);
    }

    public Groupon(GrouponActivityPo po) {
        this.setId(po.getId());
        this.setBeginTime(po.getBeginTime());
        this.setEndTime(po.getEndTime());
        this.setName(po.getName());
    }
}
