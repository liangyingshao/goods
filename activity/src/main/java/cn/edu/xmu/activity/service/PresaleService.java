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
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.dubbo.config.annotation.DubboReference;
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

    @DubboReference
    IGoodsService iGoodsService;

    public ReturnObject<PageInfo<VoObject>> QueryPresales(Long shopId, Long skuId, Integer state, Integer timeline, Integer page, Integer pagesize, boolean isadmin) {

        //1.调用dao查询
        List<PresaleActivityPo> results = presaleDao.queryPresales(shopId,skuId,state,timeline,isadmin).getData();

        //2.分页返回
        PageHelper.startPage(page, pagesize);
        List<VoObject> BoList = new ArrayList<>(results.size());
        for(PresaleActivityPo po: results)
        {
            //3. 查询presale对应的sku
            SimpleGoodsSkuDTO simpleGoodsSkuDTO = iGoodsService.getSimpleSkuBySkuId(skuId).getData();
            //4. 查询presale对应的shop
            SimpleShopDTO simpleShopDTO = iGoodsService.getSimpleShopByShopId(shopId).getData();
            Presale bo = new Presale(po,simpleGoodsSkuDTO,simpleShopDTO);
            BoList.add(bo);
        }
        PageInfo<VoObject> PresalePage = PageInfo.of(BoList);
        return new ReturnObject<>(PresalePage);
    }

    public ReturnObject createPresaleOfSKU(Long shopId, Long id, PresaleVo presaleVo) {

        //1. shopId是否存在
        SimpleShopDTO simpleShopDTO = iGoodsService.getSimpleShopByShopId(shopId).getData();
        if(simpleShopDTO == null)
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);

        //2. 检查是否存在skuId
        SimpleGoodsSkuDTO simpleGoodsSkuDTO = iGoodsService.getSimpleSkuBySkuId(id).getData();
        if(simpleGoodsSkuDTO == null)
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);

        //3. 此sku是否在此shop中,否则无权限操作
        if(iGoodsService.getShopIdBySkuId(id).getData()!=shopId)
            return new ReturnObject(ResponseCode.FIELD_NOTVALID);//TODO 考虑错误码是否合适

        //4. 此sku是否正在参加其他预售
        if(presaleDao.checkInPresale(id,presaleVo.getBeginTime(),presaleVo.getEndTime()).getData())
            return new ReturnObject(ResponseCode.FIELD_NOTVALID);//TODO 考虑错误码是否合适

        //5. 插入数据库
        PresaleActivityPo presaleActivityPo = presaleDao.createPresaleOfSKU(shopId, id, presaleVo).getData();

        //6.封装返回对象
        Presale presale = new Presale(presaleActivityPo,simpleGoodsSkuDTO,simpleShopDTO);
        return new ReturnObject<>(presale);

    }

    public ReturnObject modifyPresaleOfSKU(Long shopId, Long id, PresaleVo presaleVo) {
        return presaleDao.modifyPresaleOfSKU(shopId,id,presaleVo);
    }

    public ReturnObject cancelPresaleOfSKU(Long shopId, Long id) {
        return presaleDao.cancelPresaleOfSKU(shopId,id);
    }

    public ReturnObject putPresaleOnShelves(Long shopId, Long id) {
        return presaleDao.putPresaleOnShelves(shopId,id);
    }

    public ReturnObject putPresaleOffShelves(Long shopId, Long id) {
        return presaleDao.putPresaleOffShelves(shopId,id);
    }
}
