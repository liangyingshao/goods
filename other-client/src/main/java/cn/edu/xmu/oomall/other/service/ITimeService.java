package cn.edu.xmu.oomall.other.service;

import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.oomall.other.model.TimeDTO;

import java.util.List;

public interface ITimeService {

    /**
     * 获取当前类型为type的所有时间段的id
     */
    ReturnObject<List<Long>> listSelectAllTimeSegmentId(Byte type);

    /**
     * 根据id查询（秒杀/广告）时间段
     */
    ReturnObject<TimeDTO> getTimeSegmentId(Byte type, Long id);

    /**
     * 获得当前（秒杀/广告）时间段id
     */
    ReturnObject<Long> getCurrentSegmentId(Byte type);
}