package vn.chiendt.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@RedisHash("PermissionHash")
public class PermissionHash implements Serializable {
    @Id
    private String id; // save username
    private List<String> roles;
}
