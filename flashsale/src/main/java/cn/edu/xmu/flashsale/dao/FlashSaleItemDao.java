package cn.edu.xmu.flashsale.dao;

import cn.edu.xmu.flashsale.mapper.FlashSaleItemPoMapper;
import cn.edu.xmu.flashsale.model.bo.FlashSaleItem;
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
import java.util.ArrayList;
import java.util.List;

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

    public ReturnObject deleteSKUofTopic(Long fid, Long id) {
        try
        {
            int ret = flashSaleItemPoMapper.deleteByPrimaryKey(id);
            if(ret == 0)
            {
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            }
            else
                return new ReturnObject(ResponseCode.OK);
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

    public ReturnObject<List<FlashSaleItem>> getSKUofTopic(Long id, Integer page, Integer pageSize) {
        FlashSaleItemPoExample example = new FlashSaleItemPoExample();
        FlashSaleItemPoExample.Criteria criteria = example.createCriteria();
        criteria.andSaleIdEqualTo(id);
//        PageHelper.startPage(page, pageSize);
        List<FlashSaleItemPo> flashSaleItemPoList = new ArrayList();
        try {
            flashSaleItemPoList = flashSaleItemPoMapper.selectByExample(example);
            List<FlashSaleItem> ret = new ArrayList<>(flashSaleItemPoList.size());
            for (FlashSaleItemPo po : flashSaleItemPoList) {
                FlashSaleItem flashSaleItem = new FlashSaleItem(po);
                ret.add(flashSaleItem);
            }
//            PageInfo<VoObject> flashsaleItemPage = PageInfo.of(ret);
            return new ReturnObject<>(ret);
        } catch (DataAccessException e){
            logger.error("selectAllPassComment: DataAccessException:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
    }

    public ReturnObject<List<FlashSaleItemPo>> selectByFlashsaleId(Long id) {
        FlashSaleItemPoExample example = new FlashSaleItemPoExample();
        FlashSaleItemPoExample.Criteria criteria = example.createCriteria();
        criteria.andSaleIdEqualTo(id);
        return new ReturnObject<>(flashSaleItemPoMapper.selectByExample(example));
    }

    public ReturnObject<FlashSaleItemPo> getByPrimaryKey(Long id) {
        try
        {
            FlashSaleItemPo ret = flashSaleItemPoMapper.selectByPrimaryKey(id);
            if(ret == null)
            {
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            }
            else
                return new ReturnObject(ret);
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

    public ReturnObject deleteBySaleId(Long id) {
        try {
            FlashSaleItemPoExample example = new FlashSaleItemPoExample();
            FlashSaleItemPoExample.Criteria criteria = example.createCriteria();
            criteria.andSaleIdEqualTo(id);
            flashSaleItemPoMapper.deleteByExample(example);
            return new ReturnObject(ResponseCode.OK);//因为是级联删除，所以id不一定会有对应的item，即使删除0行也正确
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
}
