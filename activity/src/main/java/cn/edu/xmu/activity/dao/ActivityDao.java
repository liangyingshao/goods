package cn.edu.xmu.activity.dao;

import cn.edu.xmu.activity.mapper.*;
import cn.edu.xmu.activity.model.bo.*;
import cn.edu.xmu.activity.model.po.*;
import cn.edu.xmu.activity.model.po.CouponSkuPoExample;
import cn.edu.xmu.activity.model.vo.*;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.ooad.util.bloom.BloomFilterHelper;
import cn.edu.xmu.ooad.util.bloom.RedisBloomFilter;
import cn.edu.xmu.oomall.goods.model.CouponInfoDTO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Charsets;
import com.google.common.hash.Funnels;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class ActivityDao  implements InitializingBean
{
    private static final Logger logger = LoggerFactory.getLogger(ActivityDao.class);

    @Autowired
    private CouponSkuPoMapper couponSkuMapper;

    @Autowired
    private CouponActivityPoMapper activityMapper;

    @Autowired
    private CouponPoMapper couponMapper;


    @Autowired
    RedisTemplate redisTemplate;

    RedisBloomFilter bloomFilter;

    String[] fieldName;
    final String suffixName="BloomFilter";

    /**
     * 通过该参数选择是否清空布隆过滤器
     */
    private boolean reinitialize=true;


    /**
     * 初始化布隆过滤器
     * @throws Exception
     * createdBy: LiangJi3229 2020-11-10 18:41
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        BloomFilterHelper bloomFilterHelper=new BloomFilterHelper<>(Funnels.stringFunnel(Charsets.UTF_8),1000,0.02);
        fieldName=new String[]{"coupon"};
        bloomFilter=new RedisBloomFilter(redisTemplate,bloomFilterHelper);
        if(reinitialize){
            for(int i=0;i<fieldName.length;i++){
                redisTemplate.delete(fieldName[i]+suffixName);
            }
        }

    }

    /**
     * 查找布隆过滤器里是否有该用户领过该活动优惠券的记录
     * @param activityId
     * @param userId
     * @return ReturnObject
     */
    public ReturnObject checkCouponBloomFilter(Long activityId,Long userId){
        Map.Entry<Long,Long> coupon=new AbstractMap.SimpleEntry<Long,Long>(activityId,userId);
        if(bloomFilter.includeByBloomFilter("coupon"+suffixName,coupon.toString()))
            return new ReturnObject(ResponseCode.COUPON_FINISH);
        return null;
    }

    /**
     * 由属性名及属性值设置相应布隆过滤器
     * @param name 属性名
     * @param po po对象
     * createdBy: LiangJi3229 2020-11-10 18:41
     */
    public void setBloomFilterByName(String name,CouponPo po) {
        try {
            Field field = CouponPo.class.getDeclaredField(name);
            Method method=po.getClass().getMethod("get"+name.substring(0,1).toUpperCase()+name.substring(1));
            logger.debug("add value "+method.invoke(po)+" to "+field.getName()+suffixName);
            bloomFilter.addByBloomFilter(field.getName()+suffixName,method.invoke(po));
        }
        catch (Exception ex){
            logger.error("Exception happened:"+ex.getMessage());
        }
    }

    /**
     * 向布隆过滤器添加coupon的内容
     * @param activityId
     * @param userId
     */
    public void setBloomFilterOfCoupon(Long activityId,Long userId)
    {
        Map.Entry<Long,Long> coupon=new AbstractMap.SimpleEntry<Long,Long>(activityId,userId);
        bloomFilter.addByBloomFilter("coupon"+suffixName,coupon.toString());
    }

    public void initialize() throws Exception {
        //初始化couponSku
        CouponSkuPoExample example = new CouponSkuPoExample();
        CouponSkuPoExample.Criteria criteria = example.createCriteria();

        List<CouponSkuPo> couponSkuPos = couponSkuMapper.selectByExample(example);

        for (CouponSkuPo po : couponSkuPos) {
            CouponSkuPo newPo = new CouponSkuPo();
            newPo.setActivityId(po.getActivityId());
            newPo.setSkuId(po.getSkuId());
            newPo.setId(po.getId());
            couponSkuMapper.updateByPrimaryKeySelective(newPo);
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

        //初始化coupon
        CouponPoExample couponExample=new CouponPoExample();
        CouponPoExample.Criteria couponCriteria=couponExample.createCriteria();
        List<CouponPo> couponPos=couponMapper.selectByExample(couponExample);
        for(CouponPo po:couponPos)
        {
            CouponPo newPo=new CouponPo();
            newPo.setActivityId(po.getActivityId());
            newPo.setCustomerId(po.getCustomerId());
            newPo.setId(po.getId());
            couponMapper.updateByPrimaryKeySelective(newPo);
        }
    }

    /**
     * 查看优惠活动中的商品
     * @param id
     * @return PageInfo<CouponSku>
     */
    public List<CouponSkuPo> getCouponSkuList(Long id)
    {
//        GoodsSkuPo skuPo;
//        GoodsSku sku;
        //根据活动id得到SKU的id列表，返回给service层，service根据sku的id调用远程服务得到{name,id}
//        PageHelper.startPage(page,pageSize);
//        logger.debug("page="+page+" pageSize="+pageSize);
//        CouponSkuPoExample couponSkuExample=new CouponSkuPoExample();
//        CouponSkuPoExample.Criteria couponSpuCriteria =couponSkuExample.createCriteria();
//        couponSpuCriteria.andActivityIdEqualTo(id);
//        List<CouponSkuPo>couponSkuPos=couponSkuMapper.selectByExample(couponSkuExample);
//        List<GoodsSkuCouponRetVo>skuCouponRetVos=new ArrayList<>();
//        for(CouponSkuPo couponSkuPo:couponSkuPos)
//        {
//            skuPo=skuMapper.selectByPrimaryKey(couponSkuPo.getSkuId());
//            sku=new GoodsSku(skuPo);
//            GoodsSkuCouponRetVo retVo=new GoodsSkuCouponRetVo();
//            retVo.set(sku);
//            skuCouponRetVos.add(retVo);
//        }
        CouponSkuPoExample example = new CouponSkuPoExample();
        CouponSkuPoExample.Criteria criteria = example.createCriteria();
        criteria.andActivityIdEqualTo(id);
        List<CouponSkuPo> list = couponSkuMapper.selectByExample(example);
        return list;
    }

    /**
     * 管理员为己方某优惠券活动新增限定范围
     * @param shopId
     * @param id
     * @param couponSkus
     * @return CouponSkuRetVo
     */
    public ReturnObject createCouponSkus(Long shopId, Long id, List<CouponSku> couponSkus) {
        //活动存在
        CouponActivityPo activityPo = activityMapper.selectByPrimaryKey(id);
        if (activityPo == null) return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);

        //活动和shopId匹配
        if(activityPo.getShopId()!=shopId)return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);

        //对每个SKU进行判断、添加
        for(CouponSku couponSku:couponSkus)
        {

            //活动“待上线”
            if (activityPo.getBeginTime().isAfter(LocalDateTime.now())//考虑到惰性更新状态【待上线】->【进行中】的情况
                    && CouponActivity.DatabaseState.getTypeByCode(activityPo.getState().intValue()).equals(CouponActivity.DatabaseState.EXECUTABLE))//【可执行】
            {
                //之前没有添加过该SKU
                CouponSkuPoExample alreadyExample=new CouponSkuPoExample();
                CouponSkuPoExample.Criteria alreadyCriteria=alreadyExample.createCriteria();
                alreadyCriteria.andSkuIdEqualTo(couponSku.getSkuId());
                alreadyCriteria.andActivityIdEqualTo(id);
                List<CouponSkuPo> alreadyPos=couponSkuMapper.selectByExample(alreadyExample);
                if(alreadyPos!=null&&alreadyPos.size()>0)return new ReturnObject(ResponseCode.ACTIVITYALTER_INVALID);

                CouponSkuPo couponSkuPo = couponSku.getCouponSkuPo();
                couponSkuPo.setActivityId(id);
                couponSkuPo.setGmtCreate(LocalDateTime.now());
                couponSkuPo.setGmtModified(LocalDateTime.now());
                try {
                    int ret = couponSkuMapper.insert(couponSkuPo);
                    if (ret == 0) {
                        //插入失败
                        logger.debug("createCouponSpu: insert couponSku fail : " + couponSkuPo.toString());
                        return new ReturnObject<>(ResponseCode.FIELD_NOTVALID, String.format("couponSpu字段不合法：" + couponSkuPo.toString()));
                    } else {
                        //插入成功
                        logger.debug("createCouponSku: insert couponSku = " + couponSkuPo.toString());
                        //检验
                        CouponSkuPoExample couponSpuExample = new CouponSkuPoExample();
                        CouponSkuPoExample.Criteria couponSpuCriteria = couponSpuExample.createCriteria();
                        couponSpuCriteria.andActivityIdEqualTo(couponSkuPo.getActivityId());
                        couponSpuCriteria.andSkuIdEqualTo(couponSkuPo.getSkuId());
                        List<CouponSkuPo> checkPos = couponSkuMapper.selectByExample(couponSpuExample);
                        if (checkPos.size() == 0)
                            return new ReturnObject<>(ResponseCode.FIELD_NOTVALID, String.format("couponSku字段不合法：" + couponSkuPo.toString()));
                    }
                } catch (DataAccessException e) {
                    // 其他数据库错误
                    logger.debug("other sql exception : " + e.getMessage());
                    return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
                } catch (Exception e) {
                    // 其他Exception错误
                    logger.error("other exception : " + e.getMessage());
                    return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
                }
            } else return new ReturnObject<>(ResponseCode.COUPONACT_STATENOTALLOW);
        }
        return new ReturnObject();
    }

    /**
     * 店家删除己方某优惠券活动的某限定范围
     * @param shopId
     * @param id
     * @return ReturnObject
     */
    public ReturnObject deleteCouponSku(Long shopId, Long id)
    {
        //CouponSku存在
        CouponSkuPo couponSkuPo=couponSkuMapper.selectByPrimaryKey(id);
        if(couponSkuPo==null)return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);

        //shopId和CouponSku匹配
        CouponActivityPo activityPo= activityMapper.selectByPrimaryKey(couponSkuPo.getActivityId());
        if(activityPo.getShopId()!=shopId)return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);

        //活动"未上线"
        if(activityPo.getBeginTime().isAfter(LocalDateTime.now())//未开始
                && CouponActivity.DatabaseState.getTypeByCode(activityPo.getState().intValue()).equals(CouponActivity.DatabaseState.EXECUTABLE))//【可执行】
        {
            try{
                int ret=couponSkuMapper.deleteByPrimaryKey(id);
                if(ret==0){
                    //删除失败
                    logger.debug("deleteCouponSpu: delete couponSpu fail : " + couponSkuPo.toString());
                    return new ReturnObject<>(ResponseCode.FIELD_NOTVALID, String.format("couponSpu字段不合法：" + couponSkuPo.toString()));
                }
                else {
                    //删除成功
                    logger.debug("deleteCouponSpu: delete couponSpu = " + couponSkuPo.toString());
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
     * 买家查看优惠券列表
     * @param userId
     * @param state
     * @param page
     * @param pageSize
     * @return PageInfo<CouponRetVo>
     */
    public PageInfo<CouponRetVo> showCoupons(Long userId, Integer state, Integer page, Integer pageSize)
    {
        //设置查询条件
        CouponPoExample couponExample=new CouponPoExample();
        CouponPoExample.Criteria couponCriteria=couponExample.createCriteria();
        couponCriteria.andCustomerIdEqualTo(userId);
        PageHelper.startPage(page,pageSize);
        logger.debug("page="+page+" pageSize="+pageSize);
        if(Coupon.State.getTypeByCode(state).equals(Coupon.State.UNAVAILABLE))
        {
            //未达上线时间
            couponCriteria.andBeginTimeGreaterThan(LocalDateTime.now());

            //未取消
            couponCriteria.andStateNotEqualTo(Coupon.State.DISABLED.getCode().byteValue());
        }
        else if(Coupon.State.getTypeByCode(state).equals(Coupon.State.AVAILABLE))
        {
            //时间范围正确
            couponCriteria.andBeginTimeLessThanOrEqualTo(LocalDateTime.now());
            couponCriteria.andEndTimeGreaterThan(LocalDateTime.now());

            //未紧急下线、未使用
            couponCriteria.andStateNotEqualTo(Coupon.State.DISABLED.getCode().byteValue());
            couponCriteria.andStateNotEqualTo(Coupon.State.USED.getCode().byteValue());
        }
        else if(Coupon.State.getTypeByCode(state).equals(Coupon.State.USED))
            //已使用一定会改状态
            couponCriteria.andStateEqualTo(Coupon.State.USED.getCode().byteValue());
        else if(Coupon.State.getTypeByCode(state).equals(Coupon.State.DISABLED))
            //失效了会及时更改状态
            couponCriteria.andStateEqualTo(Coupon.State.DISABLED.getCode().byteValue());

        List<CouponPo> couponPos= couponMapper.selectByExample(couponExample);

        //构造RetVo
        List<CouponRetVo> couponRetVos=new ArrayList<CouponRetVo>();
        CouponActivityPo activityPo;
        for(int i=0;i< couponPos.size();++i)
        {
            //coupon部分设置
            Coupon coupon=new Coupon(couponPos.get(i));
            CouponRetVo retVo=new CouponRetVo();
            retVo.set(coupon);

            //activity部分设置
            activityPo= activityMapper.selectByPrimaryKey(couponPos.get(i).getActivityId());
            CouponActivityByCouponRetVo activityRetVo=new CouponActivityByCouponRetVo();
            CouponActivity activity=new CouponActivity(activityPo);
            activityRetVo.set(activity);
            retVo.setActivity(activityRetVo);

            //添加
            couponRetVos.add(retVo);
        }
        return new PageInfo<>(couponRetVos);
    }

    /**
     * 买家使用自己某优惠券
     * @param userId
     * @param id
     * @return ReturnObject
     */
    public ReturnObject useCoupon(Long userId, Long id)
    {
        //coupon存在
        CouponPo couponPo=couponMapper.selectByPrimaryKey(id);
        if(couponPo==null)return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);

        //在用户名下
        if(couponPo.getCustomerId()!=userId)return new ReturnObject(ResponseCode.RESOURCE_ID_OUTSCOPE);

        if(couponPo.getBeginTime().isBefore(LocalDateTime.now())&&couponPo.getEndTime().isAfter(LocalDateTime.now())//在进行中范围
                &&!Coupon.State.getTypeByCode(couponPo.getState().intValue()).equals(Coupon.State.DISABLED)//未失效
                &&!Coupon.State.getTypeByCode(couponPo.getState().intValue()).equals(Coupon.State.USED))//未使用
        {
            //尝试更改状态
            couponPo.setState(Coupon.State.USED.getCode().byteValue());
            couponPo.setGmtModified(LocalDateTime.now());
            try{
                int ret=couponMapper.updateByPrimaryKeySelective(couponPo);
                if(ret==0){
                    //更新失败
                    logger.debug("useCoupon: update coupon fail : " + couponPo.toString());
                    return new ReturnObject<>(ResponseCode.FIELD_NOTVALID, String.format("coupon字段不合法：" + couponPo.toString()));
                }
                else {
                    //更新成功
                    logger.debug("useCoupon: update coupon = " + couponPo.toString());
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
        else return new ReturnObject(ResponseCode.COUPON_STATENOTALLOW);
    }

    //据说已废弃
    /**
     * 买家删除自己某优惠券
     * @param userId
     * @param id
     * @return ReturnObject
     */
    public ReturnObject deleteCoupon(Long userId, Long id)
    {
        //coupon存在
        CouponPo couponPo=couponMapper.selectByPrimaryKey(id);
        if(couponPo==null)return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);

        //在用户名下
        if(couponPo.getCustomerId()!=userId)return new ReturnObject(ResponseCode.RESOURCE_ID_OUTSCOPE);

        //USED状态的优惠券不能删除
        if(!Coupon.State.getTypeByCode(couponPo.getState().intValue()).equals(Coupon.State.USED))
        {
            //没有对应的属性，我先暂时设置为将state改为DISABLED
            couponPo.setState(Coupon.State.DISABLED.getCode().byteValue());
            try{
                int ret=couponMapper.updateByPrimaryKeySelective(couponPo);
                if(ret==0){
                    //删除失败
                    logger.debug("deleteCoupon: delete coupon fail : " + couponPo.toString());
                    return new ReturnObject<>(ResponseCode.FIELD_NOTVALID, String.format("coupon字段不合法：" + couponPo.toString()));
                }
                else {
                    //删除成功
                    logger.debug("deleteCoupon: delete coupon = " + couponPo.toString());
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
        else return new ReturnObject(ResponseCode.COUPON_STATENOTALLOW);
    }

    /**
     * 买家领取活动优惠券
     * @param userId
     * @param id
     * @return ReturnObject<CouponNewRetVo>
     */
    public ReturnObject<List<CouponNewRetVo>> getCoupon(Long userId, Long id)
    {
        CouponActivityPo activityPo=activityMapper.selectByPrimaryKey(id);

        //活动不存在
        if(activityPo==null)return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);

        //状态为OFFLINE
        if(CouponActivity.DatabaseState.getTypeByCode(activityPo.getState().intValue()).equals(CouponActivity.DatabaseState.CANCELED))
            return new ReturnObject<>(ResponseCode.COUPON_END);

        //状态为FINISHED
        //已达结束时间
        if(LocalDateTime.now().isAfter(activityPo.getEndTime()))
            return new ReturnObject<>(ResponseCode.COUPON_END);

        //活动尚不能领券（含TO_BE_ONLINE和ONLINE的couponTime之前）
        if(activityPo.getCouponTime().isAfter(LocalDateTime.now()))
            return new ReturnObject<>(ResponseCode.COUPON_NOTBEGIN);

        //活动进行中
        //先到bloom过滤器里查询未果
        ReturnObject returnObject=checkCouponBloomFilter(id,userId);
        if(returnObject.getCode().equals(ResponseCode.COUPON_FINISH))return returnObject;

        //查询优惠券发放情况
        CouponPoExample alreadyExample=new CouponPoExample();
        CouponPoExample.Criteria alreadyCriteria= alreadyExample.createCriteria();
        alreadyCriteria.andActivityIdEqualTo(id);
        //每人限量模式
        if(CouponActivity.Type.getTypeByCode(activityPo.getQuantitiyType().intValue()).equals(CouponActivity.Type.LIMIT_PER_PERSON))
            alreadyCriteria.andCustomerIdEqualTo(userId);
        List<CouponPo> alreadyPos=couponMapper.selectByExample(alreadyExample);
        //券已领罄
        if(alreadyPos.size()==activityPo.getQuantity()//总量控制模式下券已发完或每人限量模式下该用户领的券已达上限
                ||(CouponActivity.Type.getTypeByCode(activityPo.getQuantitiyType().intValue()).equals(CouponActivity.Type.LIMIT_TOTAL_NUM)&&alreadyPos.size()>0))//总量控制模式下该用户已领过券
        {
            setBloomFilterOfCoupon(id,userId);
            return new ReturnObject<>(ResponseCode.COUPON_FINISH);
        }

        //可领券，设置券属性
        List<CouponNewRetVo>retVos=new ArrayList<>();
        for(int i=0;i<(CouponActivity.Type.getTypeByCode(activityPo.getQuantitiyType().intValue()).equals(CouponActivity.Type.LIMIT_PER_PERSON)?activityPo.getQuantity():1);i++)
        {
            CouponPo newPo = new CouponPo();
            newPo.setCustomerId(userId);
            newPo.setActivityId(id);
            newPo.setCouponSn(Common.genSeqNum());
            newPo.setGmtCreate(LocalDateTime.now());
            newPo.setGmtModified(LocalDateTime.now());
            newPo.setName(activityPo.getName());
            newPo.setState(Coupon.State.AVAILABLE.getCode().byteValue());
            if (activityPo.getValidTerm() == 0)//与活动同时
            {
                newPo.setBeginTime(activityPo.getBeginTime());
                newPo.setEndTime(activityPo.getEndTime());
            } else//自领券起
            {
                newPo.setBeginTime(LocalDateTime.now());
                newPo.setEndTime(LocalDateTime.now().plusDays(activityPo.getValidTerm()));
            }

            try {
                int ret = couponMapper.insertSelective(newPo);
                if (ret == 0) {
                    //插入失败
                    logger.debug("getCoupon: insert coupon fail : " + newPo.toString());
                    return new ReturnObject<>(ResponseCode.FIELD_NOTVALID, String.format("coupon字段不合法：" + newPo.toString()));
                } else {
                    //插入成功
                    logger.debug("getCoupon: insert coupon = " + newPo.toString());

                    //检验
                    CouponPoExample checkExample = new CouponPoExample();
                    CouponPoExample.Criteria checkCriteria = checkExample.createCriteria();
                    checkCriteria.andActivityIdEqualTo(newPo.getActivityId());
                    checkCriteria.andCustomerIdEqualTo(newPo.getCustomerId());
                    checkCriteria.andCouponSnEqualTo(newPo.getCouponSn());
//                checkCriteria.andGmtCreateEqualTo(newPo.getGmtCreate());
//                checkCriteria.andGmtModifiedEqualTo(newPo.getGmtModified());
                    checkCriteria.andNameEqualTo(newPo.getName());
                    checkCriteria.andStateEqualTo(Coupon.State.AVAILABLE.getCode().byteValue());
                    checkCriteria.andBeginTimeEqualTo(newPo.getBeginTime());
                    checkCriteria.andEndTimeEqualTo(newPo.getEndTime());
                    List<CouponPo> couponPos = couponMapper.selectByExample(checkExample);
                    if (couponPos.size() == 0)
                        return new ReturnObject<>(ResponseCode.FIELD_NOTVALID, String.format("coupon字段不合法：" + newPo.toString()));
                    else {
                        //构造RetVo
                        CouponNewRetVo retVo = new CouponNewRetVo();
                        Coupon coupon = new Coupon(couponPos.get(0));
                        retVo.set(coupon);
                        CouponActivityByNewCouponRetVo activityRetVo = new CouponActivityByNewCouponRetVo();
                        CouponActivity activity = new CouponActivity(activityPo);
                        activityRetVo.set(activity);
                        retVo.setActivity(activityRetVo);
                        retVos.add(retVo);
                    }
                }
            } catch (DataAccessException e) {
                // 其他数据库错误
                logger.debug("other sql exception : " + e.getMessage());
                return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
            } catch (Exception e) {
                // 其他Exception错误
                logger.error("other exception : " + e.getMessage());
                return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
            }
        }
        setBloomFilterOfCoupon(id,userId);
        return new ReturnObject<List<CouponNewRetVo>>(retVos);
    }

    /**
     * 优惠券退回
     *
     * @param shopId
     * @param id
     * @return ReturnObject
     */
    public ReturnObject returnCoupon(Long shopId, Long id)
    {
        CouponPo couponPo=couponMapper.selectByPrimaryKey(id);

        //优惠券存在
        if(couponPo==null)return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);

        //是该商店下的活动
        CouponActivityPo activityPo=activityMapper.selectByPrimaryKey(couponPo.getActivityId());
        if(activityPo.getShopId()!=shopId)return new ReturnObject(ResponseCode.RESOURCE_ID_OUTSCOPE);

        //确实是之前使用了
        if(!Coupon.State.getTypeByCode(couponPo.getState().intValue()).equals(Coupon.State.USED))
            return new ReturnObject(ResponseCode.COUPON_STATENOTALLOW);

        //设置新的有效期
        LocalDateTime gmtModified=couponPo.getGmtModified();
        couponPo.setEndTime(couponPo.getEndTime().minusSeconds(couponPo.getBeginTime().getSecond()).plusSeconds(gmtModified.getSecond()));
        couponPo.setBeginTime(gmtModified);
        couponPo.setGmtModified(LocalDateTime.now());
        //设置状态
        couponPo.setState(Coupon.State.AVAILABLE.getCode().byteValue());

        //尝试修改
        try{
            int ret=couponMapper.updateByPrimaryKeySelective(couponPo);
            if(ret==0){
                //删除失败
                logger.debug("returnCoupon: update coupon fail : " + couponPo.toString());
                return new ReturnObject<>(ResponseCode.FIELD_NOTVALID, String.format("coupon字段不合法：" + couponPo.toString()));
            }
            else {
                //删除成功
                logger.debug("returnCoupon: update coupon = " + couponPo.toString());
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

    /**
     * 店家查询己方某优惠券活动详情
     * @param shopId
     * @param id
     * @return ReturnObject
     */
    public ReturnObject showCouponActivity(Long shopId, Long id) {

//        CouponActivityPo activityPo= activityMapper.selectByPrimaryKey(id);
//        if(activityPo==null)
//            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
//        if(!activityPo.getShopId().equals(shopId))
//            return new ReturnObject(ResponseCode.RESOURCE_ID_OUTSCOPE);
//
//        //查找该优惠活动所属商店
//        ShopPo shopPo=shopMapper.selectByPrimaryKey(shopId);
//        //查找创建活动管理员（待添加）
//        //CreatedBy
//        //查找修改活动管理员（待添加）
//        //ModifiedBy
//        CouponActivity couponActivity=new CouponActivity(activityPo);
//        CouponActivityVo couponActivityVo=new CouponActivityVo(couponActivity);
//        SimpleShopVo simpleShopVo=new SimpleShopVo(shopPo);
//        couponActivityVo.setShopVo(simpleShopVo);
//        //couponActivityVo.setCreatedBy();
//        //couponActivityVo.setModifiedBy();
//        return new ReturnObject<>(couponActivityVo);
        return new ReturnObject();
    }

    /**
     * 管理员新建己方优惠活动
     * @param activity
     * @return ReturnObject
     */
    public ReturnObject<CouponActivityVo> addCouponActivity(CouponActivity activity) {
        CouponActivityPo activityPo=activity.createActivityPo();
        ReturnObject<CouponActivityVo> returnObject=null;
//        try{
//            int ret = activityMapper.insertSelective(activityPo);
//            if (ret == 0) {
//                //插入失败
//                logger.debug("insertRole: insert coupon activity fail " + activityPo.toString());
//                returnObject = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("新增失败：" + activityPo.getName()));
//            } else {
//                //插入成功
//                logger.debug("insertRole: insert coupon activity = " + activityPo.toString());
//                //检验
//                CouponActivityPoExample couponActivityExample=new CouponActivityPoExample();
//                CouponActivityPoExample.Criteria couponActivityCriteria=couponActivityExample.createCriteria();
//                couponActivityCriteria.andNameEqualTo(activityPo.getName());
//                couponActivityCriteria.andShopIdEqualTo(activityPo.getShopId());
//                couponActivityCriteria.andBeginTimeEqualTo(activityPo.getBeginTime());
//                couponActivityCriteria.andEndTimeEqualTo(activityPo.getEndTime());
//                List<CouponActivityPo> checkPos=activityMapper.selectByExample(couponActivityExample);
//                if(checkPos.size()==0)return new ReturnObject<>(ResponseCode.FIELD_NOTVALID, String.format("couponSpu字段不合法：" + activityPo.toString()));
//                else{//设置RetVo
//                    CouponActivity retActivity =new CouponActivity(checkPos.get(0));
//                    CouponActivityVo retVo=new CouponActivityVo(retActivity);
//                    //设置优惠活动所属商店
//                    ShopPo shopPo=shopMapper.selectByPrimaryKey(retActivity.getShopId());
//                    SimpleShopVo simpleShopVo=new SimpleShopVo(shopPo);
//                    retVo.setShopVo(simpleShopVo);
//                    //设置创建者、修改者
//                    //couponActivityVo.setCreatedBy();
//                    //couponActivityVo.setModifiedBy();
//                    return new ReturnObject<>(retVo);
//
//                }
//
//            }
//        }
//        catch (DataAccessException e) {
//            // 其他数据库错误
//            logger.debug("other sql exception : " + e.getMessage());
//            returnObject = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
//        }
//        catch (Exception e) {
//            // 其他Exception错误
//            logger.error("other exception : " + e.getMessage());
//            returnObject = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
//        }
        return returnObject;
    }

    /**
     * 管理员修改己方优惠活动
     * @param activity
     * @return ReturnObject
     */
    public ReturnObject modifyCouponActivity(CouponActivity activity) {
        CouponActivityPo activityPo=activity.createActivityPo();
        ReturnObject returnObject=null;
        CouponActivityPoExample activityPoExample=new CouponActivityPoExample();
        CouponActivityPoExample.Criteria criteria=activityPoExample.createCriteria();
        criteria.andIdEqualTo(activity.getId());
        criteria.andShopIdEqualTo(activity.getShopId());
        criteria.andStateEqualTo((byte)0);//待修改活动必须为“可执行”
        try{
            int ret = activityMapper.updateByExampleSelective(activityPo,activityPoExample);
            if(ret==0){//修改失败
                logger.debug("updateCouponActivity fail:"+activityPo.toString());
                returnObject=new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,String.format("该优惠活动不存在"));
            }
            else{//修改成功
                logger.debug("updateCouponActivity success:"+activityPo.toString());
                returnObject =new ReturnObject<>();
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
     * 管理员下线己方优惠活动
     * @param shopId
     * @param id
     * @return ReturnObject
     */
    public ReturnObject offlineCouponActivity(Long shopId, Long id) {
        ReturnObject returnObject=null;
        CouponActivityPoExample activityPoExample=new CouponActivityPoExample();
        CouponActivityPoExample.Criteria criteria=activityPoExample.createCriteria();
        criteria.andIdEqualTo(id);
        criteria.andShopIdEqualTo(shopId);
        //criteria.andStateEqualTo((byte)0);//待取消活动必须为“可执行”
        try{
            //下线优惠活动
            List<CouponActivityPo> activityPo=activityMapper.selectByExample(activityPoExample);
            if(!activityPo.get(0).getState().equals((byte)0)){//未找到符合条件的优惠活动
//                returnObject=new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,String.format("下线优惠活动失败"));
                return returnObject=new ReturnObject<>(ResponseCode.ACTIVITYALTER_INVALID,String.format("下线优惠活动失败"));
            }

            activityPo.get(0).setState((byte)1);
            int ret = activityMapper.updateByExampleSelective(activityPo.get(0),activityPoExample);
            if(ret==0){//修改失败
                logger.debug("updateCouponActivity fail:"+activityPo.toString());
                //returnObject=new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,String.format("下线优惠活动失败"));
                returnObject=new ReturnObject<>(ResponseCode.ACTIVITYALTER_INVALID,String.format("下线优惠活动失败"));
            }
            else{//修改活动状态成功
                logger.debug("updateCouponActivity success:"+activityPo.toString());
                returnObject =new ReturnObject<>();
                //若为发券类型优惠活动，将发行未用优惠券一并下线
                if(!activityPo.get(0).getQuantity().equals(0)){
                    CouponPoExample couponPoExample=new CouponPoExample();
                    CouponPoExample.Criteria criteria1=couponPoExample.createCriteria();
                    criteria1.andActivityIdEqualTo(activityPo.get(0).getId());
                    criteria1.andStateEqualTo((byte)1);//下线状态为【可用】优惠券
                    try {
                        List<CouponPo> couponPos=couponMapper.selectByExample(couponPoExample);
                        for(int i=0;i<couponPos.size();i++){
                            //将优惠券状态设置为【失效】
                            couponPos.get(i).setState((byte)3);
                            //写回
                            couponMapper.updateByPrimaryKey(couponPos.get(i));
                        }
                        returnObject =new ReturnObject<>();
                    }
                    catch (Exception e){//数据库中无状态为【可用】优惠券，仍然下线成功
                        returnObject =new ReturnObject<>();
                    }

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

    /**
     * 查询上线优惠活动列表
     * @param shopId
     * @param timeline
     * @param page
     * @param pageSize
     * @return ReturnObject<PageInfo<CouponActivityByNewCouponRetVo>>
     */
    public ReturnObject<PageInfo<CouponActivityByNewCouponRetVo>> showActivities(Long shopId, Integer timeline, Integer page, Integer pageSize) {
        CouponActivityPoExample activityExample=new CouponActivityPoExample();
        CouponActivityPoExample.Criteria criteria=activityExample.createCriteria();
        criteria.andStateEqualTo((byte)0);//必须为可执行活动
        //设置shopId
        if(shopId!=null)
            criteria.andShopIdEqualTo(shopId);
        //设置timeline
        if(timeline==0)
            criteria.andBeginTimeGreaterThan(LocalDateTime.now());
        else if(timeline==2){
            criteria.andBeginTimeLessThanOrEqualTo(LocalDateTime.now());
            criteria.andEndTimeGreaterThanOrEqualTo(LocalDateTime.now());
        }
        else if(timeline==3)
            criteria.andEndTimeLessThan(LocalDateTime.now());
        else if(timeline==1){//明天上线的
            LocalDateTime searchTime= LocalDateTime.now();
            searchTime=searchTime.plusDays(2);
            searchTime=searchTime.minusHours(searchTime.getHour());
            searchTime=searchTime.minusMinutes(searchTime.getMinute());
            searchTime=searchTime.minusSeconds(searchTime.getSecond());
            searchTime=searchTime.minusNanos(searchTime.getNano());
            LocalDateTime searchTimeMax=searchTime;//时间段上限
            LocalDateTime searchTimeMin=searchTime.minusDays(1);//时间段下限
            criteria.andBeginTimeGreaterThanOrEqualTo(searchTimeMin);//beginTime>=明日零点
            criteria.andBeginTimeLessThan(searchTimeMax);//beginTime<后日零点
        }
        //分页查询
        PageHelper.startPage(page, pageSize);
        logger.debug("page = " + page + "pageSize = " + pageSize);
        List<CouponActivityPo> activityPos=null;
        try{
            activityPos=activityMapper.selectByExample(activityExample);
            if(activityPos.size()==0)
                return new ReturnObject<>(ResponseCode.ACTIVITY_NOTFOUND);
            List<CouponActivityByNewCouponRetVo> retList=new ArrayList<>(activityPos.size());
            for(CouponActivityPo po:activityPos){
                CouponActivity bo=new CouponActivity(po);
                CouponActivityByNewCouponRetVo vo=new CouponActivityByNewCouponRetVo();
                vo.set(bo);
                retList.add(vo);
            }
            PageInfo<CouponActivityByNewCouponRetVo>activityPage=PageInfo.of(retList);
            return new ReturnObject<>(activityPage) ;
        }
        catch (DataAccessException e){
            logger.error("selectAllRole: DataAccessException:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }

    }

    /**
     * 查询下线优惠活动列表
     * @param shopId
     * @param page
     * @param pageSize
     * @return ReturnObject<PageInfo<CouponActivityByNewCouponRetVo>>
     */
    public ReturnObject<PageInfo<CouponActivityByNewCouponRetVo>> showInvalidCouponActivities(Long shopId, Integer page, Integer pageSize) {
        CouponActivityPoExample activityExample=new CouponActivityPoExample();
        CouponActivityPoExample.Criteria criteria=activityExample.createCriteria();
        criteria.andStateEqualTo((byte)1);//必须为不可执行活动
        //设置shopId
        criteria.andShopIdEqualTo(shopId);
        //分页查询
        PageHelper.startPage(page, pageSize);
        logger.debug("page = " + page + "pageSize = " + pageSize);
        List<CouponActivityPo> activityPos=null;
        try{
            activityPos=activityMapper.selectByExample(activityExample);
            if(activityPos.size()==0)
                return new ReturnObject<>(ResponseCode.ACTIVITY_NOTFOUND);
            List<CouponActivityByNewCouponRetVo> retList=new ArrayList<>(activityPos.size());
            for(CouponActivityPo po:activityPos){
                CouponActivity bo=new CouponActivity(po);
                CouponActivityByNewCouponRetVo vo=new CouponActivityByNewCouponRetVo();
                vo.set(bo);
                retList.add(vo);
            }
            PageInfo<CouponActivityByNewCouponRetVo>activityPage=PageInfo.of(retList);
            return new ReturnObject<>(activityPage) ;
        }
        catch (DataAccessException e){
            logger.error("selectAllRole: DataAccessException:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
    }

    public List<CouponInfoDTO> getCouponInfoBySkuId(Long skuId)
    {
        CouponActivityPo activityPo;
        CouponSkuPoExample couponSkuExample=new CouponSkuPoExample();
        CouponSkuPoExample.Criteria couponSkuCriteria=couponSkuExample.createCriteria();
        couponSkuCriteria.andSkuIdEqualTo(skuId);
        List<CouponSkuPo> couponSkuPos=couponSkuMapper.selectByExample(couponSkuExample);
        List<CouponInfoDTO> couponInfoDTOs=new ArrayList<>();
        for(CouponSkuPo couponSkuPo:couponSkuPos)
        {
            activityPo=activityMapper.selectByPrimaryKey(couponSkuPo.getActivityId());
            LocalDateTime beginTime=activityPo.getBeginTime();
            LocalDateTime endTime=activityPo.getEndTime();
            if(CouponActivity.DatabaseState.getTypeByCode(activityPo.getState().intValue()).equals(CouponActivity.DatabaseState.EXECUTABLE)&&
            !beginTime.isAfter(LocalDateTime.now())&&endTime.isAfter(LocalDateTime.now()))
            {
                CouponInfoDTO couponInfoDTO=new CouponInfoDTO();
                couponInfoDTO.setBeginTime(beginTime);
                couponInfoDTO.setEndTime(endTime);
                couponInfoDTO.setId(activityPo.getId());
                couponInfoDTO.setName(activityPo.getName());
                couponInfoDTOs.add(couponInfoDTO);
            }
        }
        return couponInfoDTOs;
    }
}