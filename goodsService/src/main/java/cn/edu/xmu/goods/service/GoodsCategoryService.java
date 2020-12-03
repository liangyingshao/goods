package cn.edu.xmu.goods.service;

import cn.edu.xmu.goods.dao.GoodsCategoryDao;
import cn.edu.xmu.goods.model.bo.GoodsCategory;
import cn.edu.xmu.goods.model.vo.GoodsCategoryVo;
import cn.edu.xmu.ooad.util.ReturnObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * description: 类目服务类
 * date: 2020/12/1 1:54
 * author: 张悦
 * version: 1.0
 */
@Service
public class GoodsCategoryService {

    @Autowired
    private GoodsCategoryDao goodsCategoryDao;

    /**
     * description: 修改商品类目信息
     * version: 1.0 
     * date: 2020/12/3 15:11 
     * author: 张悦 
     * 
     * @param id
    * @param vo
     * @return cn.edu.xmu.ooad.util.ReturnObject
     */ 
    public ReturnObject changeCategory(Long id, GoodsCategoryVo vo){
        return goodsCategoryDao.modifyGoodsCategoryByVo(id, vo);
    }

    /**
     * description: 根据 id 删除类目 
     * version: 1.0 
     * date: 2020/12/3 15:06 
     * author: 张悦 
     * 
     * @param id
     * @return cn.edu.xmu.ooad.util.ReturnObject<java.lang.Object>
     */ 
    @Transactional
    public ReturnObject<Object> deleteCategory(Long id) {

        return goodsCategoryDao.physicallyDeleteCategory(id);
    }

    /**
     * description: 新建类目 
     * version: 1.0 
     * date: 2020/12/3 15:06 
     * author: 张悦 
     * 
     * @param goodsCategory
 * @param pid
     * @return cn.edu.xmu.ooad.util.ReturnObject
     */ 
    @Transactional
    public ReturnObject insertGoodsCategory(GoodsCategory goodsCategory,Long pid) {
        ReturnObject<GoodsCategory> retObj = goodsCategoryDao.insertGoodsCategory(goodsCategory,pid);
        return retObj;
    }

    /**
     * description: 根据类目ID返回所有子分类列表
     * version: 1.0 
     * date: 2020/12/3 15:07 
     * author: 张悦 
     * 
     * @param id
     * @return cn.edu.xmu.ooad.util.ReturnObject
     */ 
    public ReturnObject getSubcategories(Long id) {
            return goodsCategoryDao.getSubcategoriesById(id);
    }

}
