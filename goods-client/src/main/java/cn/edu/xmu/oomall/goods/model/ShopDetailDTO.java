package cn.edu.xmu.oomall.goods.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Caixin
 * @date 2020-12-08 15:45
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopDetailDTO implements Serializable {
    private Long shopId;
    private String name;
    private Byte state;
    private String gmtCreate;
    private String gmtModified;
}
