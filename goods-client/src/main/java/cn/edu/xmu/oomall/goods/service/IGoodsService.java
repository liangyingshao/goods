package cn.edu.xmu.oomall.goods.service;

import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.oomall.goods.model.*;

import java.awt.datatransfer.Clipboard;
import java.util.List;
import java.util.Map;

/**
 * @author Qiuyan Qian
 * @date Created in 2020/12/01 09:23
 */
public interface IGoodsService {

    /**
     * 根据shopId获取当前shop所有skuId的list
     */
    ReturnObject<List<Long>> getAllSkuIdByShopId(Long shopId);

    /**
     * 根据skuid查询shopid
     */
    ReturnObject<Long> getShopIdBySkuId(Long skuId);

    /**
     * TODO 增加时查询是否存在用
     * 查询该skuId是否存在
     */
    ReturnObject<Boolean> getVaildSkuId(Long skuId);

    /**
     * 根据skuId查询该sku部分信息
     */
    ReturnObject<SkuInfoDTO> getSelectSkuInfoBySkuId(Long skuId);

    /**
     * 根据skuId的List查询所有sku具体信息，可只查一次拿回所有
     */
    ReturnObject<Map<Long, SkuInfoDTO>> listSelectSkuInfoById(List<Long> skuIdList);

    /**
     * 根据skuId查询该sku具体信息
     */
    ReturnObject<GoodsInfoDTO> getSelectGoodsInfoBySkuId(Long skuId);

    /**
     * 根据SkuId返回其id和name
     */
    List<SkuNameInfoDTO> getSelectSkuNameListBySkuIdList(List<Long> idList);

    ReturnObject checkSkuUsableBySkuShop(Long skuId,Long shopId);

    /**
     * 根据shopId返回其id和name
     */
    ReturnObject<SimpleShopDTO> getSimpleShopByShopId(Long shopId);

    ReturnObject<SimpleGoodsSkuDTO> getSimpleSkuBySkuId(Long skuId);

    ReturnObject<GoodsSpuPoDTO> getSpuBySpuId(Long spuId);

    List<SkuInfoDTO> getSelectSkuListBySkuIdList(List<Long> idList);

    //根据float_price更新库存

    /**
     * 应订单组要求添加
     */
    ReturnObject<ShopDetailDTO> getShopInfoByShopId(Long shopId);


    ReturnObject<ShopDetailDTO> getShopInfoBySkuId(Long skuId);

    /**
     * 根据skuId查找商品信息
     * @param skuId
     * @return GoodsDetailDTO
     * @author Cai Xinlu
     * @date 2020-12-09 17:03
     */
    ReturnObject<GoodsDetailDTO> getGoodsBySkuId(Long skuId);

    /**
     * 获得默认运费模板
     * @param skuId
     * @return GoodsFreightDTO
     */
    ReturnObject<GoodsFreightDTO> getGoodsFreightDetailBySkuId(Long skuId);


    /**
     * 根据skuId查找商品信息
     * 0普通活动或者可能是秒杀  1团购  2预售 3优惠券
     * quantity可正可负
     * @param skuId
     * @return GoodsDetailDTO
     * @author Cai Xinlu
     * @date 2020-12-09 17:03
     */
    ReturnObject<GoodsDetailDTO> getGoodsBySkuId(Long skuId, Byte type, Long activityId, Integer quantity);
}
