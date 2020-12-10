package cn.edu.xmu.activity.service;

import cn.edu.xmu.activity.dao.GrouponDao;
import cn.edu.xmu.activity.model.vo.GrouponVo;
import cn.edu.xmu.ooad.util.ReturnObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public ReturnObject modifyGrouponofSPU(Long shopId, Long id, GrouponVo grouponVo) {
        return grouponDao.modifyGrouponofSPU(shopId, id, grouponVo);
    }
}
