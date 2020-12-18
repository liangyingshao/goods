package cn.edu.xmu.activity.service;

import cn.edu.xmu.activity.dao.GrouponDao;
import cn.edu.xmu.activity.model.bo.ActivityStatus;
import cn.edu.xmu.activity.model.bo.NewGroupon;
import cn.edu.xmu.activity.model.po.GrouponActivityPo;
import cn.edu.xmu.activity.model.po.GrouponActivityPoExample;
import cn.edu.xmu.activity.model.vo.GrouponVo;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.oomall.goods.model.GoodsSpuPoDTO;
import cn.edu.xmu.oomall.goods.model.SimpleShopDTO;
import cn.edu.xmu.oomall.goods.service.IGoodsService;
import cn.edu.xmu.oomall.order.service.IOrderService;
import com.github.pagehelper.PageInfo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Reference;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * description: GrouponService
 * date: 2020/12/9 15:05
 * author: 杨铭
 * version: 1.0
 */
@Service
public class GrouponService {


    @Autowired
    GrouponDao grouponDao;

    @DubboReference(check = false)
    IGoodsService iGoodsService;

    @DubboReference(check = false)
    IOrderService iOrderService;

    private static final Logger logger = LoggerFactory.getLogger(GrouponService.class);
    
    public ReturnObject modifyGrouponofSPU(Long shopId, Long id, GrouponVo grouponVo) {
        return grouponDao.modifyGrouponofSPU(shopId, id, grouponVo);
    }

    public ReturnObject createGrouponofSPU(Long shopId, Long id, GrouponVo grouponVo) {

        //1. shopId是否存在
        SimpleShopDTO simpleShopDTO = iGoodsService.getSimpleShopByShopId(shopId).getData();
        if(simpleShopDTO == null)
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);

        //2. 检查是否存在spuId
        GoodsSpuPoDTO goodsSpuPoDTO = iGoodsService.getSpuBySpuId(id).getData();
        if(goodsSpuPoDTO == null)
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);

        //3. 此spu是否在此shop中,否则无权限操作
        if(goodsSpuPoDTO.getShopId()!=shopId)
            return new ReturnObject(ResponseCode.RESOURCE_ID_OUTSCOPE);//TODO 考虑错误码是否合适

        //4. 此spu是否正在参加其他团购
        if(grouponDao.checkInGroupon(id,grouponVo.getBeginTime(),grouponVo.getEndTime()).getData())
            return new ReturnObject(ResponseCode.FIELD_NOTVALID);//TODO 考虑错误码是否合适

        return grouponDao.createGrouponofSPU(shopId, id, grouponVo, goodsSpuPoDTO, simpleShopDTO);

    }

    public ReturnObject putGrouponOnShelves(Long shopId, Long id) {
        return grouponDao.putGrouponOnShelves(shopId,id);

    }

    public ReturnObject putGrouponOffShelves(Long shopId, Long id) {
        try {
            iOrderService.putGrouponOffshelves(id);
        } catch (Exception e) {
            logger.debug("dubbo error!");
        }
        return grouponDao.putGrouponOffShelves(shopId,id);
    }

    public ReturnObject cancelGrouponofSPU(Long shopId, Long id) {
        try {
            iOrderService.putGrouponOffshelves(id);
        } catch (Exception e) {
            logger.debug("dubbo error!");
        }
        return grouponDao.cancelGrouponofSPU(shopId,id);
    }

    public ReturnObject<PageInfo<VoObject>> queryGroupons(Long shopId, Long spu_id, Integer state, Integer timeline, LocalDateTime beginTime, LocalDateTime endTime, Integer page, Integer pagesize, Boolean isadmin) {
        return grouponDao.queryGroupons(shopId, spu_id, state, timeline, beginTime, endTime, page, pagesize, isadmin);
    }
}
