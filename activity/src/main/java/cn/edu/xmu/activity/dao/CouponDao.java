package cn.edu.xmu.activity.dao;

import cn.edu.xmu.activity.mapper.*;
import cn.edu.xmu.activity.model.bo.*;
import cn.edu.xmu.activity.model.po.*;
import cn.edu.xmu.activity.model.po.CouponSkuPoExample;
import cn.edu.xmu.activity.model.vo.*;
import cn.edu.xmu.ooad.util.*;
import cn.edu.xmu.ooad.util.bloom.BloomFilterHelper;
import cn.edu.xmu.ooad.util.bloom.RedisBloomFilter;
import cn.edu.xmu.oomall.goods.model.CouponInfoDTO;
import cn.edu.xmu.oomall.goods.model.SimpleShopDTO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Charsets;
import com.google.common.hash.Funnels;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Repository
public class CouponDao implements InitializingBean
{
    private static final Logger logger = LoggerFactory.getLogger(CouponDao.class);

    @Autowired
    private MyCouponSkuPoMapper couponSkuMapper;

    @Autowired
    private CouponActivityPoMapper activityMapper;

    @Autowired
    private MyCouponPoMapper couponMapper;

    @Resource
    private RocketMQTemplate rocketMQTemplate;


    @Autowired
    RedisTemplate redisTemplate;

    RedisBloomFilter bloomFilter;

    String[] fieldName;
    final String suffixName="BloomFilter";

    /**
     * 通过该参数选择是否清空布隆过滤器
     */
    private final boolean reinitialize=true;


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
            for (String s : fieldName) {
                redisTemplate.delete(s + suffixName);
            }
        }
    }


    /**
     * 查找布隆过滤器里是否有该用户领过该活动优惠券的记录
     * @param activityId
     * @param userId
     * @return ReturnObject
     */
    public ReturnObject<ResponseCode> checkCouponBloomFilter(Long activityId,Long userId){
        Map.Entry<Long,Long> coupon=new AbstractMap.SimpleEntry<Long,Long>(activityId,userId);
        if(bloomFilter.includeByBloomFilter("coupon"+suffixName,coupon.toString()))
            return new ReturnObject<>(ResponseCode.COUPON_FINISH);
        return new ReturnObject<>();
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
        CouponActivityPo activityPo= activityMapper.selectByPrimaryKey(id);
        if(activityPo==null||!CouponActivity.DatabaseState.getTypeByCode(activityPo.getState().intValue()).equals(CouponActivity.DatabaseState.ONLINE))
            return null;

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
    public ReturnObject<List<CouponSkuRetVo>> createCouponSkus(Long shopId, Long id, List<CouponSku> couponSkus) {
        //活动存在
        CouponActivityPo activityPo = activityMapper.selectByPrimaryKey(id);
        if (activityPo == null) return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);

        //活动和shopId匹配
        if(!Objects.equals(activityPo.getShopId(), shopId))return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);

        List<CouponSkuPo>couponSkuPos=new ArrayList<>();
        //对每个SKU进行判断、添加
        for(CouponSku couponSku:couponSkus)
        {
            //【已删除】
            if(CouponActivity.DatabaseState.getTypeByCode(activityPo.getState().intValue()).equals(CouponActivity.DatabaseState.DELETED))
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);

            //【已上线】
            if(CouponActivity.DatabaseState.getTypeByCode(activityPo.getState().intValue()).equals(CouponActivity.DatabaseState.DELETED))
                return new ReturnObject<>(ResponseCode.COUPONACT_STATENOTALLOW);

            //【已下线】
            //之前没有添加过该SKU
            CouponSkuPoExample alreadyExample=new CouponSkuPoExample();
            CouponSkuPoExample.Criteria alreadyCriteria=alreadyExample.createCriteria();
            alreadyCriteria.andSkuIdEqualTo(couponSku.getSkuId());
            alreadyCriteria.andActivityIdEqualTo(id);
            List<CouponSkuPo> alreadyPos=couponSkuMapper.selectByExample(alreadyExample);
            if(alreadyPos!=null&&alreadyPos.size()>0)return new ReturnObject<>(ResponseCode.ACTIVITYALTER_INVALID);

            //设置CouponSkuPo
            CouponSkuPo couponSkuPo = couponSku.getCouponSkuPo();
            couponSkuPo.setActivityId(id);
            couponSkuPo.setGmtCreate(LocalDateTime.now());
            couponSkuPo.setGmtModified(LocalDateTime.now());
            couponSkuPos.add(couponSkuPo);
        }

        //尝试插入
        try {
            int ret = couponSkuMapper.insertSelectiveBatch(couponSkuPos);
            if (ret == 0) {
                //插入失败
                logger.debug("createCouponSpu: insert couponSkus fail : " + couponSkuPos.toString());
                return new ReturnObject<>(ResponseCode.FIELD_NOTVALID, "couponSpu字段不合法：" + couponSkuPos.toString());
            } else {
                return new ReturnObject<>();
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

    /**
     * 店家删除己方某优惠券活动的某限定范围
     * @param shopId
     * @param id
     * @return ReturnObject
     */
    public ReturnObject<ResponseCode> deleteCouponSku(Long shopId, Long id)
    {
        //CouponSku存在
        CouponSkuPo couponSkuPo=couponSkuMapper.selectByPrimaryKey(id);
        if(couponSkuPo==null)return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);

        //shopId和CouponSku匹配
        CouponActivityPo activityPo= activityMapper.selectByPrimaryKey(couponSkuPo.getActivityId());
        if(!Objects.equals(activityPo.getShopId(), shopId))return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);

        //【已删除】
        if(CouponActivity.DatabaseState.getTypeByCode(activityPo.getState().intValue()).equals(CouponActivity.DatabaseState.DELETED))
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);

        //【已上线】
        if(CouponActivity.DatabaseState.getTypeByCode(activityPo.getState().intValue()).equals(CouponActivity.DatabaseState.DELETED))
            return new ReturnObject<>(ResponseCode.COUPONACT_STATENOTALLOW);

        //【已下线】
        //尝试删除
        try{
            int ret=couponSkuMapper.deleteByPrimaryKey(id);
            if(ret==0){
                //删除失败
                logger.debug("deleteCouponSpu: delete couponSpu fail : " + couponSkuPo.toString());
                return new ReturnObject<>(ResponseCode.FIELD_NOTVALID, "couponSpu字段不合法：" + couponSkuPo.toString());
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
        for (CouponPo couponPo : couponPos) {
            //coupon部分设置
            Coupon coupon = new Coupon(couponPo);
            CouponRetVo retVo = new CouponRetVo();
            retVo.set(coupon);

            //activity部分设置
            activityPo = activityMapper.selectByPrimaryKey(couponPo.getActivityId());
            CouponActivityByCouponRetVo activityRetVo = new CouponActivityByCouponRetVo();
            CouponActivity activity = new CouponActivity(activityPo);
            activityRetVo.set(activity);
            retVo.setActivity(activityRetVo);

            //添加
            couponRetVos.add(retVo);
        }
        PageHelper.startPage(page,pageSize);
        logger.debug("page="+page+" pageSize="+pageSize);
        return new PageInfo<>(couponRetVos);
    }

    /**
     * 买家使用自己某优惠券
     * @param userId
     * @param id
     * @return ReturnObject
     */
    public ReturnObject<ResponseCode> useCoupon(Long userId, Long id)
    {
        //coupon存在
        CouponPo couponPo=couponMapper.selectByPrimaryKey(id);
        if(couponPo==null)return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);

        //在用户名下
        if(!Objects.equals(couponPo.getCustomerId(), userId))return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);

        if(!couponPo.getBeginTime().isAfter(LocalDateTime.now())&&couponPo.getEndTime().isAfter(LocalDateTime.now())//在进行中范围
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
                    return new ReturnObject<>(ResponseCode.FIELD_NOTVALID, "coupon字段不合法：" + couponPo.toString());
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
        else return new ReturnObject<>(ResponseCode.COUPON_STATENOTALLOW);
    }

    //据说已废弃
    /**
     * 买家删除自己某优惠券
     * @param userId
     * @param id
     * @return ReturnObject
     */
    public ReturnObject<ResponseCode> deleteCoupon(Long userId, Long id)
    {
        //coupon存在
        CouponPo couponPo=couponMapper.selectByPrimaryKey(id);
        if(couponPo==null)return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);

        //在用户名下
        if(!Objects.equals(couponPo.getCustomerId(), userId))return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);

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
                    return new ReturnObject<>(ResponseCode.FIELD_NOTVALID, "coupon字段不合法：" + couponPo.toString());
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
        else return new ReturnObject<>(ResponseCode.COUPON_STATENOTALLOW);
    }

    /**
     * 买家领取活动优惠券
     * @param userId
     * @param id
     * @return ReturnObject<CouponNewRetVo>
     */
    public ReturnObject<List<String>> getCoupon(Long userId, Long id)
    {
        logger.debug("getCoupon:userId="+userId+" activityId="+id);
        int quantity;
        String key="ca_"+id;//redis里存的活动对应的key
        CouponActivityPo activityPo=activityMapper.selectByPrimaryKey(id);
        CouponActivity.DatabaseState activityState=CouponActivity.DatabaseState.getTypeByCode(activityPo.getState().intValue());
        LocalDateTime nowTime=LocalDateTime.now();
        CouponActivity.Type type;

        //活动不存在
        if(activityPo==null||
                activityState.equals(CouponActivity.DatabaseState.DELETED))
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);

        //状态为OFFLINE
        if(activityState.equals(CouponActivity.DatabaseState.OFFLINE))
            return new ReturnObject<>(ResponseCode.COUPON_END);

        //状态为FINISHED
        //已达结束时间
        if(nowTime.isAfter(activityPo.getEndTime()))
            return new ReturnObject<>(ResponseCode.COUPON_END);

        //活动尚不能领券（含TO_BE_ONLINE和ONLINE的couponTime之前）
        if(activityPo.getCouponTime().isAfter(nowTime))
            return new ReturnObject<>(ResponseCode.COUPON_NOTBEGIN);

        //活动进行中
        //先到bloom过滤器里查询
        ReturnObject returnObject=checkCouponBloomFilter(id,userId);
        if(returnObject.getCode().equals(ResponseCode.COUPON_FINISH))return returnObject;


        //查询优惠券发放情况
        //先找redis
        if(redisTemplate.opsForHash().hasKey(key,"quantity"))
        {
            type= CouponActivity.Type.getTypeByCode((Integer) redisTemplate.opsForHash().get(key,"quantityType"));
            if(type.equals(CouponActivity.Type.LIMIT_PER_PERSON))
                quantity= (int) redisTemplate.opsForHash().get(key,"quantity");
            else if(redisTemplate.opsForHash().get(key,"quantity").equals(0))
                    return new ReturnObject<>(ResponseCode.COUPON_FINISH);
            else quantity=1;
        }
        //没在redis找到，需查数据库
        else {
            //查询数据库
            CouponPoExample alreadyExample = new CouponPoExample();
            CouponPoExample.Criteria alreadyCriteria = alreadyExample.createCriteria();
            alreadyCriteria.andActivityIdEqualTo(id);
            //每人限量模式
            if (CouponActivity.Type.getTypeByCode(activityPo.getQuantitiyType().intValue()).equals(CouponActivity.Type.LIMIT_PER_PERSON))
                alreadyCriteria.andCustomerIdEqualTo(userId);
            List<CouponPo> alreadyPos = couponMapper.selectByExample(alreadyExample);


            int size= alreadyPos==null?0:alreadyPos.size();
            int couponQuantity=activityPo.getQuantity();
            //添加到redis
            type=CouponActivity.Type.getTypeByCode(activityPo.getQuantitiyType().intValue());
            redisTemplate.opsForHash().put(key,"quantityType",type.getCode().byteValue());

            //券已领罄
            //总量控制模式下券已发完或每人限量模式下该用户领的券已达上限
            if (couponQuantity==size)
            {
                //每人限发模式下领取数量达到上限
                if(type.equals(CouponActivity.Type.LIMIT_PER_PERSON))
                {
                    redisTemplate.opsForHash().put(key,"quantity",couponQuantity);
                    setBloomFilterOfCoupon(id, userId);
                }
                //总量控制模式下券已发完
                else redisTemplate.opsForHash().put(key,"quantity",0);

                //设置过期时间为活动结束时间
                redisTemplate.expire(key,Duration.between(nowTime,activityPo.getEndTime()).toHours(), TimeUnit.HOURS);

                return new ReturnObject<>(ResponseCode.COUPON_FINISH);
            }

            //总量控制模式下该用户已领过券
            if(type.equals(CouponActivity.Type.LIMIT_TOTAL_NUM) && size > 0)
            {
                setBloomFilterOfCoupon(id, userId);
                redisTemplate.opsForHash().put(key,"quantity",couponQuantity-size);
                //设置过期时间为活动结束时间
                redisTemplate.expire(key,Duration.between(nowTime,activityPo.getEndTime()).toHours(), TimeUnit.HOURS);

                return new ReturnObject<>(ResponseCode.COUPON_FINISH);
            }

            quantity=type.equals(CouponActivity.Type.LIMIT_PER_PERSON)?couponQuantity:1;
        }

        //可领券，设置券属性
        List<CouponPo>newPos=new ArrayList<>();
        for(int i=0;i<quantity;i++)
        {
            CouponPo newPo = new CouponPo();
            newPo.setCustomerId(userId);
            newPo.setActivityId(id);
            newPo.setCouponSn(Common.genSeqNum());
            newPo.setGmtCreate(nowTime);
            newPo.setGmtModified(nowTime);
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
            newPos.add(newPo);
        }


        String json = JacksonUtil.toJson(newPos);
        Message message = MessageBuilder.withPayload(json).build();
        logger.info("sendLogMessage: message = " + message);
        rocketMQTemplate.asyncSend("coupon-topic", message, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                System.out.println(sendResult.getSendStatus());
            }

            @Override
            public void onException(Throwable e) {
                System.out.println(e.getMessage());
            }
        });

        //构造RetVos
        List<String> couponSns=newPos.stream().map(CouponPo::getCouponSn).collect(Collectors.toList());

        //更新redis
        if(type.equals(CouponActivity.Type.LIMIT_TOTAL_NUM))
        {
            int rest= (int) redisTemplate.opsForHash().get(key,"quantity");
            redisTemplate.opsForHash().delete(key,"quantity");
            redisTemplate.opsForHash().put(key,"quantity",rest-1);
        }

        //更新bloom过滤器
        setBloomFilterOfCoupon(id,userId);

        //返回
        return new ReturnObject<List<String>>(couponSns);
    }

    /**
     * 优惠券退回
     *
     * @param shopId
     * @param id
     * @return ReturnObject
     */
    public ReturnObject<ResponseCode> returnCoupon(Long shopId, Long id)
    {
        CouponPo couponPo=couponMapper.selectByPrimaryKey(id);

        //优惠券存在
        if(couponPo==null)return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);

        //是该商店下的活动
        CouponActivityPo activityPo=activityMapper.selectByPrimaryKey(couponPo.getActivityId());
        if(!Objects.equals(activityPo.getShopId(), shopId))return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);

        //确实是之前使用了
        if(!Coupon.State.getTypeByCode(couponPo.getState().intValue()).equals(Coupon.State.USED))
            return new ReturnObject<>(ResponseCode.COUPON_STATENOTALLOW);

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
                return new ReturnObject<>(ResponseCode.FIELD_NOTVALID, "coupon字段不合法：" + couponPo.toString());
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
     * @param simpleShopDTO
     * @param id
     * @param createByName
     * @param modiByName
     * @return ReturnObject
     */
    public ReturnObject<Object> showCouponActivity(SimpleShopDTO simpleShopDTO, Long id, String createByName, String modiByName) {

        CouponActivityPo activityPo= activityMapper.selectByPrimaryKey(id);
        if(activityPo==null)
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        if(!activityPo.getShopId().equals(simpleShopDTO.getId()))
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);


        CouponActivity couponActivity=new CouponActivity(activityPo);
        CouponActivityVo couponActivityVo=new CouponActivityVo(couponActivity);
        //设置该优惠活动所属商店
        couponActivityVo.setShopVo(simpleShopDTO);
        //设置创建者、修改者
        CreatedBy createdBy=new CreatedBy();
        createdBy.setId(activityPo.getCreatedBy());
        createdBy.setUsername(createByName);
        couponActivityVo.setCreatedBy(createdBy);
        ModifiedBy modiBy=new ModifiedBy();
        modiBy.setId(activityPo.getModiBy());
        modiBy.setUsername(modiByName);
        couponActivityVo.setModifiedBy(modiBy);
        return new ReturnObject<Object>(couponActivityVo);

    }

    /**
     * 管理员新建己方优惠活动
     * @param activity
     * @param createByName
     * @return ReturnObject
     */
    public ReturnObject<CouponActivityVo> addCouponActivity(CouponActivity activity, SimpleShopDTO simpleShop, String createByName) {
        CouponActivityPo activityPo=activity.createActivityPo();
        ReturnObject<CouponActivityVo> returnObject=null;
        try{
            int ret = activityMapper.insertSelective(activityPo);
            if (ret == 0) {
                //插入失败
                logger.debug("insertRole: insert coupon activity fail " + activityPo.toString());
                returnObject = new ReturnObject<>(ResponseCode.ACTIVITYALTER_INVALID, String.format("新增失败：" + activityPo.getName()));
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
                if(checkPos.size()==0)return new ReturnObject<>(ResponseCode.FIELD_NOTVALID, String.format("couponActivity字段不合法：" + activityPo.toString()));
                else{//设置RetVo
                    CouponActivity retActivity =new CouponActivity(checkPos.get(0));
                    CouponActivityVo retVo=new CouponActivityVo(retActivity);
                    //设置优惠活动所属商店
                    if(simpleShop!=null)
                    retVo.setShopVo(simpleShop);
                    //设置创建者、修改者
                    CreatedBy createdBy=new CreatedBy();
                    createdBy.setId(activity.getCreatedBy());
                    createdBy.setUsername(createByName);
                    retVo.setCreatedBy(createdBy);
                    ModifiedBy modiBy=new ModifiedBy();
                    modiBy.setId(null);
                    modiBy.setUsername(null);
                    retVo.setModifiedBy(modiBy);
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

    /**
     * 管理员修改己方优惠活动
     * @param activity
     * @return ReturnObject
     */
    public ReturnObject<ResponseCode> modifyCouponActivity(CouponActivity activity) {
        CouponActivityPo activityPo=activity.createActivityPo();
        ReturnObject<ResponseCode> returnObject=new ReturnObject(ResponseCode.OK);
        CouponActivityPoExample activityPoExample=new CouponActivityPoExample();
        CouponActivityPoExample.Criteria criteria=activityPoExample.createCriteria();
        criteria.andIdEqualTo(activity.getId());
        criteria.andShopIdEqualTo(activity.getShopId());
        criteria.andStateEqualTo((byte)0);//待修改活动必须为【已下线】
        try{
            int ret = activityMapper.updateByExampleSelective(activityPo,activityPoExample);
            if(ret==0){//修改失败
                logger.debug("updateCouponActivity fail:"+activityPo.toString());
                returnObject=new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,String.format("该优惠活动不存在"));
            }
            else{//修改成功
                logger.debug("updateCouponActivity success:"+activityPo.toString());
                returnObject =new ReturnObject<>(ResponseCode.OK);
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

    public ReturnObject<ResponseCode> offlineCouponActivity(Long shopId, Long id,Long userId) {
        ReturnObject returnObject=new ReturnObject();
        CouponActivityPoExample activityPoExample=new CouponActivityPoExample();
        CouponActivityPoExample.Criteria criteria=activityPoExample.createCriteria();
        criteria.andIdEqualTo(id);
        criteria.andShopIdEqualTo(shopId);
        try{
            //下线优惠活动
            List<CouponActivityPo> activityPo=activityMapper.selectByExample(activityPoExample);
            if(activityPo==null)//未找到符合条件的优惠活动
            {
                CouponActivityPo ca=activityMapper.selectByPrimaryKey(id);
                if(ca==null)
                    return returnObject=new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
                else if(!ca.getShopId().equals(shopId))
                    return returnObject=new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
            }
            if(!activityPo.get(0).getState().equals((byte)1)){//未找到符合条件的优惠活动
                return returnObject=new ReturnObject<>(ResponseCode.ACTIVITYALTER_INVALID,String.format("不可重复下线"));
            }
            activityPo.get(0).setState((byte)0);//活动状态修改为0【已下线】
            activityPo.get(0).setModiBy(userId);//修改者更新
            int ret = activityMapper.updateByExampleSelective(activityPo.get(0),activityPoExample);
            if(ret==0){//修改失败
                logger.debug("updateCouponActivity fail:"+activityPo.toString());
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
                        for (CouponPo couponPo : couponPos) {
                            //将优惠券状态设置为【失效】
                            couponPo.setState((byte) 3);
                            //写回
                            couponMapper.updateByPrimaryKey(couponPo);
                        }
                        returnObject =new ReturnObject<>();
                    }
                    catch (Exception e){//数据库中无状态为【可用】优惠券，仍然下线成功
                        returnObject =new ReturnObject<>();
                    }

                }
                //若redis中有该活动，则删除
                String key="ca_"+id;
                if(redisTemplate.opsForHash().hasKey(key,"quantity")||redisTemplate.opsForHash().hasKey(key,"quantityType")){
                    redisTemplate.opsForHash().delete(key,"quantity");
                    redisTemplate.opsForHash().delete(key,"quantityType");
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
     * 管理员上线己方优惠活动
     * @param shopId
     * @param id
     * @return ReturnObject
     */
    public ReturnObject<ResponseCode> onlineCouponActivity(Long shopId, Long id, Long userId) {

        ReturnObject returnObject=new ReturnObject();
        CouponActivityPoExample activityPoExample=new CouponActivityPoExample();
        CouponActivityPoExample.Criteria criteria=activityPoExample.createCriteria();
        criteria.andIdEqualTo(id);
        criteria.andShopIdEqualTo(shopId);
        try{
            //上线优惠活动
            List<CouponActivityPo> activityPo=activityMapper.selectByExample(activityPoExample);
            if(activityPo==null)//未找到符合条件的优惠活动
            {
                CouponActivityPo ca=activityMapper.selectByPrimaryKey(id);
                if(ca==null)
                    return returnObject=new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
                else if(!ca.getShopId().equals(shopId))
                    return returnObject=new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
            }
            if(!activityPo.get(0).getState().equals((byte)0)){//未找到符合条件的优惠活动
                return returnObject=new ReturnObject<>(ResponseCode.ACTIVITYALTER_INVALID,String.format("不可重复下线"));
            }
            activityPo.get(0).setState((byte)1);//活动状态修改为1【已上线】
            activityPo.get(0).setModiBy(userId);//修改者更新
            int ret = activityMapper.updateByExampleSelective(activityPo.get(0),activityPoExample);
            if(ret==0){//修改失败
                logger.debug("updateCouponActivity fail:"+activityPo.toString());
                returnObject=new ReturnObject<>(ResponseCode.ACTIVITYALTER_INVALID,String.format("下线优惠活动失败"));
            }
            else{//修改活动状态成功
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
        criteria.andStateEqualTo((byte)1);//必须为【已上线】活动
        //设置shopId
        if(shopId!=null)
            criteria.andShopIdEqualTo(shopId);
        //设置timeline
        if(timeline==0)//未上线的
            criteria.andBeginTimeGreaterThan(LocalDateTime.now());
        else if(timeline==2){//正在进行中的
            criteria.andBeginTimeLessThanOrEqualTo(LocalDateTime.now());
            criteria.andEndTimeGreaterThanOrEqualTo(LocalDateTime.now());
        }
        else if(timeline==3)//已下线的
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
        List<CouponActivityPo> activityPos=null;
        try{
            activityPos=activityMapper.selectByExample(activityExample);
//            if(activityPos.size()==0)//未找到相应活动
//                return new ReturnObject<>(ResponseCode.OK);
            List<CouponActivityByNewCouponRetVo> retList=new ArrayList<>(activityPos.size());
            if(activityPos.size()!=0)
            for(CouponActivityPo po:activityPos){
                CouponActivity bo=new CouponActivity(po);
                CouponActivityByNewCouponRetVo vo=new CouponActivityByNewCouponRetVo();
                vo.set(bo);
                retList.add(vo);
            }
            //分页查询
            PageHelper.startPage(page, pageSize);
            logger.debug("page = " + page + "pageSize = " + pageSize);
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
        criteria.andStateEqualTo((byte)0);//必须为【已下线】活动
        //设置shopId
        criteria.andShopIdEqualTo(shopId);

        List<CouponActivityPo> activityPos=null;
        try{
            activityPos=activityMapper.selectByExample(activityExample);
//            if(activityPos.size()==0)//未找到符合条件的活动
//                return new ReturnObject<>(ResponseCode.ACTIVITY_NOTFOUND);
            List<CouponActivityByNewCouponRetVo> retList=new ArrayList<>(activityPos.size());
            if(activityPos.size()!=0)
            for(CouponActivityPo po:activityPos){
                CouponActivity bo=new CouponActivity(po);
                CouponActivityByNewCouponRetVo vo=new CouponActivityByNewCouponRetVo();
                vo.set(bo);
                retList.add(vo);
            }
            //分页查询
            PageHelper.startPage(page, pageSize);
            logger.debug("page = " + page + "pageSize = " + pageSize);
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
            if(CouponActivity.DatabaseState.getTypeByCode(activityPo.getState().intValue()).equals(CouponActivity.DatabaseState.ONLINE)&&
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

    /**
     * 将今天上线的优惠活动详情load到redis
     */
    public void loadingTomorrowActivities(){

        CouponActivityPoExample activityExample=new CouponActivityPoExample();
        CouponActivityPoExample.Criteria criteria=activityExample.createCriteria();
        criteria.andStateEqualTo((byte)1);//必须[已上线]活动
        //明天上线的活动
        LocalDateTime searchTime= LocalDateTime.now();
        searchTime=searchTime.plusDays(1);
        searchTime=searchTime.minusHours(searchTime.getHour());
        searchTime=searchTime.minusMinutes(searchTime.getMinute());
        searchTime=searchTime.minusSeconds(searchTime.getSecond());
        searchTime=searchTime.minusNanos(searchTime.getNano());
        LocalDateTime searchTimeMax=searchTime;//时间段上限
        LocalDateTime searchTimeMin=searchTime.minusDays(1);//时间段下限
        criteria.andBeginTimeGreaterThanOrEqualTo(searchTimeMin);//beginTime>=今日零点
        criteria.andBeginTimeLessThan(searchTimeMax);//beginTime<明日零点

        List<CouponActivityPo> activityPos=null;
        try{
            activityPos=activityMapper.selectByExample(activityExample);
            if(activityPos.size()==0)
                return;

            for(CouponActivityPo po:activityPos){
                CouponActivity bo=new CouponActivity(po);
                String key="ca_"+po.getId();
                //若redis中无该优惠活动或该活动quantity有变化
                if(!redisTemplate.opsForHash().hasKey(key,"quantity")||!redisTemplate.opsForHash().get(key,"quantity").equals(po.getQuantity())){

                    redisTemplate.opsForHash().delete(key,"quantity");
                    redisTemplate.opsForHash().put(key,"quantity",po.getQuantity());
                }
                //若redis中无该优惠活动或该活动quantityType有变化
                if(!redisTemplate.opsForHash().hasKey(key,"quantityType")||!redisTemplate.opsForHash().get(key,"quantityType").equals(po.getQuantitiyType())){

                    redisTemplate.opsForHash().delete(key,"quantityType");
                    redisTemplate.opsForHash().put(key,"quantityType",po.getQuantitiyType());
                }
                //设置过期时间为活动结束时间
                LocalDateTime timeNow=LocalDateTime.now();
                Duration duration = Duration.between(timeNow,po.getEndTime());
                long timeOut=duration.toHours();
                redisTemplate.expire(key,timeOut, TimeUnit.HOURS);
            }
        }
        catch (DataAccessException e){
            logger.error("selectAllRole: DataAccessException:" + e.getMessage());
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
        }
    }

    //上传图片相关变量
    private final String davUsername="night";
    private final String davPassword="tiesuolianhuan123";
    private final String baseUrl="http://172.16.4.146:8888/webdav/";//需要写成我们组服务器的webdev地址


    /**
     * 上传优惠活动图片
     * @param activity
     * @param file
     * @return  ReturnObject
     * @author 24320182203254 秦楚彦
     * Created at 2020/12/10 10：51
     */
    public ReturnObject<Object> uploadActivityImg(CouponActivity activity, MultipartFile file) {
        ReturnObject returnObject = null;
        try {
            //获得该活动信息
            CouponActivityPo activityPo = activityMapper.selectByPrimaryKey(activity.getId());
            //该优惠活动不存在
            if (activityPo == null)
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            //对不属于操作者店铺的商品SPU进行操作
            if (!activityPo.getShopId().equals(activity.getShopId()))
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);

            returnObject = ImgHelper.remoteSaveImg(file, 2, davUsername, davPassword, baseUrl);

            //文件上传错误
            if (!returnObject.getCode().equals(ResponseCode.OK)) {
                logger.debug(returnObject.getErrmsg());
                return returnObject;
            }
            String oldFilename = activity.getImageUrl();
            activity.setImageUrl(returnObject.getData().toString());
            returnObject = modifyCouponActivity(activity);

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
            return new ReturnObject<>(ResponseCode.FILE_NO_WRITE_PERMISSION);
        } catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            returnObject = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
        return returnObject;
    }

    public void insertCouponsBatch(List<CouponPo> coupons)
    {
        try{
            couponMapper.insertSelectiveBatch(coupons);
        }
        catch (Exception e)
        {
            logger.error("严重错误：" + e.getMessage());
        }
    }

    public Boolean judgeCouponValid(Long couponId) {
        CouponPo couponPo=couponMapper.selectByPrimaryKey(couponId);
        if(!couponPo.getBeginTime().isAfter(LocalDateTime.now())&&couponPo.getEndTime().isAfter(LocalDateTime.now())//在进行中范围
                &&!Coupon.State.getTypeByCode(couponPo.getState().intValue()).equals(Coupon.State.DISABLED)//未失效
                &&!Coupon.State.getTypeByCode(couponPo.getState().intValue()).equals(Coupon.State.USED))//未使用
                return true;
        return false;
    }

    public ReturnObject<Object> getCouponActivity(Long activityId,Long shopId){
        CouponActivityPo activityPo=activityMapper.selectByPrimaryKey(activityId);
        if(activityPo==null)return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        if(!activityPo.getShopId().equals(shopId))return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
        CouponActivity couponActivity= new CouponActivity(activityPo);
        return new ReturnObject<>(couponActivity);
    }

    public Boolean judgeCouponActivityIdValid(Long couponActivityId) {
        CouponActivityPo activityPo=activityMapper.selectByPrimaryKey(couponActivityId);

        //若活动状态不为已下线
        if(activityPo==null||!activityPo.getState().equals(1))return false;
        LocalDateTime now=LocalDateTime.now();
        if(activityPo.getBeginTime().isBefore(now)&&activityPo.getEndTime().isAfter(now))return true;
        return false;

    }

    /**
     * 获取可用优惠活动规则列表
     * @param couponId
     * @param activityIds
     * @return  List<String>
     */
    public List<String> getActivityRules(Long couponId, List<Long> activityIds) {
        //活动规则列表 优惠券对应活动po 优惠券对应活动规则
        List<String>activityRules=new ArrayList<>();
        CouponActivityPo couponActivityPo=new CouponActivityPo();
        String couponRule=null;

        //判断优惠券是否有效并获取规则
        if(judgeCouponValid(couponId)){
            CouponPo couponPo=couponMapper.selectByPrimaryKey(couponId);
            couponActivityPo=activityMapper.selectByPrimaryKey(couponPo.getActivityId());
            couponRule=couponActivityPo.getStrategy();
        }
        //获取有效活动规则列表
        for(Long activityId:activityIds){
            //判断优惠活动是否有效
            if(judgeCouponActivityIdValid(activityId)){//有效活动
                CouponActivityPo activityPo=activityMapper.selectByPrimaryKey(activityId);
                //若为不发券优惠活动，直接加入
                if(activityPo.getQuantity().equals(0)){
                    activityRules.add(activityPo.getStrategy());
                }
                else if(activityPo.getId().equals(couponActivityPo.getId())){//若为发券优惠活动，判断该活动是否与优惠券对应
                    activityRules.add(couponRule);
                }
                else{//若为发券优惠活动，且与优惠券对应不上
                    activityRules.add(null);
                }
            }
            else activityRules.add(null);//无效活动
        }
        return activityRules;

    }


}
