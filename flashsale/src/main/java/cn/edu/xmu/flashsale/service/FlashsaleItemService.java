package cn.edu.xmu.flashsale.service;

import cn.edu.xmu.flashsale.dao.FlashSaleDao;
import cn.edu.xmu.flashsale.dao.FlashSaleItemDao;
import cn.edu.xmu.flashsale.model.bo.FlashSaleItem;
import cn.edu.xmu.flashsale.model.po.FlashSaleItemPo;
import cn.edu.xmu.flashsale.model.po.FlashSaleItemPoExample;
import cn.edu.xmu.flashsale.model.po.FlashSalePo;
import cn.edu.xmu.flashsale.model.vo.FlashsaleItemRetVo;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.oomall.goods.model.SkuInfoDTO;
import cn.edu.xmu.oomall.goods.service.IGoodsService;
import cn.edu.xmu.oomall.other.service.ITimeService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import javax.annotation.Resource;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class FlashsaleItemService {

    private static final Logger logger = LoggerFactory.getLogger(FlashSaleDao.class);

    @DubboReference(check = false)
    private IGoodsService goodsService;

    @Autowired
    private FlashSaleItemDao flashSaleItemDao;

    @Autowired
    private FlashSaleDao flashSaleDao;

    @Autowired
//    @Resource
    private ReactiveRedisTemplate<String, Serializable> reactiveRedisTemplate;

    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;

    //响应式返回
    public Flux<FlashsaleItemRetVo> queryTopicsByTime(Long id) {
        LocalDateTime dateTime = LocalDateTime.now();
        logger.error("FlashSaleItem:" + dateTime.toLocalDate().toString() + id.toString());
        return reactiveRedisTemplate.opsForSet().members("FlashSaleItem:" + dateTime.toLocalDate().toString() + id.toString()).map(x->
        {
//            SkuInfoDTO skuInfoDTO = goodsService.getSelectSkuInfoBySkuId(((FlashSaleItemPo)x).getGoodsSkuId()).getData();//懒得检验code； ，应该也不会有错吧
            SkuInfoDTO skuInfoDTO = new SkuInfoDTO();
            skuInfoDTO.setId(((FlashSaleItemPo)x).getGoodsSkuId());
            logger.error(x.toString());
            logger.error(((FlashSaleItemPo)x).toString());
            FlashsaleItemRetVo retVo = new FlashsaleItemRetVo((FlashSaleItemPo)x);
            retVo.setGoodsSku(skuInfoDTO);
            logger.error("retVo:"+ retVo.toString());
            return retVo;
        });
    }

    public ReturnObject<FlashsaleItemRetVo> addSKUofTopic(Long id, Long skuId, Long price, Integer quantity) {
        logger.error("3");
        //不允许增加今天和明天的秒杀商品
        ReturnObject<FlashSalePo> flashSalePoReturnObject = flashSaleDao.selectByFlashsaleId(id);
        logger.error("4");
        if(flashSalePoReturnObject.getCode()!=ResponseCode.OK)
            return new ReturnObject<>(flashSalePoReturnObject.getCode());
        else {
            if(flashSalePoReturnObject.getData().getFlashDate().isBefore(LocalDateTime.now().plusDays(1))) {
                return new ReturnObject<>(ResponseCode.FIELD_NOTVALID);
            }
        }
        logger.error("5");
        //获得sku信息
        ReturnObject<SkuInfoDTO> returnObject = goodsService.getSelectSkuInfoBySkuId(skuId);
        logger.error("goods Service返回给flashsale："+returnObject.getData().toString());
        if(returnObject.getCode()!= ResponseCode.OK) {//错误
            return new ReturnObject(returnObject.getCode());
        } else if(returnObject.getData() == null) {//不存在，返回资源不存在
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
        }

        logger.error("6");
        //存在，调dao
        ReturnObject<FlashsaleItemRetVo> itemRetObj =  flashSaleItemDao.addSKUofTopic(id, skuId, price, quantity);
        //插入不成功
        if(itemRetObj.getCode()!=ResponseCode.OK) {
            return new ReturnObject<>(itemRetObj.getCode());
        }
        logger.error("7");
        //插入成功
        FlashsaleItemRetVo retVo = itemRetObj.getData();
        retVo.setGoodsSku(returnObject.getData());
        return new ReturnObject<>(retVo);
    }

    public ReturnObject deleteSKUofTopic(Long fid, Long id) {
        ReturnObject<FlashSalePo> flashSalePoReturnObject = flashSaleDao.selectByFlashsaleId(fid);
        String key = "FlashSaleItem:" + flashSalePoReturnObject.getData().getFlashDate().toLocalDate().toString() + flashSalePoReturnObject.getData().getTimeSegId().toString();
        FlashSaleItemPo itemPo;
        if(flashSalePoReturnObject.getCode()!=ResponseCode.OK)
            return new ReturnObject<>(flashSalePoReturnObject.getCode());
        ReturnObject<FlashSaleItemPo> itemRetObj = flashSaleItemDao.getByPrimaryKey(id);
        if(itemRetObj.getCode()!=ResponseCode.OK) {
            return itemRetObj;
        } else {
            itemPo = itemRetObj.getData();
        }
        ReturnObject retObj = flashSaleItemDao.deleteSKUofTopic(fid, id);
        if(retObj.getCode()==ResponseCode.OK && flashSalePoReturnObject.getData().getFlashDate().isBefore(LocalDateTime.now().plusDays(1))) {
            redisTemplate.boundSetOps(key).remove(itemPo);
        }
        return retObj;
    }

    public ReturnObject<PageInfo<VoObject>> getSKUofTopic(Long id, Integer page, Integer pageSize) {
        ReturnObject<PageInfo<VoObject>> finalRetObj = new ReturnObject<>();
        List<VoObject> itemRetVoList = new ArrayList<>();
        PageHelper.startPage(page, pageSize);

        //查出item相关信息
        ReturnObject<List<FlashSaleItem>> returnObject = flashSaleItemDao.getSKUofTopic(id, page, pageSize);
        //根据skuId查出sku具体信息,构造retVo
        List<FlashSaleItem> flashSaleItemList = returnObject.getData();
        for (FlashSaleItem flashSaleItem : flashSaleItemList) {
            ReturnObject<SkuInfoDTO> skuRetObj = goodsService.getSelectSkuInfoBySkuId(flashSaleItem.getSkuId());
            FlashsaleItemRetVo vo = new FlashsaleItemRetVo(flashSaleItem);
            vo.setGoodsSku(skuRetObj.getData());
            itemRetVoList.add(vo);
        }
        PageInfo<VoObject> flashsaleItemPage = PageInfo.of(itemRetVoList);
        return new ReturnObject<>(flashsaleItemPage);
    }

    public void loadRedis() {
        //准备好秒杀id
        List<FlashSalePo> flashSalePosToday = flashSaleDao.selectByFlashDate(LocalDateTime.now()).getData();
        List<FlashSalePo> flashSalePosTom = flashSaleDao.selectByFlashDate(LocalDateTime.now().plusDays(1)).getData();
        //准备好时段id
        Byte type = 1;
        //List<Long> list = iTimeService.listSelectAllTimeSegmentId(type).getData();
        List<Long> list = new ArrayList<>();
        list.add(1L);
        list.add(2L);
        //将今天的item按照flashsale-segment存进redis
        for (FlashSalePo po:flashSalePosToday) {
            List<FlashSaleItemPo> itemPos = flashSaleItemDao.selectByFlashsaleId(po.getId()).getData();
            String key = "FlashSaleItem:" + po.getFlashDate().toLocalDate().toString() + po.getTimeSegId().toString();
            for (FlashSaleItemPo itemPo : itemPos) {
                redisTemplate.boundSetOps(key).add((Serializable) itemPo);
            }
            redisTemplate.expire(key, 24, TimeUnit.HOURS);
        }
        //将明天的item存进redis
        for (FlashSalePo po:flashSalePosTom) {
            List<FlashSaleItemPo> itemPos = flashSaleItemDao.selectByFlashsaleId(po.getId()).getData();
            String key = "FlashSaleItem:" + po.getFlashDate().toLocalDate().toString() + po.getTimeSegId().toString();
            for (FlashSaleItemPo itemPo : itemPos) {
                redisTemplate.boundSetOps(key).add((Serializable) itemPo);
            }
            redisTemplate.expire(key, 48, TimeUnit.HOURS);
        }
    }

}
