package cn.edu.xmu.activity.model.bo;

import cn.edu.xmu.activity.model.po.GrouponActivityPo;
import cn.edu.xmu.activity.model.vo.GrouponRetVo;
import cn.edu.xmu.ooad.model.VoObject;
import lombok.Data;

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
    String beginTime;
    String endTime;

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
        this.setBeginTime(po.getBeginTime().toString());
        this.setEndTime(po.getEndTime().toString());
        this.setName(po.getName());
    }
}
