package com.shopping.client;

import com.shopping.server.UserServer;
import com.shopping.stubs.order.Order;
import com.shopping.stubs.order.OrderRequest;
import com.shopping.stubs.order.OrderResponse;
import com.shopping.stubs.order.OrderServiceGrpc;
import io.grpc.Channel;
import io.grpc.ManagedChannel;

import java.util.List;
import java.util.logging.Logger;

public class OrderClient {

    private static final Logger logger = Logger.getLogger(OrderClient.class.getName());
    private OrderServiceGrpc.OrderServiceBlockingStub orderServiceBlockingStub;

    public OrderClient(Channel channel) {
        orderServiceBlockingStub = OrderServiceGrpc.newBlockingStub(channel);
    }

    public List<Order> getOrders(int userId){
        logger.info("We have triggered the client");
        OrderRequest orderRequest = OrderRequest.newBuilder().setUserId(userId).build();
        OrderResponse orderResponse = orderServiceBlockingStub.getOrderForUser(orderRequest);
        return orderResponse.getOrderList();
    }
}
