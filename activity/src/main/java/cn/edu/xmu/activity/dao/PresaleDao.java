package cn.edu.xmu.activity.dao;

import cn.edu.xmu.activity.mapper.PresaleActivityPoMapper;
import cn.edu.xmu.activity.model.bo.ActivityStatus;
import cn.edu.xmu.activity.model.bo.Presale;
import cn.edu.xmu.activity.model.po.GrouponActivityPo;
import cn.edu.xmu.activity.model.po.PresaleActivityPo;
import cn.edu.xmu.activity.model.po.PresaleActivityPoExample;
import cn.edu.xmu.activity.model.vo.PresaleVo;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.oomall.goods.model.GoodsSpuPoDTO;
import cn.edu.xmu.oomall.goods.model.PresaleDTO;
import cn.edu.xmu.oomall.goods.model.SimpleGoodsSkuDTO;
import cn.edu.xmu.oomall.goods.model.SimpleShopDTO;
import cn.edu.xmu.oomall.goods.service.IGoodsService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * description: PresaleDap
 * date: 2020/12/11 11:39
 * author: 杨铭
 * version: 1.0
 */
@Repository
public class PresaleDao {

    @Autowired
    PresaleActivityPoMapper presaleActivityPoMapper;

    @DubboReference
    IGoodsService iGoodsService;

    private static final Logger logger = LoggerFactory.getLogger(PresaleDao.class);

    public ReturnObject<List<PresaleActivityPo>> queryPresales(Long shopId, Long skuId, Integer state, Integer timeline, boolean isadmin) {
        //1.创建规则
        PresaleActivityPoExample example = new PresaleActivityPoExample();
        PresaleActivityPoExample.Criteria criteria = example.createCriteria();

        //2.按店铺id查询
        if(shopId!=null && shopId!=-1L && shopId!=0L)
            criteria.andShopIdEqualTo(shopId);
        //3.按sku_id查询 (若shopId和skuId不匹配，直接返回空)
        if(skuId!=null)
            criteria.andGoodsSkuIdEqualTo(skuId);
        //4.按状态查询
        if(state!=null) {
            if(state == 0)
                criteria.andStateEqualTo(ActivityStatus.OFF_SHELVES.getCode().byteValue());
            else if(state == 1)
                criteria.andStateEqualTo(ActivityStatus.ON_SHELVES.getCode().byteValue());
            else if(state == 2)
                criteria.andStateEqualTo(ActivityStatus.DELETED.getCode().byteValue());
            else
                return new ReturnObject<>(ResponseCode.FIELD_NOTVALID);
        }
        //5.按timeline查询
        switch (timeline){
            case 0:
                criteria.andBeginTimeGreaterThan(LocalDateTime.now());
                break;
            case 1:
                LocalDateTime searchTime= LocalDateTime.now();
                searchTime=searchTime.plusDays(2);
                searchTime=searchTime.minusHours(searchTime.getHour());
                searchTime=searchTime.minusMinutes(searchTime.getMinute());
                searchTime=searchTime.minusSeconds(searchTime.getSecond());
                searchTime=searchTime.minusNanos(searchTime.getNano());
                LocalDateTime searchTimeMax=searchTime;//时间段上限
                LocalDateTime searchTimeMin=searchTime.minusDays(1);//时间段下限
                criteria.andBeginTimeGreaterThanOrEqualTo(searchTimeMin);//beginTime>=明日零点
                criteria.andBeginTimeLessThan(searchTimeMax);//beginTime<后日零点
                break;
            case 2:
                criteria.andBeginTimeLessThanOrEqualTo(LocalDateTime.now());
                criteria.andEndTimeGreaterThanOrEqualTo(LocalDateTime.now());
                break;
            case 3:
                criteria.andEndTimeLessThan(LocalDateTime.now());

        }

        //7.如果不是管理员，仅显示有效的活动
        if(!isadmin) {
            criteria.andStateEqualTo(ActivityStatus.ON_SHELVES.getCode().byteValue());
            criteria.andEndTimeGreaterThan(LocalDateTime.now());
        }

        //8.查询数据库
        List<PresaleActivityPo> results = null;
        try {
            results = presaleActivityPoMapper.selectByExample(example);
        } catch (Exception e) {
            StringBuilder message = new StringBuilder().append("queryPresales: ").append(e.getMessage());
            logger.error(message.toString());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }

        //TODO 返回需要查浮动库存表
        //9.返回
        return new ReturnObject<>(results);



    }
    public ReturnObject<Boolean> checkInPresale(Long id,String beginTime,String endTime){
        PresaleActivityPoExample example = new PresaleActivityPoExample();
        PresaleActivityPoExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsSkuIdEqualTo(id);
        List<PresaleActivityPo> list = null;
        try {
            list = presaleActivityPoMapper.selectByExample(example);
        } catch (Exception e) {
            StringBuilder message = new StringBuilder().append("checkInPresale: ").append(e.getMessage());
            logger.error(message.toString());
        }

        //数据库中的活动开始时间晚于endTime，或结束时间早于beginTime，才返回false
        if(!list.isEmpty())
        {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            for(PresaleActivityPo p : list){
                if(!(p.getBeginTime().isAfter(LocalDateTime.parse(endTime,dtf))||p.getEndTime().isBefore(LocalDateTime.parse(beginTime,dtf))))
                    return new ReturnObject<>(true);
            }
        }
        return new ReturnObject<>(false);
    }


    public ReturnObject<PresaleActivityPo> createPresaleOfSKU(Long shopId, Long id, PresaleVo presaleVo) {

        PresaleActivityPo presaleActivityPo = new PresaleActivityPo();
        presaleActivityPo.setShopId(shopId);
        presaleActivityPo.setId(id);
        presaleActivityPo.setAdvancePayPrice(presaleVo.getAdvancePayPrice());
        presaleActivityPo.setRestPayPrice(presaleVo.getRestPayPrice());
        presaleActivityPo.setQuantity(presaleVo.getQuantity());
        presaleActivityPo.setState(ActivityStatus.OFF_SHELVES.getCode().byteValue());
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        presaleActivityPo.setBeginTime(LocalDateTime.parse(presaleVo.getBeginTime(),df));
        presaleActivityPo.setEndTime(LocalDateTime.parse(presaleVo.getEndTime(),df));
        presaleActivityPo.setPayTime(LocalDateTime.parse(presaleVo.getPayTime(),df));

        try {
            presaleActivityPoMapper.insertSelective(presaleActivityPo);
        } catch (Exception e) {
            StringBuilder message = new StringBuilder().append("createPresaleOfSKU: ").append(e.getMessage());
            logger.error(message.toString());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }
        return new ReturnObject<>(presaleActivityPo);
    }

    public ReturnObject modifyPresaleOfSKU(Long shopId, Long id, PresaleVo presaleVo) {

        //1.查询此presale
        PresaleActivityPo oldPo = null;
        try {
            oldPo = presaleActivityPoMapper.selectByPrimaryKey(id);
        } catch (Exception e) {
            StringBuilder message = new StringBuilder().append("modifyPresaleofSPU:select: ").append(e.getMessage());
            logger.error(message.toString());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }
        if(oldPo == null)
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);

        //2.若shopId不一致，则无权限访问
        if(oldPo.getShopId()!= shopId)
            return new ReturnObject<>(ResponseCode.AUTH_NOT_ALLOW);

        //3.若状态不为下线，则团购活动禁止
        if(oldPo.getState()!=ActivityStatus.OFF_SHELVES.getCode().byteValue())
            return new ReturnObject<>(ResponseCode.PRESALE_STATENOTALLOW);

        //4.修改数据库
        PresaleActivityPo presaleActivityPo = new PresaleActivityPo();
        presaleActivityPo.setShopId(shopId);
        presaleActivityPo.setId(id);
        presaleActivityPo.setAdvancePayPrice(presaleVo.getAdvancePayPrice());
        presaleActivityPo.setRestPayPrice(presaleVo.getRestPayPrice());
        presaleActivityPo.setQuantity(presaleVo.getQuantity());
        presaleActivityPo.setState(ActivityStatus.OFF_SHELVES.getCode().byteValue());
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        presaleActivityPo.setBeginTime(LocalDateTime.parse(presaleVo.getBeginTime(),df));
        presaleActivityPo.setEndTime(LocalDateTime.parse(presaleVo.getEndTime(),df));
        presaleActivityPo.setPayTime(LocalDateTime.parse(presaleVo.getPayTime(),df));

        try {
            presaleActivityPoMapper.updateByPrimaryKeySelective(presaleActivityPo);
        } catch (Exception e) {
            StringBuilder message = new StringBuilder().append("modifyPresaleofSPU:update: ").append(e.getMessage());
            logger.error(message.toString());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }

        //5.返回
        return new ReturnObject<>(ResponseCode.OK);
    }

    public ReturnObject cancelPresaleOfSKU(Long shopId, Long id) {

        //1.查询此presale
        PresaleActivityPo oldPo = null;
        try {
            oldPo = presaleActivityPoMapper.selectByPrimaryKey(id);
        } catch (Exception e) {
            StringBuilder message = new StringBuilder().append("modifyPresaleofSPU:select: ").append(e.getMessage());
            logger.error(message.toString());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }

        //2.若shopId不一致，则无权限访问
        if(oldPo.getShopId()!= shopId)
            return new ReturnObject<>(ResponseCode.AUTH_NOT_ALLOW);

        //3.若状态不为下线，则团购活动禁止
        if(oldPo.getState()!=ActivityStatus.OFF_SHELVES.getCode().byteValue())
            return new ReturnObject<>(ResponseCode.GROUPON_STATENOTALLOW);

        //4.修改状态
        PresaleActivityPo newPresalePo = new PresaleActivityPo();
        newPresalePo.setId(id);
        newPresalePo.setState(ActivityStatus.DELETED.getCode().byteValue());
        try {
            presaleActivityPoMapper.updateByPrimaryKeySelective(newPresalePo);
        } catch (Exception e) {
            StringBuilder message = new StringBuilder().append("cancelPresaleofSPU: ").append(e.getMessage());
            logger.error(message.toString());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }

        //5.返回
        return new ReturnObject<>(ResponseCode.OK);
    }

    public ReturnObject putPresaleOnShelves(Long shopId, Long id) {


        //1.查询此presale
        PresaleActivityPo presaleActivityPo = null;
        try {
            presaleActivityPo = presaleActivityPoMapper.selectByPrimaryKey(id);
        } catch (Exception e) {
            StringBuilder message = new StringBuilder().append("putGrouponOnShelves: ").append(e.getMessage());
            logger.error(message.toString());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }
        if(presaleActivityPo == null)
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);

        //2.若shopId不一致，则无权限访问
        if(presaleActivityPo.getShopId()!= shopId)
            return new ReturnObject<>(ResponseCode.AUTH_NOT_ALLOW);

        //3.若状态已删除，或已上线，则预售活动禁止
        if(presaleActivityPo.getState() == ActivityStatus.DELETED.getCode().byteValue()
                ||presaleActivityPo.getState() == ActivityStatus.ON_SHELVES.getCode().byteValue()){
            return new ReturnObject<>(ResponseCode.PRESALE_STATENOTALLOW);
        }

        //4.若时间已过期，则无效，预售活动禁止
        if(presaleActivityPo.getBeginTime().isBefore(LocalDateTime.now())||
                presaleActivityPo.getEndTime().isBefore(LocalDateTime.now())) {
            return new ReturnObject<>(ResponseCode.GROUPON_STATENOTALLOW);
        }

        //5.修改状态
        PresaleActivityPo newPresalePo = new PresaleActivityPo();
        newPresalePo.setId(id);
        newPresalePo.setState(ActivityStatus.ON_SHELVES.getCode().byteValue());
        try {
            presaleActivityPoMapper.updateByPrimaryKeySelective(newPresalePo);
        } catch (Exception e) {
            StringBuilder message = new StringBuilder().append("putPresaleOnShelves: ").append(e.getMessage());
            logger.error(message.toString());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }

        //6.返回
        return new ReturnObject<>(ResponseCode.OK);
    }

    public ReturnObject putPresaleOffShelves(Long shopId, Long id) {

        //1.查询此presale
        PresaleActivityPo presaleActivityPo = null;
        try {
            presaleActivityPo = presaleActivityPoMapper.selectByPrimaryKey(id);
        } catch (Exception e) {
            StringBuilder message = new StringBuilder().append("putGrouponOnShelves: ").append(e.getMessage());
            logger.error(message.toString());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }
        if(presaleActivityPo == null)
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);

        //2.若shopId不一致，则无权限访问
        if(presaleActivityPo.getShopId()!= shopId)
            return new ReturnObject<>(ResponseCode.AUTH_NOT_ALLOW);

        //3.若状态已删除，或已下线，则预售活动禁止
        if(presaleActivityPo.getState() == ActivityStatus.DELETED.getCode().byteValue()
                ||presaleActivityPo.getState() == ActivityStatus.OFF_SHELVES.getCode().byteValue()){
            return new ReturnObject<>(ResponseCode.PRESALE_STATENOTALLOW);
        }

        //4.若时间已过期，则无效，预售活动禁止
        if(presaleActivityPo.getBeginTime().isBefore(LocalDateTime.now())||
                presaleActivityPo.getEndTime().isBefore(LocalDateTime.now())) {
            return new ReturnObject<>(ResponseCode.GROUPON_STATENOTALLOW);
        }

        //5.修改状态
        PresaleActivityPo newPresalePo = new PresaleActivityPo();
        newPresalePo.setId(id);
        newPresalePo.setState(ActivityStatus.OFF_SHELVES.getCode().byteValue());
        try {
            presaleActivityPoMapper.updateByPrimaryKeySelective(newPresalePo);
        } catch (Exception e) {
            StringBuilder message = new StringBuilder().append("putPresaleOnShelves: ").append(e.getMessage());
            logger.error(message.toString());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }

        //6.返回
        return new ReturnObject<>(ResponseCode.OK);

    }


    public ReturnObject<PresaleDTO> judgePresaleValid(Long presaleId) {
        //1.查找是否有此presale
        PresaleActivityPo po = null;
        try {
            po = presaleActivityPoMapper.selectByPrimaryKey(presaleId);
        } catch (Exception e) {
            StringBuilder message = new StringBuilder().append("judgePresaleValid: ").append(e.getMessage());
            logger.error(message.toString());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }
        //2.新建DTO
        PresaleDTO presaleDTO = new PresaleDTO();

        //3.无此id则返回false，有则校验时间
        if(po!=null && po.getBeginTime().isBefore(LocalDateTime.now())&&
                po.getPayTime().isAfter(LocalDateTime.now())){
            presaleDTO.setIsValid(true);
            presaleDTO.setAdvancePayPrice(po.getAdvancePayPrice());
            presaleDTO.setRestPayPrice(po.getRestPayPrice());
        }
        else
            presaleDTO.setIsValid(false);

        return new ReturnObject<>(presaleDTO);


    }

    public ReturnObject<Boolean> paymentPresaleIdValid(Long presaleId) {

        //1.查找是否有此presale
        PresaleActivityPo po = null;
        try {
            po = presaleActivityPoMapper.selectByPrimaryKey(presaleId);
        } catch (Exception e) {
            StringBuilder message = new StringBuilder().append("judgePresaleValid: ").append(e.getMessage());
            logger.error(message.toString());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }
        //2.无此id则返回false，有则校验时间
        if(po!=null && po.getPayTime().isBefore(LocalDateTime.now())&&
                po.getEndTime().isAfter(LocalDateTime.now())){
            return new ReturnObject<>(true);
        }
        else
            return new ReturnObject<>(false);
    }
}
