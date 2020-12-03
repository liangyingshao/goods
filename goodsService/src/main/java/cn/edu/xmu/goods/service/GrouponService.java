package cn.edu.xmu.goods.service;


import cn.edu.xmu.goods.dao.GrouponDao;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * description: GrouponService
 * date: 2020/12/1 0:11
 * author: 杨铭
 * version: 1.0
 */
@Service
public class GrouponService {

    @Autowired
    GrouponDao grouponDao;
    public ReturnObject<VoObject> getgrouponState() {
        return grouponDao.getgrouponState();
    }
}
