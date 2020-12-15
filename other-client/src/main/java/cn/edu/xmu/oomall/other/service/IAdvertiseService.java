package cn.edu.xmu.oomall.other.service;


import cn.edu.xmu.ooad.util.ReturnObject;

public interface IAdvertiseService {

    /**
     * 将时段下的广告时段置为0
     */
    ReturnObject deleteTimeSegmentAdvertisements();

}
