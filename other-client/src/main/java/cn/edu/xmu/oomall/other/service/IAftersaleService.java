package cn.edu.xmu.oomall.other.service;

import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.oomall.other.model.AftersaleDTO;

/**
 * @author Caixin
 * @date 2020-12-05 21:45
 */
public interface IAftersaleService {
    /**
     * 通过aftersaleId查找userId
     */
    ReturnObject<Long> findUserIdbyAftersaleId(Long aftersaleId);

    /**
     * 通过aftersaleId查找shopId
     */
    ReturnObject<Long> findShopIdbyAftersaleId(Long aftersaleId);
}
