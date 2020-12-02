package cn.edu.xmu.goods.dao;

import cn.edu.xmu.goods.mapper.BrandPoMapper;
import cn.edu.xmu.goods.mapper.GoodsCategoryPoMapper;
import cn.edu.xmu.goods.mapper.GoodsSpuPoMapper;
import cn.edu.xmu.goods.model.bo.GoodsSpu;
import cn.edu.xmu.goods.model.po.BrandPo;
import cn.edu.xmu.goods.model.po.GoodsCategoryPo;
import cn.edu.xmu.goods.model.po.GoodsSpuPo;
import cn.edu.xmu.goods.model.po.GoodsSpuPoExample;
import cn.edu.xmu.goods.model.vo.GoodsSpuVo;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.util.Objects;

/**
 * 商品SPU访问类
 * @author 24320182203254 秦楚彦
 * Created at 2020/11/30 12：34
 */
@Repository
public class GoodsSpuDao {
    private static final Logger logger = LoggerFactory.getLogger(GoodsSpuDao.class);

    @Autowired
    private GoodsSpuPoMapper goodsSpuMapper;

    @Autowired
    private GoodsCategoryPoMapper goodsCategoryMapper;

    @Autowired
    private BrandPoMapper brandMapper;

    /**
     * 增加一个SPU
     * @param spu SPUbo
     * @return  ReturnObject<GoodsSpu> 新增结果
     * @author 24320182203254 秦楚彦
     * Created at 2020/12/01 00：42
     */
    public ReturnObject<GoodsSpu> addSpu(GoodsSpu spu) {
        GoodsSpuPo spuPo =spu.createSpuPo();
        ReturnObject<GoodsSpu> returnObject=null;
        try{
            int ret = goodsSpuMapper.insertSelective(spuPo);
            if (ret == 0) {
                //插入失败
                logger.debug("insertRole: insert role fail " + spuPo.toString());
                returnObject = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("新增失败：" + spuPo.getName()));
            } else {
                //插入成功
                logger.debug("insertRole: insert role = " + spuPo.toString());
                spu.setId(spuPo.getId());
                returnObject = new ReturnObject<>(spu);
            }
        }
        catch (DataAccessException e) {
            if (Objects.requireNonNull(e.getMessage()).contains("?")) {
                //若有重复的角色名则新增失败????
                logger.debug("updateRole: have same role name = " + spuPo.getName());
                returnObject = new ReturnObject<>(ResponseCode.ROLE_REGISTERED, String.format("?：" + spuPo.getName()));
            } else {
                // 其他数据库错误
                logger.debug("other sql exception : " + e.getMessage());
                returnObject = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
            }
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            returnObject = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
        return returnObject;
    }

    /**
     * 根据SPUid,查看一条商品SPU的详细信息
     * @param id
     * @return  GoodsSpuVo
     * @author 24320182203254 秦楚彦
     */

    public ReturnObject<Object> showSpu(Long id) {

        GoodsSpuPo spuPo= goodsSpuMapper.selectByPrimaryKey(id);
        if(spuPo==null)
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
        GoodsSpu spu=new GoodsSpu(spuPo);
        GoodsSpuVo spuVo= new GoodsSpuVo(spu);
        return new ReturnObject<>(spuVo);

    }

    /**
     * 更改SPU状态（上下架）
     * @param spu
     * @return  RetrunObject
     * @author 24320182203254 秦楚彦
     */
    public ReturnObject<Object> changeState(GoodsSpu spu) {
        ReturnObject<Object> returnObject;
        GoodsSpuPo spuPo=goodsSpuMapper.selectByPrimaryKey(spu.getId());
        //判断是否重复更改
        if(spu.getState().getCode().equals(spuPo.getState().intValue()))
            return new ReturnObject<>(ResponseCode.STATE_NOCHANGE);
        //判断shopId是否对应的上
        if(spu.getShopId().equals(spuPo.getShopId()))
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("spuId与shopId不对应"));
        //执行修改
        spuPo.setState(spu.getState().getCode().byteValue());
        int ret;
        try{
            ret=goodsSpuMapper.updateByPrimaryKeySelective(spuPo);
            if(ret==0){
                //修改失败
                logger.debug("changeSpuState: update role fail : " + spuPo.toString());
                returnObject = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("spuid不存在：" + spuPo.getId()));
            }else{
                //修改成功
                logger.debug("changeSpuState: update role : " + spuPo.toString());
                returnObject=new ReturnObject<>();
            }
        }catch (DataAccessException e){
            logger.debug("other sql exception : " + e.getMessage());
            returnObject = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        catch(Exception e){
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            returnObject = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));

        }
        return returnObject;
    }

    /**
     * 修改SPU
     * @param spu SPUbo
     * @return  ReturnObject<Object> 修改结果
     * @author 24320182203254 秦楚彦
     * Created at 2020/12/01 22：07
     */
    public ReturnObject<Object> modifyGoodsSpu(GoodsSpu spu) {

        GoodsSpuPo spuPo = spu.createSpuPo();
        ReturnObject<Object> returnObject = null;
        GoodsSpuPoExample spuPoExample = new GoodsSpuPoExample();
        GoodsSpuPoExample.Criteria criteria = spuPoExample.createCriteria();
        criteria.andIdEqualTo(spu.getId());
        criteria.andShopIdEqualTo(spu.getShopId());
        try{
            int ret = goodsSpuMapper.updateByExampleSelective(spuPo, spuPoExample);
            if (ret == 0) {
                //修改失败
                logger.debug("updateRole: update spu fail : " + spuPo.toString());
                returnObject = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("spuid不存在：" + spuPo.getId()));
            } else {
                //修改成功
                logger.debug("updateRole: update spu = " + spuPo.toString());
                returnObject = new ReturnObject<>();
            }
        }
        catch (DataAccessException e) {
            // 其他数据库错误
            logger.debug("other sql exception : " + e.getMessage());
            returnObject = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            returnObject = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
        return returnObject;
    }

    /**
     * 修改SPU的CategoryId
     * @param spu SPUbo
     * @return  ReturnObject<Object> 修改结果
     * @author 24320182203254 秦楚彦
     * Created at 2020/12/01 22：57
     */
    public ReturnObject<Object> addSpuCategory(GoodsSpu spu) {
        ReturnObject<Object> returnObject = null;
        //获得该分类信息
        try {
            GoodsCategoryPo categoryPo=goodsCategoryMapper.selectByPrimaryKey(spu.getCategoryId());
            //该分类不存在
            if(categoryPo==null)
                return returnObject=new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            //该分类为一级分类，不可加入
            if(categoryPo.getPid().equals((long)0))
                return returnObject=new ReturnObject<>(ResponseCode.CATEALTER_INVALID);
            //该分类为二级分类，将SPU加入
            spu.setDisabled(GoodsSpu.SpuState.ONSHELF);//提前设置，避免空指针错误
            returnObject=modifyGoodsSpu(spu);
        }
        catch (DataAccessException e) {
            // 其他数据库错误
            logger.debug("other sql exception : " + e.getMessage());
            returnObject = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            returnObject = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
        return returnObject;
    }

    /**
     * 移除SPU的CategoryId
     * @param spu SPUbo
     * @return  ReturnObject<Object> 修改结果
     * @author 24320182203254 秦楚彦
     * Created at 2020/12/02 10:39
     */
    public ReturnObject<Object> removeSpuCategory(GoodsSpu spu) {
        ReturnObject<Object> returnObject = null;
        //获得该分类信息
        try {
            GoodsCategoryPo categoryPo=goodsCategoryMapper.selectByPrimaryKey(spu.getCategoryId());
            //该分类不存在
            if(categoryPo==null)
                return returnObject=new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            //请求移出分类与SPU实际所属分类不一致 或请求移出分类为一级分类
            if(!categoryPo.getId().equals(spu.getCategoryId())||categoryPo.getPid().equals((long)0))
                return returnObject=new ReturnObject<>(ResponseCode.CATEALTER_INVALID);
            //将SPU移出该分类
            spu.setDisabled(GoodsSpu.SpuState.ONSHELF);//提前设置，避免空指针错误
            spu.setCategoryId((long)0);//提前设置，避免空指针错误
            returnObject=modifyGoodsSpu(spu);
        }
        catch (DataAccessException e) {
            // 其他数据库错误
            logger.debug("other sql exception : " + e.getMessage());
            returnObject = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            returnObject = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
        return returnObject;
    }

    /**
     * 修改SPU的BrandId
     * @param spu SPUbo
     * @return  ReturnObject<Object> 修改结果
     * @author 24320182203254 秦楚彦
     * Created at 2020/12/02 22：30
     */
    public ReturnObject<Object> addSpuBrand(GoodsSpu spu) {
        ReturnObject<Object> returnObject = null;
        //获得该品牌信息
        try {
            BrandPo brandPo = brandMapper.selectByPrimaryKey(spu.getBrandId());
            //该品牌不存在
            if(brandPo==null)
                return returnObject=new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            //该品牌存在，将SPU加入
            spu.setDisabled(GoodsSpu.SpuState.ONSHELF);//提前设置，避免空指针错误
            returnObject=modifyGoodsSpu(spu);
        }
        catch (DataAccessException e) {
            // 其他数据库错误
            logger.debug("other sql exception : " + e.getMessage());
            returnObject = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            returnObject = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
        return returnObject;
    }

    /**
     * 移除SPU的BrandId
     * @param spu SPUbo
     * @return  ReturnObject<Object> 修改结果
     * @author 24320182203254 秦楚彦
     * Created at 2020/12/02 22：32
     */
    public ReturnObject<Object> removeSpuBrand(GoodsSpu spu) {
        ReturnObject<Object> returnObject = null;
        //获得该品牌信息
        try {
            GoodsSpuPo spuPo=goodsSpuMapper.selectByPrimaryKey(spu.getId());
            BrandPo brandPo = brandMapper.selectByPrimaryKey(spu.getBrandId());
            //该品牌不存在
            if(brandPo==null)
                return returnObject=new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            //请求移出品牌与SPU实际所属品牌不一致 或SPU本无品牌
            if(!brandPo.getId().equals(spuPo.getBrandId())||spuPo.getBrandId().equals(0))
                return returnObject=new ReturnObject<>(ResponseCode.BRANDALTER_INVALID);
            //将SPU移出该品牌
            spu.setDisabled(GoodsSpu.SpuState.ONSHELF);//提前设置，避免空指针错误？？状态存疑
            spu.setBrandId((long)0);//提前设置，避免空指针错误
            returnObject=modifyGoodsSpu(spu);
        }
        catch (DataAccessException e) {
            // 其他数据库错误
            logger.debug("other sql exception : " + e.getMessage());
            returnObject = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            returnObject = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
        return returnObject;
    }
}
