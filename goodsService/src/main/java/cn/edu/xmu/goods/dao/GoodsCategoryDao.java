package cn.edu.xmu.goods.dao;
import cn.edu.xmu.goods.model.bo.GoodsCategory;
import cn.edu.xmu.goods.model.vo.GoodsCategoryVo;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.goods.mapper.*;
import cn.edu.xmu.goods.model.po.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Objects;

/**
 * 分类访问类
 * @author Ming Qiu
 * createdBy Ming Qiu 2020/11/02 13:57
 * modifiedBy 王纬策 2020/11/7 19:20
 **/
@Repository
public class GoodsCategoryDao {

    private static final Logger logger = LoggerFactory.getLogger(GoodsCategoryDao.class);

    @Autowired
    private GoodsCategoryPoMapper goodsCategoryMapper;

//    @Autowired
    //private RedisTemplate<String, Serializable> redisTemplate;



    /**
     * 根据 id 修改商品类目信息
     *
     * @param goodsCategoryVo 传入的 GoodsCategory 对象
     * @return 返回对象 ReturnObj
     * @author 19720182203919 李涵
     * Created at 2020/11/4 20:30
     * Modified by 19720182203919 李涵 at 2020/11/5 10:42
     */
    public ReturnObject<Object> modifyGoodsCategoryByVo(Long id, GoodsCategoryVo goodsCategoryVo) {
        // 查询密码等资料以计算新签名
        GoodsCategoryPo orig = goodsCategoryMapper.selectByPrimaryKey(id);
        // 不修改已被逻辑废弃的账户
        if (orig == null ) {
            logger.info("类目不存在或已被删除：id = " + id);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }

        GoodsCategory goodsCategory = new GoodsCategory(orig);
        GoodsCategoryPo po = goodsCategory.createUpdatePo(goodsCategoryVo);

        // 更新数据库
        ReturnObject<Object> retObj;
        int ret;
        try {
            ret = goodsCategoryMapper.updateByPrimaryKeySelective(po);
        } catch (DataAccessException e) {
            // 如果发生 Exception，判断是什么重复错误 比如类目名称重复
            if (Objects.requireNonNull(e.getMessage()).contains("goods_category_name_uindex")) {
                logger.info("类目名称重复：" + goodsCategoryVo.getName());
                retObj = new ReturnObject<>(ResponseCode.CATEGORY_NAME_SAME);
            }
//            else if (e.getMessage().contains("auth_goodsCategory.auth_goodsCategory_email_uindex")) {
//                logger.info("邮箱重复：" + goodsCategoryVo.getEmail());
//                retObj = new ReturnObject<>(ResponseCode.EMAIL_REGISTERED);
//            }
            else {
                // 其他情况属未知错误
                logger.error("数据库错误：" + e.getMessage());
                retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                        String.format("发生了严重的数据库错误：%s", e.getMessage()));
            }
            return retObj;
        } catch (Exception e) {
            // 其他 Exception 即属未知错误
            logger.error("严重错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                    String.format("发生了严重的未知错误：%s", e.getMessage()));
        }
        // 检查更新有否成功
        if (ret == 0) {
            logger.info("类目不存在或已被删除：id = " + id);
            retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        } else {
            logger.info("类目 id = " + id + " 的资料已更新");
            retObj = new ReturnObject<>();
        }
        return retObj;
    }

    /**
     * (物理) 删除category
     *
     * @param id 用户 id
     * @return 返回对象 ReturnObj
     * @author 19720182203919 李涵
     * Created at 2020/11/4 20:30
     * Modified by 19720182203919 李涵 at 2020/11/5 10:42
     */
    public ReturnObject<Object> physicallyDeleteCategory(Long id) {
        ReturnObject<Object> retObj;
        int ret = goodsCategoryMapper.deleteByPrimaryKey(id);
        if (ret == 0) {
            logger.info("category不存在或已被删除：id = " + id);
            retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        } else {
            logger.info("category id = " + id + " 已被永久删除");
            retObj = new ReturnObject<>();
        }
        return retObj;
    }
//
public void initialize() throws Exception {
    //初始化goodsCategory
    GoodsCategoryPoExample example = new GoodsCategoryPoExample();
    GoodsCategoryPoExample.Criteria criteria = example.createCriteria();
    //criteria.andSignatureIsNull();

    List<GoodsCategoryPo> goodsCategoryPos = goodsCategoryMapper.selectByExample(example);

    for (GoodsCategoryPo po : goodsCategoryPos) {
        GoodsCategoryPo newPo = new GoodsCategoryPo();
        newPo.setPid(po.getPid());
        newPo.setId(po.getId());
        newPo.setName(po.getName());
        goodsCategoryMapper.updateByPrimaryKeySelective(newPo);
    }

}

    /**
     * description: insertGoodsCategory
     * version: 1.0
     * date: 2020/12/2 19:05
     * author: 张悦
     *
     * @param goodsCategory
     * @return cn.edu.xmu.ooad.util.ReturnObject<cn.edu.xmu.goods.model.bo.GoodsCategory>
     */
    public ReturnObject<GoodsCategory> insertGoodsCategory(GoodsCategory goodsCategory, long pid) {

        if(pid!=0) {
            //寻找id = pid 的分类
            GoodsCategoryPo orig = goodsCategoryMapper.selectByPrimaryKey(pid);
            //只能在已有分类下新建子分类
            if (orig == null) {
                logger.info("父级类目不存在：id = " + pid);
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            }
        }

        goodsCategory.setPid(pid);
        GoodsCategoryPo goodsCategoryPo = goodsCategory.gotGoodsCategoryPo();

        ReturnObject<GoodsCategory> retObj = null;
        try{
            int ret = goodsCategoryMapper.insertSelective(goodsCategoryPo);
            if (ret == 0) {
                //插入失败
                logger.debug("insertGoodsCategory: insert goodsCategory fail " + goodsCategoryPo.toString());
                retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("新增失败：" + goodsCategoryPo.getName()));
            } else {
                //插入成功
                logger.debug("insertGoodsCategory: insert goodsCategory = " + goodsCategoryPo.toString());
                goodsCategory.setId(goodsCategoryPo.getId());
                retObj = new ReturnObject<>(goodsCategory);
            }
        }
        catch (DataAccessException e) {
            if (Objects.requireNonNull(e.getMessage()).contains("goods_category_name_uindex")) {
                //若有重复的分类名则新增失败
                logger.debug("updateGoodsCategory: have same goodsCategory name = " + goodsCategoryPo.getName());
                retObj = new ReturnObject<>(ResponseCode.CATEGORY_NAME_SAME, String.format("类目名称已存在：" + goodsCategoryPo.getName()));
            } else {
                // 其他数据库错误
                logger.debug("other sql exception : " + e.getMessage());
                retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
            }
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
        return retObj;
    }

    public ReturnObject getSubcategoriesById(Long id) {

        GoodsCategoryPo orig = goodsCategoryMapper.selectByPrimaryKey(id);
        if (orig == null ) {
            logger.info("类目不存在：id = " + id);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }

        GoodsCategoryPoExample example = new GoodsCategoryPoExample();
        GoodsCategoryPoExample.Criteria criteria = example.createCriteria();
        criteria.andPidEqualTo(id);
        List<GoodsCategoryPo> results = goodsCategoryMapper.selectByExample(example);

        return new ReturnObject<>(results);
    }
}
