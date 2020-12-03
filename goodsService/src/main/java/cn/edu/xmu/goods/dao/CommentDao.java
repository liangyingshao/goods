package cn.edu.xmu.goods.dao;

import cn.edu.xmu.goods.mapper.CommentPoMapper;
import cn.edu.xmu.goods.mapper.GoodsSkuPoMapper;
import cn.edu.xmu.goods.mapper.GoodsSpuPoMapper;
import cn.edu.xmu.goods.mapper.ShopPoMapper;
import cn.edu.xmu.goods.model.bo.Comment;
import cn.edu.xmu.goods.model.bo.GoodsSpu;
import cn.edu.xmu.goods.model.po.CommentPo;
import cn.edu.xmu.goods.model.po.CommentPoExample;
import cn.edu.xmu.goods.model.po.GoodsSpuPoExample;
import cn.edu.xmu.goods.model.po.ShopPo;
import cn.edu.xmu.goods.model.vo.CommentRetVo;
import cn.edu.xmu.goods.model.vo.Customer;
import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sun.mail.iap.Response;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.*;

@Repository
public class CommentDao {
    @Autowired
    private CommentPoMapper commentPoMapper;

    @Autowired
    private GoodsSkuPoMapper goodsSkuPoMapper;

    @Autowired
    private GoodsSpuPoMapper goodsSpuPoMapper;

    private static final Logger logger = LoggerFactory.getLogger(CommentDao.class);

    public ReturnObject<CommentRetVo> addSkuComment(Comment comment) {
        ReturnObject returnObject = null;
        CommentPo commentPo = new CommentPo();
        commentPo.setCustomerId(comment.getCustomerId());
        commentPo.setGoodsSkuId(comment.getGoodsSkuId());
        commentPo.setOrderitemId(comment.getOrderitemId());
        commentPo.setType(comment.getType());
        commentPo.setContent(comment.getContent());
        commentPo.setState((byte) Comment.State.TOAUDIT.getCode());
        commentPo.setGmtCreate(LocalDateTime.now());
//        logger.debug("success insert Comment: " + commentPo.getId());
        try{
            //是不是该查一下orderItemId是否已经有评论了
            CommentPoExample orderIdExample = new CommentPoExample();
            CommentPoExample.Criteria orderItemCriteria = orderIdExample.createCriteria();
            orderItemCriteria.andOrderitemIdEqualTo(commentPo.getOrderitemId());
            List<CommentPo> orderIdList = commentPoMapper.selectByExample(orderIdExample);
            if(orderIdList.size()!=0)
            {
                return new ReturnObject<>(ResponseCode.COMMENT_EXISTED);
            }
            int ret = commentPoMapper.insert(commentPo);
            if (ret == 0)
            {
                //修改失败
//                logger.debug("addFloatPrice: insert floatPrice fail : " + floatPricePo.toString());
                return new ReturnObject<>(ResponseCode.FIELD_NOTVALID,"插入失败");
            }
            else {
                //检验是不是真的插入成功了
                CommentPoExample example=new CommentPoExample();
                CommentPoExample.Criteria criteria=example.createCriteria();
                criteria.andCustomerIdEqualTo(comment.getCustomerId());
                criteria.andGoodsSkuIdEqualTo(comment.getGoodsSkuId());
                criteria.andOrderitemIdEqualTo(comment.getOrderitemId());
                criteria.andTypeEqualTo(comment.getType());
                criteria.andContentEqualTo(comment.getContent());
                criteria.andStateEqualTo(commentPo.getState());
                List<CommentPo> pos=commentPoMapper.selectByExample(example);
                if(pos.size()==0)
                {
                    return new ReturnObject<>(ResponseCode.FIELD_NOTVALID,"插入失败");
                }
                else
                {
                    //构造CommentRetVo
                    Customer customer=new Customer();
                    //根据comment.getCustomerId()查询name和realName
                    customer.setId(comment.getCustomerId());
                    customer.setUserName("用户姓名");
                    customer.setRealName("真实姓名");
                    CommentRetVo commentRetVo=new CommentRetVo(new Comment(commentPo));
                    commentRetVo.setCustomer(customer);
                    return new ReturnObject<>(commentRetVo);
                }
            }
        }
        catch (DataAccessException e){
//            logger.error("selectAllPassComment: DataAccessException:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        catch (Exception e) {
            // 其他Exception错误
//            logger.error("other exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
    }

    public ReturnObject<PageInfo<VoObject>> selectAllPassComment(Long SKU_Id, Integer pageNum, Integer pageSize) {
        CommentPoExample example = new CommentPoExample();
        CommentPoExample.Criteria criteria = example.createCriteria();
        //增加state=2的查询
        Byte state = 2;
        criteria.andStateEqualTo(state);
        criteria.andGoodsSkuIdEqualTo(SKU_Id);
        //criteria.andDepartIdEqualTo(departId);
        //分页查询
        PageHelper.startPage(pageNum, pageSize);
        logger.debug("page = " + pageNum + "pageSize = " + pageSize);
        List<CommentPo> commentPos = null;
        try {
            commentPos = commentPoMapper.selectByExample(example);
            List<VoObject> ret = new ArrayList<>(commentPos.size());
            for (CommentPo po : commentPos) {
                Comment comment = new Comment(po);
                ret.add(comment);
            }
            PageInfo<VoObject> commentPage = PageInfo.of(ret);
            return new ReturnObject<>(commentPage);
        }
        catch (DataAccessException e){
            logger.error("selectAllPassComment: DataAccessException:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
    }

    public ReturnObject<Object> auditComment(Long comment_id, boolean conclusion) {
        try {
            CommentPo commentPo = commentPoMapper.selectByPrimaryKey(comment_id);
            if (commentPo == null) {//如果没有这条评论
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            }
            else {
                Byte newState;
                if(commentPo.getState() != 0){
                    return new ReturnObject<>(ResponseCode.COMMENT_AUDITED);//该评论已经审核过了
                }
                if(conclusion) {//通过审核
                    newState = 2;
                }
                else {//不通过审核
                    newState = 1;
                }
                commentPo.setState(newState);
                commentPo.setGmtModified(LocalDateTime.now());
                commentPoMapper.updateByPrimaryKey(commentPo);
                return new ReturnObject<>(ResponseCode.OK);
            }
        }
        catch(Exception e) {
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
    }

    public ReturnObject<PageInfo<VoObject>> showComment(Long user_Id, Integer pageNum, Integer pageSize) {
        CommentPoExample example = new CommentPoExample();
        CommentPoExample.Criteria criteria = example.createCriteria();
        //增加顾客Id=user_Id的查询
        criteria.andCustomerIdEqualTo(user_Id);
        //分页查询
        PageHelper.startPage(pageNum, pageSize);
        List<CommentPo> commentPos = null;
        try {
            commentPos = commentPoMapper.selectByExample(example);
            List<VoObject> ret = new ArrayList<>(commentPos.size());
            for (CommentPo po : commentPos) {
                Comment comment = new Comment(po);
                ret.add(comment);
            }
            PageInfo<VoObject> commentPage = PageInfo.of(ret);
            return new ReturnObject<>(commentPage);
        }
        catch (DataAccessException e){
            logger.error("selectAllPassComment: DataAccessException:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
    }

    public ReturnObject<PageInfo<VoObject>> showUnAuditComments(Integer comment_state, Integer pageNum, Integer pageSize) {
        CommentPoExample example = new CommentPoExample();
        CommentPoExample.Criteria criteria = example.createCriteria();
        //增加state=0或者1或者2的查询
        Byte state = comment_state.byteValue();
        criteria.andStateEqualTo(state);
        //分页查询
        PageHelper.startPage(pageNum, pageSize);
//        logger.debug("page = " + pageNum + "pageSize = " + pageSize);
        List<CommentPo> commentPos = null;
        try {
            commentPos = commentPoMapper.selectByExample(example);
            List<VoObject> ret = new ArrayList<>(commentPos.size());
            for (CommentPo po : commentPos) {
                Comment comment = new Comment(po);
                ret.add(comment);
            }
            PageInfo<VoObject> commentPage = PageInfo.of(ret);
            return new ReturnObject<>(commentPage);
        }
        catch (DataAccessException e){
            logger.error("selectAllPassComment: DataAccessException:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
    }
}