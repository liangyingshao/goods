package cn.edu.xmu.oomall.other.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalTime;

/**
 * @date 2020/12/15 23:08
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
