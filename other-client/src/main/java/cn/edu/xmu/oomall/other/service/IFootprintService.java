package cn.edu.xmu.oomall.other.service;


import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;

import java.util.List;

/**
 * 足迹调用接口
 *
 * @author yang8miao
 * @date 2020-12-12 16:06
 */
public interface IFootprintService {

    ReturnObject<ResponseCode> postFootprint(Long customerId, Long skuId);
}
