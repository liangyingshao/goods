package cn.edu.xmu.oomall.order.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Caixin
 * @date 2020-12-09 17:12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FreightDTO implements Serializable {

    private Integer quantity;

    private Long regionId;

    private Long skuId;
}
