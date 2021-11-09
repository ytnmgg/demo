package com.hy.project.demo.model.sso;

/**
 * @author rick.wl
 * @date 2021/11/04
 */
public class AccountContext {
    private static final InheritableThreadLocal<Account> ACCOUNT_HOLDER = new InheritableThreadLocal<Account>();

    public static void setAccount(final Account account) {
        ACCOUNT_HOLDER.set(account);
    }

    public static Account getAccount() {
        return ACCOUNT_HOLDER.get();
    }

    public static void removeAccount() {
        ACCOUNT_HOLDER.remove();
    }
}
