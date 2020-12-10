package cn.edu.xmu.flashsale.dao;

import cn.edu.xmu.flashsale.mapper.FlashSaleItemPoMapper;
import cn.edu.xmu.flashsale.model.po.FlashSaleItemPo;
import cn.edu.xmu.flashsale.model.po.FlashSaleItemPoExample;
import cn.edu.xmu.flashsale.model.vo.FlashsaleItemRetVo;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
@Slf4j
public class FlashSaleItemDao {

    private static final Logger logger = LoggerFactory.getLogger(FlashSaleDao.class);

    @Autowired
    private FlashSaleItemPoMapper flashSaleItemPoMapper;

    public ReturnObject<FlashsaleItemRetVo> addSKUofTopic(Long id, Long skuId, Long price, Integer quantity) {
        try {
            FlashSaleItemPo po = new FlashSaleItemPo();
            po.setGmtCreate(LocalDateTime.now());
            po.setGoodsSkuId(skuId);
            po.setSaleId(id);
            po.setPrice(price);
            po.setQuantity(quantity);
            int ret = flashSaleItemPoMapper.insert(po);
            if(ret == 0)
            {
                return new ReturnObject(ResponseCode.FIELD_NOTVALID);
            }
            FlashSaleItemPoExample example = new FlashSaleItemPoExample();
            return new ReturnObject<>(new FlashsaleItemRetVo(po));
        } catch (DataAccessException e)
        {
            // 其他数据库错误
            logger.debug("other sql exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
    }
}
