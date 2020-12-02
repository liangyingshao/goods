package cn.edu.xmu.goods.dao;

import cn.edu.xmu.goods.mapper.FloatPricePoMapper;
import cn.edu.xmu.goods.model.bo.FloatPrice;
import cn.edu.xmu.goods.model.po.CommentPo;
import cn.edu.xmu.goods.model.po.CommentPoExample;
import cn.edu.xmu.goods.model.po.FloatPricePo;
import cn.edu.xmu.goods.model.po.FloatPricePoExample;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

@Repository
public class FloatPriceDao {
    @Autowired
    private FloatPricePoMapper floatPricePoMapper;

    private static final Logger logger = LoggerFactory.getLogger(FloatPriceDao.class);

    public ReturnObject<FloatPrice> add_floating_price(FloatPrice floatPrice) {
        FloatPricePo floatPricePo = floatPrice.getFloatPricePo();
        ReturnObject<FloatPrice> retObj = null;
        try{
            //先查出SKU对应的所有未来时间段的价格浮动项,按照开始时间从早到晚排序
            FloatPricePoExample example = new FloatPricePoExample();
            FloatPricePoExample.Criteria criteria = example.createCriteria();
            criteria.andGoodsSkuIdEqualTo(floatPrice.getGoodsSkuId());
            criteria.andBeginTimeGreaterThanOrEqualTo(LocalDateTime.now());
            example.setOrderByClause("begin_time ASC");
            List<FloatPricePo> floatPricePos = floatPricePoMapper.selectByExample(example);
            //循环遍历找出开始时间大于当前插入数据开始时间者before

            //检查before-1的结束时间>插入数据开始时间&&插入数据的结束时间<before的开始时间
                //找到满足条件者，数据满足插入条件
                //否则返回商品浮动价格时间冲突错误902
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
}
