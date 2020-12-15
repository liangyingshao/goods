package cn.edu.xmu.oomall.goods.service;

import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.oomall.goods.model.GoodsInfoDTO;
import cn.edu.xmu.oomall.goods.model.SkuInfoDTO;
import cn.edu.xmu.oomall.goods.model.*;

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
     * 足迹、收藏的api的返回需要
     * @personInCharge yang8miao
     */
    ReturnObject<SkuInfoDTO> getSelectSkuInfoBySkuId(Long skuId);

    /**
     * 根据skuId的List查询所有sku具体信息，可只查一次拿回所有
     * @description 暂时不用
     */
    ReturnObject<Map<Long, SkuInfoDTO>> listSelectSkuInfoById(List<Long> skuIdList);

    /**
     * 根据skuId查询该sku具体信息
     * 购物车的api的返回需要
     * @personInCharge yang8miao
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

    /**
     * 获得默认运费模板
     * @param skuId
     * @return GoodsFreightDTO
     */
    ReturnObject<GoodsFreightDTO> getGoodsFreightDetailBySkuId(Long skuId);

    ReturnObject<GoodsSpuPoDTO> getSpuBySpuId(Long id);

    /**
     * 根据spuId查询skuId的list
     */
    ReturnObject<List<Long>> getSkuIdsBySpuId(Long spuId);

    ReturnObject<SimpleGoodsSkuDTO> getSimpleSkuBySkuId(Long skuId);

    List<SkuInfoDTO> getSelectSkuListBySkuIdList(List<Long> idList);

    /**
     * 根据skuId查找店铺信息
     */
    ReturnObject<ShopDetailDTO> getShopInfoBySkuId(Long skuId);

    /**
     * 根据shopId查找店铺信息
     */
    ReturnObject<ShopDetailDTO> getShopInfoByShopId(Long shopId);

    /**
     * 根据skuId查找商品信息
     * 0普通活动或者可能是秒杀  1团购  2预售  3优惠券
     * quantity可正可负
     */
    ReturnObject<GoodsDetailDTO> getGoodsBySkuId(Long skuId, Byte type, Long activityId, Integer quantity);

    /**
     *将所有运费模板值为freightId的spu改为默认运费模板
     */
    ReturnObject<Boolean> updateSpuFreightId(Long freightModelId);

    /**
     * 通知商品模块扣库存
     */
    ReturnObject<ResponseCode> signalDecrInventory(List<Long> skuIds, List<Integer> quantity);

    /**
     * 根据activityId获得优惠活动Json字符串
     */
    ReturnObject<List<String>> getActivityRule(Long couponId,List<Long> activityId);
}
