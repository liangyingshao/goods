package cn.edu.xmu.goods.model.vo;

import cn.edu.xmu.goods.model.bo.Brand;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * detailiption: BrandVo
 * date: 2020/12/1 1:54
 * author: 张悦
 * version: 1.0
 */
@Data
@ApiModel("品牌传值对象")
public class BrandVo {

    @NotBlank(message = "品牌名称不能为空")
    @ApiModelProperty(value = "品牌名称")
    private String name;

    @ApiModelProperty(value = "品牌描述")
    private String detail;

    public Brand createBrand() {
        Brand brand = new Brand();
        brand.setDetail(this.detail);
        brand.setName(this.name);
        return brand;
    }
}
