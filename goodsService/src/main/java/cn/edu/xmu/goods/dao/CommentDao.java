package cn.edu.xmu.goods.dao;

import cn.edu.xmu.goods.mapper.CommentPoMapper;
import cn.edu.xmu.goods.model.bo.Comment;
import cn.edu.xmu.goods.model.po.CommentPo;
import cn.edu.xmu.goods.model.po.CommentPoExample;
import cn.edu.xmu.goods.model.po.ShopPo;
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

    private static final Logger logger = LoggerFactory.getLogger(CommentDao.class);

    public ReturnObject<VoObject> addSkuComment(Long id, String content, Long type, Long userId, Long SKU_Id) {
        ReturnObject returnObject = null;
        CommentPo commentPo = new CommentPo();
        commentPo.setCustomerId(userId);
        commentPo.setGoodsSkuId(SKU_Id);
        commentPo.setOrderitemId(id);
        commentPo.setType((byte) type.intValue());
        commentPo.setContent(content);
        commentPo.setState((byte) Comment.State.TOAUDIT.getCode());
        commentPo.setGmtCreate(LocalDateTime.now());
        logger.debug("success insert Comment: " + commentPo.getId());

        try{
            returnObject = new ReturnObject<>(commentPoMapper.insert(commentPo));
        }
        catch (DataAccessException e)
        {
            if (Objects.requireNonNull(e.getMessage()).contains("auth_user.user_name_uindex")) {
                //断进来之后再看是什么错误
                logger.debug("insert: " + commentPo.getContent());
//                returnObject = new ReturnObject<>(ResponseCode.ROLE_REGISTERED, String.format("用户名重复：" + commentPo.getContent()));
//            } else {
//                logger.debug("sql exception : " + e.getMessage());
//                returnObject = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
            }
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            returnObject = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
        return new ReturnObject<>(ResponseCode.OK);
    }

    public ReturnObject<PageInfo<VoObject>> selectAllPassComment(Long SKU_Id, Integer pageNum, Integer pageSize) {
        CommentPoExample example = new CommentPoExample();
        CommentPoExample.Criteria criteria = example.createCriteria();
        //增加state=2的查询
        Byte state = 2;
        criteria.andStateEqualTo(state);
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
                    return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);//该评论已经审核过了，错误码需要调整
                }
                if(conclusion) {//通过审核
                    newState = 2;
                }
                else {//不通过审核
                    newState = 1;
                }
                commentPo.setState(newState);
                commentPoMapper.updateByPrimaryKey(commentPo);
                return new ReturnObject<>(ResponseCode.OK);
            }
        }
        catch(Exception e) {
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
    }
}