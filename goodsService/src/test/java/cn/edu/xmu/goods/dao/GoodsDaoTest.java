package cn.edu.xmu.goods.dao;

import cn.edu.xmu.goods.GoodsServiceApplication;
import cn.edu.xmu.goods.model.po.GoodsSkuPo;
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
        PageInfo<GoodsSkuPo>skuPos=goodsDao.getSkuList(null,null, (long) 273,"drh-d0001",1,5);
        assertEquals(skuPos.getList().size(),1);
        skuPos=goodsDao.getSkuList(null,null, null,null,1,5);
        assertEquals(skuPos.getList().size(),5);
        skuPos=goodsDao.getSkuList(null,null, null,null,2,5);
        assertEquals(skuPos.getList().size(),5);
        skuPos=goodsDao.getSkuList(null,null, (long) 273,null,1,5);
        assertEquals(skuPos.getList().size(),1);
    }

}
