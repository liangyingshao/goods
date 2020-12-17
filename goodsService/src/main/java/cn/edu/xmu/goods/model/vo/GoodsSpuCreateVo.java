package cn.edu.xmu.goods.model.vo;

import cn.edu.xmu.goods.model.bo.GoodsSpu;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 新增商品SPU传入VO
 * @author 24320182203254 秦楚彦
 * @date 2020/11/30 22:37
 */
@Data
@ApiModel(description = "新增商品SPU信息视图对象")
public class GoodsSpuCreateVo {

    @ApiModelProperty(name = "Spu名")
    private String name;

    @ApiModelProperty(name = "SPU详细描述")
    private String description;

    @ApiModelProperty(name = "SPU规格")
    private String specs;

    /**
     * 构造函数
     * @author 24320182203254 秦楚彦
     * @date 2020/11/30 22:52
     */
    public GoodsSpu createSpu() {
        GoodsSpu spu =new GoodsSpu();
        spu.setDetail(this.description);
        spu.setName(this.name);
        spu.setSpecs(this.specs);
        return spu;
    }
}
