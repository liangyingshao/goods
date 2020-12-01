package cn.edu.xmu.goods.dao;

import cn.edu.xmu.goods.GoodsServiceApplication;
import cn.edu.xmu.goods.model.bo.FloatPrice;
import cn.edu.xmu.goods.model.bo.GoodsSku;
import cn.edu.xmu.goods.model.po.GoodsSkuPo;
import cn.edu.xmu.goods.model.vo.GoodsSkuVo;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import com.github.pagehelper.PageInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest(classes = GoodsServiceApplication.class)
@Transactional
class GoodsDaoTest {

    @Autowired
    private GoodsDao goodsDao;

    @Test
    void getSkuList() {
        PageInfo<GoodsSkuPo>skuPos=goodsDao.getSkuList((long)0,null, (long) 273,"drh-d0001",1,5);
        assertEquals(skuPos.getList().size(),1);
        skuPos=goodsDao.getSkuList(null,null, null,null,1,5);
        assertEquals(skuPos.getList().size(),5);
        skuPos=goodsDao.getSkuList(null,null, null,null,2,5);
        assertEquals(skuPos.getList().size(),5);
        skuPos=goodsDao.getSkuList(null,null, (long) 273,null,1,5);
        assertEquals(skuPos.getList().size(),1);
    }

    @Test
    void getSku() {
        GoodsSkuPo skuPo=goodsDao.getSku((long) 273);
        assertEquals(skuPo.getName(),"+");
        assertEquals(skuPo.getGoodsSpuId(),(long)273);
    }

    @Test
    void logicalDelete() {
        ReturnObject returnObject=goodsDao.logicalDelete((long)0,(long)273);
        assertEquals(returnObject.getCode(),ResponseCode.OK);
        returnObject=goodsDao.logicalDelete((long)1,(long)273);
        assertEquals(returnObject.getCode(),ResponseCode.RESOURCE_ID_NOTEXIST);
        returnObject=goodsDao.logicalDelete((long)0,(long)1);
        assertEquals(returnObject.getCode(),ResponseCode.RESOURCE_ID_NOTEXIST);
    }

    @Test
    void modifySku() {
        GoodsSku sku=new GoodsSku();
        sku.setId((long)273);
        sku.setName("name");
        sku.setInventory(9999);
        sku.setOriginalPrice((long) 100);
        sku.setConfiguration("configuration");
        sku.setWeight((long) 100);
        sku.setDetail("detail");
        ReturnObject returnObject=goodsDao.modifySku((long)0,sku);
        assertEquals(returnObject.getCode(),ResponseCode.OK);

        returnObject=goodsDao.modifySku((long)1,sku);
        assertEquals(returnObject.getCode(),ResponseCode.RESOURCE_ID_NOTEXIST);

    }

    @Test
    void uploadSkuImg()
    {
        GoodsSku sku=new GoodsSku();
        sku.setId((long)273);
        sku.setImageUrl("http://47.52.88.176/file/images/201610/file_57fae8f7240c6.jpg");
        ReturnObject returnObject=goodsDao.uploadSkuImg(sku);
        assertEquals(returnObject.getCode(),ResponseCode.OK);
    }

    @Test
    void addFloatPrice()
    {
        LocalDateTime beginTime= LocalDateTime.of(2020,12,12,10,0,0);
        LocalDateTime endTime=LocalDateTime.of(2020,12,30,10,0,0);
        System.out.println(beginTime);
        FloatPrice floatPrice=new FloatPrice();
        floatPrice.setGoodsSkuId((long)273);
        floatPrice.setActivityPrice((long) 100);
        floatPrice.setBeginTime(beginTime);
        floatPrice.setEndTime(endTime);
        floatPrice.setQuantity(9999);
        ReturnObject returnObject=goodsDao.addFloatPrice((long)0,floatPrice);
        assertEquals(returnObject.getCode(),ResponseCode.OK);

        returnObject=goodsDao.addFloatPrice((long)1,floatPrice);
        assertEquals(returnObject.getCode(),ResponseCode.RESOURCE_ID_NOTEXIST);
    }
}
