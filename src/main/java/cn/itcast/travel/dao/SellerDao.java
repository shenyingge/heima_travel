package cn.itcast.travel.dao;

import cn.itcast.travel.domain.Seller;

public interface SellerDao {
    //根据id查询
    public Seller findById(int sid);
}
