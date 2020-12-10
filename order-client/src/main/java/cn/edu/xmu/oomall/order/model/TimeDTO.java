package cn.edu.xmu.oomall.order.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalTime;

/**
 * 时间dto
 *
 * @author wwc
 * @date 2020/11/24 23:08
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeDTO implements Serializable {
    private Long id;
    private Byte type;
    private LocalTime beginTime;
    private LocalTime endTime;
}
