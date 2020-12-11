package cn.edu.xmu.oomall.goods.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Caixin
 * @date 2020-12-09 17:31
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoodsFreightDTO implements Serializable {
    private Integer weight;
    private Long freightModelId;
    private Long shopId;
}
