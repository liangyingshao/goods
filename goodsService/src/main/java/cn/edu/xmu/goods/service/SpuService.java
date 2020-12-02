package cn.edu.xmu.goods.service;

import cn.edu.xmu.goods.dao.GoodsSpuDao;
import cn.edu.xmu.goods.model.bo.GoodsSpu;
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
        spu.setState(GoodsSpu.SpuState.ONSHELF);
    ReturnObject<Object> returnObject=spuDao.changeState(spu);
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
        spu.setState(GoodsSpu.SpuState.OFFSHELF);
        ReturnObject<Object> returnObject=spuDao.changeState(spu);
        return returnObject;
    }


}
