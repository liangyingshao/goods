package cn.edu.xmu.goods.service;

import cn.edu.xmu.goods.dao.FloatPriceDao;
import cn.edu.xmu.goods.model.bo.FloatPrice;
import cn.edu.xmu.ooad.util.ReturnObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FloatPriceService {
    @Autowired
    FloatPriceDao floatPriceDao;

    @Transactional
    public ReturnObject add_floating_price(FloatPrice floatPrice) {
        ReturnObject<FloatPrice> retObj = floatPriceDao.add_floating_price(floatPrice);
        return retObj;
    }

    @Transactional
    public ReturnObject invalidFloatPrice(Long id) {
        ReturnObject<Object> retObj = floatPriceDao.invalidFloatPrice(id);
        return retObj;
    }
}
