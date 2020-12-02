package cn.edu.xmu.goods.service;

import cn.edu.xmu.goods.dao.GoodsDao;
import cn.edu.xmu.goods.model.bo.FloatPrice;
import cn.edu.xmu.goods.model.bo.GoodsSku;
import cn.edu.xmu.goods.model.po.GoodsSkuPo;
import cn.edu.xmu.goods.model.po.ShopPo;
import cn.edu.xmu.goods.model.vo.FloatPriceRetVo;
import cn.edu.xmu.goods.model.vo.GoodsSkuDetailRetVo;
import cn.edu.xmu.goods.model.vo.GoodsSkuRetVo;
import cn.edu.xmu.goods.model.vo.GoodsSkuVo;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ImgHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoodsService {

    private Logger logger = LoggerFactory.getLogger(GoodsService.class);
    @Autowired
    GoodsDao goodsDao;
    private String davUsername="oomall";
    private String davPassword="admin";
    private String baseUrl="http://192.168.148.131:8888/webdav/";

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
    public ReturnObject<PageInfo<GoodsSkuRetVo>> getSkuList(Long shopId, String skuSn, Long spuId, String spuSn, Integer page, Integer pageSize)
    {
        PageInfo<GoodsSkuRetVo> skuRetVos=goodsDao.getSkuList(shopId,skuSn,spuId,spuSn,page,pageSize);
        return new ReturnObject<>(skuRetVos);
    }

    /**
     * 获得sku的详细信息
     * @param id
     * @return ReturnObject<VoObject>
     */
    @Transactional
    public ReturnObject<GoodsSkuDetailRetVo> getSku(Long id)
    {
        GoodsSkuDetailRetVo retVo=goodsDao.getSku(id);
        if(retVo==null)return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        return new ReturnObject<>(retVo);
    }

    /**
     * sku上传图片
     * @param shopId
     * @param id
     * @param file
     * @return ReturnObject<VoObject>
     */
    @Transactional
    public ReturnObject<VoObject> uploadSkuImg(Long shopId, Long id, MultipartFile file)
    {
        GoodsSkuPo skuPo = goodsDao.internalGetSku(id);
        if(skuPo == null) {
            return new ReturnObject<>(null);
        }
        GoodsSku sku = new GoodsSku(skuPo);

        ReturnObject returnObject;
        try{
            returnObject = ImgHelper.remoteSaveImg(file,2,davUsername, davPassword,baseUrl);

            //文件上传错误
            if(returnObject.getCode()!=ResponseCode.OK){
                logger.debug(returnObject.getErrmsg());
                return returnObject;
            }

            String oldFilename = sku.getImageUrl();
            sku.setImageUrl(returnObject.getData().toString());
            ReturnObject updateReturnObject = goodsDao.uploadSkuImg(sku);

            //数据库更新失败，需删除新增的图片
            if(updateReturnObject.getCode()==ResponseCode.FIELD_NOTVALID){
                ImgHelper.deleteRemoteImg(returnObject.getData().toString(),davUsername, davPassword,baseUrl);
                return updateReturnObject;
            }

            //数据库更新成功需删除旧图片，未设置则不删除
            if(oldFilename!=null) {
                ImgHelper.deleteRemoteImg(oldFilename, davUsername, davPassword,baseUrl);
            }
        }
        catch (IOException e){
            logger.debug("uploadImg: I/O Error:" + baseUrl);
            return new ReturnObject(ResponseCode.FILE_NO_WRITE_PERMISSION);
        }
        return returnObject;
    }

    /**
     * 管理员或店家逻辑删除SKU
     * @param shopId
     * @param id
     * @return ReturnObject
     */
    @Transactional
    public ReturnObject deleteSku(Long shopId, Long id)
    {
        return goodsDao.logicalDelete(shopId,id);
    }

    /**
     * 管理员或店家修改SKU信息
     * @param shopId
     * @param bo
     * @return ReturnObject
     */
    @Transactional
    public ReturnObject modifySku(Long shopId, GoodsSku bo)
    {
        return goodsDao.modifySku(shopId,bo);
    }

    /**
     * 管理员新增商品价格浮动
     * @param shopId
     * @param floatPrice
     * @param userId
     * @return ReturnObject
     */
    @Transactional
    public ReturnObject<FloatPriceRetVo> addFloatPrice(Long shopId, FloatPrice floatPrice, Long userId)
    {
        ReturnObject<FloatPriceRetVo> returnObject= goodsDao.addFloatPrice(shopId,floatPrice,userId);

        return returnObject;
    }

    /**
     * 管理员添加新的SKU到SPU里
     * @param shopId
     * @param sku
     * @return ReturnObject<GoodsSkuRetVo>
     */
    @Transactional
    public ReturnObject<GoodsSkuRetVo> createSKU(Long shopId, GoodsSku sku)
    {
        ReturnObject<GoodsSkuRetVo>returnObject=goodsDao.createSKU(shopId,sku);
        return returnObject;
    }
}
