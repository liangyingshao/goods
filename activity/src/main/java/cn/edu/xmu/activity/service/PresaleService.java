package cn.edu.xmu.activity.service;

import cn.edu.xmu.activity.dao.PresaleDao;
import cn.edu.xmu.activity.model.bo.Presale;
import cn.edu.xmu.activity.model.po.PresaleActivityPo;
import cn.edu.xmu.activity.model.vo.PresaleRetVo;
import cn.edu.xmu.activity.model.vo.PresaleVo;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.oomall.goods.model.SimpleGoodsSkuDTO;
import cn.edu.xmu.oomall.goods.model.SimpleShopDTO;
import cn.edu.xmu.oomall.goods.service.IGoodsService;
import cn.edu.xmu.oomall.order.service.IOrderService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.parser.Part;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * description: PresaleService
 * date: 2020/12/11 11:37
 * author: 杨铭
 * version: 1.0
 */
@Service
public class PresaleService {

    @Autowired
    PresaleDao presaleDao;

    @DubboReference(check = false)
    IGoodsService iGoodsService;

    @DubboReference(check = false)
    IOrderService iOrderService;

    private static final Logger logger = LoggerFactory.getLogger(PresaleService.class);

    public ReturnObject<PageInfo<VoObject>> QueryPresales(Long shopId, Long skuId, Integer state, Integer timeline, Integer page, Integer pagesize, boolean isadmin) {

        //1.调用dao查询
        List<PresaleActivityPo> results = null;
        ReturnObject<List<PresaleActivityPo>> returnObject = presaleDao.queryPresales(shopId,skuId,state,timeline,isadmin);

        //2.返回dao层的错误
        if(returnObject.getCode() != ResponseCode.OK) {
            return new ReturnObject<PageInfo<VoObject>>(returnObject.getCode());
        }

        //3.取出dao层的数据
        results = presaleDao.queryPresales(shopId,skuId,state,timeline,isadmin).getData();

        //4.分页返回
        PageHelper.startPage(page, pagesize);
        List<VoObject> BoList = new ArrayList<>(results.size());
        for(PresaleActivityPo po: results)
        {
            //5. 查询此presale对应的sku
            SimpleGoodsSkuDTO simpleGoodsSkuDTO = iGoodsService.getSimpleSkuBySkuId(po.getGoodsSkuId()).getData();
            //6. 查询此presale对应的shop
            SimpleShopDTO simpleShopDTO = iGoodsService.getSimpleShopByShopId(po.getShopId()).getData();
            Presale bo = new Presale(po,simpleGoodsSkuDTO,simpleShopDTO);
            BoList.add(bo);
        }
        PageInfo<VoObject> PresalePage = PageInfo.of(BoList);
        //改为传入的pageSize
        PresalePage.setPageSize(pagesize);
        return new ReturnObject<>(PresalePage);

    }

    public ReturnObject createPresaleOfSKU(Long shopId, Long id, PresaleVo presaleVo) {

        //1. shopId是否存在
        ReturnObject<SimpleShopDTO> returnObject = iGoodsService.getSimpleShopByShopId(shopId);
        SimpleShopDTO simpleShopDTO = returnObject.getData();

        if(simpleShopDTO == null)
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);

        //2. 检查是否存在skuId
        //TODO sku state=2 则不应该拿到
        SimpleGoodsSkuDTO simpleGoodsSkuDTO = iGoodsService.getSimpleSkuBySkuId(id).getData();
        if(simpleGoodsSkuDTO == null){
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
        }


        //3. 此sku是否在此shop中,否则无权限操作
        if(iGoodsService.getShopIdBySkuId(id).getData()!=shopId){
            logger.debug("此shop无权限操作此sku");
            return new ReturnObject(ResponseCode.RESOURCE_ID_OUTSCOPE);
        }


        //4. 此sku是否正在参加其他预售
        if(presaleDao.checkInPresale(id,presaleVo.getBeginTime(),presaleVo.getEndTime()).getData()){
            logger.debug("此sku正在参加其他预售");
            return new ReturnObject(ResponseCode.PRESALE_STATENOTALLOW);
        }
        
        return presaleDao.createPresaleOfSKU(shopId, id, presaleVo,simpleGoodsSkuDTO,simpleShopDTO);
        
    }

    public ReturnObject modifyPresaleOfSKU(Long shopId, Long id, PresaleVo presaleVo) {
        return presaleDao.modifyPresaleOfSKU(shopId,id,presaleVo);
    }

    public ReturnObject cancelPresaleOfSKU(Long shopId, Long id) {
        try {
            iOrderService.putPresaleOffshevles(id);
        } catch (Exception e) {
            logger.debug("dubbo error!");
        }
        return presaleDao.cancelPresaleOfSKU(shopId,id);
    }

    public ReturnObject putPresaleOnShelves(Long shopId, Long id) {

        return presaleDao.putPresaleOnShelves(shopId,id);
    }

    public ReturnObject putPresaleOffShelves(Long shopId, Long id) {

        try {
            iOrderService.putPresaleOffshevles(id);
        } catch (Exception e) {
            logger.debug("dubbo error!");
        }
        return presaleDao.putPresaleOffShelves(shopId,id);
    }
}
