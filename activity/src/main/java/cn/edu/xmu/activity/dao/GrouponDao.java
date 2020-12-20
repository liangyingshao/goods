package cn.edu.xmu.activity.dao;


import cn.edu.xmu.activity.mapper.GrouponActivityPoMapper;
import cn.edu.xmu.activity.model.bo.ActivityStatus;
import cn.edu.xmu.activity.model.bo.Groupon;
import cn.edu.xmu.activity.model.bo.NewGroupon;
import cn.edu.xmu.activity.model.po.GrouponActivityPo;
import cn.edu.xmu.activity.model.po.GrouponActivityPoExample;
import cn.edu.xmu.activity.model.vo.GrouponVo;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.oomall.goods.model.GoodsSpuPoDTO;
import cn.edu.xmu.oomall.goods.model.SimpleShopDTO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;

/**
 * description: GrouponDao
 * date: 2020/12/1 0:12
 * author: 杨铭
 * version: 1.0
 */
@Repository
public class GrouponDao {

    @Autowired
    GrouponActivityPoMapper grouponActivityPoMapper;


    private static final Logger logger = LoggerFactory.getLogger(GrouponDao.class);


    /**
     * description: createGrouponofSPU
     * version: 1.0
     * date: 2020/12/6 20:57
     * author: 杨铭
     *
     * @param shopId
     * @param id
     * @param grouponVo
     * @return cn.edu.xmu.ooad.util.ReturnObject
     */
    public ReturnObject<NewGroupon> createGrouponofSPU(Long shopId, Long id, GrouponVo grouponVo, GoodsSpuPoDTO goodsSpuPoDTO, SimpleShopDTO simpleShopDTO) {


        //1. 此spu是否正在参加其他团购
        if(checkInGroupon(id,grouponVo.getBeginTime(),grouponVo.getEndTime()).getData())
            return new ReturnObject(ResponseCode.FIELD_NOTVALID);

        //3. 创建
        GrouponActivityPo grouponActivityPo = new GrouponActivityPo();
        grouponActivityPo.setShopId(shopId);
        grouponActivityPo.setGoodsSpuId(id);
        grouponActivityPo.setState(ActivityStatus.OFF_SHELVES.getCode().byteValue());
        grouponActivityPo.setStrategy(grouponVo.getStrategy());

        grouponActivityPo.setBeginTime(grouponVo.getBeginTime());
        grouponActivityPo.setEndTime(grouponVo.getEndTime());

        try {
            grouponActivityPoMapper.insertSelective(grouponActivityPo);
        } catch (Exception e) {
            StringBuilder message = new StringBuilder().append("createGrouponofSPU: ").append(e.getMessage());
            logger.error(message.toString());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }
        NewGroupon newGrouponActivity = new NewGroupon(grouponActivityPo,goodsSpuPoDTO,simpleShopDTO);
        return new ReturnObject<>(newGrouponActivity);


    }

    public ReturnObject putGrouponOnShelves(Long shopId, Long id) {

        //1.查询此groupon
        GrouponActivityPo grouponActivityPo = null;
        try {
            grouponActivityPo = grouponActivityPoMapper.selectByPrimaryKey(id);
        } catch (Exception e) {
            StringBuilder message = new StringBuilder().append("putGrouponOnShelves: ").append(e.getMessage());
            logger.error(message.toString());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }
        if(grouponActivityPo == null)
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);

        //2.若shopId不一致，则无权限访问
        if(grouponActivityPo.getShopId()!= shopId)
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);

        //3.若状态已删除，或已上线，则团购活动禁止
        if(grouponActivityPo.getState() == ActivityStatus.DELETED.getCode().byteValue()
                ||grouponActivityPo.getState() == ActivityStatus.ON_SHELVES.getCode().byteValue()){
            return new ReturnObject<>(ResponseCode.GROUPON_STATENOTALLOW);
        }

        //4.若时间已过期，则无效，团购活动禁止
        if(grouponActivityPo.getBeginTime().isBefore(LocalDateTime.now())||
                grouponActivityPo.getEndTime().isBefore(LocalDateTime.now())) {
            return new ReturnObject<>(ResponseCode.GROUPON_STATENOTALLOW);
        }

        //5.修改状态
        GrouponActivityPo newGrouponPo = new GrouponActivityPo();
        newGrouponPo.setId(id);
        newGrouponPo.setState(ActivityStatus.ON_SHELVES.getCode().byteValue());
        try {
            grouponActivityPoMapper.updateByPrimaryKeySelective(newGrouponPo);
        } catch (Exception e) {
            StringBuilder message = new StringBuilder().append("putGrouponOnShelves: ").append(e.getMessage());
            logger.error(message.toString());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }

        //6.返回
        return new ReturnObject<>(ResponseCode.OK);
    }

    public ReturnObject putGrouponOffShelves(Long shopId, Long id) {

        //1.查询此groupon
        GrouponActivityPo grouponActivityPo = null;
        try {
            grouponActivityPo = grouponActivityPoMapper.selectByPrimaryKey(id);
        } catch (Exception e) {
            StringBuilder message = new StringBuilder().append("putGrouponOnShelves: ").append(e.getMessage());
            logger.error(message.toString());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }
        if(grouponActivityPo == null)
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);

        //2.若shopId不一致，则无权限访问
        if(grouponActivityPo.getShopId()!= shopId)
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);

        //3.若状态已删除，或已下线，则团购活动禁止
        if(grouponActivityPo.getState() == ActivityStatus.DELETED.getCode().byteValue()
                ||grouponActivityPo.getState() == ActivityStatus.OFF_SHELVES.getCode().byteValue()){
            return new ReturnObject<>(ResponseCode.GROUPON_STATENOTALLOW);
        }

        //4.若时间已过期，则无效，团购活动禁止
        if(grouponActivityPo.getBeginTime().isBefore(LocalDateTime.now())||
                grouponActivityPo.getEndTime().isBefore(LocalDateTime.now())) {
            return new ReturnObject<>(ResponseCode.GROUPON_STATENOTALLOW);
        }

        //5.修改状态
        GrouponActivityPo newGrouponPo = new GrouponActivityPo();
        newGrouponPo.setId(id);
        newGrouponPo.setState(ActivityStatus.OFF_SHELVES.getCode().byteValue());
        try {
            grouponActivityPoMapper.updateByPrimaryKeySelective(newGrouponPo);
        } catch (Exception e) {
            StringBuilder message = new StringBuilder().append("putGrouponOffShelves: ").append(e.getMessage());
            logger.error(message.toString());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }

        //6.返回
        return new ReturnObject<>(ResponseCode.OK);

    }

    public ReturnObject modifyGrouponofSPU(Long shopId, Long id, GrouponVo grouponVo) {

        //1.查询此groupon
        GrouponActivityPo oldPo = null;
        try {
            oldPo = grouponActivityPoMapper.selectByPrimaryKey(id);
        } catch (Exception e) {
            StringBuilder message = new StringBuilder().append("modifyGrouponofSPU:select: ").append(e.getMessage());
            logger.error(message.toString());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }
        if(oldPo == null)
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);


        //2.若shopId不一致，则无权限访问
        if(oldPo.getShopId()!= shopId)
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);

        //3.若状态不为下线，则团购活动禁止
        if(oldPo.getState()!=ActivityStatus.OFF_SHELVES.getCode().byteValue())
            return new ReturnObject<>(ResponseCode.GROUPON_STATENOTALLOW);

        //4.修改数据库
        GrouponActivityPo grouponActivityPo = new GrouponActivityPo();
        grouponActivityPo.setId(id);
        grouponActivityPo.setStrategy(grouponVo.getStrategy());
        grouponActivityPo.setBeginTime(grouponVo.getBeginTime());
        grouponActivityPo.setEndTime(grouponVo.getEndTime());

        try {
            grouponActivityPoMapper.updateByPrimaryKeySelective(grouponActivityPo);
        } catch (Exception e) {
            StringBuilder message = new StringBuilder().append("modifyGrouponofSPU:update: ").append(e.getMessage());
            logger.error(message.toString());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }

        //5.返回
        return new ReturnObject<>(ResponseCode.OK);
    }

    public ReturnObject cancelGrouponofSPU(Long shopId, Long id) {

        //1.查询此groupon
        GrouponActivityPo oldPo = null;
        try {
            oldPo = grouponActivityPoMapper.selectByPrimaryKey(id);
        } catch (Exception e) {
            StringBuilder message = new StringBuilder().append("modifyGrouponofSPU:select: ").append(e.getMessage());
            logger.error(message.toString());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }
        if(oldPo == null)
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);

        //2.若shopId不一致，则无权限访问
        if(oldPo.getShopId()!= shopId)
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);

        //3.若状态不为下线，则团购活动禁止
        if(oldPo.getState()!=ActivityStatus.OFF_SHELVES.getCode().byteValue())
            return new ReturnObject<>(ResponseCode.GROUPON_STATENOTALLOW);

        //4.修改状态
        GrouponActivityPo newGrouponPo = new GrouponActivityPo();
        newGrouponPo.setId(id);
        newGrouponPo.setState(ActivityStatus.DELETED.getCode().byteValue());
        try {
            grouponActivityPoMapper.updateByPrimaryKeySelective(newGrouponPo);
        } catch (Exception e) {
            StringBuilder message = new StringBuilder().append("cancelGrouponofSPU: ").append(e.getMessage());
            logger.error(message.toString());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }

        //5.返回
        return new ReturnObject<>(ResponseCode.OK);
    }

    public ReturnObject<Boolean> checkInGroupon(Long id,LocalDateTime beginTime,LocalDateTime endTime) {
        GrouponActivityPoExample example = new GrouponActivityPoExample();
        GrouponActivityPoExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsSpuIdEqualTo(id);
        List<GrouponActivityPo> list = null;
        try {
            list = grouponActivityPoMapper.selectByExample(example);
        } catch (Exception e) {
            StringBuilder message = new StringBuilder().append("checkInGroupon: ").append(e.getMessage());
            logger.error(message.toString());
        }

        //数据库中的活动开始时间晚于endTime，或结束时间早于beginTime，才返回false
        if(!list.isEmpty())
        {
            for(GrouponActivityPo p : list){
                if(!(p.getBeginTime().isAfter(endTime)||p.getEndTime().isBefore(beginTime)))
                    return new ReturnObject<>(true);
            }
        }
        return new ReturnObject<>(false);

    }


    public ReturnObject<PageInfo<VoObject>> queryGroupons(Long shopId, Long spu_id, Integer state, Integer timeline, LocalDateTime beginTime, LocalDateTime endTime, Integer page, Integer pagesize, Boolean isadmin) {
        //1.创建规则
        GrouponActivityPoExample example = new GrouponActivityPoExample();
        GrouponActivityPoExample.Criteria criteria = example.createCriteria();

        //2.按店铺id查询
        if(shopId!=null && shopId!=-1L && shopId!=0L)
            criteria.andShopIdEqualTo(shopId);
        //3.按spu_id查询 (若shopId和spuId不匹配，直接返回空)
        if(spu_id!=null)
            criteria.andGoodsSpuIdEqualTo(spu_id);
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
        if(timeline!=null)
        {
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
        }

        //6.按时间段查询
        if(beginTime!=null)
            criteria.andBeginTimeGreaterThanOrEqualTo(beginTime);
        if(endTime!=null)
            criteria.andEndTimeLessThanOrEqualTo(endTime);

        //7.如果不是管理员，仅显示有效的活动
        if(!isadmin) {
            criteria.andStateEqualTo(ActivityStatus.ON_SHELVES.getCode().byteValue());
            //criteria.andEndTimeGreaterThan(LocalDateTime.now());
        }

        //8.查询数据库
        List<GrouponActivityPo> results = null;
        try {
            results = grouponActivityPoMapper.selectByExample(example);
        } catch (Exception e) {
            StringBuilder message = new StringBuilder().append("queryGroupons: ").append(e.getMessage());
            logger.error(message.toString());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }

        //9.分页返回
        PageHelper.startPage(page, pagesize);
        List<VoObject> BoList = new ArrayList<>(results.size());
        for(GrouponActivityPo p: results)
        {
            Groupon bo = new Groupon(p);
            BoList.add(bo);
        }
        PageInfo<VoObject> GrouponPage = PageInfo.of(BoList);
        //改为传入的pageSize
        GrouponPage.setPageSize(pagesize);
        return new ReturnObject<>(GrouponPage);
    }


    public ReturnObject<Boolean> judgeGrouponIdValid(Long grouponId) {

        //根据id查找
        GrouponActivityPo po = null;
        try {
            po = grouponActivityPoMapper.selectByPrimaryKey(grouponId);
        } catch (Exception e) {
            StringBuilder message = new StringBuilder().append("judgeGrouponValid: ").append(e.getMessage());
            logger.error(message.toString());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }
        //时间和状态都合法
        if(po!=null && po.getBeginTime().isBefore(LocalDateTime.now())&&
                po.getEndTime().isAfter(LocalDateTime.now()) &&
                po.getState() == ActivityStatus.ON_SHELVES.getCode().byteValue()){
            return new ReturnObject<>(true);
        }

        return new ReturnObject<>(false);
    }
}
