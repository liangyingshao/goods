package cn.edu.xmu.flashsale.service.impl;

import cn.edu.xmu.flashsale.dao.FlashSaleDao;
import cn.edu.xmu.flashsale.dao.FlashSaleItemDao;
import cn.edu.xmu.flashsale.model.po.FlashSaleItemPo;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.oomall.goods.model.GoodsDetailDTO;
import cn.edu.xmu.oomall.goods.service.IFlashsaleService;
import cn.edu.xmu.oomall.other.service.ITimeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDateTime;
import java.util.Set;

@Slf4j
@DubboService
public class IFlashsaleServiceImpl implements IFlashsaleService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private FlashSaleDao flashSaleDao;

    @Autowired
    private FlashSaleItemDao flashSaleItemDao;

    @DubboReference
    private ITimeService iTimeService;

    @Override
    public ReturnObject deleteSegmentFlashsale(Long id) {
        //删除redis
        //构造Key
        String key = "FlashSaleItem:" + LocalDateTime.now().toString() + id.toString();
        redisTemplate.delete(key);
        key = "FlashSaleItem:" + LocalDateTime.now().plusDays(1).toString() + id.toString();
        redisTemplate.delete(key);

        //删除Flashsale
        //在删除Flashsale同时利用Flashsale的Id删除FlashsaleItem
        ReturnObject returnObject = flashSaleDao.deleteSegmentFlashsale(id);
        if(returnObject.getCode()!= ResponseCode.OK) {
            return returnObject;
        }

        return new ReturnObject(ResponseCode.OK);
    }

    @Override
    public ReturnObject<GoodsDetailDTO> modifyFlashsaleItem(Long skuId, Integer quantity) {
        Byte type = 1;
        ReturnObject<Long> returnObject = iTimeService.getCurrentSegmentId(type);
        Long id = returnObject.getData();
        String key = "FlashSaleItem:" + LocalDateTime.now().toString() + id.toString();
        Set<FlashSaleItemPo> itemSet = redisTemplate.opsForSet().members(key);
        for (FlashSaleItemPo item : itemSet) {
            if(item.getGoodsSkuId()==skuId) {
                Integer inventory=item.getQuantity();
                if (inventory < -quantity)//负数是减，正数是加
                    return new ReturnObject<>(ResponseCode.SKU_NOTENOUGH);
                GoodsDetailDTO dto = new GoodsDetailDTO();
                dto.setPrice(item.getPrice());
                dto.setInventory(item.getQuantity());
                //扣库存或者增库存
                redisTemplate.boundSetOps(key).remove(item);
                item.setQuantity(item.getQuantity()+quantity);
                redisTemplate.boundSetOps(key).add(item);
                return new ReturnObject<>(dto);
            }
        }
        //应该返回不是秒杀商品
        return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, "该商品不是秒杀商品");
    }
}
