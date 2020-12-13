package cn.edu.xmu.flashsale.model.vo;

import cn.edu.xmu.flashsale.model.po.FlashSalePo;
import cn.edu.xmu.oomall.other.model.TimeDTO;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class FlashsaleNewRetVo {
    private Long id;
    @DateTimeFormat
    private LocalDateTime flashDate;
    private TimeDTO timeDTO;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;

    public FlashsaleNewRetVo(FlashSalePo po)
    {
        this.flashDate = po.getFlashDate();
        this.id = po.getId();
        this.gmtCreate = po.getGmtCreate();
        this.gmtModified = po.getGmtModified();
    }

}
