package cn.edu.xmu.goods.dao;

import cn.edu.xmu.goods.mapper.GoodsSkuPoMapper;
import cn.edu.xmu.goods.mapper.GoodsSpuPoMapper;
import cn.edu.xmu.goods.model.bo.GoodsSku;
import cn.edu.xmu.goods.model.po.*;
import cn.edu.xmu.goods.model.vo.GoodsSkuRetVo;
import cn.edu.xmu.goods.model.vo.GoodsSkuVo;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public class GoodsDao {

    private static final Logger logger = LoggerFactory.getLogger(GoodsDao.class);
    @Autowired
    private GoodsSkuPoMapper skuMapper;
    @Autowired
    private GoodsSpuPoMapper spuMapper;

    public void initialize() throws Exception {
        //初始化sku
        GoodsSkuPoExample example = new GoodsSkuPoExample();
        GoodsSkuPoExample.Criteria criteria = example.createCriteria();

        List<GoodsSkuPo> skuPos = skuMapper.selectByExample(example);

        for (GoodsSkuPo po : skuPos) {
            GoodsSkuPo newPo = new GoodsSkuPo();
            newPo.setSkuSn(po.getSkuSn());
            newPo.setGoodsSpuId(po.getGoodsSpuId());
            newPo.setId(po.getId());
            skuMapper.updateByPrimaryKeySelective(newPo);
        }
        //初始化spu
        GoodsSpuPoExample example1 = new GoodsSpuPoExample();
        GoodsSpuPoExample.Criteria criteria1 = example1.createCriteria();

        List<GoodsSpuPo> spuPos = spuMapper.selectByExample(example1);

        for (GoodsSpuPo po : spuPos) {
            GoodsSpuPo newPo = new GoodsSpuPo();
            newPo.setId(po.getId());
            newPo.setGoodsSn(po.getGoodsSn());
            newPo.setShopId(po.getShopId());
            spuMapper.updateByPrimaryKeySelective(newPo);
        }
    }
    public ReturnObject<ShopPo> modifyShop(Long id, String name)
    {
        return new ReturnObject<>(ResponseCode.OK);
    }

    /**
     * 查询SKU
     * @param shopId
     * @param skuSn
     * @param spuId
     * @param spuSn
     * @param page
     * @param pageSize
     * @return PageInfo<GoodsSkuPo>
     */
    public PageInfo<GoodsSkuPo> getSkuList(Long shopId,String skuSn,Long spuId,String spuSn,Integer page,Integer pageSize)
    {
        GoodsSkuPoExample skuExample=new GoodsSkuPoExample();
        GoodsSkuPoExample.Criteria skuCriteria=skuExample.createCriteria();
        if(skuSn!=null&&!skuSn.isBlank())skuCriteria.andSkuSnEqualTo(skuSn);
        if(spuId!=null)skuCriteria.andGoodsSpuIdEqualTo(spuId);
        List<GoodsSkuPo> skuPos=new ArrayList<>();
        PageHelper.startPage(page,pageSize);
        logger.debug("page="+page+" pageSize="+pageSize);
        if((spuSn!=null&&!spuSn.isBlank())||shopId!=null)
        {
            GoodsSpuPoExample spuExample=new GoodsSpuPoExample();
            GoodsSpuPoExample.Criteria spuCriteria= spuExample.createCriteria();
            if(!spuSn.isBlank())spuCriteria.andGoodsSnEqualTo(spuSn);
            if(shopId!=null)spuCriteria.andShopIdEqualTo(shopId);
            List<GoodsSpuPo> spuPos=spuMapper.selectByExample(spuExample);
            for (int i=0;i<spuPos.size();++i)
            {
                skuCriteria.andGoodsSpuIdEqualTo(spuPos.get(i).getId());
                if(i==0)skuPos=skuMapper.selectByExample(skuExample);
                else skuPos.addAll(skuMapper.selectByExample(skuExample));
            }
        }
        else skuPos=skuMapper.selectByExample(skuExample);
        return new PageInfo<>(skuPos);
    }

    /**
     * 获得sku的详细信息
     * @param id
     * @return GoodsSkuPo
     */
    public GoodsSkuPo getSku(Long id)
    {
        return skuMapper.selectByPrimaryKey(id);
    }

    /**
     * sku上传图片
     * @param sku
     * @return ReturnObject
     */
    public ReturnObject uploadSkuImg(GoodsSku sku)
    {
        ReturnObject returnObject;
        GoodsSkuPo newSkuPo = new GoodsSkuPo();
        newSkuPo.setId(sku.getId());
        newSkuPo.setImageUrl(sku.getImageUrl());
        int ret = skuMapper.updateByPrimaryKeySelective(newSkuPo);
        if (ret == 0) {
            logger.debug("uploadSkuImg: update fail. sku id: " + sku.getId());
            returnObject = new ReturnObject(ResponseCode.FIELD_NOTVALID);
        } else {
            logger.debug("uploadSkuImg: update sku success : " + sku);
            returnObject = new ReturnObject();
        }
        return returnObject;
    }

    /**
     * 管理员或店家逻辑删除SKU
     * @param shopId
     * @param id
     * @return ReturnObject
     */
    public ReturnObject logicalDelete(Long shopId, Long id)
    {
        GoodsSkuPo skuPo=skuMapper.selectByPrimaryKey(id);
        if(skuPo==null)return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        GoodsSpuPo spuPo=spuMapper.selectByPrimaryKey(skuPo.getGoodsSpuId());
        if(spuPo.getShopId()==shopId)
        {
            int ret=skuMapper.updateByPrimaryKey(skuPo);
            if(ret==0)
            {
                logger.debug("logicalDelete:update fail.sku id="+id);
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            }
            else return new ReturnObject();
        }
        else return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
    }

    public ReturnObject modifySku(Long shopId, GoodsSku sku)
    {
        GoodsSkuPo selecctSkuPo=skuMapper.selectByPrimaryKey(sku.getId());
        GoodsSpuPoExample spuPoExample=new GoodsSpuPoExample();
        GoodsSpuPoExample.Criteria criteria1=spuPoExample.createCriteria();
        criteria1.andShopIdEqualTo(shopId);
        criteria1.andIdEqualTo(selecctSkuPo.getGoodsSpuId());
        List<GoodsSpuPo> spuPos=spuMapper.selectByExample(spuPoExample);
        if(spuPos.size()==0)return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST,String.format("skuId不存在："+sku.getId()));
        GoodsSkuPo skuPo=sku.getGoodsSkuPo();
        try{
            int ret = skuMapper.updateByPrimaryKeySelective(skuPo);
            if (ret == 0)
            {
                //修改失败
                logger.debug("modifySku: update sku fail : " + skuPo.toString());
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("skuId不存在：" + skuPo.getId()));
            }
            else {
                //修改成功
                logger.debug("modifySku: update sku = " + skuPo.toString());
                return new ReturnObject<>();
            }
        }
        catch (DataAccessException e)
        {
            if (Objects.requireNonNull(e.getMessage()).contains("auth_role.auth_role_name_uindex"))
            {
                //若有重复的角色名则修改失败
                logger.debug("modifySku: have same sku name = " + skuPo.getName());
                return new ReturnObject<>(ResponseCode.ROLE_REGISTERED, String.format("角色名重复：" + skuPo.getName()));
            }
            else {
                // 其他数据库错误
                logger.debug("other sql exception : " + e.getMessage());
                return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
            }
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
    }
}
