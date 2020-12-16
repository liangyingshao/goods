package cn.edu.xmu.oomall.order.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleFreightModelDTO implements Serializable {
    private Long id;
    private String name;
    private Byte type;
    private Integer unit;
    private Boolean isDefault;
    private LocalDateTime gmtCreated;
    private LocalDateTime gmtModified;
}