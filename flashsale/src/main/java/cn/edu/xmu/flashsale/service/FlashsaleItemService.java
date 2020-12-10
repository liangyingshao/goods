package cn.edu.xmu.flashsale.service;

import cn.edu.xmu.flashsale.dao.FlashSaleItemDao;
import cn.edu.xmu.flashsale.model.bo.FlashSaleItem;
import cn.edu.xmu.flashsale.model.vo.FlashsaleItemRetVo;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.oomall.goods.model.SkuInfoDTO;
import cn.edu.xmu.oomall.goods.service.IGoodsService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FlashsaleItemService {

    @DubboReference
    private IGoodsService goodsService;

    @Autowired
    private FlashSaleItemDao flashSaleItemDao;

    public ReturnObject<FlashsaleItemRetVo> addSKUofTopic(Long id, Long skuId, Long price, Integer quantity) {
        //获得sku信息
        ReturnObject<SkuInfoDTO> returnObject = goodsService.getSelectSkuInfoBySkuId(skuId);
        if(returnObject.getCode()!= ResponseCode.OK) {//错误
            return new ReturnObject(returnObject.getCode());
        } else if(returnObject.getData() == null) {//不存在，返回资源不存在
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
        }

        //存在，调dao
        ReturnObject<FlashsaleItemRetVo> itemRetObj =  flashSaleItemDao.addSKUofTopic(id, skuId, price, quantity);
        //插入不成功
        if(itemRetObj.getCode()!=ResponseCode.OK) {
            return new ReturnObject<>(itemRetObj.getCode());
        }
        //插入成功
        FlashsaleItemRetVo retVo = itemRetObj.getData();
        retVo.setGoodsSku(returnObject.getData());
        return new ReturnObject<>(retVo);
    }

    public ReturnObject deleteKUofTopic(Long fid, Long id) {
        return flashSaleItemDao.deleteKUofTopic(fid, id);
    }

    public ReturnObject<PageInfo<VoObject>> getSKUofTopic(Long id, Integer page, Integer pageSize) {
        ReturnObject<PageInfo<VoObject>> finalRetObj = new ReturnObject<>();
        List<VoObject> itemRetVoList = new ArrayList<>();
        PageHelper.startPage(page, pageSize);

        //查出item相关信息
        ReturnObject<List<FlashSaleItem>> returnObject = flashSaleItemDao.getSKUofTopic(id, page, pageSize);
        //根据skuId查出sku具体信息,构造retVo
        List<FlashSaleItem> flashSaleItemList = returnObject.getData();
        for (FlashSaleItem flashSaleItem : flashSaleItemList) {
            ReturnObject<SkuInfoDTO> skuRetObj = goodsService.getSelectSkuInfoBySkuId(flashSaleItem.getSkuId());
            FlashsaleItemRetVo vo = new FlashsaleItemRetVo(flashSaleItem);
            vo.setGoodsSku(skuRetObj.getData());
            itemRetVoList.add(vo);
        }
        PageInfo<VoObject> flashsaleItemPage = PageInfo.of(itemRetVoList);
        return new ReturnObject<>(flashsaleItemPage);
    }
}
