package cn.edu.xmu.goods.model.bo;

import cn.edu.xmu.goods.model.po.GoodsCategoryPo;
import cn.edu.xmu.goods.model.vo.GoodsCategoryRetVo;
import cn.edu.xmu.goods.model.vo.GoodsCategorySimpleRetVo;
import cn.edu.xmu.goods.model.vo.GoodsCategoryVo;
import cn.edu.xmu.ooad.model.VoObject;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


@Data
public class GoodsCategory implements VoObject, Serializable {
    private Long id;

    private Long pid;

    private String name;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    public GoodsCategory() {
    }

    public GoodsCategory(GoodsCategoryPo po) {
        this.id = po.getId();
        this.name = po.getName();
        this.pid = po.getPid();
        this.gmtCreate = po.getGmtCreate();
        this.gmtModified = po.getGmtModified();
    }

    @Override
    public Object createVo() {
        return new GoodsCategoryRetVo(this);
    }


    @Override
    public GoodsCategorySimpleRetVo createSimpleVo() {
        return new GoodsCategorySimpleRetVo(this);
    }

    public GoodsCategoryPo createUpdatePo(GoodsCategoryVo vo){
        GoodsCategoryPo po = new GoodsCategoryPo();
        po.setId(this.getId());
        po.setName(vo.getName());
        po.setPid(null);
        po.setGmtCreate(null);
        po.setGmtModified(LocalDateTime.now());
        return po;
    }

    public GoodsCategoryPo gotGoodsCategoryPo() {
        GoodsCategoryPo po = new GoodsCategoryPo();
        po.setId(this.getId());
        po.setPid(this.getPid());
        po.setName(this.getName());
        po.setGmtCreate(this.getGmtCreate());
        po.setGmtModified(this.getGmtModified());
        return po;
    }

}
