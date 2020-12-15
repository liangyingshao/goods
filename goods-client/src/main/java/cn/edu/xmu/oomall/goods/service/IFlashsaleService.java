package cn.edu.xmu.oomall.goods.service;

import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.oomall.goods.model.GoodsDetailDTO;

public interface IFlashsaleService {
    
    /**
     * 删除时间段对应的所有flashsale
     */
    ReturnObject deleteSegmentFlashsale(Long id);

    ReturnObject<GoodsDetailDTO> modifyFlashsaleItem(Long skuId, Integer quantity);
}