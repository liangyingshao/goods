package cn.edu.xmu.goods.dao;

import cn.edu.xmu.goods.mapper.FloatPricePoMapper;
import cn.edu.xmu.goods.model.bo.FloatPrice;
import cn.edu.xmu.goods.model.po.FloatPricePo;
import cn.edu.xmu.goods.model.po.FloatPricePoExample;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FloatPriceDao {
    @Autowired
    private FloatPricePoMapper floatPricePoMapper;

    private static final Logger logger = LoggerFactory.getLogger(FloatPriceDao.class);

    public ReturnObject<FloatPrice> add_floating_price(FloatPrice floatPrice) {
        FloatPricePo floatPricePo = floatPrice.getFloatPricePo();
        ReturnObject<FloatPrice> retObj = null;
        try{
//            //先查出SKU对应的所有未来时间段的价格浮动项,按照开始时间从早到晚排序
//            FloatPricePoExample example = new FloatPricePoExample();
//            FloatPricePoExample.Criteria criteria = example.createCriteria();
//            criteria.andGoodsSkuIdEqualTo(floatPrice.getGoodsSkuId());
//            criteria.andBeginTimeGreaterThanOrEqualTo(LocalDateTime.now());
//            example.setOrderByClause("begin_time ASC");
//            List<FloatPricePo> floatPricePos = floatPricePoMapper.selectByExample(example);
//            //循环遍历找出开始时间 晚于 当前插入数据开始时间者before
//            int pos=0;
//            for (;pos<floatPricePos.size();pos++)
//            {
//                if(floatPricePos.get(pos).getBeginTime().isAfter(floatPricePo.getBeginTime()))
//                {
//                    break;
//                }
//            }
//            //检查before-1的结束时间>插入数据开始时间&&插入数据的结束时间<before的开始时间
//            if(floatPricePos==null || (pos==0||floatPricePos.get(pos).getEndTime().isBefore(floatPrice.getBeginTime()))&&floatPricePos.get(pos).getBeginTime().isAfter(floatPrice.getEndTime()))
//            {
//                //找到满足条件者，数据满足插入条件
//                int ret = floatPricePoMapper.insertSelective(floatPricePo);
//                if (ret == 0) {
//                    //插入失败
//                    retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("新增失败"));
//                } else {
//                    //插入成功
//                    floatPrice.setId(floatPricePo.getId());
//                    retObj = new ReturnObject<>(floatPrice);
//                }
//            }
//            else{
//                //否则返回商品浮动价格时间冲突错误902
//                retObj = new ReturnObject<>(ResponseCode.SKUPRICE_CONFLICT, String.format("新增失败"));
//            }

            //查出beginTime有没有介于某个已存在的时段里
            FloatPricePoExample beginExample = new FloatPricePoExample();
            FloatPricePoExample.Criteria beginCriteria = beginExample.createCriteria();
            beginCriteria.andGoodsSkuIdEqualTo(floatPrice.getGoodsSkuId());
            beginCriteria.andBeginTimeLessThan(floatPrice.getBeginTime());
            beginCriteria.andEndTimeGreaterThan(floatPrice.getBeginTime());
            List<FloatPricePo> floatPricePos1 = floatPricePoMapper.selectByExample(beginExample);
            //查出endTime有没有介于某个已存在的时段里
            FloatPricePoExample endExample = new FloatPricePoExample();
            FloatPricePoExample.Criteria endCriteria = beginExample.createCriteria();
            endCriteria.andGoodsSkuIdEqualTo(floatPrice.getGoodsSkuId());
            endCriteria.andBeginTimeLessThan(floatPrice.getBeginTime());
            endCriteria.andEndTimeGreaterThan(floatPrice.getBeginTime());
            List<FloatPricePo> floatPricePos2 = floatPricePoMapper.selectByExample(beginExample);
            if(floatPricePos1.size()==0&&floatPricePos2.size()==0)
            {
                //找到满足条件者，数据满足插入条件
                int ret = floatPricePoMapper.insertSelective(floatPricePo);
                if (ret == 0) {
                    //插入失败
                    retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("新增失败"));
                } else {
                    //插入成功
                    floatPrice.setId(floatPricePo.getId());
                    retObj = new ReturnObject<>(floatPrice);
                }
            }
            else{
                //否则返回商品浮动价格时间冲突错误902
                retObj = new ReturnObject<>(ResponseCode.SKUPRICE_CONFLICT, String.format("新增失败"));
            }
        }
        catch (DataAccessException e) {
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
        return retObj;
    }

    public ReturnObject<Object> invalidFloatPrice(Long id){
        FloatPricePo floatPricePo = floatPricePoMapper.selectByPrimaryKey(id);
        //价格浮动项不存在
        if(floatPricePo==null || FloatPrice.Validation.getTypeByCode(floatPricePo.getValid().intValue())==FloatPrice.Validation.INVALID)
        {
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }

        try
        {
            byte valid=1;
            floatPricePo.setValid(valid);
            int ret = floatPricePoMapper.insertSelective(floatPricePo);
            if (ret == 0)
            {
                //修改失败,一般来说不会这样的
                logger.debug("addFloatPrice: insert floatPrice fail : " + floatPricePo.toString());
                return new ReturnObject<>(ResponseCode.FIELD_NOTVALID, String.format("floatPrice字段不合法：" + floatPricePo.toString()));
            }
            else {
                //修改成功
                logger.debug("addFloatPrice: insert floatPrice = " + floatPricePo.toString());
                return new ReturnObject<>(ResponseCode.OK);
            }
        }catch (DataAccessException e)
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
}
