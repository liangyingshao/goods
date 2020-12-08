package cn.edu.xmu.oomall.goods.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 简单店铺VO
 * @author 24320182203254 秦楚彦
 * @date 2020/11/30 13:25
 */
@Data
@ApiModel
public class SimpleShopDTO {
    @ApiModelProperty(name = "店铺id", value = "id")
    private Long id;

    @ApiModelProperty(name = "店铺名", value = "userName")
    private String name;
}
