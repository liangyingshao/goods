package cn.edu.xmu.goods.dao;

import cn.edu.xmu.goods.mapper.CommentPoMapper;
import cn.edu.xmu.goods.model.bo.Comment;
import cn.edu.xmu.goods.model.po.CommentPo;
import cn.edu.xmu.goods.model.po.ShopPo;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
                logger.debug("insertUser: have same user name = " + commentPo.getContent());
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

//    public Map getcommentState()
//    {
//        Map<Comment.State,String> map=new HashMap<>();
//        map = Comment.State.getAllState();
//        logger.debug("findcommentState: " + map);
//        return map;
//    }
}