package cn.edu.xmu.oomall.goods.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PresaleDTO implements Serializable{
    private Boolean isValid;//预售活动是否有效

    private Long advancePayPrice;//预付金额

    private Long restPayPrice;//尾款金额

}
