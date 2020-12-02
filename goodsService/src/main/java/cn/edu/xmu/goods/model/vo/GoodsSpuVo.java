package cn.edu.xmu.goods.model.vo;

import cn.edu.xmu.goods.model.bo.GoodsSpu;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 商品SPU VO
 * @author 24320182203254 秦楚彦
 * @date 2020/11/30 13:10
 */
@Data
@ApiModel(description = "查看商品SPU信息视图对象")
public class GoodsSpuVo {
    @ApiModelProperty(name = "Spuid")
    private Long id;

    @ApiModelProperty(name = "Spu名")
    private String name;

    @ApiModelProperty(name = "品牌")
    private SimpleBrandVo brandVo;

    @ApiModelProperty(name = "类别")
    private SimpleCategoryVo categoryVo;

    @ApiModelProperty(name = "SKU列表")
    private List<SimpleSkuVo> goodsSkuList;

    @ApiModelProperty(name = "运费模板")
    private FreightVo freightvo;

    @ApiModelProperty(name = "店铺")
    private SimpleShopVo shopVo;

    @ApiModelProperty(name = "商品序列号")
    private String goodsSn;

    @ApiModelProperty(name = "SPU详情")
    private String detail;

    @ApiModelProperty(name = "图片url")
    private String imageUrl;

    @ApiModelProperty(name = "规格")
    private String spec;

    @ApiModelProperty(name = "SPU状态")
    private Byte state;

    @ApiModelProperty(name = "是否失效")
    private Byte disabled;

    /**
     * 构造函数
     * @param bo Bo对象
     */

    public GoodsSpuVo(GoodsSpu bo){
        this.id=bo.getId();
        this.name=bo.getName();
        this.goodsSn=bo.getGoodsSn();
        this.detail=bo.getDetail();
        this.imageUrl=bo.getImageUrl();
        this.state=bo.getState().getCode().byteValue();
        this.spec=bo.getSpec();
        this.disabled=bo.getDisabled().getCode().byteValue();
    }

}
