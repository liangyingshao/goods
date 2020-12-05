package cn.edu.xmu.goods.dao;

import cn.edu.xmu.goods.mapper.CouponActivityPoMapper;
import cn.edu.xmu.goods.mapper.CouponSpuPoMapper;
import cn.edu.xmu.goods.mapper.GoodsSpuPoMapper;
import cn.edu.xmu.goods.mapper.ShopPoMapper;
import cn.edu.xmu.goods.model.bo.CouponActivity;
import cn.edu.xmu.goods.model.bo.CouponSpu;
import cn.edu.xmu.goods.model.bo.GoodsSku;
import cn.edu.xmu.goods.model.bo.GoodsSpu;
import cn.edu.xmu.goods.model.po.*;
import cn.edu.xmu.goods.model.po.CouponSpuPoExample;
import cn.edu.xmu.goods.model.vo.*;
import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.annotation.Depart;
import cn.edu.xmu.ooad.annotation.LoginUser;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
public class ActivityDao {
    private static final Logger logger = LoggerFactory.getLogger(ActivityDao.class);
    
    @Autowired
    private CouponSpuPoMapper couponSpuMapper;

    @Autowired
    private GoodsSpuPoMapper spuMapper;

    @Autowired
    private ShopPoMapper shopMapper;

    @Autowired
    private CouponActivityPoMapper activityMapper;

    public void initialize() throws Exception {
        //初始化couponSpu
        CouponSpuPoExample example = new CouponSpuPoExample();
        CouponSpuPoExample.Criteria criteria = example.createCriteria();

        List<CouponSpuPo> couponSpuPos = couponSpuMapper.selectByExample(example);

        for (CouponSpuPo po : couponSpuPos) {
            CouponSpuPo newPo = new CouponSpuPo();
            newPo.setActivityId(po.getActivityId());
            newPo.setSpuId(po.getSpuId());
            newPo.setId(po.getId());
            couponSpuMapper.updateByPrimaryKeySelective(newPo);
        }

        //初始化SPU
        GoodsSpuPoExample spuExample=new GoodsSpuPoExample();
        GoodsSpuPoExample.Criteria spuCriteria= spuExample.createCriteria();;
        List<GoodsSpuPo>spuPos=spuMapper.selectByExample(spuExample);
        for(GoodsSpuPo po:spuPos)
        {
            GoodsSpuPo newPo=new GoodsSpuPo();
            newPo.setShopId(po.getShopId());
            newPo.setGoodsSn(po.getGoodsSn());
            newPo.setId(po.getId());
            newPo.setBrandId(po.getBrandId());
            newPo.setCategoryId(po.getCategoryId());
            newPo.setFreightId(po.getFreightId());
            spuMapper.updateByPrimaryKeySelective(newPo);
        }

        //初始化couponActivity
        CouponActivityPoExample activityExample=new CouponActivityPoExample();
        CouponActivityPoExample.Criteria activityCriteria=activityExample.createCriteria();
        List<CouponActivityPo> activityPos=activityMapper.selectByExample(activityExample);
        for(CouponActivityPo po:activityPos)
        {
            CouponActivityPo newPo=new CouponActivityPo();
            newPo.setId(po.getId());
            newPo.setShopId(po.getShopId());
            activityMapper.updateByPrimaryKeySelective(newPo);
        }
    }

    /**
     * 查看优惠活动中的商品
     * @param id
     * @param page
     * @param pageSize
     * @return PageInfo<CouponSpu>
     */
    public PageInfo<GoodsSpuCouponRetVo> getCouponSpuList(Long id, Integer page, Integer pageSize)
    {
        GoodsSpuPo spuPo;
        GoodsSpu spu;
        PageHelper.startPage(page,pageSize);
        logger.debug("page="+page+" pageSize="+pageSize);
        CouponSpuPoExample couponSpuExample=new CouponSpuPoExample();
        CouponSpuPoExample.Criteria couponSpuCriteria =couponSpuExample.createCriteria();
        couponSpuCriteria.andActivityIdEqualTo(id);
        List<CouponSpuPo>couponSpuPos=couponSpuMapper.selectByExample(couponSpuExample);
        List<GoodsSpuCouponRetVo>spuCouponRetVos=new ArrayList<>();
        for(CouponSpuPo couponSpuPo:couponSpuPos)
        {
            spuPo=spuMapper.selectByPrimaryKey(couponSpuPo.getSpuId());
            spu=new GoodsSpu(spuPo);
            GoodsSpuCouponRetVo retVo=new GoodsSpuCouponRetVo();
            retVo.set(spu);
            spuCouponRetVos.add(retVo);
        }
        return new PageInfo<>(spuCouponRetVos);
    }

    /**
     * 管理员为己方某优惠券活动新增限定范围
     * @param shopId
     * @param couponSpu
     * @return CouponSpuRetVo
     */
    public ReturnObject<CouponSpuRetVo> createCouponSpu(Long shopId, CouponSpu couponSpu) {
        //SPU存在
        GoodsSpuPo spuPo=spuMapper.selectByPrimaryKey(couponSpu.getSpuId());
        if(spuPo==null|| GoodsSpu.SpuState.getTypeByCode(spuPo.getDisabled().intValue()).equals(GoodsSpu.SpuState.DELETED))
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);

        //SPU和shopId匹配
        if(spuPo.getShopId()!=shopId)return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);

        //活动存在
        CouponActivityPo activityPo=activityMapper.selectByPrimaryKey(couponSpu.getActivityId());
        if(activityPo==null)return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);

        //活动“待上线”
        if(activityPo.getBeginTime().isAfter(LocalDateTime.now())//考虑到惰性更新状态【待上线】->【进行中】的情况
                &&CouponActivity.State.getTypeByCode(activityPo.getState().intValue()).equals(CouponActivity.State.TO_BE_ONLINE))
        {
            CouponSpuPo couponSpuPo=couponSpu.getCouponSpuPo();
            try{
                int ret=couponSpuMapper.insert(couponSpuPo);
                if(ret==0)
                {
                    //插入失败
                    logger.debug("createCouponSpu: insert couponSpu fail : " + couponSpuPo.toString());
                    return new ReturnObject<>(ResponseCode.FIELD_NOTVALID, String.format("couponSpu字段不合法：" + couponSpuPo.toString()));
                }
                else {
                    //插入成功
                    logger.debug("createCouponSpu: insert couponSpu = " + couponSpuPo.toString());
                    //检验
                    CouponSpuPoExample couponSpuExample=new CouponSpuPoExample();
                    CouponSpuPoExample.Criteria couponSpuCriteria=couponSpuExample.createCriteria();
                    couponSpuCriteria.andActivityIdEqualTo(couponSpuPo.getActivityId());
                    couponSpuCriteria.andSpuIdEqualTo(couponSpuPo.getSpuId());
                    List<CouponSpuPo> checkPos=couponSpuMapper.selectByExample(couponSpuExample);
                    if(checkPos.size()==0)return new ReturnObject<>(ResponseCode.FIELD_NOTVALID, String.format("couponSpu字段不合法：" + couponSpuPo.toString()));
                    else {
                        //设置RetVo
                        CouponSpu retCouponSpu=new CouponSpu(checkPos.get(0));
                        CouponSpuRetVo retVo=new CouponSpuRetVo();
                        retVo.set(retCouponSpu);
                        GoodsSpu retSpu=new GoodsSpu(spuPo);
                        GoodsSpuCouponCreateRetVo createRetVo=new GoodsSpuCouponCreateRetVo();
                        createRetVo.set(retSpu);
                        return new ReturnObject<>();
                    }
                }
            }
            catch (DataAccessException e)
            {
                // 其他数据库错误
                logger.debug("other sql exception : " + e.getMessage());
                return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
            }
            catch (Exception e) {
                // 其他Exception错误
                logger.error("other exception : " + e.getMessage());
                return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
            }
        }
        else return new ReturnObject<>(ResponseCode.COUPONACT_STATENOTALLOW);
    }

    /**
     * 店家删除己方某优惠券活动的某限定范围
     * @param shopId
     * @param id
     * @return ReturnObject
     */
    public ReturnObject deleteCouponSpu(Long shopId, Long id)
    {
        //CouponSpu存在
        CouponSpuPo couponSpuPo=couponSpuMapper.selectByPrimaryKey(id);
        if(couponSpuPo==null)return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);

        //shopId和CouponSpu匹配
        CouponActivityPo activityPo= activityMapper.selectByPrimaryKey(couponSpuPo.getActivityId());
        if(activityPo.getShopId()!=shopId)return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);

        //活动"未上线"
        if(activityPo.getBeginTime().isAfter(LocalDateTime.now())//考虑到惰性更新状态【待上线】->【进行中】的情况
                && CouponActivity.State.getTypeByCode(activityPo.getState().intValue()).equals(CouponActivity.State.TO_BE_ONLINE))
        {
            try{
                int ret=couponSpuMapper.deleteByPrimaryKey(id);
                if(ret==0){
                    //删除失败
                    logger.debug("deleteCouponSpu: delete couponSpu fail : " + couponSpuPo.toString());
                    return new ReturnObject<>(ResponseCode.FIELD_NOTVALID, String.format("couponSpu字段不合法：" + couponSpuPo.toString()));
                }
                else {
                    //删除成功
                    logger.debug("deleteCouponSpu: delete couponSpu = " + couponSpuPo.toString());
                    return new ReturnObject<>();
                }
            }
            catch (DataAccessException e)
            {
                // 其他数据库错误
                logger.debug("other sql exception : " + e.getMessage());
                return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
            }
            catch (Exception e) {
                // 其他Exception错误
                logger.error("other exception : " + e.getMessage());
                return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
            }
        }
        else return new ReturnObject(ResponseCode.COUPONACT_STATENOTALLOW);
    }

    /**
     * 店家查询己方某优惠券活动详情
     * @param shopId
     * @param id
     * @return ReturnObject
     */
    public ReturnObject showCouponActivity(Long shopId, Long id) {

        CouponActivityPo activityPo= activityMapper.selectByPrimaryKey(id);
        if(activityPo==null)
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
        if(!activityPo.getShopId().equals(shopId))
            return new ReturnObject(ResponseCode.RESOURCE_ID_OUTSCOPE);

        //查找该优惠活动所属商店
        ShopPo shopPo=shopMapper.selectByPrimaryKey(shopId);
        //查找创建活动管理员（待添加）
        //CreatedBy
        //查找修改活动管理员（待添加）
        //ModifiedBy
        CouponActivity couponActivity=new CouponActivity(activityPo);
        CouponActivityVo couponActivityVo=new CouponActivityVo(couponActivity);
        SimpleShopVo simpleShopVo=new SimpleShopVo(shopPo);
        couponActivityVo.setShopVo(simpleShopVo);
        //couponActivityVo.setCreatedBy();
        //couponActivityVo.setModifiedBy();
        return new ReturnObject<>(couponActivityVo);
    }

    /**
     * 管理员新建己方优惠活动
     * @param activity
     * @return ReturnObject
     */
    public ReturnObject<CouponActivityVo> addCouponActivity(CouponActivity activity) {
        CouponActivityPo activityPo=activity.createActivityPo();
        ReturnObject<CouponActivityVo> returnObject=null;
        try{
            int ret = activityMapper.insertSelective(activityPo);
            if (ret == 0) {
                //插入失败
                logger.debug("insertRole: insert coupon activity fail " + activityPo.toString());
                returnObject = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("新增失败：" + activityPo.getName()));
            } else {
                //插入成功
                logger.debug("insertRole: insert coupon activity = " + activityPo.toString());
                //检验
                CouponActivityPoExample couponActivityExample=new CouponActivityPoExample();
                CouponActivityPoExample.Criteria couponActivityCriteria=couponActivityExample.createCriteria();
                couponActivityCriteria.andNameEqualTo(activityPo.getName());
                couponActivityCriteria.andShopIdEqualTo(activityPo.getShopId());
                couponActivityCriteria.andBeginTimeEqualTo(activityPo.getBeginTime());
                couponActivityCriteria.andEndTimeEqualTo(activityPo.getEndTime());
                List<CouponActivityPo> checkPos=activityMapper.selectByExample(couponActivityExample);
                if(checkPos.size()==0)return new ReturnObject<>(ResponseCode.FIELD_NOTVALID, String.format("couponSpu字段不合法：" + activityPo.toString()));
                else{//设置RetVo
                    CouponActivity retActivity =new CouponActivity(checkPos.get(0));
                    CouponActivityVo retVo=new CouponActivityVo(retActivity);
                    //设置优惠活动所属商店
                    ShopPo shopPo=shopMapper.selectByPrimaryKey(retActivity.getShopId());
                    SimpleShopVo simpleShopVo=new SimpleShopVo(shopPo);
                    retVo.setShopVo(simpleShopVo);
                    //设置创建者、修改者
                    //couponActivityVo.setCreatedBy();
                    //couponActivityVo.setModifiedBy();
                    return new ReturnObject<>(retVo);

                }

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
}
