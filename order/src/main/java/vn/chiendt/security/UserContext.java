package vn.chiendt.security;

public class UserContext {
    private static final ThreadLocal<Long> userId = new ThreadLocal<>();

    public static void setUserId(long id) {
        userId.set(id);
    }

    public static Long getUserId() {
        return userId.get();
    }

    public static void clear(){
        userId.remove();
    }
}
