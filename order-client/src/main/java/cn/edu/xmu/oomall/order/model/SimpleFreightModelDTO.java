package cn.edu.xmu.oomall.order.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 简单运费模板dto
 * @date 2020/12/13 17:13
 * @version 1.0
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleFreightModelDTO implements Serializable
{
    private Long id;
    private String name;
    private Byte type;
    private Integer unit;
    private Boolean isDefault;
    private LocalDateTime gmtCreated;
    private LocalDateTime gmtModified;
}