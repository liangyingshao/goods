package cn.edu.xmu.goods.dao;

import cn.edu.xmu.goods.mapper.GrouponActivityPoMapper;
import cn.edu.xmu.goods.mapper.ShopPoMapper;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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

    public ReturnObject<VoObject> getgrouponState() {
        return new ReturnObject<>(ResponseCode.OK);
    }
}
