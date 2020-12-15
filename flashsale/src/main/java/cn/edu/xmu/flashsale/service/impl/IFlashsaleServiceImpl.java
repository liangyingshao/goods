package cn.edu.xmu.flashsale.service.impl;

import cn.edu.xmu.flashsale.dao.FlashSaleDao;
import cn.edu.xmu.flashsale.dao.FlashSaleItemDao;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.oomall.goods.service.IFlashsaleService;
import com.sun.mail.iap.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDateTime;

@Slf4j
@DubboService
public class IFlashsaleServiceImpl implements IFlashsaleService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private FlashSaleDao flashSaleDao;

    @Autowired
    private FlashSaleItemDao flashSaleItemDao;

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
}
