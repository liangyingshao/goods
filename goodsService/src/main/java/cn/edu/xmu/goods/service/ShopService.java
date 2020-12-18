package cn.edu.xmu.goods.service;

import cn.edu.xmu.goods.dao.ShopDao;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ReturnObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * description: ShopService
 * date: 2020/12/10 13:08
 * author: 杨铭
 * version: 1.0
 */
@Service
public class ShopService {


    @Autowired
    ShopDao goodsDao;

    @Transactional
    public ReturnObject<VoObject> modifyShop(Long shopid, String name) {
        //1.是否有权限修改此店铺
        return goodsDao.modifyShop(shopid,name);

    }

    @Transactional
    public ReturnObject<VoObject> addShop(Long id, String name) {
        return goodsDao.addShop(id,name);
    }

    @Transactional
    public ReturnObject<VoObject> deleteShop(Long shopId) {
        return goodsDao.deleteShop(shopId);
    }

    @Transactional
    public ReturnObject<VoObject> onshelfShop(Long shopId) {
        return goodsDao.onshelfShop(shopId);
    }

    @Transactional
    public ReturnObject<VoObject> offshelfShop(Long shopId) {
        return goodsDao.offshelfShop(shopId);
    }

    @Transactional
    public ReturnObject<VoObject> auditShop(Long shopId,boolean conclusion) {
        return goodsDao.auditShop(shopId,conclusion);
    }
}
