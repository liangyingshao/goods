package cn.edu.xmu.goods.service.impl;

import cn.edu.xmu.goods.dao.GoodsDao;
import cn.edu.xmu.goods.dao.GoodsSpuDao;
import cn.edu.xmu.goods.dao.ShopDao;
import cn.edu.xmu.goods.model.po.GoodsSkuPo;
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

    @DubboReference
    private IActivityService IActivityService;

    @DubboReference
    private IFlashsaleService iFlashsaleService;

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
        SimpleGoodsSkuDTO simpleGoodsSkuDTO=goodsDao.getSimpleSkuBySkuId(skuId);
        simpleGoodsSkuDTO.setPrice(goodsDao.getPriceBySkuId(skuId).getData());
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

    @Override
    public ReturnObject<ShopDetailDTO> getShopInfoBySkuId(Long skuId) {
        return null;
    }

    @Override
    public ReturnObject<GoodsDetailDTO> getGoodsBySkuId(Long skuId) {
        GoodsDetailDTO goodsDetailDTO=goodsDao.getGoodsBySkuId(skuId).getData();
        goodsDetailDTO.setPrice(goodsDao.getPriceBySkuId(skuId).getData());
        return goodsDao.getGoodsBySkuId(skuId);
    }

    @Override
    public ReturnObject<GoodsFreightDTO> getGoodsFreightDetailBySkuId(Long skuId) {
        return null;
    }

    @Override
    public ReturnObject<GoodsDetailDTO> getGoodsBySkuId(Long skuId, Byte type, Long activityId, Integer quantity) {
        ReturnObject<GoodsDetailDTO> returnObject=goodsDao.getGoodsBySkuId(skuId);
        if(returnObject.getCode().equals(ResponseCode.OK)) {
            switch (type) {
                case (2)://预售
                {
                    returnObject= IActivityService.modifyPresaleInventory(activityId, quantity);
                    break;
                }
                case (0)://秒杀/普通
                {
                    returnObject = iFlashsaleService.modifyFlashsaleItem(skuId,quantity);
                    if(returnObject.getCode()==ResponseCode.RESOURCE_ID_NOTEXIST) {
                        //不是秒杀商品，查出普通SKU
                        returnObject=goodsDao.getGoodsBySkuId(skuId);
                    } else {
                        GoodsDetailDTO dto = returnObject.getData();
                        dto.setName(goodsDao.getGoodsBySkuId(skuId).getData().getName());//获取sku name
                        returnObject = new ReturnObject<>(dto);
                    }
                    break;
                }
                case (1)://团购
                case (3)://优惠券
                break;
            }
            if(returnObject.getCode().equals(ResponseCode.OK))
                returnObject=goodsDao.modifyInventory(skuId,quantity);//在这里更新SKU的inventory
        }
        return returnObject;
    }

    @Override
    public ReturnObject<ShopDetailDTO> getShopInfoByShopId(Long shopId) {
        ShopDetailDTO shopDetailDTO = null;
        ShopPo shopPo = shopDao.getShopById(shopId).getData();
        shopDetailDTO.setShopId(shopPo.getId());
        shopDetailDTO.setName(shopPo.getName());
        shopDetailDTO.setState(shopPo.getState());
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        shopDetailDTO.setGmtCreate(dtf.format(shopPo.getGmtCreate()));
        shopDetailDTO.setGmtModified(dtf.format(shopPo.getGmtModified()));
        return new ReturnObject<>(shopDetailDTO);
    }
}
