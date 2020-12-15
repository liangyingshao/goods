package cn.edu.xmu.oomall.other.service;

import cn.edu.xmu.ooad.util.ReturnObject;

public interface IAddressService {

    /**
     * 查询该地区id是否被废弃
     */
    ReturnObject<Boolean> getValidRegionId(Long regionId);

}
