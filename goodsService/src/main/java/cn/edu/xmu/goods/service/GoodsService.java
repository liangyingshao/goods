package cn.edu.xmu.goods.service;

import cn.edu.xmu.goods.dao.GoodsDao;
import cn.edu.xmu.goods.model.bo.FloatPrice;
import cn.edu.xmu.goods.model.bo.GoodsSku;
import cn.edu.xmu.goods.model.bo.GoodsSpu;
import cn.edu.xmu.goods.model.po.GoodsSkuPo;
import cn.edu.xmu.goods.model.vo.*;
import cn.edu.xmu.goods.service.impl.IGoodsServiceImpl;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ImgHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.oomall.other.service.IShareService;
import cn.edu.xmu.privilegeservice.client.IUserService;
import com.github.pagehelper.PageInfo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import cn.edu.xmu.oomall.other.service.*;

import java.io.IOException;

@Service
public class GoodsService {

    private final Logger logger = LoggerFactory.getLogger(GoodsService.class);
    @Autowired
    GoodsDao goodsDao;
    private final String davUsername="oomall";
    private final String davPassword="admin";
    private final String baseUrl="http://192.168.148.131:8888/webdav/";

    @DubboReference(check = false)
    private IShareService iShareService;


    @DubboReference(check = false)
    private IUserService iUserService;

    @Autowired
    private IGoodsServiceImpl iGoodsService;
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
        if(skuRetVos!=null&&skuRetVos.getList().size()>0)
            skuRetVos.getList().forEach(x->x.setPrice(iGoodsService.getPriceBySkuId(x.getId()).getData()));
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
        ReturnObject<GoodsSkuDetailRetVo> retVo=goodsDao.getSku(id);
        if(retVo.getCode().equals(ResponseCode.OK))
        {
            retVo.getData().setPrice(iGoodsService.getPriceBySkuId(id).getData());
            retVo.getData().setShareable(iShareService.skuSharable(id).getData());
        }

        return retVo;
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
    public ReturnObject<ResponseCode> deleteSku(Long shopId, Long id)
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
    public ReturnObject<ResponseCode> modifySku(Long shopId, GoodsSku bo)
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
        if(returnObject.getData()!=null)
        {
            CreatedBy createdBy=new CreatedBy();
            ModifiedBy modifiedBy=new ModifiedBy();
            String userName=iUserService.getUserName(userId);
            createdBy.set(userId,userName);
            modifiedBy.set(userId,userName);
            returnObject.getData().setCreatedBy(createdBy);
            returnObject.getData().setModifiedBy(modifiedBy);
        }
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

    /**
     * 查看一条分享商品SKU的详细信息（需登录）
     * @param id
     * @return ReturnObject<GoodsSkuRetVo>
     */
    @Transactional
    public ReturnObject<GoodsSkuRetVo> getShareSku(Long id)
    {
        ReturnObject<GoodsSkuRetVo> returnObject=goodsDao.getShareSku(id);
        return returnObject;
    }

    /**
     * 店家商品上架
     * @param shopId
     * @param id
     * @return ReturnObject
     */
    @Transactional
    public ReturnObject<ResponseCode> putGoodsOnSale(Long shopId, Long id) {
        ReturnObject<ResponseCode> returnObject=goodsDao.putGoodsOnSale(shopId,id);
        return returnObject;
    }

    /**
     * 店家商品下架
     * @param shopId
     * @param id
     * @return ReturnObject
     */
    public ReturnObject<ResponseCode> putOffGoodsOnSale(Long shopId,Long id)
    {
        ReturnObject<ResponseCode> returnObject=goodsDao.putOffGoodsOnSale(shopId,id);
        return returnObject;
    }
}
