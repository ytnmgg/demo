package com.hy.project.demo.model.sso;

import java.util.Date;

/**
 * @author rick.wl
 * @date 2021/08/11
 */
public class User extends Account {

    private static final long serialVersionUID = 8996878748316353439L;

    private Date gmtCreate;
    private Date gmtModified;

    /**
     * 加密后的密码，使用BCryptPasswordEncoder加密及验证
     * 要注意它有最大密码长度限制，通常为50～72字符，准确的长度限制取决于具体的Bcrypt实现
     * 所以我们在创建账号的时候，需要限制一下密码最大长度，比如32位
     */
    private String password;

    private String status;

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Account getAccount() {
        Account account = new Account();
        account.setUid(this.getUid());
        account.setName(this.getName());
        account.setRole(this.getRole());
        return account;
    }
}
