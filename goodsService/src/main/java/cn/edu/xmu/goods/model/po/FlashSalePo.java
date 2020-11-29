package cn.edu.xmu.goods.model.po;

import java.util.Date;

public class FlashSalePo {
    private Long id;

    private Date flashDate;

    private Long timeSegId;

    private Date gmtCreated;

    private Date gmtModified;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getFlashDate() {
        return flashDate;
    }

    public void setFlashDate(Date flashDate) {
        this.flashDate = flashDate;
    }

    public Long getTimeSegId() {
        return timeSegId;
    }

    public void setTimeSegId(Long timeSegId) {
        this.timeSegId = timeSegId;
    }

    public Date getGmtCreated() {
        return gmtCreated;
    }

    public void setGmtCreated(Date gmtCreated) {
        this.gmtCreated = gmtCreated;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }
}