package cn.edu.xmu.goods.service;

import cn.edu.xmu.goods.dao.GoodsSpuDao;
import cn.edu.xmu.goods.model.bo.GoodsSpu;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 商品SPU服务类
 * @author 24320182203254 秦楚彦
 * Created at 2020/11/30 12：34
 */

@Service
public class SpuService {

    @Autowired
    GoodsSpuDao spuDao;

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
    public ReturnObject removeSpuCategory(GoodsSpu spu) {
        ReturnObject<Object> returnObject = spuDao.removeSpuCategory(spu);
        return returnObject;
    }
}
