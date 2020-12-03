package cn.edu.xmu.goods.service;

import cn.edu.xmu.goods.dao.BrandDao;
import cn.edu.xmu.goods.model.bo.Brand;
import cn.edu.xmu.goods.model.po.BrandPo;
import cn.edu.xmu.goods.model.vo.BrandVo;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ImgHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * description: 品牌服务类
 * date: 2020/12/1 1:54
 * author: 张悦
 * version: 1.0
 */
@Service
public class BrandService {

    private Logger logger = LoggerFactory.getLogger(BrandService.class);

    @Autowired
    BrandDao brandDao;

    //@Value("${privilegeservice.dav.sername}")
    private String davBrandname;
    //@Value("${privilegeservice.dav.password}")
    private String davPassword;
    //@Value("${privilegeservice.dav.baseUrl}")
    private String baseUrl;

    /**
     * description: 分页查询所有品牌信息
     * version: 1.0 
     * date: 2020/12/3 15:03 
     * author: 张悦 
     * 
     * @param page
     * @param pagesize
     * @return cn.edu.xmu.ooad.util.ReturnObject<com.github.pagehelper.PageInfo<cn.edu.xmu.ooad.model.VoObject>>
     */ 
    public ReturnObject<PageInfo<VoObject>> findAllBrands(Integer page, Integer pagesize) {

        PageHelper.startPage(page, pagesize);
        PageInfo<BrandPo> brandPos = brandDao.findAllBrands();

        List<VoObject> brands = brandPos.getList().stream().map(Brand::new).collect(Collectors.toList());

        PageInfo<VoObject> returnObject = new PageInfo<>(brands);
        returnObject.setPages(brandPos.getPages());
        returnObject.setPageNum(brandPos.getPageNum());
        returnObject.setPageSize(brandPos.getPageSize());
        returnObject.setTotal(brandPos.getTotal());

        return new ReturnObject<>(returnObject);
    }

    /**
     * description: 新增品牌
     * version: 1.0
     * date: 2020/12/2 19:02
     * author: 张悦
     *
     * @param brand
     * @return cn.edu.xmu.ooad.util.ReturnObject
     */
    @Transactional
    public ReturnObject insertBrand(Brand brand) {
        ReturnObject<Brand> retObj = brandDao.insertBrand(brand);
        return retObj;
    }

    /**
     * description: 根据 ID 和 BrandVo 修改品牌信息
     * version: 1.0 
     * date: 2020/12/3 15:04 
     * author: 张悦 
     * 
     * @param id
     * @param vo
     * @return cn.edu.xmu.ooad.util.ReturnObject<java.lang.Object>
     */ 
    @Transactional
    public ReturnObject<Object> modifyBrandInfo(Long id, BrandVo vo) {
        return brandDao.modifyBrandByVo(id, vo);
    }

    /**
     * description: 根据 id 删除品牌
     * version: 1.0 
     * date: 2020/12/3 15:04 
     * author: 张悦 
     * 
     * @param id
     * @return cn.edu.xmu.ooad.util.ReturnObject<java.lang.Object>
     */ 
    @Transactional
    public ReturnObject<Object> deleteBrand(Long id) {
        return brandDao.physicallyDeleteBrand(id);
    }

    /**
     * description: 根据 id 修改品牌信息
     * version: 1.0
     * date: 2020/12/3 15:05
     * author: 张悦
     *
     * @param id
 * @param vo
     * @return cn.edu.xmu.ooad.util.ReturnObject
     */

    public ReturnObject changeBrand(Long id, BrandVo vo){
        return brandDao.modifyBrandByVo(id, vo);
    }

    /**
     * 上传图片
     * @author 3218
     * @param id: 用户id
     * @param multipartFile: 文件
     * @return
     */
    @Transactional
    public ReturnObject uploadBrandImg(Long id, MultipartFile multipartFile){
        ReturnObject<Brand> brandReturnObject = brandDao.getBrandById(id);

        if(brandReturnObject.getCode() == ResponseCode.RESOURCE_ID_NOTEXIST) {
            return brandReturnObject;
        }
        Brand brand = brandReturnObject.getData();

        ReturnObject returnObject = new ReturnObject();
        try{
            returnObject = ImgHelper.remoteSaveImg(multipartFile,2,davBrandname, davPassword,baseUrl);

            //文件上传错误
            if(returnObject.getCode()!=ResponseCode.OK){
                logger.debug(returnObject.getErrmsg());
                return returnObject;
            }

            String oldFilename = brand.getImageUrl();
            brand.setImageUrl(returnObject.getData().toString());
            ReturnObject updateReturnObject = brandDao.updateBrandImage(brand);

            //数据库更新失败，需删除新增的图片
            if(updateReturnObject.getCode()==ResponseCode.FIELD_NOTVALID){
                ImgHelper.deleteRemoteImg(returnObject.getData().toString(),davBrandname, davPassword,baseUrl);
                return updateReturnObject;
            }

            //数据库更新成功需删除旧图片，未设置则不删除
            if(oldFilename!=null) {
                ImgHelper.deleteRemoteImg(oldFilename, davBrandname, davPassword,baseUrl);
            }
        }
        catch (IOException e){
            logger.debug("uploadImg: I/O Error:" + baseUrl);
            return new ReturnObject(ResponseCode.FILE_NO_WRITE_PERMISSION);
        }
        return returnObject;
    }
}
