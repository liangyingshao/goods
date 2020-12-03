package cn.edu.xmu.goods.dao;

import cn.edu.xmu.goods.mapper.ShopPoMapper;
import cn.edu.xmu.goods.model.po.ShopPo;
import cn.edu.xmu.goods.model.vo.ShopStateVo;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ResponseUtil;
import cn.edu.xmu.ooad.util.ReturnObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;


@Repository
public class ShopDao {

    @Autowired
    ShopPoMapper shopPoMapper;

    private static final Logger logger = LoggerFactory.getLogger(ShopDao.class);

    /**
     * description: modifyShop
     * version: 1.0
     * date: 2020/11/29 23:12
     * author: 杨铭
     *
     * @param id 店铺id
     * @param name 店铺名
     * @return cn.edu.xmu.ooad.util.ReturnObject<cn.edu.xmu.ooad.model.VoObject>
     */
    public ReturnObject<VoObject> modifyShop(Long id, String name)
    {
        ShopPo shopPo = new ShopPo();
        shopPo.setName(name);
        shopPo.setId(id);
        try {
            shopPoMapper.updateByPrimaryKeySelective(shopPo);
        } catch (DataAccessException e) {
            StringBuilder message = new StringBuilder().append("modifyShop: ").append(e.getMessage());
            logger.error(message.toString());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }
        return new ReturnObject<>(ResponseCode.OK);
    }


    /**
     * description: addShop
     * version: 1.0
     * date: 2020/11/29 23:11
     * author: 杨铭
     *
     * @param id 用户id
     * @param name 店铺名称
     * @return cn.edu.xmu.ooad.util.ReturnObject<cn.edu.xmu.ooad.model.VoObject>
     */
    public ReturnObject<VoObject> addShop(Long id, String name) {

        //根据id查询此用户的departId是否是-1
        //调用权限模块dubbo

        //新增店铺
        ShopPo shopPo = new ShopPo();
        shopPo.setName(name);

        try {
            shopPoMapper.insertSelective(shopPo);
        } catch (Exception e) {
            StringBuilder message = new StringBuilder().append("addShop: ").append(e.getMessage());
            logger.error(message.toString());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }

        return new ReturnObject<>(ResponseCode.OK);
    }

    /**
     * description: deleteShop
     * version: 1.0
     * date: 2020/11/29 23:39
     * author: 杨铭
     * 
     * @param shopId 店铺id
     * @return cn.edu.xmu.ooad.util.ReturnObject<cn.edu.xmu.ooad.model.VoObject>
     */ 
    public ReturnObject<VoObject> deleteShop(Long shopId) {

        ShopPo shopPo = new ShopPo();
        shopPo.setState(ShopPo.ShopStatus.CLOSED.getCode().byteValue());
        shopPo.setId(shopId);
        try {
            shopPoMapper.updateByPrimaryKeySelective(shopPo);
        } catch (Exception e) {
            StringBuilder message = new StringBuilder().append("deleteShop: ").append(e.getMessage());
            logger.error(message.toString());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }
        return new ReturnObject<>(ResponseCode.OK);
    }


    /**
     * description: onshelfShop
     * version: 1.0
     * date: 2020/12/3 8:35
     * author: 杨铭
     *
     * @param shopId 店铺id
     * @return cn.edu.xmu.ooad.util.ReturnObject<cn.edu.xmu.ooad.model.VoObject>
     */
    public ReturnObject<VoObject> onshelfShop(Long shopId) {
        //修改状态
        ShopPo shopPo = new ShopPo();
        shopPo.setId(shopId);
        shopPo.setState(ShopPo.ShopStatus.ONLINE.getCode().byteValue());
        try {
            shopPoMapper.updateByPrimaryKeySelective(shopPo);
        } catch (Exception e) {
            StringBuilder message = new StringBuilder().append("onshelfShop: ").append(e.getMessage());
            logger.error(message.toString());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }
        return new ReturnObject<>(ResponseCode.OK);
    }

    /**
     * description: offshelfShop
     * version: 1.0
     * date: 2020/12/3 8:35
     * author: 杨铭
     *
     * @param shopId 店铺id
     * @return cn.edu.xmu.ooad.util.ReturnObject<cn.edu.xmu.ooad.model.VoObject>
     */
    public ReturnObject<VoObject> offshelfShop(Long shopId) {
        //修改状态
        ShopPo shopPo = new ShopPo();
        shopPo.setId(shopId);
        shopPo.setState(ShopPo.ShopStatus.OFFLINE.getCode().byteValue());
        try {
            shopPoMapper.updateByPrimaryKeySelective(shopPo);
        } catch (Exception e) {
            StringBuilder message = new StringBuilder().append("offshelfShop: ").append(e.getMessage());
            logger.error(message.toString());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }
        return new ReturnObject<>(ResponseCode.OK);
    }

    /**
     * description: auditShop
     * version: 1.0
     * date: 2020/12/3 8:36
     * author: 杨铭
     *
     * @param shopId 店铺id
     * @param conclusion 审核是否通过
     * @return cn.edu.xmu.ooad.util.ReturnObject<cn.edu.xmu.ooad.model.VoObject>
     */
    public ReturnObject<VoObject> auditShop(Long shopId,boolean conclusion) {
        ShopPo shopPo = new ShopPo();
        shopPo.setId(shopId);
        if(conclusion)
            shopPo.setState(ShopPo.ShopStatus.ONLINE.getCode().byteValue());
        else
            shopPo.setState(ShopPo.ShopStatus.AUDIT_FAIL.getCode().byteValue());

        try {
            shopPoMapper.updateByPrimaryKeySelective(shopPo);
        } catch (Exception e) {
            StringBuilder message = new StringBuilder().append("auditShop: ").append(e.getMessage());
            logger.error(message.toString());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }
        return new ReturnObject<>(ResponseCode.OK);
    }
}
