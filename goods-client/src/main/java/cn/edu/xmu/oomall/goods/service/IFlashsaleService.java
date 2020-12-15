package cn.edu.xmu.oomall.goods.service;

import cn.edu.xmu.ooad.util.ReturnObject;

/**
 * @author: 24320182203259 邵良颖
 * Created at: 2020-12-08 13:29
 * version: 1.0
 */
public interface IFlashsaleService {

    /**
     * 删除时间段对应的所有flashsale
     * id：时间段id
     */
    ReturnObject deleteSegmentFlashsale(Long id);
}
