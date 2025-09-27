package vn.chiendt.request;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class NotificationRequest {
    private List<String> playerIds;
    private String message;
}
