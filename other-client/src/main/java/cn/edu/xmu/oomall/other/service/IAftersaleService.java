package cn.edu.xmu.oomall.other.service;


import cn.edu.xmu.ooad.util.ReturnObject;

public interface IAftersaleService {

    /**
     * 通过aftersaleId查找userId
     */
    ReturnObject<Long> findUserIdbyAftersaleId(Long aftersaleId);

    /**
     * 通过aftersaleId查找shopId
     */
    ReturnObject<Long> findShopIdbyAftersaleId(Long aftersaleId);

    /**
     * 通过aftersaleId查找orderItemId
     */
    ReturnObject<Long> findOrderItemIdbyAftersaleId(Long aftersaleId);

}
