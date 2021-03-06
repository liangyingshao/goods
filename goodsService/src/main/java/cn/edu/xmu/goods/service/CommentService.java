package cn.edu.xmu.goods.service;

import cn.edu.xmu.goods.dao.CommentDao;
import cn.edu.xmu.goods.model.bo.Comment;
import cn.edu.xmu.goods.model.vo.CommentRetVo;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.oomall.order.model.OrderDTO;
import cn.edu.xmu.oomall.order.service.IOrderService;
import cn.edu.xmu.privilegeservice.client.IUserService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class CommentService {

    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);

    @Autowired
    private CommentDao commentDao;

    @DubboReference(check = false)
    private IOrderService iOrderService;

    @Transactional
    public ReturnObject<CommentRetVo> addSkuComment(Comment comment) {
        logger.error("进入增加SKU评论service层，调用iOrderService.getUserSelectSOrderInfo");
        ReturnObject<OrderDTO> orderDTOReturnObject = iOrderService.getUserSelectSOrderInfo(comment.getCustomerId(), comment.getOrderitemId());
        logger.error("iOrderService.getUserSelectSOrderInfo没有直接抛异常");
//        ReturnObject<OrderDTO> orderDTOReturnObject = new ReturnObject<>(new OrderDTO());
        //根据comment.getOrderitemId()得到对应的订单条目记录orderItem
        if(orderDTOReturnObject.getData() == null)// 记录不存在
        {
            // orderItem为null，返回903
            logger.error("iOrderService.getUserSelectSOrderInfo返回为空");

            return new ReturnObject<>(ResponseCode.USER_NOTBUY);
        }
        else// 查到记录，从中拿出SKU_Id
        {
            logger.error("iOrderService.getUserSelectSOrderInfo正常返回");
//            List<Long> list = new ArrayList<>();
//            list.add(comment.getOrderitemId());
//            logger.error("iOrderService.getUserSelectOrderInfoByList: ");
//            ReturnObject<Map<Long, OrderDTO>> mapReturnObject = iOrderService.getUserSelectOrderInfoByList(comment.getCustomerId(), list);
//            Map<Long, OrderDTO> map = new HashMap<Long, OrderDTO>();
//            map.put(comment.getOrderitemId(), new OrderDTO());
//            ReturnObject<Map<Long, OrderDTO>> mapReturnObject = new ReturnObject<Map<Long, OrderDTO>>(map);
            //一致，给SKU_Id赋值
            comment.setGoodsSkuId(orderDTOReturnObject.getData().getSkuId());//(mapReturnObject.getData().get(comment.getOrderitemId()).getSkuId());
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
        logger.error("service");
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