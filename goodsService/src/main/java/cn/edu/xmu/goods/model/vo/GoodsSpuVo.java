package cn.edu.xmu.goods.model.vo;

import cn.edu.xmu.goods.model.bo.GoodsSpu;
import cn.edu.xmu.oomall.order.model.SimpleFreightModelDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品SPU VO
 * @author 24320182203254 秦楚彦
 * @date 2020/11/30 13:10
 * modified 2020/12/11 12:20
 */
@Data
@ApiModel(description = "查看商品SPU信息视图对象")
public class GoodsSpuVo {
    @ApiModelProperty(name = "Spuid")
    private Long id;

    @ApiModelProperty(name = "Spu名")
    private String name;

    @ApiModelProperty(name = "品牌")
    private SimpleBrandVo brand;

    @ApiModelProperty(name = "类别")
    private SimpleCategoryVo category;

    @ApiModelProperty(name = "运费模板")
    private SimpleFreightModelDTO freight;

    @ApiModelProperty(name = "店铺")
    private SimpleShopVo shop;

    @ApiModelProperty(name = "商品序列号")
    private String goodsSn;

    @ApiModelProperty(name = "SPU详情")
    private String detail;

    @ApiModelProperty(name = "图片url")
    private String imageUrl;

    @ApiModelProperty(name = "规格")
    private String specs;

    @ApiModelProperty(name = "SKU列表")
    private List<GoodsSkuRetVo> skuList;

    @ApiModelProperty(name = "SPU状态")
    private Byte state;

    @ApiModelProperty(name = "SPU新建时间")
    private LocalDateTime gmtCreate;

    @ApiModelProperty(name = "SPU修改时间")
    private LocalDateTime gmtModified;

    @ApiModelProperty(name = "是否失效")
    private boolean disable;

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
        this.specs=bo.getSpecs();
        this.gmtCreate=bo.getGmtCreated();
        this.gmtModified=bo.getGmtModified();
        this.disable=bo.isDisable();

    }

}
