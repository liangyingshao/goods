package cn.edu.xmu.goods.dao;

import cn.edu.xmu.goods.mapper.GoodsSpuPoMapper;
import cn.edu.xmu.goods.model.bo.GoodsSpu;
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
}
