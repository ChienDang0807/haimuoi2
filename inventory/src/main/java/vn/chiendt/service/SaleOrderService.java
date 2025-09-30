package vn.chiendt.service;

import vn.chiendt.dto.request.SaleOrderCreationRequest;
import vn.chiendt.dto.request.SaleOrderUpdateRequest;
import vn.chiendt.dto.response.PageResponse;
import vn.chiendt.dto.response.SaleOrderResponse;

public interface SaleOrderService {
    PageResponse<?> getAllSaleOrder(String sort, int page, int size);

    SaleOrderResponse getSaleOrderDetail(String id);

    String createSaleOrder(SaleOrderCreationRequest request);

    void updateSaleOrder(SaleOrderUpdateRequest request);

    void cancelSaleOrder(String id);

    void deleteSaleOrder(String id);
}

