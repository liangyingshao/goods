package cn.edu.xmu.goods.model.bo;

import cn.edu.xmu.goods.model.po.BrandPo;
import cn.edu.xmu.goods.model.vo.BrandRetVo;
import cn.edu.xmu.goods.model.vo.BrandSimpleRetVo;
import cn.edu.xmu.goods.model.vo.BrandVo;
import cn.edu.xmu.ooad.model.VoObject;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Brand implements VoObject,Serializable{
    
    private Long id;

    private String name;

    private String imageUrl;

    private String detail;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    public Brand() {
    }

    public Brand(BrandPo po) {
        this.id = po.getId();
        this.name = po.getName();
        this.imageUrl = po.getImageUrl();
        this.detail = po.getDetail();
        this.gmtCreate = po.getGmtCreate();
        this.gmtModified = po.getGmtModified();
    }


    @Override
    public Object createVo() {
        return new BrandRetVo(this);
    }


    @Override
    public BrandSimpleRetVo createSimpleVo() {
        return new BrandSimpleRetVo(this);
    }

    public BrandPo createUpdatePo(BrandVo vo){
        BrandPo po = new BrandPo();
        po.setId(this.getId());
        po.setName(vo.getName());
        po.setDetail(vo.getDetail());
        po.setImageUrl(this.getImageUrl());
        po.setGmtCreate(null);
        po.setGmtModified(LocalDateTime.now());
        return po;
    }

    public BrandPo gotBrandPo() {
        BrandPo po = new BrandPo();
        po.setId(this.getId());
        po.setName(this.getName());
        po.setImageUrl(this.getImageUrl());
        po.setDetail(this.getDetail());
        po.setGmtCreate(this.getGmtCreate());
        po.setGmtModified(this.getGmtModified());
        return po;
    }
}
