package cn.edu.xmu.goods.model.vo;

import cn.edu.xmu.goods.model.bo.GoodsCategory;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * description: GoodsCategorySimpleRetVo
 * date: 2020/12/1 1:54
 * author: 张悦
 * version: 1.0
 */
@Data
@ApiModel(description = "分类视图对象")
public class GoodsCategorySimpleRetVo {
    @ApiModelProperty(value = "分类id")
    private Long id;

    @ApiModelProperty(value = "分类名称")
    private String name;

    public GoodsCategorySimpleRetVo(GoodsCategory goodsCategory) {
        this.id = goodsCategory.getId();
        this.name = goodsCategory.getName();
    }
}
