package cn.edu.xmu.goods.service.impl;

import cn.edu.xmu.goods.dao.GoodsDao;
import cn.edu.xmu.goods.model.vo.GoodsSkuDetailRetVo;
import cn.edu.xmu.goods.model.vo.GoodsSkuRetVo;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.oomall.goods.model.GoodsInfoDTO;
import cn.edu.xmu.oomall.goods.model.SkuInfoDTO;
import cn.edu.xmu.oomall.goods.model.SkuNameInfoDTO;
import cn.edu.xmu.oomall.goods.service.IGoodsService;
//import com.alibaba.dubbo.config.annotation.Service;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

//@Component
@DubboService
public class IGoodsServiceImpl implements IGoodsService {
    @Autowired
    GoodsDao goodsDao;

    @Override
    public ReturnObject<List<Long>> getAllSkuIdByShopId(Long shopId) {
        return null;
    }

    @Override
    public ReturnObject<Long> getShopIdBySkuId(Long skuId) {
        return null;
    }

    @Override
    public ReturnObject<Boolean> getVaildSkuId(Long skuId) {
        return null;
    }

    @Override
    public ReturnObject<SkuInfoDTO> getSelectSkuInfoBySkuId(Long skuId) {
        return null;
    }

    @Override
    public ReturnObject<Map<Long, SkuInfoDTO>> listSelectSkuInfoById(List<Long> skuIdList) {
        return null;
    }

    @Override
    public ReturnObject<GoodsInfoDTO> getSelectGoodsInfoBySkuId(Long skuId) {
        return null;
    }

    @Override
    public ReturnObject<List<SkuNameInfoDTO>> getSelectSkuNameListBySkuIdList(List<Long> idList) {
        List<SkuNameInfoDTO> nameList = null;
        for (int i=0;i<idList.size();i++)
        {
            GoodsSkuDetailRetVo goodsSkuRetVo = goodsDao.getSku(idList.get(i));
            SkuNameInfoDTO skuNameInfoDTO = new SkuNameInfoDTO();
            skuNameInfoDTO.setId(skuNameInfoDTO.getId());
            skuNameInfoDTO.setName(goodsSkuRetVo.getName());
            nameList.add(skuNameInfoDTO);
        }
        return new ReturnObject<List<SkuNameInfoDTO>>(nameList);
    }

}
