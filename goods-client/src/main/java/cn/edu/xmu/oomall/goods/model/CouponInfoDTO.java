package cn.edu.xmu.oomall.goods.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponInfoDTO implements Serializable {
    /**
     * 优惠活动id
     */
    private Long id;
    private String name;
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
}
