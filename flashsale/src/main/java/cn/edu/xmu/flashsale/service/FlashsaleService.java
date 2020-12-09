package cn.edu.xmu.flashsale.service;
import cn.edu.xmu.flashsale.dao.FlashSaleDao;
import cn.edu.xmu.flashsale.model.vo.FlashsaleNewRetVo;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.oomall.other.model.TimeDTO;
import cn.edu.xmu.oomall.other.service.ITimeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class FlashsaleService {
    @DubboReference
    private ITimeService timeService;

    @Autowired
    private FlashSaleDao flashsaleDao;

    //响应式返回
    public ReturnObject<VoObject> queryTopicsByTime(Long id) {
        ReturnObject<VoObject> returnObject = new ReturnObject<>();
        try
        {
            Byte timeType = 1;//0代表广告，1代表秒杀
            //调用other的微服务得到id对应的时段的具体数据
            ReturnObject<TimeDTO> timeDTOReturnObject = timeService.getTimeSegmentId(timeType,id);
            //根据flash_date=today+时段id查询flashsale
        }
        catch (Exception e) {

        }
        return returnObject;
    }

    public ReturnObject<FlashsaleNewRetVo> createflash(Long id, LocalDateTime flashDate) {
        ReturnObject<FlashsaleNewRetVo> returnObject = new ReturnObject<>();
        try
        {
            Byte timeType = 1;//0代表广告，1代表秒杀
            //调用other的微服务得到id对应的时段的具体数据
//            ReturnObject<TimeDTO> timeDTOReturnObject = timeService.getTimeSegmentId(timeType,id);
            ReturnObject<TimeDTO> timeDTOReturnObject = new ReturnObject<>(new TimeDTO());
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
}
