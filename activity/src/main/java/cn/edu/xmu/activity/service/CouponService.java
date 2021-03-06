package cn.edu.xmu.activity.service;

import cn.edu.xmu.activity.dao.CouponDao;
import cn.edu.xmu.activity.model.bo.CouponActivity;
import cn.edu.xmu.activity.model.po.CouponActivityPo;
import cn.edu.xmu.activity.model.po.CouponSkuPo;
import cn.edu.xmu.activity.model.vo.*;
import cn.edu.xmu.activity.model.bo.CouponSku;
import cn.edu.xmu.activity.model.vo.CouponRetVo;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.oomall.goods.service.*;
import cn.edu.xmu.oomall.goods.model.*;
import cn.edu.xmu.privilegeservice.client.IUserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CouponService {
    private Logger logger = LoggerFactory.getLogger(CouponService.class);

    @Autowired
    private CouponDao couponDao;

    @DubboReference(check = false)
    private IGoodsService iGoodsService;

    @DubboReference(check = false)
    private IUserService iUserService;

    /**
     * 查看优惠活动中的商品
     * @param id
     * @param page
     * @param pageSize
     * @return ReturnObject<PageInfo<CouponSku>>
     */
    @Transactional
    public ReturnObject<PageInfo<SkuInfoDTO>> getCouponSkuList(Long id, Integer page, Integer pageSize) {
        //获取Sku的id列表，根据SKUid列表调用远程服务获取每一个sku的name
//        PageInfo<GoodsSkuCouponRetVo>couponSkus=activityDao.getCouponSkuList(id,page,pageSize);
        List<CouponSkuPo> list = couponDao.getCouponSkuList(id);
        if(list==null||list.size()==0)return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);

        PageHelper.startPage(page,pageSize);
        List<Long> idList = new ArrayList<>(list.stream().map(CouponSkuPo::getSkuId).collect(Collectors.toList()));
        List<SkuInfoDTO> skuList = iGoodsService.getSelectSkuListBySkuIdList(idList);

        PageInfo<SkuInfoDTO> skuInfoDTOPageInfo = PageInfo.of(skuList);
        skuInfoDTOPageInfo.setPageSize(pageSize);
        skuInfoDTOPageInfo.setPageNum(page);
        return new ReturnObject<>(skuInfoDTOPageInfo);
    }

    /**
     * 管理员为己方某优惠券活动新增限定范围
     * @param shopId
     * @param id
     * @param couponSkus
     * @return ReturnObject<CouponSkuRetVo>
     */
    @Transactional
    public ReturnObject<List<CouponSkuRetVo>> createCouponSkus(Long shopId, Long id, List<CouponSku> couponSkus) {
        ReturnObject<List<CouponSkuRetVo>> returnObject;
        for(CouponSku couponSku:couponSkus)
        {
            returnObject= iGoodsService.checkSkuUsableBySkuShop(couponSku.getSkuId(), shopId);
            if(returnObject.getCode()!= ResponseCode.OK)return returnObject;
        }
        returnObject= couponDao.createCouponSkus(shopId,id, couponSkus);
        return returnObject;
    }

    /**
     * 店家删除己方某优惠券活动的某限定范围
     * @param shopId
     * @param id
     * @return ReturnObject
     */
    @Transactional
    public ReturnObject<ResponseCode> deleteCouponSku(Long shopId, Long id)
    {
        ReturnObject<ResponseCode> returnObject= couponDao.deleteCouponSku(shopId,id);
        return returnObject;
    }

    /**
     * 买家查看优惠券列表
     * @param userId
     * @param state
     * @param page
     * @param pageSize
     * @return ReturnObject<PageInfo<CouponRetVo>>
     */
    @Transactional
    public ReturnObject<PageInfo<CouponRetVo>> showCoupons(Long userId, Integer state, Integer page, Integer pageSize)
    {
        PageInfo<CouponRetVo> returnObject= couponDao.showCoupons(userId,state,page,pageSize);
        returnObject.setPageNum(page);
        returnObject.setPageSize(pageSize);
        return new ReturnObject<PageInfo<CouponRetVo>>(returnObject);
    }

    //变内部接口了
    /**
     * 买家使用自己某优惠券
     * @param userId
     * @param id
     * @return ReturnObject
     */
    @Transactional
    public ReturnObject<ResponseCode> useCoupon(Long userId, Long id)
    {
        ReturnObject<ResponseCode> returnObject= couponDao.useCoupon(userId,id);
        return returnObject;
    }

    //据说已废弃
    /**
     * 买家删除自己某优惠券
     * @param userId
     * @param id
     * @return ReturnObject
     */
    @Transactional
    public ReturnObject<ResponseCode> deleteCoupon(Long userId, Long id)
    {
        ReturnObject<ResponseCode> returnObject= couponDao.deleteCoupon(userId,id);
        return returnObject;
    }

    /**
     * 买家领取活动优惠券
     * @param userId
     * @param id
     * @param departId
     * @return ReturnObject<CouponNewRetVo>
     */
    @Transactional
    public ReturnObject<List<String>> getCoupon(Long userId, Long id, Long departId)
    {
        logger.debug("getCoupon:userId="+userId+" activityId="+id);
        ReturnObject<List<String>> returnObject= couponDao.getCoupon(userId,id,departId);
        return returnObject;
    }

    //变内部接口了
    /**
     * 优惠券退回
     *
     * @param shopId
     * @param id
     * @return ReturnObject
     */
    @Transactional
    public ReturnObject<ResponseCode> returnCoupon(Long shopId, Long id)
    {
        ReturnObject<ResponseCode> returnObject= couponDao.returnCoupon(shopId,id);
        return returnObject;
    }

    /**
     * 店家查询己方某优惠券活动
     * @param shopId
     * @param id
     * @return ReturnObject
     */
    @Transactional
    public ReturnObject<Object> showCouponActivity(Long shopId, Long id) {
        ReturnObject<SimpleShopDTO> simpleShopDTOReturnObject=new ReturnObject<>();
        if(shopId!=0) {
            simpleShopDTOReturnObject = iGoodsService.getSimpleShopByShopId(shopId);
            //商店不存在，很抽象
            if (simpleShopDTOReturnObject.getCode().equals(ResponseCode.RESOURCE_ID_NOTEXIST))
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }//获取创建者修改者ID
        ReturnObject<CouponActivity> retCouponActivity=couponDao.getCouponActivity(id,shopId);
        //不存在该活动或无权限先返回
        if(retCouponActivity.getCode().equals(ResponseCode.RESOURCE_ID_NOTEXIST))
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        if(retCouponActivity.getCode().equals(ResponseCode.RESOURCE_ID_OUTSCOPE))
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
        String createByName="";
        String modiByName="";
        CouponActivity bo=retCouponActivity.getData();
        if(bo!=null)
        {
            createByName=iUserService.getUserName(bo.getCreatedBy());
            modiByName=iUserService.getUserName(bo.getModiBy());
        }
        ReturnObject<Object> returnObject= couponDao.showCouponActivity(simpleShopDTOReturnObject.getData(),id,createByName,modiByName);
        return returnObject;
    }

    /**
     * 管理员新建己方优惠活动
     * @param activity
     * @return ReturnObject
     */
    public ReturnObject<CouponActivityVo> addCouponActivity(CouponActivity activity) {
        Long shopId = activity.getShopId();
        ReturnObject<SimpleShopDTO> simpleShopDTOReturnObject=iGoodsService.getSimpleShopByShopId(shopId);
        String createByName="";
        //createByName=iPrivilegeService.getUserName(activity.getCreatedBy());
        ReturnObject<CouponActivityVo> returnObject= couponDao.addCouponActivity(activity,simpleShopDTOReturnObject.getData(),createByName);
        return null;
    }

    /**
     * 管理员修改己方优惠活动
     * @param activity
     * @return ReturnObject
     */
    public ReturnObject<ResponseCode> modifyCouponActivity(CouponActivity activity) {
        ReturnObject<ResponseCode> returnObject= couponDao.modifyCouponActivity(activity);
        return returnObject;
    }

    /**
     * 管理员下线己方优惠活动
     * @param shopId
     * @param id
     * @return ReturnObject
     */
    public ReturnObject<ResponseCode> offlineCouponActivity(Long shopId, Long id,Long userId) {
        ReturnObject<ResponseCode> returnObject= couponDao.offlineCouponActivity(shopId, id,userId);
        return returnObject;
    }

    /**
     * 管理员上线己方优惠活动
     * @param shopId
     * @param id
     * @param userId
     * @return ReturnObject
     */
    public ReturnObject onlineCouponActivity(Long shopId, Long id, Long userId) {
        ReturnObject<ResponseCode> returnObject= couponDao.onlineCouponActivity(shopId, id,userId);
        return returnObject;
    }
    /**
     * 删除优惠活动
     * @param shopId
     * @param id
     * @param userId
     * @return ReturnObject
     */
    public ReturnObject deleteCouponActivity(Long shopId, Long id, Long userId) {
        ReturnObject<ResponseCode> returnObject= couponDao.deleteCouponActivity(shopId, id,userId);
        return returnObject;
    }
    /**
     * 查看上线的优惠活动列表
     * @param shopId
     * @param timeline
     * @param page
     * @param pageSize
     * @return ReturnObject
     */
    public ReturnObject<PageInfo<CouponActivityByNewCouponRetVo>> showActivities(Long shopId, Integer timeline, Integer page, Integer pageSize) {
        ReturnObject<PageInfo<CouponActivityByNewCouponRetVo>> returnObject= couponDao.showActivities(shopId,timeline,page,pageSize);
        returnObject.getData().setPageSize(pageSize);
        returnObject.getData().setPageNum(page);
        return returnObject;
    }

    /**
     * 查看下线的优惠活动列表
     * @param shopId
     * @param page
     * @param pageSize
     * @return ReturnObject
     */
    public ReturnObject<PageInfo<CouponActivityByNewCouponRetVo>> showInvalidCouponActivities(Long shopId, Integer page, Integer pageSize) {
        ReturnObject<PageInfo<CouponActivityByNewCouponRetVo>> returnObject= couponDao.showInvalidCouponActivities(shopId,page,pageSize);
        returnObject.getData().setPageNum(page);
        returnObject.getData().setPageSize(pageSize);
        return returnObject;
    }

    /**
     * 上传优惠活动图片
     * @param activity
     * @param file
     * @return ReturnObject
     */
    public ReturnObject<Object> uploadActivityImg(CouponActivity activity, MultipartFile file) {
        ReturnObject<Object> returnObject = couponDao.uploadActivityImg(activity,file);
        return returnObject;
    }


}
