package cn.edu.xmu.goods.model.vo;

import cn.edu.xmu.goods.model.bo.Brand;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * description: BrandRetVo
 * date: 2020/12/1 1:54
 * author: 张悦
 * version: 1.0
 */
@Data
@ApiModel
public class BrandRetVo {

    private Long id;

    private String name;

    private String imageUrl;

    private String detail;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    public BrandRetVo(Brand obj){
        this.id = obj.getId();
        this.imageUrl = obj.getImageUrl();
        this.detail = obj.getDetail();
        this.name = obj.getName();
        this.gmtCreate = obj.getGmtCreate();
        this.gmtModified = obj.getGmtModified();
    }
}
