package cn.edu.xmu.goods.service;

import cn.edu.xmu.goods.dao.CommentDao;
import cn.edu.xmu.goods.model.bo.Comment;
import cn.edu.xmu.goods.model.vo.CommentRetVo;
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
    public ReturnObject<CommentRetVo> addSkuComment(Comment comment) {
        Comment orderItem=new Comment();//其实不应该是Comment类型的。。。
        orderItem.setCustomerId(comment.getCustomerId());//保证现在的假数据满足一致条件
        //根据comment.getOrderitemId()得到对应的订单条目记录orderItem
        if(orderItem == null)// 记录不存在
        {
            // orderItem为null，返回903
            return new ReturnObject<>(ResponseCode.USER_NOTBUY);
        }
        else// 查到记录，从中拿出SKU_Id
        {
            //判断orderItem中的customerId和comment.getCustomerId()
            if(orderItem.getCustomerId()!=comment.getCustomerId())
            {
                //如果不一致，返回903
                return new ReturnObject<>(ResponseCode.USER_NOTBUY);
            }
            else
            {
                //一致，给SKU_Id赋值
                orderItem.setGoodsSkuId(1L);
                comment.setGoodsSkuId(orderItem.getGoodsSkuId());
            }
        }
//        //根据用户id找到用户信息，应该是其他模块的内部接口，但是在user模块里没找到对应的API，/users/{id}是外部接口
////        "customer": {
////            "id": 0,
////            "userName": "string",
////            "realName": "string"
////        }
//        Long id=0L;
//        String content="";
//        Long userId=0L;
//        Long type=0L;
//        Long SKU_Id=0L;
        return commentDao.addSkuComment(comment);
    }

    public ReturnObject<PageInfo<VoObject>> selectAllPassComment(Long SKU_Id, Integer pageNum, Integer pageSize) {
        ReturnObject<PageInfo<VoObject>> returnObject = commentDao.selectAllPassComment(SKU_Id, pageNum, pageSize);
        return returnObject;
    }

    @Transactional
    public ReturnObject<Object> auditComment(Long comment_id, boolean conclusion) {
        return commentDao.auditComment(comment_id, conclusion);
    }

    public ReturnObject<PageInfo<VoObject>> showComment(Long user_Id, Integer pageNum, Integer pageSize) {
        //还得根据用户id找到用户信息封装进data里面，应该是其他模块的内部接口，但是在user模块里没找到对应的API，/users/{id}是外部接口
//        "customer": {
//            "id": 0,
//            "userName": "string",
//            "realName": "string"
//        }
        ReturnObject<PageInfo<VoObject>> returnObject = commentDao.showComment(user_Id, pageNum, pageSize);
        return returnObject;
    }

    public ReturnObject<PageInfo<VoObject>> showUnAuditComments(Integer comment_state, Integer pageNum, Integer pageSize) {
        //还得根据用户id找到用户信息封装进data里面，应该是其他模块的内部接口，但是在user模块里没找到对应的API，/users/{id}是外部接口
//        "customer": {
//            "id": 0,
//            "userName": "string",
//            "realName": "string"
//        }
        ReturnObject<PageInfo<VoObject>> returnObject = commentDao.showUnAuditComments(comment_state, pageNum, pageSize);
        return returnObject;
    }

    //我觉得得另写一个函数，根据用户id找到用户信息封装进data里面
}