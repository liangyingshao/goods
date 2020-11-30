package cn.edu.xmu.goods.dao;

import cn.edu.xmu.goods.mapper.GoodsSkuPoMapper;
import cn.edu.xmu.goods.mapper.GoodsSpuPoMapper;
import cn.edu.xmu.goods.model.bo.GoodsSku;
import cn.edu.xmu.goods.model.po.*;
import cn.edu.xmu.goods.model.vo.GoodsSkuRetVo;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class GoodsDao {

    private static final Logger logger = LoggerFactory.getLogger(GoodsDao.class);
    @Autowired
    private GoodsSkuPoMapper skuMapper;
    @Autowired
    private GoodsSpuPoMapper spuMapper;

    public void initialize() throws Exception {
        //初始化sku
        GoodsSkuPoExample example = new GoodsSkuPoExample();
        GoodsSkuPoExample.Criteria criteria = example.createCriteria();

        List<GoodsSkuPo> skuPos = skuMapper.selectByExample(example);

        for (GoodsSkuPo po : skuPos) {
            GoodsSkuPo newPo = new GoodsSkuPo();
            newPo.setSkuSn(po.getSkuSn());
            newPo.setGoodsSpuId(po.getGoodsSpuId());
            newPo.setId(po.getId());
            skuMapper.updateByPrimaryKeySelective(newPo);
        }
        //初始化spu
        GoodsSpuPoExample example1 = new GoodsSpuPoExample();
        GoodsSpuPoExample.Criteria criteria1 = example1.createCriteria();

        List<GoodsSpuPo> spuPos = spuMapper.selectByExample(example1);

        for (GoodsSpuPo po : spuPos) {
            GoodsSpuPo newPo = new GoodsSpuPo();
            newPo.setId(po.getId());
            newPo.setGoodsSn(po.getGoodsSn());
            newPo.setShopId(po.getShopId());
            spuMapper.updateByPrimaryKeySelective(newPo);
        }
    }
    public ReturnObject<ShopPo> modifyShop(Long id, String name)
    {
        return new ReturnObject<>(ResponseCode.OK);
    }

    /**
     * 查询SKU
     * @param shopId
     * @param skuSn
     * @param spuId
     * @param spuSn
     * @param page
     * @param pageSize
     * @return PageInfo<GoodsSkuPo>
     */
    public PageInfo<GoodsSkuPo> getSkuList(Long shopId,String skuSn,Long spuId,String spuSn,Integer page,Integer pageSize)
    {
        GoodsSkuPoExample skuExample=new GoodsSkuPoExample();
        GoodsSkuPoExample.Criteria skuCriteria=skuExample.createCriteria();
        if(skuSn!=null&&!skuSn.isBlank())skuCriteria.andSkuSnEqualTo(skuSn);
        if(spuId!=null)skuCriteria.andGoodsSpuIdEqualTo(spuId);
        List<GoodsSkuPo> skuPos=new ArrayList<>();
        PageHelper.startPage(page,pageSize);
        logger.debug("page="+page+" pageSize="+pageSize);
        if((spuSn!=null&&!spuSn.isBlank())||shopId!=null)
        {
            GoodsSpuPoExample spuExample=new GoodsSpuPoExample();
            GoodsSpuPoExample.Criteria spuCriteria= spuExample.createCriteria();
            if(!spuSn.isBlank())spuCriteria.andGoodsSnEqualTo(spuSn);
            if(shopId!=null)spuCriteria.andShopIdEqualTo(shopId);
            List<GoodsSpuPo> spuPos=spuMapper.selectByExample(spuExample);
            for (int i=0;i<spuPos.size();++i)
            {
                skuCriteria.andGoodsSpuIdEqualTo(spuPos.get(i).getId());
                if(i==0)skuPos=skuMapper.selectByExample(skuExample);
                else skuPos.addAll(skuMapper.selectByExample(skuExample));
            }
        }
        else skuPos=skuMapper.selectByExample(skuExample);
        return new PageInfo<>(skuPos);
    }

    /**
     * 获得sku的详细信息
     * @param id
     * @return GoodsSkuPo
     */
    public GoodsSkuPo getSku(Long id)
    {
        return skuMapper.selectByPrimaryKey(id);
    }
}
