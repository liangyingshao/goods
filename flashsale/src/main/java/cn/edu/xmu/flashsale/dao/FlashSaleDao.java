package cn.edu.xmu.flashsale.dao;

import cn.edu.xmu.flashsale.mapper.FlashSalePoMapper;
import cn.edu.xmu.flashsale.model.po.FlashSalePo;
import cn.edu.xmu.flashsale.model.po.FlashSalePoExample;
import cn.edu.xmu.flashsale.model.vo.FlashsaleNewRetVo;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@Slf4j
public class FlashSaleDao {

    private static final Logger logger = LoggerFactory.getLogger(FlashSaleDao.class);

    @Autowired
    private FlashSalePoMapper flashSalePoMapper;

    public ReturnObject<FlashsaleNewRetVo> createflash(Long id, LocalDateTime flashDate) {
        //查询数据库中是否有对应日期时段的记录存在
        FlashSalePoExample example = new FlashSalePoExample();
        FlashSalePoExample.Criteria criteria = example.createCriteria();
        criteria.andTimeSegIdEqualTo(id);
        criteria.andFlashDateEqualTo(flashDate);
        List<FlashSalePo> flashSalePoList = flashSalePoMapper.selectByExample(example);
        //已经有秒杀活动，返回时段冲突错误
        if(flashSalePoList.size() != 0)
        {
            return new ReturnObject<>(ResponseCode.TIMESEG_CONFLICT);
        }
        //如果没有，插入数据库
        try
        {
            FlashSalePo flashSalePo = new FlashSalePo();
            flashSalePo.setFlashDate(flashDate);
            flashSalePo.setTimeSegId(id);
            flashSalePo.setGmtCreate(LocalDateTime.now());
            int ret = flashSalePoMapper.insert(flashSalePo);
            if (ret == 0)
            {
                //修改失败
                logger.debug("addFloatPrice: insert floatPrice fail : " + flashSalePo.toString());
                return new ReturnObject<>(ResponseCode.FIELD_NOTVALID, String.format("floatPrice字段不合法：" + flashSalePo.toString()));
            }
            else {
                //修改成功
                logger.debug("addFloatPrice: insert floatPrice = " + flashSalePo.toString());
                //检验
                FlashSalePoExample flashSalePoExample=new FlashSalePoExample();
                FlashSalePoExample.Criteria flashsaleCriteria=flashSalePoExample.createCriteria();
                criteria.andFlashDateEqualTo(flashDate);
                criteria.andTimeSegIdEqualTo(id);
                List<FlashSalePo> checkList = flashSalePoMapper.selectByExample(flashSalePoExample);
                if(checkList.size()==0)
                    return new ReturnObject<>(ResponseCode.FIELD_NOTVALID);
                else {//构造FlashsaleNewRetVo
                    FlashsaleNewRetVo retVo=new FlashsaleNewRetVo(checkList.get(0));
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

    public ReturnObject deleteflashsale(Long id) {
        try
        {
            int ret = flashSalePoMapper.deleteByPrimaryKey(id);
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
}
