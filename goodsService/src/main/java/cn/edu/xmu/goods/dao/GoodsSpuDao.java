package cn.edu.xmu.goods.dao;

import cn.edu.xmu.goods.mapper.*;
import cn.edu.xmu.goods.model.bo.GoodsSku;
import cn.edu.xmu.goods.model.bo.GoodsSpu;
import cn.edu.xmu.goods.model.po.*;
import cn.edu.xmu.goods.model.vo.GoodsSkuRetVo;
import cn.edu.xmu.goods.model.vo.GoodsSpuVo;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ImgHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.oomall.order.model.SimpleFreightModelDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
    private ShopPoMapper shopMapper;

    @Autowired
    private GoodsSkuPoMapper goodsSkuMapper;

    @Autowired
    private GoodsCategoryPoMapper goodsCategoryMapper;

    @Autowired
    private BrandPoMapper brandMapper;

    @Autowired
    private FloatPricePoMapper floatMapper;

    //上传图片相关变量
    private String davUsername="night";
    private String davPassword="tiesuolianhuan123";
    private String baseUrl="http://172.16.4.146:8888/webdav/";//需要写成我们组服务器的webdev地址

    /**
     * 增加一个SPU
     * @param spu SPUbo
     * @return  ReturnObject<GoodsSpu> 新增结果
     * @author 24320182203254 秦楚彦
     * Created at 2020/12/01 00：42
     */
    public ReturnObject<GoodsSpu> addSpu(GoodsSpu spu) {
        GoodsSpuPo spuPo =spu.createSpuPo();
        ReturnObject<GoodsSpu> returnObject;
        ShopPo shopPo=shopMapper.selectByPrimaryKey(spu.getShopId());
        if(shopPo!=null){
            if(shopPo.getState().equals(1)||shopPo.getState().equals(2))//如果商店状态为[未上线]或[已上线],可添加SPU
            {
                try {
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
                } catch (DataAccessException e) {
                    // 其他数据库错误
                    logger.debug("other sql exception : " + e.getMessage());
                    returnObject = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));

                } catch (Exception e) {
                    // 其他Exception错误
                    logger.error("other exception : " + e.getMessage());
                    returnObject = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
                }
            }

        }
        returnObject=new ReturnObject<>(ResponseCode.SHOP_NOTOPERABLE);
        return returnObject;
    }

    /**
     * 根据SPUid,查看一条商品SPU的详细信息
     * @param id
     * @return  GoodsSpuVo
     * @author 24320182203254 秦楚彦
     */

    public ReturnObject<Object> showSpu(Long id, SimpleFreightModelDTO freightModelDTO) {

        GoodsSpuPo spuPo= goodsSpuMapper.selectByPrimaryKey(id);

        if(spuPo==null)
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
        if(spuPo.getDisabled().equals(1))//disable=true 对SPU的无效操作
            return new ReturnObject(ResponseCode.SPU_NOTOPERABLE);

        //查找该spu下的所有sku
        GoodsSkuPoExample skuPoExample=new GoodsSkuPoExample();
        GoodsSkuPoExample.Criteria skuCriteria=skuPoExample.createCriteria();
        skuCriteria.andGoodsSpuIdEqualTo(spuPo.getId());
        skuCriteria.andDisabledEqualTo((byte)0);
        List<GoodsSkuPo> skuPos=goodsSkuMapper.selectByExample(skuPoExample);
        List<GoodsSku>skus=skuPos.stream().map(GoodsSku::new).collect(Collectors.toList());
        for(GoodsSku sku:skus)
        {
            FloatPricePoExample floatExample=new FloatPricePoExample();
            FloatPricePoExample.Criteria floatCriteria=floatExample.createCriteria();
            floatCriteria.andGoodsSkuIdEqualTo(sku.getId());
            floatCriteria.andBeginTimeLessThanOrEqualTo(LocalDateTime.now());
            floatCriteria.andEndTimeGreaterThanOrEqualTo(LocalDateTime.now());
            List<FloatPricePo> floatPos=floatMapper.selectByExample(floatExample);
            if(floatPos.size()==0)sku.setPrice(sku.getOriginalPrice());
            else if(floatPos.size()==1)sku.setPrice(floatPos.get(0).getActivityPrice());
        }

        List<GoodsSkuRetVo> ret = skus.stream().map(GoodsSkuRetVo::new).collect(Collectors.toList());
        GoodsSpu spu=new GoodsSpu(spuPo);
        GoodsSpuVo spuVo= new GoodsSpuVo(spu);
        spuVo.setSkuList(ret);
        if(freightModelDTO!=null)
        spuVo.setFreight(freightModelDTO);
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
//        if(spu.getState().getCode().equals(spuPo.getState().intValue()))
//            return new ReturnObject<>(ResponseCode.STATE_NOCHANGE);
        //判断shopId是否对应的上
        if(spu.getShopId().equals(spuPo.getShopId()))
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("spuId与shopId不对应"));
        //执行修改
//        spuPo.setState(spu.getState().getCode().byteValue());
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
            int ret = goodsSpuMapper.updateByPrimaryKeySelective(spuPo);
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
//            spu.setDisabled(false);//提前设置，避免空指针错误
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
//            spu.setDisabled(false);//提前设置，避免空指针错误
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
//            spu.setDisabled(false);//提前设置，避免空指针错误
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
//            spu.setDisabled(false);//提前设置，避免空指针错误
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

    /**
     * 逻辑删除商品SPU
     * @param shopId 店铺id
     * @param id spuId
     * @return  ReturnObject<Object> 修改结果
     * @author 24320182203254 秦楚彦
     * Created at 2020/12/02 22：32
     */
    public ReturnObject<Object> deleteGoodsSpu(Long shopId,Long id) {
        ReturnObject<Object> returnObject = null;
        try {
            //获得该SPU信息
            GoodsSpuPo spuPo=goodsSpuMapper.selectByPrimaryKey(id);
            //该SPU不存在或shopId不对应
            if(spuPo==null||!spuPo.getShopId().equals(shopId))
                return returnObject=new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            //该SPU已被逻辑删除, disable==(Byte) 1 or state==DELETED
            if(spuPo.getDisabled().equals((byte)1))//||spuPo.getState().equals(GoodsSpu.SpuState.DELETED.getCode().byteValue()))
                return returnObject=new ReturnObject<>(ResponseCode.BRANDALTER_INVALID);
            //查找该spu下的所有sku
            GoodsSkuPoExample skuPoExample=new GoodsSkuPoExample();
            GoodsSkuPoExample.Criteria skuCriteria=skuPoExample.createCriteria();
            skuCriteria.andGoodsSpuIdEqualTo(spuPo.getId());
            skuCriteria.andDisabledEqualTo((byte)0);
            List<GoodsSkuPo> skuPos=goodsSkuMapper.selectByExample(skuPoExample);
            //若SPU无SKU，物理删除SPU
            if(skuPos==null)
            {
                goodsSpuMapper.deleteByPrimaryKey(id);
                return new ReturnObject<>(ResponseCode.OK);
            }
            //若SPU下有SKU，将SKU的state设为6：已删除
            for(GoodsSkuPo skuPo:skuPos)
            {
                GoodsSkuPoExample skuExample = new GoodsSkuPoExample();
                GoodsSkuPoExample.Criteria criteria = skuExample.createCriteria();
                criteria.andIdEqualTo(skuPo.getId());
                criteria.andGoodsSpuIdEqualTo(id);
                skuPo.setDisabled((byte)6);
                int ret=goodsSkuMapper.updateByExample(skuPo,skuExample);
                if (ret == 0) {
                    //修改失败
                    logger.debug("deleteSKUofSPU: update spu fail : " + skuPo.toString());
                    returnObject = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("skuid不存在：" + skuPo.getId()));
                }
            }
            //将SPU物理删除
            goodsSpuMapper.deleteByPrimaryKey(id);
            return new ReturnObject<>(ResponseCode.OK);
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
     * 上传商品SPU图片
     * @param spu
     * @return  ReturnObject
     * @author 24320182203254 秦楚彦
     * Created at 2020/12/03 12：32
     */
    public ReturnObject<Object> uploadSpuImg(GoodsSpu spu,MultipartFile file) {

        ReturnObject returnObject = null;
        try {
            //获得该SPU信息
            GoodsSpuPo spuPo = goodsSpuMapper.selectByPrimaryKey(spu.getId());
            //该SPU不存在
            if (spuPo == null)
                return returnObject = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            //对不属于操作者店铺的商品SPU进行操作
            if (!spuPo.getShopId().equals(spu.getShopId()))
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
            // 该商品SPU已被删除
//            if (spuPo.getState().equals(GoodsSpu.SpuState.DELETED.getCode().byteValue()))
//                return returnObject = new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);


            returnObject = ImgHelper.remoteSaveImg(file, 2, davUsername, davPassword, baseUrl);

            //文件上传错误
            if (!returnObject.getCode().equals(ResponseCode.OK)) {
                logger.debug(returnObject.getErrmsg());
                return returnObject;
            }
            String oldFilename = spu.getImageUrl();
            spu.setImageUrl(returnObject.getData().toString());
            returnObject = modifyGoodsSpu(spu);

            //数据库更新失败，需删除新增的图片
            if (returnObject.getCode() == ResponseCode.FIELD_NOTVALID) {
                ImgHelper.deleteRemoteImg(returnObject.getData().toString(), davUsername, davPassword, baseUrl);
                return returnObject;
            }

            //数据库更新成功需删除旧图片，未设置则不删除
            if (oldFilename != null) {
                ImgHelper.deleteRemoteImg(oldFilename, davUsername, davPassword, baseUrl);
            }
        } catch (DataAccessException e) {
            // 其他数据库错误
            logger.debug("other sql exception : " + e.getMessage());
            returnObject = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (IOException e) {
            logger.debug("uploadImg: I/O Error:" + baseUrl);
            return new ReturnObject(ResponseCode.FILE_NO_WRITE_PERMISSION);
        } catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            returnObject = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
        return returnObject;
    }

    public ReturnObject<GoodsSpuPo> getSpuBySpuId(Long id)
    {
        GoodsSpuPo goodsSpuPo = null;
        try {
            goodsSpuPo = goodsSpuMapper.selectByPrimaryKey(id);
            if(goodsSpuPo==null)
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        } catch (Exception e) {
            logger.debug("other sql exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }

        return new ReturnObject<>(goodsSpuPo);
    }

    ReturnObject<List<Long>> getAllSpuIdByShopId(Long shopId) {
        GoodsSpuPoExample example = new GoodsSpuPoExample();
        GoodsSpuPoExample.Criteria criteria = example.createCriteria();
        criteria.andShopIdEqualTo(shopId);
        List<GoodsSpuPo> polist = null;
        try {
            polist = goodsSpuMapper.selectByExample(example);
        } catch (Exception e) {
            logger.debug("other sql exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        List<Long> idList = null;
        if(polist!=null) {
            for(GoodsSpuPo p : polist){
                idList.add(p.getId());
            }
        }
        return new ReturnObject<>(idList);

    }

    /**
     * 获取spu对应的运费模板id
     * @param id
     * @return  freightModelId
     * @author 24320182203254 秦楚彦
     * Created at 2020/12/13 17：51
     */
    public ReturnObject<Long> getFreightIdBySpuId(Long id) {
        GoodsSpuPo po=goodsSpuMapper.selectByPrimaryKey(id);
        if (po == null)
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        return new ReturnObject<>(po.getFreightId());
    }
}
