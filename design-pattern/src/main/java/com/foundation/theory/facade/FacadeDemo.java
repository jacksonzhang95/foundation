package com.foundation.theory.facade;

/**
 * @author : jacksonz
 * @date : 2021/10/18 14:04
 */
public class FacadeDemo<T> {

    private UserService userService;
    private UserSpaceService userSpaceService;
    private UserWalletService userWalletService;

    /**
     * 使用一个高层接口封装多个子系统的复杂调用 并 在这个接口实现事务(tcc)
     */
    public void createUser() {
        try {
            userService.addUserInfo();
            userSpaceService.initUserSpace();
            userWalletService.initUserWallet();
        } catch (Exception e) {
            userService.deleteUserInfo();
            userSpaceService.deleteUserSpace();
            userWalletService.deleteUserWallet();
        }
    }
}

interface UserService {
    void addUserInfo();
    void deleteUserInfo();
}
interface UserSpaceService{
    void initUserSpace();
    void deleteUserSpace();
}
interface UserWalletService{
    void initUserWallet();
    void deleteUserWallet();
}
