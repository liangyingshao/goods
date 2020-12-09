package cn.edu.xmu.flashsale.model.vo;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Future;
import java.time.LocalDateTime;

@Data
public class FlashsaleModifVo {
    @Future
    @DateTimeFormat
    private LocalDateTime flashDate;
    public FlashsaleModifVo()
    {

    }
}
