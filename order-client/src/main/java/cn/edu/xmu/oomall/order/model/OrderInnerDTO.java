package cn.edu.xmu.oomall.order.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Caixin
 * @date 2020-12-07 20:38
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderInnerDTO implements Serializable {
    private Long shopId;

    private Long customerId;

    private Long orderId;
}
