package cn.edu.xmu.goods.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * description: GoodsCategoryRetVo
 * date: 2020/12/1 1:54
 * author: 张悦
 * version: 1.0
 */
@Data
@ApiModel(description = "分类视图对象")
public class GoodsCategoryRetVo {
    @ApiModelProperty(value = "分类id")
    private Long id;

    @ApiModelProperty(value = "父级分类id")
    private Long pid;

    @ApiModelProperty(value = "分类描述")
    private String name;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime gmtCreate;

    @ApiModelProperty(value = "修改时间")
    private LocalDateTime gmtModified;

    public GoodsCategoryRetVo(cn.edu.xmu.goods.model.bo.GoodsCategory goodsCategory) {
        this.id = goodsCategory.getId();
        this.name = goodsCategory.getName();
        this.pid = goodsCategory.getPid();
        this.gmtCreate = goodsCategory.getGmtCreate();
        this.gmtModified = goodsCategory.getGmtModified();
    }
}
