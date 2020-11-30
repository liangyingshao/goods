package cn.edu.xmu.goods.dao;

import cn.edu.xmu.goods.GoodsServiceApplication;
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
}
