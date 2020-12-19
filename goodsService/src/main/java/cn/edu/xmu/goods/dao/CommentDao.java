package cn.edu.xmu.goods.dao;

import cn.edu.xmu.goods.mapper.CommentPoMapper;
import cn.edu.xmu.goods.mapper.GoodsSkuPoMapper;
import cn.edu.xmu.goods.mapper.GoodsSpuPoMapper;
import cn.edu.xmu.goods.model.bo.Comment;
import cn.edu.xmu.goods.model.bo.GoodsSku;
import cn.edu.xmu.goods.model.po.CommentPo;
import cn.edu.xmu.goods.model.po.CommentPoExample;
import cn.edu.xmu.goods.model.po.GoodsSkuPo;
import cn.edu.xmu.goods.model.vo.CommentRetVo;
import cn.edu.xmu.goods.model.vo.Customer;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.oomall.other.model.CustomerDTO;
import cn.edu.xmu.oomall.other.service.ICustomerService;
import cn.edu.xmu.privilegeservice.client.IUserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

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

    @DubboReference(check = false)
    private ICustomerService iCustomerService;

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
        logger.debug("success insert Comment: " + commentPo.getId());
        try{
            //是不是该查一下orderItemId是否已经有评论了
            CommentPoExample orderIdExample = new CommentPoExample();
            CommentPoExample.Criteria orderItemCriteria = orderIdExample.createCriteria();
            orderItemCriteria.andOrderitemIdEqualTo(commentPo.getOrderitemId());
            logger.error("1");
            List<CommentPo> orderIdList = commentPoMapper.selectByExample(orderIdExample);
            if(orderIdList.size()!=0)
            {
                return new ReturnObject<>(ResponseCode.COMMENT_EXISTED);
            }
            logger.error("2");
            int ret = commentPoMapper.insert(commentPo);
            logger.error("3");
            if (ret == 0)
            {
                //修改失败
//                logger.debug("addFloatPrice: insert floatPrice fail : " + floatPricePo.toString());
                return new ReturnObject<>(ResponseCode.FIELD_NOTVALID,"插入失败");
            }
            else {
                //检验是不是真的插入成功了
                logger.error(comment.toString());
                CommentPoExample example=new CommentPoExample();
                CommentPoExample.Criteria criteria=example.createCriteria();
                criteria.andCustomerIdEqualTo(comment.getCustomerId());
                criteria.andGoodsSkuIdEqualTo(comment.getGoodsSkuId());
                criteria.andOrderitemIdEqualTo(comment.getOrderitemId());
                criteria.andTypeEqualTo(comment.getType());
                criteria.andContentEqualTo(comment.getContent());
                criteria.andStateEqualTo(commentPo.getState());
                logger.error("does it in?");
                List<CommentPo> pos=commentPoMapper.selectByExample(example);
                logger.error("4");
                if(pos.size()==0)
                {
                    return new ReturnObject<>(ResponseCode.FIELD_NOTVALID,"插入失败");
                }
                else
                {
                    //构造CommentRetVo
                    Customer customer=new Customer();
                    //根据comment.getCustomerId()查询name和realName
                    ReturnObject<CustomerDTO> name = iCustomerService.findCustomerByUserId(comment.getCustomerId());
                    customer.setId(comment.getCustomerId());
                    customer.setUserName(name.getData().getUserName());
                    customer.setName(name.getData().getName());
                    CommentRetVo commentRetVo=new CommentRetVo(new Comment(commentPo));
                    commentRetVo.setCustomer(customer);
                    ReturnObject<CommentRetVo> commentRetVoReturnObject = new ReturnObject<>(commentRetVo);
                    logger.error(commentRetVoReturnObject.getData().toString());
                    return commentRetVoReturnObject;
                }
            }
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

    public ReturnObject<PageInfo<VoObject>> selectAllPassComment(Long SKU_Id, Integer pageNum, Integer pageSize) {
        //sku存在
        GoodsSkuPo goodsSkuPo = goodsSkuPoMapper.selectByPrimaryKey(SKU_Id);
        if(goodsSkuPo==null) {
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }

        CommentPoExample example = new CommentPoExample();
        CommentPoExample.Criteria criteria = example.createCriteria();
        //增加state=1的查询
        Byte state = 1;
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
                CommentRetVo commentRetVo = new CommentRetVo(comment);
                //查customer
                logger.error("haoxiaozizhaobuzhaodedaoa");
                CustomerDTO customerDTO = iCustomerService.findCustomerByUserId(comment.getCustomerId()).getData();
                logger.error(customerDTO.toString());
                Customer customer = new Customer();
                customer.setId(comment.getCustomerId());
                customer.setUserName(customerDTO.getUserName());
                customer.setName(customerDTO.getName());
                commentRetVo.setCustomer(customer);
                ret.add(commentRetVo);
                logger.error(comment.toString());
            }
            PageInfo<CommentPo> commentRetVoPageInfo = PageInfo.of(commentPos);
            PageInfo<VoObject> commentPage = PageInfo.of(ret);
            commentPage.setPages(commentRetVoPageInfo.getPages());
            commentPage.setPageNum(commentRetVoPageInfo.getPageNum());
            commentPage.setPageSize(commentRetVoPageInfo.getPageSize());
            commentPage.setTotal(commentRetVoPageInfo.getTotal());
            logger.error("DAO:"+commentPage.toString());
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
            logger.error("dao");
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