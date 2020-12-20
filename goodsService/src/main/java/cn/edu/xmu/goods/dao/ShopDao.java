package cn.edu.xmu.goods.dao;

import cn.edu.xmu.goods.mapper.ShopPoMapper;
import cn.edu.xmu.goods.model.bo.GoodsSpu;
import cn.edu.xmu.goods.model.bo.Shop;
import cn.edu.xmu.goods.model.po.ShopPo;
import cn.edu.xmu.goods.model.vo.ShopRetVo;
import cn.edu.xmu.goods.model.vo.ShopStateVo;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * description: ShopDao
 * date: 2020/12/10 13:04
 * author: 杨铭
 * version: 1.0
 */
@Repository
public class ShopDao {

    @Autowired
    ShopPoMapper shopPoMapper;

    @Autowired
    GoodsSpuDao goodsSpuDao;

    @Autowired
    GoodsDao goodsDao;

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

        //1.读出此店铺
        ShopPo oldPo = null;
        try {
            oldPo = shopPoMapper.selectByPrimaryKey(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //2.店铺不存在
        if(oldPo == null)
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);

        //3.店铺的状态为已下线，才能修改
        if(oldPo.getState()!=Shop.ShopStatus.OFFLINE.getCode().byteValue()&&oldPo.getState()!=Shop.ShopStatus.NOT_AUDIT.getCode().byteValue())
            return new ReturnObject<>(ResponseCode.SHOP_STATENOTALLOW);

        //4.修改
        ShopPo newPo = new ShopPo();
        newPo.setName(name);
        newPo.setId(id);
        try {
            shopPoMapper.updateByPrimaryKeySelective(newPo);
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
     * date: 2020/12/3 17:38
     * author: 杨铭
     *
     * @param id 店铺id
     * @param name 店铺名称
     * @return cn.edu.xmu.ooad.util.ReturnObject<cn.edu.xmu.ooad.model.VoObject>
     */
    public ReturnObject<VoObject> addShop(Long id, Long departId, String name) {

        //用户已有店铺
        if(departId!=-1)
            return new ReturnObject<>(ResponseCode.USER_HASSHOP);

        //新增店铺
        ShopPo shopPo = new ShopPo();
        shopPo.setName(name);
        shopPo.setState(Shop.ShopStatus.NOT_AUDIT.getCode().byteValue());

        try {
            shopPoMapper.insertSelective(shopPo);
        } catch (Exception e) {
            StringBuilder message = new StringBuilder().append("addShop:insert ").append(e.getMessage());
            logger.error(message.toString());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }

        //读出新的shopPo
        ShopPo newPo = null;
        try {
            newPo = shopPoMapper.selectByPrimaryKey(shopPo.getId());
        } catch (Exception e) {
            StringBuilder message = new StringBuilder().append("addShop: select ").append(e.getMessage());
            logger.error(message.toString());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }
        Shop shop = new Shop(newPo);
        return new ReturnObject<>(shop);
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

        //查询是否有shopId
        ShopPo oldpo = null;
        try {
            oldpo = shopPoMapper.selectByPrimaryKey(shopId);
        } catch (Exception e) {
            StringBuilder message = new StringBuilder().append("deleteShop: ").append(e.getMessage());
            logger.error(message.toString());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }
        if(oldpo==null)
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);

        //状态不允许
        if(oldpo.getState()!=Shop.ShopStatus.OFFLINE.getCode().byteValue()&&
                oldpo.getState()!=Shop.ShopStatus.ONLINE.getCode().byteValue())
            return new ReturnObject<>(ResponseCode.SHOP_STATENOTALLOW);

        ShopPo shopPo = new ShopPo();
        shopPo.setState(Shop.ShopStatus.CLOSED.getCode().byteValue());
        shopPo.setId(shopId);
        try {
            shopPoMapper.updateByPrimaryKeySelective(shopPo);
        } catch (Exception e) {
            StringBuilder message = new StringBuilder().append("deleteShop: ").append(e.getMessage());
            logger.error(message.toString());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }
        //级联删除
        List<Long> idlist = goodsSpuDao.getAllSpuIdByShopId(shopId).getData();
        if(idlist!=null){
            for(Long i : idlist) {
                goodsSpuDao.deleteGoodsSpu(shopId,i);
            }
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
        //查询是否有shopId
        ShopPo oldpo = null;
        try {
            oldpo = shopPoMapper.selectByPrimaryKey(shopId);
        } catch (Exception e) {
            StringBuilder message = new StringBuilder().append("deleteShop: ").append(e.getMessage());
            logger.error(message.toString());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }
        if(oldpo==null)
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);

        //只有未上线才可以
        if(oldpo.getState()!=Shop.ShopStatus.OFFLINE.getCode().byteValue())
            return new ReturnObject<>(ResponseCode.SHOP_STATENOTALLOW);

        //修改状态
        ShopPo shopPo = new ShopPo();
        shopPo.setId(shopId);
        shopPo.setState(Shop.ShopStatus.ONLINE.getCode().byteValue());
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

        //查询是否有shopId
        ShopPo oldpo = null;
        try {
            oldpo = shopPoMapper.selectByPrimaryKey(shopId);
        } catch (Exception e) {
            StringBuilder message = new StringBuilder().append("deleteShop: ").append(e.getMessage());
            logger.error(message.toString());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }
        if(oldpo==null)
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);

        //onShelf才可以
        if(oldpo.getState()!=Shop.ShopStatus.ONLINE.getCode().byteValue())
            return new ReturnObject<>(ResponseCode.SHOP_STATENOTALLOW);

        //修改状态
        ShopPo shopPo = new ShopPo();
        shopPo.setId(shopId);
        shopPo.setState(Shop.ShopStatus.OFFLINE.getCode().byteValue());
        try {
            shopPoMapper.updateByPrimaryKeySelective(shopPo);
            //级联下架sku
            List<Long> idlist = goodsDao.getAllSkuIdByShopId(shopId).getData();
            for(Long i : idlist){
                goodsDao.putOffGoodsOnSale(shopId,i);
            }
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
    public ReturnObject<VoObject> auditShop(Long shopId, Boolean conclusion) {

        //查询是否有shopId
        ShopPo oldpo = null;
        try {
            oldpo = shopPoMapper.selectByPrimaryKey(shopId);
        } catch (Exception e) {
            StringBuilder message = new StringBuilder().append("deleteShop: ").append(e.getMessage());
            logger.error(message.toString());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }
        if(oldpo==null)
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);

        //状态禁止
        if(oldpo.getState()!=Shop.ShopStatus.NOT_AUDIT.getCode().byteValue())
            return new ReturnObject<>(ResponseCode.SHOP_STATENOTALLOW);

        //更新数据库
        ShopPo shopPo = new ShopPo();
        shopPo.setId(shopId);
        if(conclusion)
            shopPo.setState(Shop.ShopStatus.ONLINE.getCode().byteValue());
        else
            shopPo.setState(Shop.ShopStatus.AUDIT_FAIL.getCode().byteValue());

        try {
            shopPoMapper.updateByPrimaryKeySelective(shopPo);
        } catch (Exception e) {
            StringBuilder message = new StringBuilder().append("auditShop: ").append(e.getMessage());
            logger.error(message.toString());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }

        return new ReturnObject<>(ResponseCode.OK);
    }

    public ReturnObject<ShopPo> getShopById(Long id)
    {
        ShopPo shopPo = null;
        try {
            shopPo = shopPoMapper.selectByPrimaryKey(id);
        } catch (Exception e) {
            StringBuilder message = new StringBuilder().append("getShopById: ").append(e.getMessage());
            logger.error(message.toString());
        }
        return new ReturnObject<>(shopPo);

    }


}
