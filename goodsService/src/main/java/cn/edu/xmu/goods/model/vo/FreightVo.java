package cn.edu.xmu.goods.model.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel
public class FreightVo {
    private Long id;
    private String name;
    private Byte type;
    private boolean isDefault;
    private LocalDateTime gmtCreated;
    private LocalDateTime gmtModified;
}