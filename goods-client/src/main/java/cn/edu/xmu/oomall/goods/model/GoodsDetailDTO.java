package cn.edu.xmu.oomall.goods.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Caixin
 * @date 2020-12-08 22:06
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoodsDetailDTO implements Serializable {
    private String name;
    private Long price;
    private Integer inventory;
}
