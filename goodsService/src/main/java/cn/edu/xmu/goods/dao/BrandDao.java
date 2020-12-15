package cn.edu.xmu.goods.dao;

import cn.edu.xmu.goods.mapper.BrandPoMapper;
import cn.edu.xmu.goods.mapper.GoodsSpuPoMapper;
import cn.edu.xmu.goods.model.bo.Brand;
import cn.edu.xmu.goods.model.vo.BrandVo;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.goods.model.po.*;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

/**
 * 品牌访问类
 **/
@Repository
public class BrandDao {

    private static final Logger logger = LoggerFactory.getLogger(BrandDao.class);

    @Autowired
    private BrandPoMapper brandMapper;
    @Autowired
    private GoodsSpuPoMapper goodsSpuMapper;

    public PageInfo<BrandPo> findAllBrands() {
        BrandPoExample example = new BrandPoExample();
        BrandPoExample.Criteria criteria = example.createCriteria();

        List<BrandPo> brands = brandMapper.selectByExample(example);

        logger.debug("findBrandById: retBrands = "+brands);

        return new PageInfo<>(brands);
    }

    public ReturnObject<Object> modifyBrandByVo(Long id, BrandVo brandVo) {
        BrandPo orig = brandMapper.selectByPrimaryKey(id);
        if (orig == null ) {
            logger.info("品牌不存在或已被删除：id = " + id);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }

        Brand brand = new Brand(orig);
        BrandPo po = brand.createUpdatePo(brandVo);


        // 更新数据库
        ReturnObject<Object> retObj;
        int ret;
        try {
            ret = brandMapper.updateByPrimaryKeySelective(po);
        } catch (DataAccessException e) {
            // 如果发生 Exception，判断是名称重复，还是其他错误
            if (Objects.requireNonNull(e.getMessage()).contains("brand_name_uindex")) {
                logger.info("品牌名称重复：" + brandVo.getName());
                retObj = new ReturnObject<>(ResponseCode.BRAND_NAME_SAME, String.format("品牌名称已存在"));

            }

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
            logger.info("品牌不存在或已被删除：id = " + id);
            retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        } else {
            logger.info("品牌 id = " + id + " 的资料已更新");
            retObj = new ReturnObject<>();
        }
        return retObj;
    }

    public ReturnObject<Brand> getBrandById(Long id) {
        BrandPo brandPo = brandMapper.selectByPrimaryKey(id);
        if (brandPo == null) {
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        Brand brand = new Brand(brandPo);
        return new ReturnObject<>(brand);
    }


    /**
     * 品牌上传图片
     * @param brand
     * @return ReturnObject
     */
    public ReturnObject uploadBrandImg(Brand brand)
    {
        ReturnObject returnObject;
        BrandPo newBrandPo = new BrandPo();
        newBrandPo.setId(brand.getId());
        newBrandPo.setImageUrl(brand.getImageUrl());
        int ret = brandMapper.updateByPrimaryKeySelective(newBrandPo);
        if (ret == 0) {
            logger.debug("uploadBrandImg: update fail. brand id: " + brand.getId());
            returnObject = new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
        } else {
            logger.debug("uploadBrandImg: update brand success : " + brand);
            returnObject = new ReturnObject();
        }
        return returnObject;
    }

    public ReturnObject<Object> physicallyDeleteBrand(Long id) {
        ReturnObject<Object> retObj;
        int ret = brandMapper.deleteByPrimaryKey(id);
        if (ret == 0) {
            logger.info("品牌不存在或已被删除：id = " + id);
            retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        } else {
            //将品牌下的商品变成没有品牌的商品
            GoodsSpuPoExample example = new GoodsSpuPoExample();
            GoodsSpuPoExample.Criteria criteria = example.createCriteria();
            criteria.andBrandIdEqualTo(id);
            List<GoodsSpuPo> goodsSpuPos = goodsSpuMapper.selectByExample(example);
            for (GoodsSpuPo po : goodsSpuPos) {
                po.setBrandId((long)0);
                goodsSpuMapper.updateByPrimaryKeySelective(po);
            }

            logger.info("品牌 id = " + id + " 已被永久删除");
            retObj = new ReturnObject<>();
        }
        return retObj;
    }

    public void initialize() throws Exception {
        //初始化brand
        BrandPoExample example = new BrandPoExample();
        BrandPoExample.Criteria criteria = example.createCriteria();
        //criteria.andSignatureIsNull();

        List<BrandPo> brandPos = brandMapper.selectByExample(example);

        for (BrandPo po : brandPos) {
            BrandPo newPo = new BrandPo();
            newPo.setImageUrl(po.getImageUrl());
            newPo.setId(po.getId());
            newPo.setName(po.getName());
            newPo.setDetail(po.getDetail());
            brandMapper.updateByPrimaryKeySelective(newPo);
        }


    }

    /**
     * description: insertBrand
     * version: 1.0 
     * date: 2020/12/2 19:05
     * author: 张悦 
     * 
     * @param brand
     * @return cn.edu.xmu.ooad.util.ReturnObject<cn.edu.xmu.goods.model.bo.Brand>
     */ 
    public ReturnObject<Brand> insertBrand(Brand brand) {
        BrandPo brandPo = brand.gotBrandPo();
        ReturnObject<Brand> retObj = null;
        try{
            int ret = brandMapper.insertSelective(brandPo);
            if (ret == 0) {
                //插入失败
                logger.debug("insertBrand: insert brand fail " + brandPo.toString());
                retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("新增失败：" + brandPo.getName()));
            } else {
                //插入成功
                logger.debug("insertBrand: insert brand = " + brandPo.toString());
                brand.setId(brandPo.getId());
                retObj = new ReturnObject<>(brand);
            }
        }
        catch (DataAccessException e) {
            if (Objects.requireNonNull(e.getMessage()).contains("brand_name_uindex")) {
                //若有重复的品牌名则新增失败
                logger.debug("updateBrand: have same brand name = " + brandPo.getName());
               // retObj = new ReturnObject<>(ResponseCode.BRAND_NAME_SAME, String.format("品牌名称已存在：" + brandPo.getName()));
                retObj = new ReturnObject<>(ResponseCode.BRAND_NAME_SAME, String.format("品牌名称已存在"));

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


    public ReturnObject updateBrandImage(Brand brand) {
        ReturnObject returnObject = new ReturnObject();
        BrandPo newBrandPo = new BrandPo();
        newBrandPo.setId(brand.getId());
        newBrandPo.setImageUrl(brand.getImageUrl());
        int ret = brandMapper.updateByPrimaryKeySelective(newBrandPo);
        if (ret == 0) {
            logger.debug("updateBrandImage: update fail. brand id: " + brand.getId());
            returnObject = new ReturnObject(ResponseCode.FIELD_NOTVALID);
        } else {
            logger.debug("updateBrandImage: update brand success : " + brand.toString());
            returnObject = new ReturnObject();
        }
        return returnObject;
    }
}
    

