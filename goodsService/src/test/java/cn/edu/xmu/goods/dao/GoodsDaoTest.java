package cn.edu.xmu.goods.dao;

import cn.edu.xmu.goods.GoodsServiceApplication;
import cn.edu.xmu.goods.model.bo.FloatPrice;
import cn.edu.xmu.goods.model.bo.GoodsSku;
import cn.edu.xmu.goods.model.bo.GoodsSpu;
import cn.edu.xmu.goods.model.vo.GoodsSkuDetailRetVo;
import cn.edu.xmu.goods.model.vo.GoodsSkuRetVo;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import com.github.pagehelper.PageInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest(classes = GoodsServiceApplication.class)
@Transactional
class GoodsDaoTest {

    @Autowired
    private GoodsDao goodsDao;

    @Autowired
    private GoodsSpuDao spuDao;

    @Test
    void getSkuList() {
        PageInfo<GoodsSkuRetVo>skus=goodsDao.getSkuList((long)0,null, (long) 273,"drh-d0001",1,5);
        assertEquals(skus.getList().size(),1);
        skus=goodsDao.getSkuList(null,null, null,null,1,5);
        assertEquals(skus.getList().size(),5);
        skus=goodsDao.getSkuList(null,null, null,null,2,5);
        assertEquals(skus.getList().size(),5);
        skus=goodsDao.getSkuList(null,null, (long) 273,null,1,5);
        assertEquals(skus.getList().size(),1);
    }

    @Test
    void getSku() {
        GoodsSkuDetailRetVo skuDetailRetVo=goodsDao.getSku((long) 273);
        assertEquals(skuDetailRetVo.getName(),"+");
        assertEquals(skuDetailRetVo.getSpu().getId(),273);

        goodsDao.logicalDelete((long)0,(long)273);
        skuDetailRetVo=goodsDao.getSku((long) 273);
        assertEquals(skuDetailRetVo,null);

        skuDetailRetVo=goodsDao.getSku((long) 1);
        assertEquals(skuDetailRetVo,null);
    }

    @Test
    void logicalDelete() {

        ReturnObject returnObject=goodsDao.logicalDelete((long)1,(long)273);
        assertEquals(returnObject.getCode(),ResponseCode.RESOURCE_ID_OUTSCOPE);

        returnObject=goodsDao.logicalDelete((long)0,(long)273);
        assertEquals(returnObject.getCode(),ResponseCode.OK);

        returnObject=goodsDao.logicalDelete((long)0,(long)273);
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
        assertEquals(returnObject.getCode(),ResponseCode.RESOURCE_ID_OUTSCOPE);

        goodsDao.logicalDelete((long)0,(long)273);
        returnObject=goodsDao.modifySku((long)0,sku);
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

        goodsDao.logicalDelete((long)0,(long)273);
        returnObject=goodsDao.uploadSkuImg(sku);
        assertEquals(returnObject.getCode(),ResponseCode.RESOURCE_ID_NOTEXIST);
    }


    @Test
    void addFloatPrice()
    {
        LocalDateTime beginTime= LocalDateTime.of(2020,12,12,10,0,0);
        LocalDateTime endTime=LocalDateTime.of(2020,12,30,10,0,0);
        System.out.println(beginTime);
        FloatPrice floatPrice=new FloatPrice();
        floatPrice.setGoodsSkuId((long)278);
        floatPrice.setActivityPrice((long) 100);
        floatPrice.setBeginTime(beginTime);
        floatPrice.setEndTime(endTime);
        floatPrice.setQuantity(100);
        floatPrice.setValid(FloatPrice.Validation.VALID);
        floatPrice.setCreatedBy((long)0);
        ReturnObject returnObject=goodsDao.addFloatPrice((long)0,floatPrice, (long)0);
        assertEquals(returnObject.getCode(),ResponseCode.OK);

        returnObject=goodsDao.addFloatPrice((long)0,floatPrice, (long)0);
        assertEquals(returnObject.getCode(),ResponseCode.SKUPRICE_CONFLICT);

        returnObject=goodsDao.addFloatPrice((long)1,floatPrice, (long)0);
        assertEquals(returnObject.getCode(),ResponseCode.RESOURCE_ID_OUTSCOPE);

        floatPrice.setGoodsSkuId((long)273);
        returnObject=goodsDao.addFloatPrice((long)0,floatPrice, (long)0);
        assertEquals(returnObject.getCode(),ResponseCode.SKU_NOTENOUGH);
    }

    @Test
    void createSKU() {
        GoodsSku sku=new GoodsSku();
        sku.setGoodsSpuId((long)273);
        sku.setSkuSn("skuSn");
        sku.setName("newSku");
        sku.setOriginalPrice((long)100);
        sku.setConfiguration("configuration");
        sku.setWeight((long)100);
        sku.setImageUrl("http://47.52.88.176/file/images/201612/file_586227f3cd5c9.jpg");
        sku.setInventory(9999);
        sku.setDetail("detail");
        sku.setDisabled(GoodsSku.State.ABLE);
        ReturnObject<GoodsSkuRetVo> returnObject=goodsDao.createSKU((long)0,sku);
        assertEquals(returnObject.getCode(),ResponseCode.OK);

        returnObject=goodsDao.createSKU((long)0,sku);
        assertEquals(returnObject.getCode(),ResponseCode.SKUSN_SAME);

        returnObject=goodsDao.createSKU((long)1,sku);
        assertEquals(returnObject.getCode(),ResponseCode.RESOURCE_ID_OUTSCOPE);
    }
}
