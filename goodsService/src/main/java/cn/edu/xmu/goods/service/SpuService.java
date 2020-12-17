package cn.edu.xmu.goods.service;

import cn.edu.xmu.goods.dao.GoodsSpuDao;
import cn.edu.xmu.goods.model.bo.GoodsSpu;
import cn.edu.xmu.goods.model.vo.GoodsSkuVo;
import cn.edu.xmu.goods.model.vo.GoodsSpuVo;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.oomall.goods.service.IGoodsService;
import cn.edu.xmu.oomall.order.model.SimpleFreightModelDTO;
import cn.edu.xmu.oomall.order.service.IOrderService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.logging.SimpleFormatter;

/**
 * 商品SPU服务类
 * @author 24320182203254 秦楚彦
 * Created at 2020/11/30 12：34
 */

@Service
public class SpuService {

    @Autowired
    GoodsSpuDao spuDao;

    @DubboReference(check = false)
    private IOrderService iOrderService;

    private Logger logger = LoggerFactory.getLogger(SpuService.class);

    /**
     * 查看一条商品SPU的详细信息（无需登录）
     * @param id
     * @return  GoodsSpuVo
     * @author 24320182203254 秦楚彦
     * Created at 2020/11/30 08：41
     * Modified at 2020/12/13
     */
    public ReturnObject<Object> showSpu(Long id) {
        ReturnObject<SimpleFreightModelDTO> retFeightModel=new ReturnObject<>();

        ReturnObject<Long> freightId=spuDao.getFreightIdBySpuId(id);
        if(!freightId.getCode().equals(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())){
            retFeightModel=iOrderService.getSimpleFreightById(freightId.getData());
        }
        ReturnObject<Object> returnObject= spuDao.showSpu(id,retFeightModel.getData());
        return returnObject;
    }

    /**
     * 新建商品SPU
     * @param spu
     * @return  ReturnObject
     * @author 24320182203254 秦楚彦
     * Created at 2020/12/1 00：36
     */
    @Transactional
    public ReturnObject addSpu(GoodsSpu spu) {
        ReturnObject<GoodsSpu> ret = spuDao.addSpu(spu);
        //ReturnObject<Object> ret=showSpu(returnObject.getData().getId());
        return ret;
    }

//    /**
//     * 商品SPU上架
//     * @param spu
//     * @return  ReturnObject
//     * @author 24320182203254 秦楚彦
//     * Created at 2020/12/01 16：48
//     */
//    @Transactional
//    public ReturnObject putGoodsOnSale(GoodsSpu spu) {
//        ReturnObject<Object> returnObject;
//        //spu.setState(GoodsSpu.SpuState.ONSHELF);
//        returnObject=spuDao.changeState(spu);
//        return returnObject;
//    }

//    /**
//     * 商品SPU下架
//     * @param spu
//     * @return  ReturnObject
//     * @author 24320182203254 秦楚彦
//     * Created at 2020/12/01 17：07
//     */
//    @Transactional
//    public ReturnObject putOffGoodsOnSale(GoodsSpu spu) {
//        ReturnObject<Object> returnObject;
//        //spu.setState(GoodsSpu.SpuState.OFFSHELF);
//        returnObject=spuDao.changeState(spu);
//        return returnObject;
//    }

    /**
     * 修改商品SPU
     * @param spu
     * @return  ReturnObject
     * @author 24320182203254 秦楚彦
     * Created at 2020/12/01 22：06
     */
    @Transactional
    public ReturnObject modifyGoodsSpu(GoodsSpu spu) {
        ReturnObject<Object> returnObject = spuDao.modifyGoodsSpu(spu);
        return returnObject;
    }

    /**
     * 将商品SPU添加至二级分类
     * @param spu
     * @return  ReturnObject
     * @author 24320182203254 秦楚彦
     * Created at 2020/12/01 22：56
     */
    @Transactional
    public ReturnObject addSpuCategory(GoodsSpu spu) {
        ReturnObject<Object> returnObject = spuDao.addSpuCategory(spu);
        return returnObject;
    }

    /**
     * 将商品SPU移出分类
     * @param spu
     * @return  ReturnObject
     * @author 24320182203254 秦楚彦
     * Created at 2020/12/02 10：38
     */
    @Transactional
    public ReturnObject removeSpuCategory(GoodsSpu spu) {
        ReturnObject<Object> returnObject = spuDao.removeSpuCategory(spu);
        return returnObject;
    }

    /**
     * 将商品SPU添加至品牌
     * @param spu
     * @return  ReturnObject
     * @author 24320182203254 秦楚彦
     * Created at 2020/12/02 22：29
     */
    @Transactional
    public ReturnObject addSpuBrand(GoodsSpu spu) {
        ReturnObject<Object> returnObject = spuDao.addSpuBrand(spu);
        return returnObject;
    }

    /**
     * 将商品SPU添加至品牌
     * @param spu
     * @return  ReturnObject
     * @author 24320182203254 秦楚彦
     * Created at 2020/12/02 22：31
     */
    @Transactional
    public ReturnObject removeSpuBrand(GoodsSpu spu) {
        ReturnObject<Object> returnObject = spuDao.removeSpuBrand(spu);
        return returnObject;
    }

    /**
     * 逻辑删除SPU
     * @return  ReturnObject
     * @author 24320182203254 秦楚彦
     * Created at 2020/12/03 01:07
     */
    @Transactional
    public ReturnObject deleteGoodsSpu(Long shopId,Long id) {
        ReturnObject<Object> returnObject = spuDao.deleteGoodsSpu(shopId,id);
        return returnObject;
    }

    /**
     * SPU上传图片
     * @param spu
     * @param file
     * @return  ReturnObject
     * @author 24320182203254 秦楚彦
     * Created at 2020/12/03 12:02
     */
    @Transactional
    public ReturnObject<Object> uploadSpuImg(GoodsSpu spu, MultipartFile file)
    {
        ReturnObject<Object> returnObject = spuDao.uploadSpuImg(spu,file);
        return returnObject;
    }

}
