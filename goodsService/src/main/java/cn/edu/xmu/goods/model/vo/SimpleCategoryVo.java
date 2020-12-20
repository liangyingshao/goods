package cn.edu.xmu.goods.model.vo;

import cn.edu.xmu.goods.model.po.GoodsCategoryPo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 简单商品类别VO
 * @author 24320182203254 秦楚彦
 * @date 2020/11/10 18:41
 */
@Data
@ApiModel
public class SimpleCategoryVo {
    @ApiModelProperty(name = "品牌id", value = "id")
    private Long id;

    @ApiModelProperty(name = "品牌名", value = "name")
    private String name;

    public SimpleCategoryVo() {

    }
    public SimpleCategoryVo(GoodsCategoryPo po) {
        this.id=po.getId();
        this.name=po.getName();
    }
}
