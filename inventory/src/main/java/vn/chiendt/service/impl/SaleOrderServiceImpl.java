package vn.chiendt.service.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import vn.chiendt.common.OrderStatus;
import vn.chiendt.dto.request.SaleOrderCreationRequest;
import vn.chiendt.dto.request.SaleOrderItemCreationRequest;
import vn.chiendt.dto.request.SaleOrderUpdateRequest;
import vn.chiendt.dto.response.PageResponse;
import vn.chiendt.dto.response.SaleOrderResponse;
import vn.chiendt.exception.ResourceNotFoundException;
import vn.chiendt.model.SaleOrder;
import vn.chiendt.model.SaleOrderItem;
import vn.chiendt.repository.SaleOrderItemRepository;
import vn.chiendt.repository.SaleOrderRepository;
import vn.chiendt.service.SaleOrderService;
import vn.chiendt.utils.PagingUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j(topic = "SALE-ORDER-SERVICE")
public class SaleOrderServiceImpl implements SaleOrderService {

    private final SaleOrderRepository saleOrderRepository;
    private final SaleOrderItemRepository saleOrderItemRepository;

    @Override
    public PageResponse<?> getAllSaleOrder(String sort, int page, int size) {
        log.info("getAllSaleOrder called");

        // Sorting
        Sort.Order order = PagingUtils.buildSortOrder(sort, "createdAt");
        int pageNo = PagingUtils.normalizePage(page);

        // Paging
        Pageable pageable = PageRequest.of(pageNo, size, Sort.by(order));

        Page<SaleOrder> orders = saleOrderRepository.findAll(pageable);

        return PageResponse.builder()
                .pageNumber(page)
                .pageSize(size)
                .totalPages(orders.getTotalPages())
                .totalElements(orders.getTotalElements())
                .data(orders.toList())
                .build();
    }

    @Override
    public SaleOrderResponse getSaleOrderDetail(String id) {
        log.info("getSaleOrderDetail called");

        SaleOrder saleOrder = getSaleOrderById(id);

        return SaleOrderResponse.builder()
                .id(saleOrder.getId())
                .customerId(saleOrder.getCustomerId())
                .status(saleOrder.getStatus())
                .totalAmount(saleOrder.getTotalAmount())
                .createdAt(saleOrder.getCreatedAt())
                .updatedAt(saleOrder.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createSaleOrder(SaleOrderCreationRequest request) {
        log.info("createSaleOrder called");

        String orderId = request.getId();
        if (!StringUtils.hasLength(orderId)) {
            orderId = UUID.randomUUID().toString();
        }

        SaleOrder saleOrder = new SaleOrder();
        saleOrder.setId(orderId);
        saleOrder.setCustomerId(request.getCustomerId());
        saleOrder.setTotalAmount(request.getTotalAmount());
        saleOrder.setCurrency(request.getCurrency());
        saleOrder.setPaymentMethod(request.getPaymentMethod());
        saleOrder.setStatus(request.getStatus());

        SaleOrder result = saleOrderRepository.save(saleOrder);
        log.info("SaleOder saved");

        if (result.getId() != null) {
            List<SaleOrderItem> saleOrderItems = new ArrayList<>();
            for (SaleOrderItemCreationRequest item : request.getItems()) {
                SaleOrderItem orderItem = new SaleOrderItem();
                orderItem.setSalesId(result.getId());
                orderItem.setProductId(item.getProductId());
                orderItem.setQuantity(item.getQuantity());
                orderItem.setPrice(item.getPrice());

                saleOrderItems.add(orderItem);
            }

            saleOrderItemRepository.saveAll(saleOrderItems);
            log.info("saleOrderItems saved");
        }

        log.info("saleOrder created id: {}", result.getId());

        return result.getId();
    }

    @Override
    public void updateSaleOrder(SaleOrderUpdateRequest request) {
        log.info("updateSaleOrder called");
    }

    @Override
    public void cancelSaleOrder(String id) {
        log.info("cancelSaleOrder called");
        SaleOrder saleOrder = getSaleOrderById(id);
        saleOrder.setStatus(OrderStatus.CANCELED);
        saleOrderRepository.save(saleOrder);
    }

    @Override
    public void deleteSaleOrder(String id) {
        log.info("deleteSaleOrder called");
        saleOrderRepository.deleteById(id);
    }

    /**
     * Get sale order by id
     *
     * @param id
     * @return
     */
    private SaleOrder getSaleOrderById(String id) {
        log.info("getSaleOrderById called");
        return saleOrderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Sale Order not found"));
    }
}