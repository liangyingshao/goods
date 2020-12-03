package cn.edu.xmu.goods.dao;

import cn.edu.xmu.goods.mapper.BrandPoMapper;
import cn.edu.xmu.goods.model.bo.Brand;
import cn.edu.xmu.goods.model.vo.BrandVo;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.ooad.util.encript.AES;
import cn.edu.xmu.ooad.util.encript.SHA256;
import cn.edu.xmu.goods.mapper.*;
import cn.edu.xmu.goods.model.bo.Brand;
import cn.edu.xmu.goods.model.po.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * 品牌访问类
 * @author Ming Qiu
 * createdBy Ming Qiu 2020/11/02 13:57
 * modifiedBy 王纬策 2020/11/7 19:20
 **/
@Repository
public class BrandDao {

    private static final Logger logger = LoggerFactory.getLogger(BrandDao.class);

    @Autowired
    private BrandPoMapper brandMapper;

    //@Autowired
    //private RedisTemplate<String, Serializable> redisTemplate;

    /**
     * 获取所有品牌信息
     * @author XQChen
     * @return List<BrandPo> 品牌列表
     */
    public PageInfo<BrandPo> findAllBrands() {
        BrandPoExample example = new BrandPoExample();
        BrandPoExample.Criteria criteria = example.createCriteria();

        List<BrandPo> brands = brandMapper.selectByExample(example);

        logger.debug("findBrandById: retBrands = "+brands);

        return new PageInfo<>(brands);
    }

    /* auth009 */

    /**
     * 根据 id 修改品牌信息
     *
     * @param brandVo 传入的 Brand 对象
     * @return 返回对象 ReturnObj
     * @author 19720182203919 李涵
     * Created at 2020/11/4 20:30
     * Modified by 19720182203919 李涵 at 2020/11/5 10:42
     */
    public ReturnObject<Object> modifyBrandByVo(Long id, BrandVo brandVo) {
        // 查询密码等资料以计算新签名
        BrandPo orig = brandMapper.selectByPrimaryKey(id);
        // 不修改已被逻辑废弃的账户
        if (orig == null ) {
            logger.info("品牌不存在或已被删除：id = " + id);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }

        // 构造 Brand 对象以计算签名
        Brand brand = new Brand(orig);
        BrandPo po = brand.createUpdatePo(brandVo);
//
//        // 将更改的联系方式 (如发生变化) 的已验证字段改为 false
//        if (brandVo.getEmail() != null && !brandVo.getEmail().equals(brand.getEmail())) {
//            po.setEmailVerified((byte) 0);
//        }
//        if (brandVo.getMobile() != null && !brandVo.getMobile().equals(brand.getMobile())) {
//            po.setMobileVerified((byte) 0);
//        }

        // 更新数据库
        ReturnObject<Object> retObj;
        int ret;
        try {
            ret = brandMapper.updateByPrimaryKeySelective(po);
        } catch (DataAccessException e) {
            // 如果发生 Exception，判断是名称重复，还是其他错误
            if (Objects.requireNonNull(e.getMessage()).contains("brand_name_uindex")) {
                logger.info("品牌名称重复：" + brandVo.getName());
                retObj = new ReturnObject<>(ResponseCode.BRAND_NAME_SAME);
            }
//            else if (e.getMessage().contains("auth_brand.auth_brand_email_uindex")) {
//                logger.info("邮箱重复：" + brandVo.getEmail());
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
//
//    /**
//     * 获得brand的详细信息
//     * @param id
//     * @return BrandPo
//     */
//    public BrandPo getBrand(Long id)
//    {
//        return brandMapper.selectByPrimaryKey(id);
//    }

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

    /**
     * (物理) 删除品牌
     *
     * @param id 品牌 id
     * @return 返回对象 ReturnObj
     * @author 19720182203919 李涵
     * Created at 2020/11/4 20:30
     * Modified by 19720182203919 李涵 at 2020/11/5 10:42
     */
    public ReturnObject<Object> physicallyDeleteBrand(Long id) {
        ReturnObject<Object> retObj;
        int ret = brandMapper.deleteByPrimaryKey(id);
        if (ret == 0) {
            logger.info("品牌不存在或已被删除：id = " + id);
            retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        } else {
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
//            newPo.setPassword(AES.encrypt(po.getPassword(), Brand.AESPASS));
//            newPo.setEmail(AES.encrypt(po.getEmail(), Brand.AESPASS));
//            newPo.setMobile(AES.encrypt(po.getMobile(), Brand.AESPASS));

//            newPo.setName(AES.encrypt(po.getName(), Brand.AESPASS));
//            newPo.setId(po.getId());

//            StringBuilder signature = Common.concatString("-", po.getBrandName(), newPo.getPassword(),
//                    newPo.getMobile(), newPo.getEmail(), po.getOpenId(), po.getState().toString(), po.getDepartId().toString(),
//                    po.getCreatorId().toString());
//            newPo.setSignature(SHA256.getSHA256(signature.toString()));

            brandMapper.updateByPrimaryKeySelective(newPo);
        }

//        //初始化BrandProxy
//        BrandProxyPoExample example1 = new BrandProxyPoExample();
//        BrandProxyPoExample.Criteria criteria1 = example1.createCriteria();
//        criteria1.andSignatureIsNull();
//        List<BrandProxyPo> brandProxyPos = brandProxyPoMapper.selectByExample(example1);
//
//        for (BrandProxyPo po : brandProxyPos) {
//            BrandProxyPo newPo = new BrandProxyPo();
//            newPo.setId(po.getId());
//            StringBuilder signature = Common.concatString("-", po.getBrandAId().toString(),
//                    po.getBrandBId().toString(), po.getBeginDate().toString(), po.getEndDate().toString(), po.getValid().toString());
//            String newSignature = SHA256.getSHA256(signature.toString());
//            newPo.setSignature(newSignature);
//            brandProxyPoMapper.updateByPrimaryKeySelective(newPo);
//        }
//
//        //初始化BrandRole
//        BrandRolePoExample example3 = new BrandRolePoExample();
//        BrandRolePoExample.Criteria criteria3 = example3.createCriteria();
//        criteria3.andSignatureIsNull();
//        List<BrandRolePo> brandRolePoList = brandRolePoMapper.selectByExample(example3);
//        for (BrandRolePo po : brandRolePoList) {
//            StringBuilder signature = Common.concatString("-",
//                    po.getBrandId().toString(), po.getRoleId().toString(), po.getCreatorId().toString());
//            String newSignature = SHA256.getSHA256(signature.toString());
//
//            BrandRolePo newPo = new BrandRolePo();
//            newPo.setId(po.getId());
//            newPo.setSignature(newSignature);
//            brandRolePoMapper.updateByPrimaryKeySelective(newPo);
//        }

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
                retObj = new ReturnObject<>(ResponseCode.BRAND_NAME_SAME, String.format("品牌名称已存在：" + brandPo.getName()));
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
    

