package vn.chiendt.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public class AdvanceSearchRequest {

    @NotNull(message = "searchFields must be not blank")
    private List<SearchField<?>> searchFields;
    private String sort;
    private int page;
    private int size;
}
