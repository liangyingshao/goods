package cn.edu.xmu.goods.dao;

import cn.edu.xmu.goods.mapper.*;
import cn.edu.xmu.goods.model.bo.FloatPrice;
import cn.edu.xmu.goods.model.bo.GoodsSku;
import cn.edu.xmu.goods.model.bo.GoodsSpu;
import cn.edu.xmu.goods.model.po.*;
import cn.edu.xmu.goods.model.vo.*;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.oomall.goods.model.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class GoodsDao {

    private static final Logger logger = LoggerFactory.getLogger(GoodsDao.class);
    @Autowired
    private GoodsSkuPoMapper skuMapper;
    @Autowired
    private GoodsSpuPoMapper spuMapper;
    @Autowired
    private FloatPricePoMapper floatMapper;
    @Autowired
    private BrandPoMapper brandMapper;
    @Autowired
    private GoodsCategoryPoMapper categoryMapper;
    @Autowired
    private ShopPoMapper shopMapper;
    @Autowired
    private FloatPricePoMapper floatPricePoMapper;

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    RedisTemplate redisTemplate;
//上传图片相关变量
    private final String davUsername="night";
    private final String davPassword="tiesuolianhuan123";
    private final String baseUrl="http://172.16.4.146:8888/webdav/";

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
        //初始化floatPrice
        FloatPricePoExample example2=new FloatPricePoExample();
        FloatPricePoExample.Criteria criteria2=example2.createCriteria();
        List<FloatPricePo> floatPricePos=floatMapper.selectByExample(example2);
        for(FloatPricePo po:floatPricePos)
        {
            FloatPricePo newPo=new FloatPricePo();
            newPo.setId(po.getId());
            newPo.setGoodsSkuId(po.getGoodsSkuId());
            floatMapper.updateByPrimaryKeySelective(newPo);
        }
        //初始化brand
        BrandPoExample example3=new BrandPoExample();
        BrandPoExample.Criteria criteria3=example3.createCriteria();
        List<BrandPo>brandPos=brandMapper.selectByExample(example3);
        for(BrandPo po:brandPos)
        {
            BrandPo newPo=new BrandPo();
            newPo.setId(po.getId());
            brandMapper.updateByPrimaryKeySelective(newPo);
        }
        //初始化category
        GoodsCategoryPoExample example4=new GoodsCategoryPoExample();
        GoodsCategoryPoExample.Criteria criteria4=example4.createCriteria();
        List<GoodsCategoryPo>categoryPos=categoryMapper.selectByExample(example4);
        for(GoodsCategoryPo po:categoryPos)
        {
            GoodsCategoryPo newPo=new GoodsCategoryPo();
            newPo.setId(po.getId());
            newPo.setPid(po.getPid());
            categoryMapper.updateByPrimaryKeySelective(newPo);
        }
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
    public PageInfo<GoodsSkuRetVo> getSkuList(Long shopId, String skuSn, Long spuId, String spuSn, Integer page, Integer pageSize)
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
            if(spuSn!=null&&!spuSn.isBlank())spuCriteria.andGoodsSnEqualTo(spuSn);
            if(shopId!=null)spuCriteria.andShopIdEqualTo(shopId);
            List<GoodsSpuPo> spuPos=spuMapper.selectByExample(spuExample);
            for (GoodsSpuPo spuPo:spuPos)
            {
                skuCriteria.andGoodsSpuIdEqualTo(spuPo.getId());
                skuPos.addAll(skuMapper.selectByExample(skuExample));
            }
        }
        else skuPos=skuMapper.selectByExample(skuExample);
        List<GoodsSku>skus=skuPos.stream().map(GoodsSku::new).collect(Collectors.toList());
//        for(GoodsSku sku:skus)
//        {
//            FloatPricePoExample floatExample=new FloatPricePoExample();
//            FloatPricePoExample.Criteria floatCriteria=floatExample.createCriteria();
//            floatCriteria.andGoodsSkuIdEqualTo(sku.getId());
//            floatCriteria.andBeginTimeLessThanOrEqualTo(LocalDateTime.now());
//            floatCriteria.andEndTimeGreaterThanOrEqualTo(LocalDateTime.now());
//            List<FloatPricePo> floatPos=floatMapper.selectByExample(floatExample);
//            if(floatPos==null||floatPos.size()==0||floatPos.get(0).getQuantity().equals(0))sku.setPrice(sku.getOriginalPrice());
//            else sku.setPrice(floatPos.get(0).getActivityPrice());
//        }
        List<GoodsSkuRetVo> ret = skus.stream().map(GoodsSkuRetVo::new).collect(Collectors.toList());
        PageInfo<GoodsSkuRetVo> returns=new PageInfo<GoodsSkuRetVo>(ret);
        returns.setPageNum(page);
        return returns;
    }

    /**
     * 获得sku的详细信息
     * @param id
     * @return GoodsSkuPo
     */
    public ReturnObject<GoodsSkuDetailRetVo> getSku(Long id)
    {
        GoodsSkuPo skuPo=skuMapper.selectByPrimaryKey(id);
        GoodsSkuDetailRetVo retVo=new GoodsSkuDetailRetVo();
        if(skuPo!=null&&GoodsSku.State.getTypeByCode(skuPo.getDisabled().intValue()) != GoodsSku.State.DELETED)
        {
            log.info("retVo.set:"+skuPo.getName());
            retVo.set(new GoodsSku(skuPo));
        }
        else return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        //此处取出的spuPo不可为null，否则返回的数据就是空的
        GoodsSpuPo spuPo= spuMapper.selectByPrimaryKey(skuPo.getGoodsSpuId());
        if(spuPo==null)return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        GoodsSpu spu=new GoodsSpu(spuPo);
        GoodsSpuVo spuVo= new GoodsSpuVo(spu);
        retVo.setSpu(spuVo);
        return new ReturnObject<>(retVo);
    }

    public GoodsSkuPo internalGetSku(Long id)
    {
        return skuMapper.selectByPrimaryKey(id);
    }

    /**
     * sku上传图片
     * @param sku
     * @return ReturnObject
     */
    public ReturnObject<ResponseCode> uploadSkuImg(GoodsSku sku)
    {
        ReturnObject<ResponseCode> returnObject;
        GoodsSkuPo skuPo=skuMapper.selectByPrimaryKey(sku.getId());
        if(skuPo==null|| GoodsSku.State.getTypeByCode(skuPo.getDisabled().intValue())== GoodsSku.State.DELETED)return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        GoodsSkuPo newSkuPo = new GoodsSkuPo();
        newSkuPo.setId(sku.getId());
        newSkuPo.setImageUrl(sku.getImageUrl());
        newSkuPo.setGmtModified(LocalDateTime.now());
        int ret = skuMapper.updateByPrimaryKeySelective(newSkuPo);
        if (ret == 0) {
            logger.debug("uploadSkuImg: update fail. sku id: " + sku.getId());
            returnObject = new ReturnObject<>(ResponseCode.FIELD_NOTVALID);
        } else {
            logger.debug("uploadSkuImg: update sku success : " + sku);
            returnObject = new ReturnObject<>();
        }
        return returnObject;
    }

    /**
     * 管理员或店家逻辑删除SKU
     * @param shopId
     * @param id
     * @return ReturnObject
     */
    public ReturnObject<ResponseCode> logicalDelete(Long shopId, Long id)
    {
        GoodsSkuPo skuPo=skuMapper.selectByPrimaryKey(id);
        if(skuPo==null|| GoodsSku.State.getTypeByCode(skuPo.getDisabled().intValue())== GoodsSku.State.DELETED)return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        GoodsSpuPo spuPo=spuMapper.selectByPrimaryKey(skuPo.getGoodsSpuId());
        if(spuPo.getShopId().equals(shopId))
        {
            skuPo.setDisabled(GoodsSku.State.DELETED.getCode().byteValue());
            skuPo.setGmtModified(LocalDateTime.now());
            String key="sku_"+id;
            if(redisTemplate.opsForHash().hasKey(key,"quantity"))
            {
                redisTemplate.opsForHash().delete(key,"quantity");
                redisTemplate.opsForHash().delete(key,"price");
            }
            int ret=skuMapper.updateByPrimaryKey(skuPo);
            if(ret==0)
            {
                logger.debug("logicalDelete:update fail.sku id="+id);
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            }
            FloatPricePo floatPo=new FloatPricePo();
            floatPo.setGoodsSkuId(skuPo.getId());
            floatPo.setValid(FloatPrice.Validation.INVALID.getCode().byteValue());
            ret=floatPricePoMapper.updateByPrimaryKeySelective(floatPo);
            return new ReturnObject<>();
        }
        else return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
    }

    /**
     * 管理员或店家修改SKU信息
     * @param shopId
     * @param sku
     * @return ReturnObject
     */
    public ReturnObject<ResponseCode> modifySku(Long shopId, GoodsSku sku)
    {
        //SKU存在
        GoodsSkuPo selectSkuPo=skuMapper.selectByPrimaryKey(sku.getId());
        if(selectSkuPo==null|| GoodsSku.State.getTypeByCode(selectSkuPo.getDisabled().intValue())== GoodsSku.State.DELETED)
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);

        //shopId和spuId匹配
        GoodsSpuPoExample spuPoExample=new GoodsSpuPoExample();
        GoodsSpuPoExample.Criteria criteria1=spuPoExample.createCriteria();
        criteria1.andShopIdEqualTo(shopId);
        criteria1.andIdEqualTo(selectSkuPo.getGoodsSpuId());
        List<GoodsSpuPo> spuPos=spuMapper.selectByExample(spuPoExample);
        if(spuPos.size()==0)return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);

        //同SPU下SKU不重名
        GoodsSkuPoExample skuExample=new GoodsSkuPoExample();
        GoodsSkuPoExample.Criteria criteria=skuExample.createCriteria();
        criteria.andGoodsSpuIdEqualTo(selectSkuPo.getGoodsSpuId());
        List<GoodsSkuPo>skuPos=skuMapper.selectByExample(skuExample);
        for(GoodsSkuPo po:skuPos)
            if(po.getName().equals(sku.getName())&&!po.getId().equals(sku.getId()))return new ReturnObject<>(ResponseCode.SKUSN_SAME, String.format("SKU名重复：" + selectSkuPo.getName()));


        //更新redis
        String key="sku_"+sku.getId();
        redisTemplate.opsForHash().delete(key,"quantity");
        redisTemplate.opsForHash().delete(key,"price");
        redisTemplate.opsForHash().put(key,"quantity",sku.getInventory());
        redisTemplate.opsForHash().put(key,"price",sku.getOriginalPrice());
        //尝试修改
        GoodsSkuPo skuPo=sku.getGoodsSkuPo();
        skuPo.setGmtModified(LocalDateTime.now());
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
            if (Objects.requireNonNull(e.getMessage()).contains("goods_sku.goods_sku_name_uindex"))
            {
                logger.debug("modifySku: have same sku name = " + skuPo.getName());
                return new ReturnObject<>(ResponseCode.FIELD_NOTVALID, "SKU名重复：" + skuPo.getName());
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

    /**
     * 管理员新增商品价格浮动
     * @param shopId
     * @param floatPrice
     * @param userId
     * @return ReturnObject
     */
    public ReturnObject<FloatPriceRetVo> addFloatPrice(Long shopId, FloatPrice floatPrice, Long userId)
    {
        //SKU存在
        GoodsSkuPo selectSkuPo=skuMapper.selectByPrimaryKey(floatPrice.getGoodsSkuId());
        if(selectSkuPo==null|| GoodsSku.State.getTypeByCode(selectSkuPo.getDisabled().intValue())== GoodsSku.State.DELETED)
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);

        //库存充足
        if(selectSkuPo.getInventory()*9/10<floatPrice.getQuantity())
            return new ReturnObject<>(ResponseCode.SKU_NOTENOUGH, "库存不足：" + floatPrice.getGoodsSkuId());

        //shopId能和skuId匹配
        GoodsSpuPoExample spuPoExample=new GoodsSpuPoExample();
        GoodsSpuPoExample.Criteria criteria1=spuPoExample.createCriteria();
        criteria1.andShopIdEqualTo(shopId);
        criteria1.andIdEqualTo(selectSkuPo.getGoodsSpuId());
        List<GoodsSpuPo> spuPos=spuMapper.selectByExample(spuPoExample);
        if(spuPos.size()==0)
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE, "skuId不存在：" + floatPrice.getGoodsSkuId());

        //时间不冲突
        FloatPricePoExample nowExample=new FloatPricePoExample();
        FloatPricePoExample.Criteria nowCriteria=nowExample.createCriteria();
        nowCriteria.andGoodsSkuIdEqualTo(floatPrice.getGoodsSkuId());
        nowCriteria.andBeginTimeLessThanOrEqualTo(floatPrice.getEndTime());
        nowCriteria.andEndTimeGreaterThanOrEqualTo(floatPrice.getBeginTime());
        List<FloatPricePo> nowFloatPos=floatMapper.selectByExample(nowExample);
        if(nowFloatPos.size()>0)
            return new ReturnObject<>(ResponseCode.SKUPRICE_CONFLICT, "floatPrice时间冲突：已有" + nowFloatPos.toString() + "待加" + floatPrice.toString());

        //尝试插入
        FloatPricePo floatPricePo=floatPrice.getFloatPricePo();
        floatPricePo.setGmtCreate(LocalDateTime.now());
        floatPricePo.setGmtModified(LocalDateTime.now());
        try{
            int ret = floatMapper.insertSelective(floatPricePo);
            if (ret == 0)
            {
                //修改失败
                logger.debug("addFloatPrice: insert floatPrice fail : " + floatPricePo.toString());
                return new ReturnObject<>(ResponseCode.FIELD_NOTVALID, "floatPrice字段不合法：" + floatPricePo.toString());
            }
            else {
                //修改成功
                logger.debug("addFloatPrice: insert floatPrice = " + floatPricePo.toString());
                //检验
                FloatPricePoExample floatExample=new FloatPricePoExample();
                FloatPricePoExample.Criteria criteria=floatExample.createCriteria();
                criteria.andGoodsSkuIdEqualTo(floatPrice.getGoodsSkuId());
                criteria.andBeginTimeEqualTo(floatPrice.getBeginTime());
                criteria.andEndTimeEqualTo(floatPrice.getEndTime());
                criteria.andActivityPriceEqualTo(floatPrice.getActivityPrice());
                criteria.andQuantityEqualTo(floatPrice.getQuantity());
                List<FloatPricePo> checkFloatPo=floatMapper.selectByExample(floatExample);
                if(checkFloatPo.size()==0)
                    return new ReturnObject<>(ResponseCode.FIELD_NOTVALID, "floatPrice字段不合法：" + floatPricePo.toString());
                else {//构造FloatPriceRetVo
                    FloatPriceRetVo retVo=new FloatPriceRetVo();
                    retVo.set(new FloatPrice(checkFloatPo.get(0)));
                    return new ReturnObject<>(retVo);
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

    /**
     * 管理员添加新的SKU到SPU里
     * @param shopId
     * @param sku
     * @return ReturnObject<GoodsSkuRetVo>
     */
    public ReturnObject<GoodsSkuRetVo> createSKU(Long shopId, GoodsSku sku)
    {
        //SPU存在
        GoodsSpuPo spuPo=spuMapper.selectByPrimaryKey(sku.getGoodsSpuId());
        if(spuPo==null||spuPo.getDisabled().equals(true))
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);

        //shopId和SPU匹配
        if(!Objects.equals(spuPo.getShopId(), shopId))
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE, "spuId不存在：" + sku.getGoodsSpuId());
        //SKU.configuration不重复
        //数据库里都是null，暂时换成name判断
        GoodsSkuPoExample skuExample=new GoodsSkuPoExample();
        GoodsSkuPoExample.Criteria skuCriteria=skuExample.createCriteria();
        skuCriteria.andGoodsSpuIdEqualTo(sku.getGoodsSpuId());
        List<GoodsSkuPo>nowSkuPos=skuMapper.selectByExample(skuExample);
        for(GoodsSkuPo skuPo:nowSkuPos)
            if(skuPo.getName().equals(sku.getName()))return new ReturnObject<>(ResponseCode.SKUSN_SAME,"SKU规格重复："+sku.getName());

        //更新redis
        String key="sku_"+sku.getId();
        redisTemplate.opsForHash().delete(key,"quantity");
        redisTemplate.opsForHash().put(key,"quantity",sku.getInventory());
        redisTemplate.opsForHash().put(key,"price",sku.getOriginalPrice());
        GoodsSkuPo skuPo=sku.getNewGoodsSkuPo();
        skuPo.setGmtCreate(LocalDateTime.now());
        skuPo.setGmtModified(LocalDateTime.now());
        try
        {
            int ret=skuMapper.insert(skuPo);
            if(ret==0)
            {
                logger.debug("createSku fail:spuId="+sku.getGoodsSpuId()+"skuPo="+skuPo);
                return new ReturnObject<>(ResponseCode.FIELD_NOTVALID, "sku字段不合法：" + skuPo.toString());
            }
            else {
                //修改成功
                logger.debug("createSku: insert floatPrice = " + skuPo.toString());
                //检验
                GoodsSkuPoExample checkSkuExample=new GoodsSkuPoExample();
                GoodsSkuPoExample.Criteria criteria=checkSkuExample.createCriteria();
                criteria.andGoodsSpuIdEqualTo(sku.getGoodsSpuId());
                criteria.andSkuSnEqualTo(sku.getSkuSn());
                criteria.andNameEqualTo(sku.getName());
                criteria.andOriginalPriceEqualTo(sku.getOriginalPrice());
                criteria.andConfigurationEqualTo(sku.getConfiguration());
                criteria.andWeightEqualTo(sku.getWeight());
                criteria.andImageUrlEqualTo(sku.getImageUrl());
                criteria.andInventoryEqualTo(sku.getInventory());
                criteria.andDetailEqualTo(sku.getDetail());
                List<GoodsSkuPo> checkSkuPo=skuMapper.selectByExample(checkSkuExample);
                if(checkSkuPo.size()==0)
                    return new ReturnObject<>(ResponseCode.FIELD_NOTVALID, String.format("floatPrice字段不合法：" + checkSkuExample.toString()));
                else {//构造FloatPriceRetVo
                    GoodsSku returnSku=new GoodsSku(checkSkuPo.get(0));
                    returnSku.setPrice(returnSku.getOriginalPrice());
                    GoodsSkuRetVo retVo=new GoodsSkuRetVo(returnSku);
                    return new ReturnObject<>(retVo);
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

    public ReturnObject<ResponseCode> checkSkuUsableBySkuShop(Long skuId, Long shopId) {
        //SKU存在
        GoodsSkuPo skuPo = skuMapper.selectByPrimaryKey(skuId);
        if (skuPo == null || GoodsSku.State.getTypeByCode(skuPo.getDisabled().intValue()).equals(GoodsSku.State.DELETED))
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);

        //SKU对应的SPU和shopId匹配
        GoodsSpuPo spuPo=spuMapper.selectByPrimaryKey(skuPo.getGoodsSpuId());
        if (!Objects.equals(spuPo.getShopId(), shopId)) return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);

        return new ReturnObject<>();
    }


    /**
     * 查看一条分享商品SKU的详细信息（需登录）
     * @param id
     * @return ReturnObject<GoodsSkuRetVo>
     */
    public ReturnObject<GoodsSkuRetVo> getShareSku(Long id)
    {
        GoodsSkuPo skuPo=skuMapper.selectByPrimaryKey(id);
        if(skuPo==null)return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);

        GoodsSku sku=new GoodsSku(skuPo);
        GoodsSkuRetVo skuRetVo=new GoodsSkuRetVo(sku);
        return new ReturnObject<>(skuRetVo);
    }

    public ReturnObject<List<Long>> getAllSkuIdByShopId(Long shopId)
    {
        GoodsSpuPoExample spuExample=new GoodsSpuPoExample();
        GoodsSpuPoExample.Criteria spuCriteria=spuExample.createCriteria();
        spuCriteria.andShopIdEqualTo(shopId);
        List<GoodsSpuPo>spuPos=spuMapper.selectByExample(spuExample);

        //查不到shop下的spu
        if(spuPos==null||spuPos.size()==0)return new ReturnObject<>(null);

        List<Long>skuIds=new ArrayList<>();
        for(GoodsSpuPo spuPo:spuPos)
        {
            GoodsSkuPoExample skuExample=new GoodsSkuPoExample();
            GoodsSkuPoExample.Criteria skuCriteria=skuExample.createCriteria();
            skuCriteria.andGoodsSpuIdEqualTo(spuPo.getId());
            List<GoodsSkuPo> skuPos=skuMapper.selectByExample(skuExample);

            //查得到spu下的sku
            if(skuPos!=null&&skuPos.size()>0)
                skuIds.addAll(skuPos.stream().map(GoodsSkuPo::getId).collect(Collectors.toList()));
        }
        return new ReturnObject<>(skuIds);
    }

    public ReturnObject<Long> getShopIdBySkuId(Long skuId)
    {
        GoodsSkuPo skuPo= null;
        try {
            skuPo = skuMapper.selectByPrimaryKey(skuId);
        } catch (Exception e) {
            StringBuilder message = new StringBuilder().append("getShopIdBySkuId: ").append(e.getMessage());
            logger.error(message.toString());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }

        //查不到sku
        if(skuPo==null)
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);

        GoodsSpuPo spuPo= null;
        try {
            spuPo = spuMapper.selectByPrimaryKey(skuPo.getGoodsSpuId());
        } catch (Exception e) {
            StringBuilder message = new StringBuilder().append("getShopIdBySkuId: ").append(e.getMessage());
            logger.error(message.toString());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }

        //查不到spu
        if(spuPo==null)
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);

        return new ReturnObject<>(spuPo.getShopId());
    }


    public ReturnObject<Boolean> getVaildSkuId(Long skuId)
    {
        GoodsSkuPo skuPo=skuMapper.selectByPrimaryKey(skuId);
        return new ReturnObject<>((skuPo!=null&&!GoodsSku.State.getTypeByCode(skuPo.getDisabled().intValue()).equals(GoodsSku.State.DELETED)));
    }

    public SkuInfoDTO getSelectSkuInfoBySkuId(Long skuId)
    {
        GoodsSkuPo skuPo=skuMapper.selectByPrimaryKey(skuId);

        //查不到sku
        if(skuPo==null)return null;

        SkuInfoDTO skuInfoDTO=new SkuInfoDTO();
        skuInfoDTO.setDisable(skuPo.getDisabled());
        skuInfoDTO.setImageUrl(skuPo.getImageUrl());
        skuInfoDTO.setInventory(skuPo.getInventory());
        skuInfoDTO.setName(skuPo.getName());
        skuInfoDTO.setOriginalPrice(skuPo.getOriginalPrice());
        skuInfoDTO.setId(skuPo.getId());
        skuInfoDTO.setSkuSn(skuPo.getSkuSn());

//        FloatPricePoExample floatExample=new FloatPricePoExample();
//        FloatPricePoExample.Criteria floatCriteria= floatExample.createCriteria();
//        floatCriteria.andBeginTimeLessThanOrEqualTo(LocalDateTime.now());
//        floatCriteria.andEndTimeGreaterThan(LocalDateTime.now());
//        floatCriteria.andGoodsSkuIdEqualTo(skuId);
//        floatCriteria.andInvalidByEqualTo(FloatPrice.Validation.VALID.getCode().longValue());
//        List<FloatPricePo> floatPos=floatMapper.selectByExample(floatExample);
//        skuInfoDTO.setPrice((floatPos==null||floatPos.size()==0||floatPos.get(0).getQuantity().equals(0))?skuPo.getOriginalPrice():floatPos.get(0).getActivityPrice());

        return skuInfoDTO;
    }

    public ReturnObject<Map<Long, SkuInfoDTO>> listSelectSkuInfoById(List<Long> skuIdList)
    {
        Map<Long,SkuInfoDTO> map=new HashMap<>();
        for(Long skuId:skuIdList)
            map.put(skuId,getSelectSkuInfoBySkuId(skuId));
        return new ReturnObject<>(map);
    }

    public GoodsInfoDTO getSelectGoodsInfoBySkuId(Long skuId)
    {
        GoodsSkuPo skuPo=skuMapper.selectByPrimaryKey(skuId);

        //SKU能查到
        if(skuPo==null)return null;

        GoodsSpuPo spuPo= spuMapper.selectByPrimaryKey(skuPo.getGoodsSpuId());
        GoodsInfoDTO goodsInfoDTO=new GoodsInfoDTO();
        goodsInfoDTO.setSkuName(skuPo.getName());
        goodsInfoDTO.setSpuId(spuPo.getId());
        goodsInfoDTO.setSpuName(spuPo.getName());
        goodsInfoDTO.setAddTime(skuPo.getGmtCreate());

//        FloatPricePoExample floatExample=new FloatPricePoExample();
//        FloatPricePoExample.Criteria floatCriteria= floatExample.createCriteria();
//        floatCriteria.andBeginTimeLessThanOrEqualTo(LocalDateTime.now());
//        floatCriteria.andEndTimeGreaterThan(LocalDateTime.now());
//        floatCriteria.andGoodsSkuIdEqualTo(skuId);
//        floatCriteria.andInvalidByEqualTo(FloatPrice.Validation.VALID.getCode().longValue());
//        List<FloatPricePo> floatPos=floatMapper.selectByExample(floatExample);
//        goodsInfoDTO.setPrice((floatPos==null||floatPos.size()==0||floatPos.get(0).getQuantity().equals(0))?skuPo.getOriginalPrice():floatPos.get(0).getActivityPrice());

        return goodsInfoDTO;
    }

    /**
     * 根据shopID获取simple shop对象
     */
    public ReturnObject<SimpleShopDTO> getSimpleShopByShopId(Long shopId) {
        ShopPo shopPo=shopMapper.selectByPrimaryKey(shopId);
        log.info("in simpleShop");
        if(shopPo==null){
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,"shopId对应的商店不存在");
        }
        SimpleShopDTO simpleShopDTO=new SimpleShopDTO();
        simpleShopDTO.setId(shopPo.getId());
        simpleShopDTO.setName(shopPo.getName());
        ReturnObject<SimpleShopDTO> returnObject = new ReturnObject<SimpleShopDTO>(simpleShopDTO);
        return returnObject;
    }

    public ReturnObject<GoodsDetailDTO> getGoodsBySkuId(Long skuId)
    {
        GoodsSkuPo skuPo=skuMapper.selectByPrimaryKey(skuId);

        //SKU不存在
        if(skuPo==null||GoodsSku.State.getTypeByCode(skuPo.getDisabled().intValue()).equals(GoodsSku.State.DELETED))
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);

        GoodsDetailDTO goodsDetailDTO=new GoodsDetailDTO();
        goodsDetailDTO.setName(skuPo.getName());

//        FloatPricePoExample floatExample=new FloatPricePoExample();
//        FloatPricePoExample.Criteria floatCriteria= floatExample.createCriteria();
//        floatCriteria.andBeginTimeLessThanOrEqualTo(LocalDateTime.now());
//        floatCriteria.andEndTimeGreaterThan(LocalDateTime.now());
//        floatCriteria.andGoodsSkuIdEqualTo(skuId);
//        floatCriteria.andInvalidByEqualTo(FloatPrice.Validation.VALID.getCode().longValue());
//        List<FloatPricePo> floatPos=floatMapper.selectByExample(floatExample);
//        goodsDetailDTO.setPrice((floatPos==null||floatPos.size()==0||floatPos.get(0).getQuantity().equals(0))?skuPo.getOriginalPrice():floatPos.get(0).getActivityPrice());

        return new ReturnObject<>(goodsDetailDTO);
    }

    /**
     * description: 根据id查询sku,预售返回时需要使用此函数
     * version: 1.0
     * date: 2020/12/11 12:30
     * author: 杨铭
     * 
     * @param skuId
     * @return cn.edu.xmu.ooad.util.ReturnObject<cn.edu.xmu.goods.model.po.GoodsSkuPo>
     */ 
    public ReturnObject<GoodsSkuPo> getGoodsSkuById(Long skuId) {
        GoodsSkuPo goodsSkuPo = null;
        try {
            goodsSkuPo = skuMapper.selectByPrimaryKey(skuId);
        } catch (Exception e) {
            StringBuilder message = new StringBuilder().append("getGoodsSkuById: ").append(e.getMessage());
            logger.error(message.toString());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }
        return new ReturnObject<>(goodsSkuPo);
    }

    /**
     * 店家商品上架
     * @param shopId
     * @param id
     * @return ReturnObject
     */
    public ReturnObject<ResponseCode> putGoodsOnSale(Long shopId, Long id) {
        //SKU存在
        GoodsSkuPo skuPo=skuMapper.selectByPrimaryKey(id);
        if(skuPo==null||GoodsSku.State.getTypeByCode(skuPo.getDisabled().intValue()).equals(GoodsSku.State.DELETED))return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);

        GoodsSku.State state=GoodsSku.State.getTypeByCode(skuPo.getDisabled().intValue());
        //shopId能对上号
        GoodsSpuPo spuPo= spuMapper.selectByPrimaryKey(skuPo.getGoodsSpuId());
        if(!spuPo.getShopId().equals(shopId))return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);

        //SKU非已上架
        if(state.equals(GoodsSku.State.ONSHELF))return new ReturnObject<>(ResponseCode.STATE_NOCHANGE);

        skuPo.setDisabled(GoodsSku.State.ONSHELF.getCode().byteValue());
        try{
            int ret=skuMapper.updateByPrimaryKeySelective(skuPo);
            if(ret==0)
            {
                logger.debug("putGoodsOnSale fail:skuPo="+skuPo);
                return new ReturnObject<>(ResponseCode.FIELD_NOTVALID, "sku字段不合法：" + skuPo.toString());
            }
            else  return new ReturnObject<>();
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
     * 店家商品下架
     * @param shopId
     * @param id
     * @return
     */
    public ReturnObject<ResponseCode> putOffGoodsOnSale(Long shopId, Long id) {
        //SKU存在
        GoodsSkuPo skuPo=skuMapper.selectByPrimaryKey(id);
        if(skuPo==null||GoodsSku.State.getTypeByCode(skuPo.getDisabled().intValue()).equals(GoodsSku.State.DELETED))return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);

        GoodsSku.State state=GoodsSku.State.getTypeByCode(skuPo.getDisabled().intValue());
        //shopId能对上号
        GoodsSpuPo spuPo= spuMapper.selectByPrimaryKey(skuPo.getGoodsSpuId());
        if(!spuPo.getShopId().equals(shopId))return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);

        //SKU非已下架
        if(state.equals(GoodsSku.State.OFFSHELF))return new ReturnObject<>(ResponseCode.STATE_NOCHANGE);

        skuPo.setDisabled(GoodsSku.State.OFFSHELF.getCode().byteValue());
        try{
            int ret=skuMapper.updateByPrimaryKeySelective(skuPo);
            if(ret==0)
            {
                logger.debug("putGoodsOnSale fail:skuPo="+skuPo);
                return new ReturnObject<>(ResponseCode.FIELD_NOTVALID, String.format("sku字段不合法：" + skuPo.toString()));
            }
            else  return new ReturnObject<>();
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

    public ReturnObject<Long> getPriceBySkuId(Long skuId) {

        //1.skuId是否存在
        GoodsSkuPo sku = null;
        try {
            sku = skuMapper.selectByPrimaryKey(skuId);
        } catch (Exception e) {
            StringBuilder message = new StringBuilder().append("getPriceBySkuId: ").append(e.getMessage());
            logger.error(message.toString());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }
        if(sku==null)
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);

        //2. 查浮动表
        FloatPricePoExample example = new FloatPricePoExample();
        FloatPricePoExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsSkuIdEqualTo(skuId);
        criteria.andBeginTimeLessThanOrEqualTo(LocalDateTime.now());
        criteria.andEndTimeGreaterThan(LocalDateTime.now());
        criteria.andGoodsSkuIdEqualTo(skuId);
        criteria.andInvalidByEqualTo(FloatPrice.Validation.VALID.getCode().longValue());

        List<FloatPricePo> floatPricePo = null;
        try {
            floatPricePo = floatPricePoMapper.selectByExample(example);
        } catch (Exception e) {
            StringBuilder message = new StringBuilder().append("getPriceBySkuId: ").append(e.getMessage());
            logger.error(message.toString());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }

        //3. 浮动表中无数据，则返回sku原价
        if(!(floatPricePo==null||floatPricePo.size()==0||floatPricePo.get(0).getQuantity().equals(0))) {
            return new ReturnObject<>(floatPricePo.get(0).getActivityPrice());
        }else {
            return new ReturnObject<>(sku.getOriginalPrice());
        }
    }

    public SimpleGoodsSkuDTO getSimpleSkuBySkuId(Long skuId) {
        GoodsSkuPo skuPo=skuMapper.selectByPrimaryKey(skuId);
        SimpleGoodsSkuDTO simpleGoodsSkuDTO = null;
        if(skuPo != null) {
            simpleGoodsSkuDTO=new SimpleGoodsSkuDTO();
            simpleGoodsSkuDTO.setDisabled(skuPo.getDisabled());
            simpleGoodsSkuDTO.setId(skuPo.getId());
            simpleGoodsSkuDTO.setImageUrl(skuPo.getImageUrl());
            simpleGoodsSkuDTO.setInventory(skuPo.getInventory());
            simpleGoodsSkuDTO.setName(skuPo.getName());
            simpleGoodsSkuDTO.setOriginalPrice(skuPo.getOriginalPrice());
            simpleGoodsSkuDTO.setSkuSn(skuPo.getSkuSn());
        }
        return simpleGoodsSkuDTO;
    }

    public ReturnObject<GoodsDetailDTO> modifyInventory(Long skuId, Integer quantity) {
        GoodsDetailDTO dto=new GoodsDetailDTO();
        //看看floatprice要不要改
        FloatPricePoExample example = new FloatPricePoExample();
        FloatPricePoExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsSkuIdEqualTo(skuId);
        criteria.andBeginTimeLessThanOrEqualTo(LocalDateTime.now());
        criteria.andEndTimeGreaterThan(LocalDateTime.now());
        criteria.andGoodsSkuIdEqualTo(skuId);
        criteria.andInvalidByEqualTo(FloatPrice.Validation.VALID.getCode().longValue());

        List<FloatPricePo> floatPricePos = new ArrayList<>();
        try {
            floatPricePos = floatPricePoMapper.selectByExample(example);
        } catch (Exception e) {
            StringBuilder message1 = new StringBuilder().append("getPriceBySkuId: ").append(e.getMessage());
            logger.error(message1.toString());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }

        //有floatPrice
        if(floatPricePos!=null&&floatPricePos.size()>0)
        {
            FloatPricePo floatPricePo=floatPricePos.get(0);
            if(floatPricePo.getQuantity()-quantity<0)
                return new ReturnObject<>(ResponseCode.SKU_NOTENOUGH);
            dto.setInventory(floatPricePo.getQuantity());
            dto.setPrice(floatPricePo.getActivityPrice());
            floatPricePo.setQuantity(floatPricePo.getQuantity()-quantity);

            try{
                int ret=floatPricePoMapper.updateByPrimaryKeySelective(floatPricePo);
                if(ret==0)
                {
                    logger.debug("modifyInventory fail:floatPricePo="+floatPricePo);
                    return new ReturnObject<>(ResponseCode.FIELD_NOTVALID, String.format("sku不合法：" + skuId));
                }
                else  return new ReturnObject<>();
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


        String key="sku_"+skuId;
        GoodsSkuPo skuPo=new GoodsSkuPo();
        Integer inventory;
        //在redis里
        if(redisTemplate.opsForHash().hasKey(key,"quantity"))
        {
            //库存充足
            inventory= (Integer) redisTemplate.opsForHash().get(key,"quantity");
            if(inventory<quantity)return new ReturnObject<>(ResponseCode.SKU_NOTENOUGH);

            //设置SKU
            skuPo.setId(skuId);

            //更新redis
            redisTemplate.opsForHash().delete(key,"quantity");
            redisTemplate.opsForHash().put(key,"quantity",inventory-quantity);
        }
        else//不在redis里
        {
            //SKU存在
            skuPo=skuMapper.selectByPrimaryKey(skuId);
            if(skuPo==null||GoodsSku.State.getTypeByCode(skuPo.getDisabled().intValue()).equals(GoodsSku.State.DELETED))return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);

            //库存充足
            inventory= skuPo.getInventory()*9/10;
            if(inventory<quantity)return new ReturnObject<>(ResponseCode.SKU_NOTENOUGH);

            //添加SKU库存
            redisTemplate.opsForHash().put(key,"quantity",inventory-quantity);
            redisTemplate.opsForHash().put(key,"price",skuPo.getOriginalPrice());
        }

        //设置dto
        if(dto.getInventory()==null||dto.getInventory()<=0)
        {
            dto.setInventory(inventory);
            dto.setPrice((Long) redisTemplate.opsForHash().get(key,"price"));
        }

        //修改SKU库存
        skuPo.setInventory(inventory-quantity);
        String json = JacksonUtil.toJson(skuPo);
        Message message = MessageBuilder.withPayload(json).build();
        logger.info("sendLogMessage: message = " + message);
        rocketMQTemplate.asyncSend("sku-topic", message, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                System.out.println(sendResult.getSendStatus());
            }

            @Override
            public void onException(Throwable e) {
                System.out.println(e.getMessage());
            }
        });


        return new ReturnObject<>(dto);
    }

    public void updateByPrimaryKeySelective(GoodsSkuPo skuPo) {
        try{
            skuMapper.updateByPrimaryKeySelective(skuPo);
        }
        catch (Exception e)
        {
            logger.error("严重错误：" + e.getMessage());
        }
    }

    public ReturnObject<GoodsFreightDTO> getGoodsFreightDetailBySkuId(Long skuId) {
        GoodsSkuPo skuPo=skuMapper.selectByPrimaryKey(skuId);
        if(skuPo==null)return new ReturnObject<>(null);
        GoodsSpuPo spuPo= spuMapper.selectByPrimaryKey(skuPo.getGoodsSpuId());
        if(spuPo==null)return new ReturnObject<>(null);
        GoodsFreightDTO dto=new GoodsFreightDTO();
        dto.setFreightModelId(spuPo.getFreightId());
        dto.setShopId(spuPo.getShopId());
        dto.setWeight(skuPo.getWeight());
        return new ReturnObject<>(dto);
    }

    public ReturnObject<ShopDetailDTO> getShopInfoBySkuId(Long skuId) {
        GoodsSkuPo skuPo=skuMapper.selectByPrimaryKey(skuId);
        if(skuPo==null)return new ReturnObject<>(null);
        GoodsSpuPo spuPo= spuMapper.selectByPrimaryKey(skuPo.getGoodsSpuId());
        if(spuPo==null)return new ReturnObject<>(null);
        ShopPo shopPo=shopMapper.selectByPrimaryKey(spuPo.getShopId());
        if(shopPo==null)return new ReturnObject<>(null);
        ShopDetailDTO dto=new ShopDetailDTO();
        dto.setGmtCreate(shopPo.getGmtCreate().toString());
        dto.setGmtModified(shopPo.getGmtModified().toString());
        dto.setName(shopPo.getName());
        dto.setState(shopPo.getState());
        return new ReturnObject<>(dto);
    }

//    public List<Long> getSkuIdsBySpuId(Long spuId) {
//        GoodsSkuPoExample example = new GoodsSkuPoExample();
//        GoodsSkuPoExample.Criteria criteria = example.createCriteria();
//        criteria.andGoodsSpuIdEqualTo(spuId);
//
//    }

    public ReturnObject<List<Long>> getSkuIdsBySpuId(Long spuId) {
        List<Long>skuIds=new ArrayList<>();
        GoodsSkuPoExample skuExample=new GoodsSkuPoExample();
        GoodsSkuPoExample.Criteria skuCriteria=skuExample.createCriteria();
        skuCriteria.andGoodsSpuIdEqualTo(spuId);
        List<GoodsSkuPo> skuPos=skuMapper.selectByExample(skuExample);
        //查到该spu下的sku
        if(skuPos!=null&&skuPos.size()>0)
            skuIds.addAll(skuPos.stream().map(GoodsSkuPo::getId).collect(Collectors.toList()));
        return new ReturnObject<>(skuIds);
    }

    /*
     *将所有运费模板值为freightId的spu改为默认运费模板
     */
    public ReturnObject<Boolean> updateSpuFreightId(Long freightModelId) {
        if(freightModelId!=null){
            GoodsSpuPoExample spuPoExample=new GoodsSpuPoExample();
            GoodsSpuPoExample.Criteria spuCriteria=spuPoExample.createCriteria();
            spuCriteria.andFreightIdEqualTo(freightModelId);
            List<GoodsSpuPo> spuPos=spuMapper.selectByExample(spuPoExample);
            if(spuPos!=null&&spuPos.size()>0)
            {//将spu设置为默认运费模板
                for(GoodsSpuPo po:spuPos)
                {
                    po.setFreightId(null);
                    //写回
                    spuMapper.updateByPrimaryKey(po);
                }
                return new ReturnObject<>(true);
            }
        }
        return new ReturnObject<>(false);
    }
}
