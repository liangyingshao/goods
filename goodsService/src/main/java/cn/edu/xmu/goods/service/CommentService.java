package cn.edu.xmu.goods.service;

import cn.edu.xmu.goods.dao.CommentDao;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class CommentService {

    @Autowired
    CommentDao commentDao;

    @Transactional
    public ReturnObject<VoObject> addSkuComment(Long id, String content, Long type, Long userId) {
        //ReturnObject<VoObject> object;
        long SKU_Id;
        // 根据id查询对应的订单条目记录，需要调用订单的，这里先做个假数据，其实放在try和catch里面是不是比较好呢
        // 查询不到记录
        if(id == 0)// 记录不存在
        {
            //返回903
            return new ReturnObject<>(ResponseCode.USER_NOTBUY);
        }
        // 查到记录，从中拿出SKU_Id
        else
        {
            //给SKU_Id赋值
            SKU_Id=1;
            //判断顾客id和userId是否一致，否则返回903
        }

        return commentDao.addSkuComment(id,content,type,userId,SKU_Id);
    }

    public ReturnObject<PageInfo<VoObject>> selectAllPassComment(Long SKU_Id, Integer pageNum, Integer pageSize) {
        ReturnObject<PageInfo<VoObject>> returnObject = commentDao.selectAllPassComment(SKU_Id, pageNum, pageSize);
        return returnObject;
    }
}