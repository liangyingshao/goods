package cn.edu.xmu.goods.model.vo;

import cn.edu.xmu.goods.model.bo.GoodsCategory;
import cn.edu.xmu.goods.model.bo.GoodsCategory;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * description: GoodsCategoryVo
 * date: 2020/12/1 1:54
 * author: 张悦
 * version: 1.0
 */
@Data
@ApiModel(description = "类目视图对象")
public class GoodsCategoryVo {
    @NotBlank(message = "类目名称不能为空")
    @ApiModelProperty(value = "类目名称")
    private String name;

    public GoodsCategory createGoodsCategory() {
        GoodsCategory goodsCategory = new GoodsCategory();
        goodsCategory.setName(this.name);
        return goodsCategory;
    }
}
