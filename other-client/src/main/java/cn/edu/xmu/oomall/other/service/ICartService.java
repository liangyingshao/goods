package cn.edu.xmu.oomall.other.service;


import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;

import java.util.List;

public interface ICartService {

    /**
     * 通过skuId清购物车
     */
    ReturnObject<ResponseCode> deleteGoodsInCart(Long customerId, List<Long> skuIdList);

}