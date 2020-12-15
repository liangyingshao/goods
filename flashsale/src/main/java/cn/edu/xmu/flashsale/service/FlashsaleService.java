package cn.edu.xmu.flashsale.service;
import cn.edu.xmu.flashsale.dao.FlashSaleDao;
import cn.edu.xmu.flashsale.dao.FlashSaleItemDao;
import cn.edu.xmu.flashsale.model.po.FlashSaleItemPo;
import cn.edu.xmu.flashsale.model.po.FlashSalePo;
import cn.edu.xmu.flashsale.model.vo.FlashsaleNewRetVo;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.oomall.other.model.TimeDTO;
import cn.edu.xmu.oomall.other.service.ITimeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@Slf4j
public class FlashsaleService {

    private static final Logger logger = LoggerFactory.getLogger(FlashSaleDao.class);

    @DubboReference
    private ITimeService timeService;

    @Autowired
    private FlashSaleDao flashsaleDao;

    @Autowired
    private FlashSaleItemDao flashSaleItemDao;

    @Autowired
    private RedisTemplate redisTemplate;

    public ReturnObject<FlashsaleNewRetVo> createflash(Long id, LocalDateTime flashDate) {
        ReturnObject<FlashsaleNewRetVo> returnObject = new ReturnObject<>();
        try
        {
            Byte timeType = 1;//0代表广告，1代表秒杀
            //调用other的微服务得到id对应的时段的具体数据
//            ReturnObject<TimeDTO> timeDTOReturnObject = timeService.getTimeSegmentId(timeType,id);
//            logger.error("timeDTOReturnObject: " + timeDTOReturnObject.toString());
            TimeDTO dto = new TimeDTO();
            dto.setId(1L);
            dto.setBeginTime(LocalTime.now());
            dto.setEndTime(LocalTime.now().plusHours(2L));
            Byte type = 1;
            dto.setType(type);
            ReturnObject<TimeDTO> timeDTOReturnObject = new ReturnObject<>(dto);
            //检查时段id是否存在
            if(timeDTOReturnObject.getData() == null)
            {
                //若不存在返回资源不存在错误
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            }

            //时段存在
            //时段id+falshDate是否已经存在
            ReturnObject<FlashSalePo> flashSalePoReturnObject = flashsaleDao.selectByFlashDateAndSegId(flashDate, id);

            if(flashSalePoReturnObject.getData()!=null) {
                return new ReturnObject<>(ResponseCode.TIMESEG_CONFLICT);
            }

            //时段不冲突
            returnObject = flashsaleDao.createflash(id, flashDate);

            FlashsaleNewRetVo retVo = returnObject.getData();
            logger.error(retVo.toString());
            retVo.setTimeDTO(timeDTOReturnObject.getData());
            logger.error(retVo.toString());
            ReturnObject<FlashsaleNewRetVo> retVoReturnObject = new ReturnObject<>(retVo);
            logger.error(retVoReturnObject.getCode().toString());
            return retVoReturnObject;
        }
        catch (Exception e) {

        }
        return returnObject;
    }

//    public ReturnObject deleteflashsale(Long id) {
//        ReturnObject<FlashSalePo> flashSalePoReturnObject = flashsaleDao.selectByFlashsaleId(id);
//        if(flashSalePoReturnObject.getCode()!= ResponseCode.OK) {
//            return flashSalePoReturnObject;
//        }
//        FlashSalePo flashSalePo = flashSalePoReturnObject.getData();
//        String key = "FlashSaleItem:" + flashSalePo.getFlashDate().toString() + flashSalePo.getTimeSegId().toString();
//        List<FlashSaleItemPo> flashSaleItemPos = flashSaleItemDao.selectByFlashsaleId(id).getData();
//        ReturnObject retObj =  flashsaleDao.deleteflashsale(id);
//        if(retObj.getCode()==ResponseCode.OK && flashSalePoReturnObject.getData().getFlashDate().isBefore(LocalDateTime.now().plusDays(1))) {
//            for (FlashSaleItemPo itemPo : flashSaleItemPos) {
//                redisTemplate.boundSetOps(key).remove(itemPo);
//            }
//        }
//        return retObj;
//    }

    public ReturnObject updateflashsale(Long id, LocalDateTime flashDate) {
        return flashsaleDao.updateflashsale(id, flashDate);
    }

    public ReturnObject flashsaleOn(Long id, Byte state) {
        return flashsaleDao.updateFlashsaleState(id, state);
    }
}
