package cn.itcast.travel.service;

import cn.itcast.travel.domain.User;

public interface UserService {
    /**
     *注册用户
     */
    boolean regist(User user);

    boolean active(String code);

    User Login(User user);
}
