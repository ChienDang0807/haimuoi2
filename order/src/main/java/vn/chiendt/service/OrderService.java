package vn.chiendt.service;

import com.google.zxing.WriterException;
import vn.chiendt.common.OrderStatus;
import vn.chiendt.dto.request.PlaceOrderRequest;
import vn.chiendt.dto.request.UpdateOrderRequest;
import vn.chiendt.dto.response.OrderListResponse;
import vn.chiendt.model.Order;

import java.awt.image.BufferedImage;

public interface OrderService {
    OrderListResponse getAllOrders(OrderStatus status, String sort, int page, int size);

    Order getOrderDetail(String orderId);

    String createOrder(PlaceOrderRequest req);

    void updateOrder(UpdateOrderRequest req);

    void checkoutOrder(String orderId);

    void cancelOrder(String orderId);

    BufferedImage generateQRCodeImage(String text) throws WriterException;

    BufferedImage generateBarCodeImage(String barcode) throws WriterException;

}
