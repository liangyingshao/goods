package cn.edu.xmu.goods.service.impl;

import cn.edu.xmu.goods.dao.GoodsDao;
import cn.edu.xmu.goods.dao.GoodsSpuDao;
import cn.edu.xmu.goods.dao.ShopDao;
import cn.edu.xmu.goods.model.po.GoodsSkuPo;
import cn.edu.xmu.goods.model.po.GoodsSpuPo;
import cn.edu.xmu.goods.model.po.ShopPo;
import cn.edu.xmu.goods.model.vo.GoodsSkuDetailRetVo;
import cn.edu.xmu.goods.model.vo.GoodsSkuRetVo;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.oomall.goods.model.*;
//import cn.edu.xmu.oomall.goods.model.GoodsDetailDTO;
//import cn.edu.xmu.oomall.goods.model.GoodsFreightDTO;
//import cn.edu.xmu.oomall.goods.model.ShopDetailDTO;
import cn.edu.xmu.oomall.goods.service.IActivityService;
import cn.edu.xmu.oomall.goods.service.IGoodsService;
//import com.alibaba.dubbo.config.annotation.Service;
import org.apache.dubbo.config.annotation.DubboReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
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

    @DubboReference
    private IActivityService IActivityService;

    @Override
    public ReturnObject<List<Long>> getAllSkuIdByShopId(Long shopId) {
        ReturnObject<List<Long>> returnObject=goodsDao.getAllSkuIdByShopId(shopId);
        return returnObject;
    }

    @Override
    public ReturnObject<Long> getShopIdBySkuId(Long skuId) {
        ReturnObject<Long> returnObject=goodsDao.getShopIdBySkuId(skuId);
        return returnObject;
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
            List<CouponInfoDTO> couponInfoDTOs = IActivityService.getCouponInfoBySkuId(skuId);
            goodsInfoDTO.setCouponActivity(couponInfoDTOs);
        }
        return new ReturnObject<>(goodsInfoDTO);
    }

    @Override
    public List<SkuNameInfoDTO> getSelectSkuNameListBySkuIdList(List<Long> idList) {
        List<SkuNameInfoDTO> nameList = new ArrayList<>();
        for (int i=0;i<idList.size();i++)
        {
            GoodsSkuDetailRetVo goodsSkuRetVo = goodsDao.getSku(idList.get(i));
            SkuNameInfoDTO skuNameInfoDTO = new SkuNameInfoDTO();
            skuNameInfoDTO.setId(skuNameInfoDTO.getId());
            skuNameInfoDTO.setName(goodsSkuRetVo.getName());
            nameList.add(skuNameInfoDTO);
        }
        return nameList;
    }

    @Override
    public ReturnObject checkSkuUsableBySkuShop(Long skuId, Long shopId) {
        ReturnObject returnObject=goodsDao.checkSkuUsableBySkuShop(skuId,shopId);
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
        GoodsSkuPo goodsSkuPo = goodsDao.getGoodsSkuById(skuId).getData();
        SimpleGoodsSkuDTO simpleGoodsSkuDTO = new SimpleGoodsSkuDTO();
        simpleGoodsSkuDTO.setId(goodsSkuPo.getId());
        simpleGoodsSkuDTO.setName(goodsSkuPo.getName());
        simpleGoodsSkuDTO.setImageUrl(goodsSkuPo.getImageUrl());
        simpleGoodsSkuDTO.setDisabled(goodsSkuPo.getDisabled());
        simpleGoodsSkuDTO.setInventory(goodsSkuPo.getInventory());
        simpleGoodsSkuDTO.setSkuSn(goodsSkuPo.getSkuSn());
        simpleGoodsSkuDTO.setOriginalPrice(goodsSkuPo.getOriginalPrice());
        return new ReturnObject<>(simpleGoodsSkuDTO);
    }

    @Override
    public ReturnObject<GoodsSpuPoDTO> getSpuBySpuId(Long spuId) {
        return null;
    }

    //@Override
    public List<SkuInfoDTO> getSelectSkuListBySkuIdList(List<Long> idList) {
        List<SkuInfoDTO> list=new ArrayList<>();
        idList.stream().forEach(x->{
            list.add(getSelectSkuInfoBySkuId(x).getData());
        });
        return list;
    }

    //    @Override
//    public ReturnObject<ShopDetailDTO> getShopInfoBySkuId(Long skuId) {
//        return null;
//    }
//
//    @Override
//    public ReturnObject<GoodsDetailDTO> getGoodsBySkuId(Long skuId) {
//        return goodsDao.getGoodsBySkuId(skuId);
//    }
//
//    @Override
//    public ReturnObject<GoodsFreightDTO> getGoodsFreightDetailBySkuId(Long skuId) {
//        return null;

}
