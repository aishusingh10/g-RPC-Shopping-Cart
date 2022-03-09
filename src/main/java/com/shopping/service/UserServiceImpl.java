package com.shopping.service;

import com.shopping.client.OrderClient;
import com.shopping.db.User;
import com.shopping.db.UserDao;
import com.shopping.server.OrderServer;
import com.shopping.stubs.order.Order;
import com.shopping.stubs.user.Gender;
import com.shopping.stubs.user.UserRequest;
import com.shopping.stubs.user.UserResponse;
import com.shopping.stubs.user.UserServiceGrpc;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserServiceImpl extends UserServiceGrpc.UserServiceImplBase {
    private UserDao userdao = new UserDao();
    private static final Logger logger = Logger.getLogger(UserServiceImpl.class.getName());
    @Override
    public void getUserDetails(UserRequest request, StreamObserver<UserResponse> responseObserver) {

        User user = userdao.getDetails(request.getUsername());
        UserResponse.Builder userResponseBuilder = UserResponse.newBuilder().setId(user.getId()).setUsername(user.getUserName()).setName(user.getName()).setAge(user.getAge()).setGender(Gender.valueOf(user.getGender()));

        List<Order> orders = getOrders(userResponseBuilder);
        logger.info("no of items for the user: " + orders.size());
        userResponseBuilder.setNoOfOrders(orders.size());
        UserResponse userResponse = userResponseBuilder.build();
        responseObserver.onNext(userResponse);
        responseObserver.onCompleted();
    }

    private List<Order> getOrders(UserResponse.Builder userResponseBuilder) {
        ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:50052").usePlaintext().build();

        OrderClient orderClient = new OrderClient(channel);
        List<Order> orders = orderClient.getOrders(userResponseBuilder.getId());

        try {
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, "Channel did not shut down", e);
        }
        return orders;
    }
}
