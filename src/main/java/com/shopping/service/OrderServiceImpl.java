package com.shopping.service;

import com.google.protobuf.util.Timestamps;
import com.shopping.db.Order;
import com.shopping.db.OrderDao;
import com.shopping.db.User;
import com.shopping.db.UserDao;
import com.shopping.stubs.order.OrderRequest;
import com.shopping.stubs.order.OrderResponse;
import com.shopping.stubs.order.OrderServiceGrpc;
import com.shopping.stubs.user.Gender;
import com.shopping.stubs.user.UserRequest;
import com.shopping.stubs.user.UserResponse;
import com.shopping.stubs.user.UserServiceGrpc;
import io.grpc.stub.StreamObserver;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

public class OrderServiceImpl extends OrderServiceGrpc.OrderServiceImplBase {

    private OrderDao orderDao = new OrderDao();

    @Override
    public void getOrderForUser(OrderRequest request, StreamObserver<OrderResponse> responseObserver) {
        List<Order> orders = orderDao.getOrders(request.getUserId());
        List<com.shopping.stubs.order.Order> ordersFromUser = orders.stream().map(order -> com.shopping.stubs.order.Order.newBuilder()
                .setUserId(order.getUserId())
                .setOrderId(order.getOrderId())
                .setNoOfItems(order.getNoOfItems())
                .setTotalAmount(order.getTotalAmount())
                .setOrderDate(Timestamps.fromMillis(order.getOrderDate().getTime())).build()).collect(Collectors.toList());

        OrderResponse orderResponse = OrderResponse.newBuilder().addAllOrder(ordersFromUser).build();

        responseObserver.onNext(orderResponse);
        responseObserver.onCompleted();
    }
}
