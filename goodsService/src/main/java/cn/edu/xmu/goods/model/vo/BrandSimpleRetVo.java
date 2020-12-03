package cn.edu.xmu.goods.model.vo;

import cn.edu.xmu.goods.model.bo.Brand;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * description: BrandSimpleRetVo
 * date: 2020/12/1 1:54
 * author: 张悦
 * version: 1.0
 */
@Data
@ApiModel
public class BrandSimpleRetVo {
    private Long id;

    private String name;

    private String imageUrl;

    public BrandSimpleRetVo(Brand obj){
        this.id = obj.getId();
        this.name = obj.getName();
        this.imageUrl = obj.getImageUrl();
    }
}
