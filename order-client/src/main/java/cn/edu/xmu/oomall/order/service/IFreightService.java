package cn.edu.xmu.oomall.order.service;

import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.oomall.order.model.SimpleFreightModelDTO;

import java.util.List;

public interface IFreightService {

    /**
     * 计算运费模板
     */
    ReturnObject<Long> calcuFreightPrice(List<Integer> count, List<Long> skuId, Long regionId);

    /**
     * 根据运费模板Id获取简单运费模板,如果freightId为null则返回默认模板
     */
    ReturnObject<SimpleFreightModelDTO> getSimpleFreightById(Long freightId);
}