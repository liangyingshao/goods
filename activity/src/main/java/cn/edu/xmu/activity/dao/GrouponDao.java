package cn.edu.xmu.activity.dao;


import cn.edu.xmu.activity.mapper.GrouponActivityPoMapper;
import cn.edu.xmu.activity.model.po.GrouponActivityPo;
import cn.edu.xmu.activity.model.vo.GrouponVo;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    public ReturnObject modifyGrouponofSPU(Long shopId, Long id, GrouponVo grouponVo) {

        //读出旧的团购活动信息
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

        //检查此用户是否有权限修改此grouponActivity
        if(oldPo.getShopId()!= shopId)
            return new ReturnObject<>(ResponseCode.GROUPON_STATENOTALLOW);

        //封装修改对象
        GrouponActivityPo grouponActivityPo = new GrouponActivityPo();
        grouponActivityPo.setId(id);
        grouponActivityPo.setStrategy(grouponVo.getStrategy());
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        grouponActivityPo.setBeginTime(LocalDateTime.parse(grouponVo.getBeginTime(),df));
        grouponActivityPo.setEndTime(LocalDateTime.parse(grouponVo.getEndTime(),df));

        try {
            grouponActivityPoMapper.updateByPrimaryKeySelective(grouponActivityPo);
        } catch (Exception e) {
            StringBuilder message = new StringBuilder().append("modifyGrouponofSPU:update: ").append(e.getMessage());
            logger.error(message.toString());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }

        return new ReturnObject<>(ResponseCode.OK);
    }


  
}
