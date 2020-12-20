package cn.edu.xmu.goods.model.vo;

import cn.edu.xmu.goods.model.po.BrandPo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 简单品牌VO
 * @author 24320182203254 秦楚彦
 * @date 2020/11/30 13:20
 */

@Data
@ApiModel
public class SimpleBrandVo {
    @ApiModelProperty(name = "品牌id", value = "id")
    private Long id;

    @ApiModelProperty(name = "品牌名", value = "name")
    private String name;

    @ApiModelProperty(name = "品牌图片url", value = "imageUrl")
    private String imgUrl;

    public SimpleBrandVo() {

    }
    public SimpleBrandVo(BrandPo po) {
        this.id=po.getId();
        this.name=po.getName();
        this.imgUrl=po.getImageUrl();
    }
}
