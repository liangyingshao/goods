package cn.edu.xmu.oomall.other.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Caixin
 * @date 2020-12-07 20:51
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AftersaleDTO implements Serializable {
    private Long shopId;
    private Long customerId;
}
