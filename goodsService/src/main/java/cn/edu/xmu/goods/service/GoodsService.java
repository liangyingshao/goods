package cn.edu.xmu.goods.service;

import cn.edu.xmu.goods.dao.GoodsDao;
import cn.edu.xmu.goods.model.bo.GoodsSku;
import cn.edu.xmu.goods.model.po.GoodsSkuPo;
import cn.edu.xmu.goods.model.po.ShopPo;
import cn.edu.xmu.goods.model.vo.GoodsSkuRetVo;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoodsService {

    @Autowired
    GoodsDao goodsDao;

    @Transactional
    public ReturnObject modifyShop(Long id, String name) {

        return goodsDao.modifyShop(id,name);

    }

    /**
     *查询SKU
     * @param shopId
     * @param skuSn
     * @param spuId
     * @param spuSn
     * @param page
     * @param pageSize
     * @return ReturnObject<PageInfo<VoObject>>
     */
    @Transactional
    public ReturnObject<PageInfo<VoObject>> getSkuList(Long shopId, String skuSn, Long spuId, String spuSn, Integer page, Integer pageSize)
    {
        PageInfo<GoodsSkuPo> skuPos=goodsDao.getSkuList(shopId,skuSn,spuId,spuSn,page,pageSize);
        List<VoObject> skus = skuPos.getList().stream().map(GoodsSku::new).collect(Collectors.toList());

        PageInfo<VoObject> returnObject = new PageInfo<>(skus);
        returnObject.setPages(skuPos.getPages());
        returnObject.setPageNum(skuPos.getPageNum());
        returnObject.setPageSize(skuPos.getPageSize());
        returnObject.setTotal(skuPos.getTotal());

        return new ReturnObject<>(returnObject);
    }
}
