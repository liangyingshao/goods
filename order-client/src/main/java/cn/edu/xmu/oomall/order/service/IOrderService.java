package cn.edu.xmu.oomall.order.service;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.oomall.order.model.*;

/**
 * 订单服务调用接口
 */
public interface IOrderService {

    /**
     * 根据运费模板Id获取简单运费模板,如果freightId为null则返回默认模板
     */
    ReturnObject<SimpleFreightModelDTO> getSimpleFreightById(Long freightId);


}
