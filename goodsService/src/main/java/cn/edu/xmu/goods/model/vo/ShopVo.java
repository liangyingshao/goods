package cn.edu.xmu.goods.model.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * description: ShopVo
 * date: 2020/12/18 13:39
 * author: 杨铭
 * version: 1.0
 */
@Data
public class ShopVo {

    @NotBlank
    @NotNull
    String name;
}
