package cn.edu.xmu.goods.service;

import cn.edu.xmu.goods.dao.ShopDao;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.privilegeservice.client.IUserService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @DubboReference(check = false)
    IUserService iUserService;

    private static final Logger logger = LoggerFactory.getLogger(ShopService.class);

    @Transactional
    public ReturnObject<VoObject> modifyShop(Long shopid, String name) {
        //1.是否有权限修改此店铺
        return goodsDao.modifyShop(shopid,name);

    }

    @Transactional
    public ReturnObject<VoObject> addShop(Long id, Long departId, String name) {
        return goodsDao.addShop(id,departId,name);
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
    public ReturnObject<VoObject> auditShop(Long shopId, boolean conclusion) {

        ReturnObject retObj = goodsDao.auditShop(shopId,conclusion);
        if(retObj.getCode()!= ResponseCode.OK)
            return retObj;

        try {
            iUserService.changeUserDepart(0L, shopId);
        } catch (Exception e) {
            logger.debug("dubbo error!");
        }
        return retObj;
    }
}
