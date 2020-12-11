package cn.edu.xmu.flashsale.service;
import cn.edu.xmu.flashsale.dao.FlashSaleDao;
import cn.edu.xmu.flashsale.model.vo.FlashsaleNewRetVo;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.oomall.other.model.TimeDTO;
import cn.edu.xmu.oomall.other.service.ITimeService;
import lombok.extern.flogger.Flogger;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class FlashsaleService {

    private static final Logger logger = LoggerFactory.getLogger(FlashSaleDao.class);

    @DubboReference
    private ITimeService timeService;

    @Autowired
    private FlashSaleDao flashsaleDao;

    public ReturnObject<FlashsaleNewRetVo> createflash(Long id, LocalDateTime flashDate) {
        ReturnObject<FlashsaleNewRetVo> returnObject = new ReturnObject<>();
        try
        {
            Byte timeType = 1;//0代表广告，1代表秒杀
            //调用other的微服务得到id对应的时段的具体数据
            ReturnObject<TimeDTO> timeDTOReturnObject = timeService.getTimeSegmentId(timeType,id);
            logger.error("timeDTOReturnObject: " + timeDTOReturnObject.toString());
//            ReturnObject<TimeDTO> timeDTOReturnObject = new ReturnObject<>(new TimeDTO());
            //检查时段id是否存在
            if(timeDTOReturnObject.getData() == null)
            {
                //若不存在返回资源不存在错误
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            }
            //时段存在
            returnObject = flashsaleDao.createflash(id, flashDate);
            FlashsaleNewRetVo retVo = returnObject.getData();
            retVo.setTimeDTO(timeDTOReturnObject.getData());
            return new ReturnObject<>(retVo);
        }
        catch (Exception e) {

        }
        return returnObject;
    }

    public ReturnObject deleteflashsale(Long id) {
        return flashsaleDao.deleteflashsale(id);
    }

    public ReturnObject updateflashsale(Long id, LocalDateTime flashDate) {
        return flashsaleDao.updateflashsale(id, flashDate);
    }

}
