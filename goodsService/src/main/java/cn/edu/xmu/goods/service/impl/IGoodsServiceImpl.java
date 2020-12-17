package cn.edu.xmu.goods.service.impl;

import cn.edu.xmu.goods.dao.GoodsDao;
import cn.edu.xmu.goods.dao.GoodsSpuDao;
import cn.edu.xmu.goods.dao.ShopDao;
import cn.edu.xmu.goods.model.po.GoodsSkuPo;
import cn.edu.xmu.goods.model.po.GoodsSpuPo;
import cn.edu.xmu.goods.model.po.ShopPo;
import cn.edu.xmu.goods.model.vo.GoodsSkuDetailRetVo;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.oomall.goods.model.*;
//import cn.edu.xmu.oomall.goods.model.GoodsDetailDTO;
//import cn.edu.xmu.oomall.goods.model.GoodsFreightDTO;
//import cn.edu.xmu.oomall.goods.model.ShopDetailDTO;
import cn.edu.xmu.oomall.goods.service.IActivityService;
import cn.edu.xmu.oomall.goods.service.IFlashsaleService;
import cn.edu.xmu.oomall.goods.service.IGoodsService;
//import com.alibaba.dubbo.config.annotation.Service;
import org.apache.dubbo.config.annotation.DubboReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j

@DubboService
public class IGoodsServiceImpl implements IGoodsService {
    @Autowired
    GoodsDao goodsDao;

    @Autowired
    GoodsSpuDao goodsSpuDao;

    @Autowired
    ShopDao shopDao;

    @DubboReference(check = false)
    private IActivityService IActivityService;

    @DubboReference(check = false)
    private IFlashsaleService iFlashsaleService;

    @Override
    public ReturnObject<List<Long>> getAllSkuIdByShopId(Long shopId) {
        ReturnObject<List<Long>> returnObject=goodsDao.getAllSkuIdByShopId(shopId);
        return returnObject;
    }

    @Override
    public ReturnObject<Long> getShopIdBySkuId(Long skuId) {
        return goodsDao.getShopIdBySkuId(skuId);

    }

    @Override
    public ReturnObject<Boolean> getVaildSkuId(Long skuId) {
        ReturnObject<Boolean> returnObject=goodsDao.getVaildSkuId(skuId);
        return returnObject;
    }

    @Override
    public ReturnObject<SkuInfoDTO> getSelectSkuInfoBySkuId(Long skuId)
    {
        SkuInfoDTO skuInfoDTO=goodsDao.getSelectSkuInfoBySkuId(skuId);
        if(skuInfoDTO!=null)
            skuInfoDTO.setPrice(goodsDao.getPriceBySkuId(skuId).getData());
        return new ReturnObject<>(skuInfoDTO);
    }

    @Override
    public ReturnObject<Map<Long, SkuInfoDTO>> listSelectSkuInfoById(List<Long> skuIdList)
    {
        ReturnObject<Map<Long, SkuInfoDTO>> returnObject=goodsDao.listSelectSkuInfoById(skuIdList);
        return returnObject;
    }

    @Override
    public ReturnObject<GoodsInfoDTO> getSelectGoodsInfoBySkuId(Long skuId)
    {
        GoodsInfoDTO goodsInfoDTO=goodsDao.getSelectGoodsInfoBySkuId(skuId);
        if(goodsInfoDTO!=null) {
            goodsInfoDTO.setPrice(goodsDao.getPriceBySkuId(skuId).getData());
            List<CouponInfoDTO> couponInfoDTOs = IActivityService.getCouponInfoBySkuId(skuId);
            goodsInfoDTO.setCouponActivity(couponInfoDTOs);
        }
        return new ReturnObject<>(goodsInfoDTO);
    }


    @Override
    public List<SkuNameInfoDTO> getSelectSkuNameListBySkuIdList(List<Long> idList) {
        List<SkuNameInfoDTO> nameList = new ArrayList<>();
        for (Long aLong : idList) {
            GoodsSkuDetailRetVo goodsSkuRetVo = goodsDao.getSku(aLong).getData();
            //判空指针
            if (goodsSkuRetVo != null) {
                SkuNameInfoDTO skuNameInfoDTO = new SkuNameInfoDTO();
                skuNameInfoDTO.setId(skuNameInfoDTO.getId());
                skuNameInfoDTO.setName(goodsSkuRetVo.getName());
                nameList.add(skuNameInfoDTO);
            }
        }
        return nameList;
    }

    @Override
    public ReturnObject<ResponseCode> checkSkuUsableBySkuShop(Long skuId, Long shopId) {
        ReturnObject<ResponseCode> returnObject=goodsDao.checkSkuUsableBySkuShop(skuId,shopId);
        return returnObject;
    }



    @Override
    public ReturnObject<SimpleShopDTO> getSimpleShopByShopId(Long id) {
        SimpleShopDTO simpleShopDTO = null;
        ShopPo shopPo = shopDao.getShopById(id).getData();
        if(shopPo!=null)
        {
            simpleShopDTO = new SimpleShopDTO();
            simpleShopDTO.setId(shopPo.getId());
            simpleShopDTO.setName(shopPo.getName());
        }
        return new ReturnObject<>(simpleShopDTO);
    }

    @Override
    public ReturnObject<SimpleGoodsSkuDTO> getSimpleSkuBySkuId(Long skuId) {
        SimpleGoodsSkuDTO simpleGoodsSkuDTO = null;
        if(skuId!=null) {
            simpleGoodsSkuDTO=goodsDao.getSimpleSkuBySkuId(skuId);
            simpleGoodsSkuDTO.setPrice(goodsDao.getPriceBySkuId(skuId).getData());
        }
        return new ReturnObject<>(simpleGoodsSkuDTO);
    }

    @Override
    public ReturnObject<GoodsSpuPoDTO> getSpuBySpuId(Long spuId) {
        GoodsSpuPo goodsSpuPo = goodsSpuDao.getSpuBySpuId(spuId).getData();
        GoodsSpuPoDTO goodsSpuPoDTO = null;
        if(goodsSpuPo!=null)
        {
            goodsSpuPoDTO = new GoodsSpuPoDTO();
            goodsSpuPoDTO.setId(goodsSpuPo.getId());
            goodsSpuPoDTO.setGmtModified(goodsSpuPo.getGmtModified());
            goodsSpuPoDTO.setGmtCreate(goodsSpuPo.getGmtCreate());
            goodsSpuPoDTO.setDisabled(goodsSpuPo.getDisabled());
            goodsSpuPoDTO.setSpec(goodsSpuPo.getSpec());
            goodsSpuPoDTO.setImageUrl(goodsSpuPo.getImageUrl());
            goodsSpuPoDTO.setDetail(goodsSpuPo.getDetail());
            goodsSpuPoDTO.setGoodsSn(goodsSpuPo.getGoodsSn());
            goodsSpuPoDTO.setShopId(goodsSpuPo.getShopId());
            goodsSpuPoDTO.setFreightId(goodsSpuPo.getFreightId());
            goodsSpuPoDTO.setCategoryId(goodsSpuPo.getCategoryId());
            goodsSpuPoDTO.setBrandId(goodsSpuPo.getBrandId());
            goodsSpuPoDTO.setName(goodsSpuPo.getName());
        }

        return new ReturnObject<>(goodsSpuPoDTO);
    }

    @Override
    public List<SkuInfoDTO> getSelectSkuListBySkuIdList(List<Long> idList) {
        List<SkuInfoDTO> list=new ArrayList<>();
        idList.forEach(x->{
            list.add(getSelectSkuInfoBySkuId(x).getData());
        });
        return list;
    }

    @Override
    public ReturnObject<ShopDetailDTO> getShopInfoBySkuId(Long skuId) {
        ReturnObject<ShopDetailDTO>returnObject=goodsDao.getShopInfoBySkuId(skuId);
        return returnObject;
    }


    @Override
    public ReturnObject<GoodsFreightDTO> getGoodsFreightDetailBySkuId(Long skuId) {
        ReturnObject<GoodsFreightDTO> returnObject=goodsDao.getGoodsFreightDetailBySkuId(skuId);
        return returnObject;
    }

    /**
     * 根据skuId查找商品信息
     * 下订单或退换货订单
     * 0普通活动或者可能是秒杀  1团购  2预售 3优惠券
     * quantity可正可负
     * @param skuId
     * @param type
     * @param activityId
     * @param quantity
     * @return ReturnObject<GoodsDetailDTO>
     */
    @Override
    public ReturnObject<GoodsDetailDTO> getGoodsBySkuId(Long skuId, Byte type, Long activityId, Integer quantity) {
        ReturnObject<GoodsDetailDTO> returnObject=goodsDao.getGoodsBySkuId(skuId);
        ReturnObject<GoodsDetailDTO>ret=new ReturnObject<>();
        switch (type) {
            case (2)://预售
            {
                ret= IActivityService.modifyPresaleInventory(activityId, quantity);
                //如果预售不存在返回不存在的错误码
                //如果预售状态不对返回不存在的错误码
                //如果库存不够设置返回库存不够的错误码
                //如果库存足够，就扣库存，并返回扣库存之前的库存和定金
                if(ret.getCode().equals(ResponseCode.OK)&&ret.getData().getInventory()>=quantity)
                {
                    returnObject.getData().setPrice(ret.getData().getPrice());
                    returnObject.getData().setInventory(ret.getData().getInventory());
                    ret=goodsDao.modifyInventory(skuId,quantity);
                }
                break;
            }
            case (0)://秒杀/普通
            {
                ret= iFlashsaleService.modifyFlashsaleItem(activityId, quantity);
                //如果秒杀不存在返回RESOURCE_ID_NOTEXIST，跳到下个环节
                //如果库存不够返回库存不够的错误码
                //如果库存足够，就扣库存，并返回扣库存之前的库存和定金
                if(ret.getCode().equals(ResponseCode.OK)&&ret.getData().getInventory()>=quantity)//秒杀
                {
                    returnObject.getData().setPrice(ret.getData().getPrice());
                    returnObject.getData().setInventory(ret.getData().getInventory());
                    ret=goodsDao.modifyInventory(skuId,quantity);
                }
                else if(ret.getCode().equals(ResponseCode.RESOURCE_ID_NOTEXIST))//普通
                {
                    ret=goodsDao.modifyInventory(skuId,quantity);
                    returnObject.getData().setPrice(ret.getData().getPrice());
                    returnObject.getData().setInventory(ret.getData().getInventory());
                }
                break;
            }
            case (1)://团购
            case (3)://优惠券
            {
                ret=goodsDao.modifyInventory(skuId,quantity);
                returnObject.getData().setPrice(ret.getData().getPrice());
                returnObject.getData().setInventory(ret.getData().getInventory());
                break;
            }
            default:return new ReturnObject<>(ResponseCode.FIELD_NOTVALID);
        }
        return returnObject;
    }


    @Override
    public ReturnObject<ResponseCode> signalDecrInventory(List<Long> skuIds, List<Integer> quantity) {
        return null;
    }


    @Override
    public ReturnObject<ShopDetailDTO> getShopInfoByShopId(Long shopId) {
        ShopDetailDTO shopDetailDTO = null;
        if(shopId!=null) {
            ShopPo shopPo = shopDao.getShopById(shopId).getData();
            shopDetailDTO.setShopId(shopPo.getId());
            shopDetailDTO.setName(shopPo.getName());
            shopDetailDTO.setState(shopPo.getState());
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            shopDetailDTO.setGmtCreate(dtf.format(shopPo.getGmtCreate()));
            shopDetailDTO.setGmtModified(dtf.format(shopPo.getGmtModified()));
        }
        return new ReturnObject<>(shopDetailDTO);
    }

    @Override
    public ReturnObject<List<Long>> getSkuIdsBySpuId(Long spuId){

        return goodsDao.getSkuIdsBySpuId(spuId);
    }

    /**
     * 根据activityId获得优惠活动Json字符串
     */
    @Override
    public ReturnObject<Boolean> updateSpuFreightId(Long freightModelId){
        return goodsDao.updateSpuFreightId(freightModelId);
    }

    public ReturnObject<Long> getPriceBySkuId(Long skuId) {
        ReturnObject<GoodsDetailDTO> ret=iFlashsaleService.modifyFlashsaleItem(skuId,0);
        if(ret.getCode().equals(ResponseCode.OK))
            return new ReturnObject<>(ret.getData().getPrice());
        return new ReturnObject<>(goodsDao.getPriceBySkuId(skuId).getData());
    }
}
