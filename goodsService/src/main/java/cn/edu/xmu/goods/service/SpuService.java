package cn.edu.xmu.goods.service;

import cn.edu.xmu.goods.dao.GoodsSpuDao;
import cn.edu.xmu.goods.model.bo.GoodsSku;
import cn.edu.xmu.goods.model.bo.GoodsSpu;
import cn.edu.xmu.goods.model.po.GoodsSkuPo;
import cn.edu.xmu.goods.model.po.GoodsSpuPo;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ImgHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 商品SPU服务类
 * @author 24320182203254 秦楚彦
 * Created at 2020/11/30 12：34
 */

@Service
public class SpuService {

    @Autowired
    GoodsSpuDao spuDao;



    private Logger logger = LoggerFactory.getLogger(SpuService.class);

    /**
     * 查看一条商品SPU的详细信息（无需登录）
     * @param id
     * @return  GoodsSpuVo
     * @author 24320182203254 秦楚彦
     * Created at 2020/11/30 08：41
     */
    public ReturnObject<Object> showSpu(Long id) {
        ReturnObject<Object> returnObject= spuDao.showSpu(id);
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
        ReturnObject<GoodsSpu> returnObject = spuDao.addSpu(spu);
        return returnObject;
    }

    /**
     * 商品SPU上架
     * @param spu
     * @return  ReturnObject
     * @author 24320182203254 秦楚彦
     * Created at 2020/12/01 16：48
     */
    @Transactional
    public ReturnObject putGoodsOnSale(GoodsSpu spu) {
        ReturnObject<Object> returnObject;
        spu.setState(GoodsSpu.SpuState.ONSHELF);
        returnObject=spuDao.changeState(spu);
        return returnObject;
    }

    /**
     * 商品SPU下架
     * @param spu
     * @return  ReturnObject
     * @author 24320182203254 秦楚彦
     * Created at 2020/12/01 17：07
     */
    @Transactional
    public ReturnObject putOffGoodsOnSale(GoodsSpu spu) {
        ReturnObject<Object> returnObject;
        spu.setState(GoodsSpu.SpuState.OFFSHELF);
        returnObject=spuDao.changeState(spu);
        return returnObject;
    }

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
     * @param spu
     * @return  ReturnObject
     * @author 24320182203254 秦楚彦
     * Created at 2020/12/03 01:07
     */
    @Transactional
    public ReturnObject deleteGoodsSpu(GoodsSpu spu) {
        ReturnObject<Object> returnObject = spuDao.deleteGoodsSpu(spu);
        return returnObject;
    }

    /**
     * 逻辑删除SPU
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
