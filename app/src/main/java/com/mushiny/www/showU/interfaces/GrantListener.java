package com.mushiny.www.showU.interfaces;

public interface GrantListener {

    void onAgree();// 某权限同意

    void onDeny();// 某权限拒绝但没选择不再询问项

    void onDenyNotAskAgain();// 某权限拒绝并选择了不再询问项
}
