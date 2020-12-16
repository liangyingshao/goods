package cn.edu.xmu.oomall.other.service;

import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.oomall.other.model.CustomerDTO;

public interface ICustomerService {

    /**
     * 通过userId查找用户信息
     */
    ReturnObject<CustomerDTO> findCustomerByUserId(Long userId);

}