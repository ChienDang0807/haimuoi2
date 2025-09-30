package vn.chiendt.utils;

import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PagingUtils {
    private static final Pattern SORT_PATTERN = Pattern.compile("(\\w+?)(:)(.*)");

    private PagingUtils() {
        // private constructor để ngăn khởi tạo class utility
    }

    public static Sort.Order buildSortOrder(String sort, String property) {
        if (!StringUtils.hasLength(sort)) {
            // fallback mặc định nếu không có sort
            return new Sort.Order(Sort.Direction.ASC, property);
        }

        Matcher matcher = SORT_PATTERN.matcher(sort);
        if (matcher.find()) {
            String columnName = matcher.group(1);
            String direction = matcher.group(3);

            if ("asc".equalsIgnoreCase(direction)) {
                return new Sort.Order(Sort.Direction.ASC, columnName);
            } else {
                return new Sort.Order(Sort.Direction.DESC, columnName);
            }
        }

        // fallback nếu pattern không khớp
        return new Sort.Order(Sort.Direction.ASC, "id");
    }

    public static int normalizePage(int page) {
        return (page > 0) ? page - 1 : 0;
    }
}
