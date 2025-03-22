package com.LiangLliu.utils.async;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AsyncComplexMergeSplitTests {
    // 模拟服务调用计数器
    private final AtomicInteger userServiceCalls = new AtomicInteger();
    private final AtomicInteger orderServiceCalls = new AtomicInteger();
    private final AtomicInteger inventoryServiceCalls = new AtomicInteger();
    private final AtomicInteger notificationCounter = new AtomicInteger();

    @Test
    void testMultiStageProcessing() throws Exception {
        // 第一阶段：并行获取三个服务数据
        CompletableFuture<UserInfo> userFuture = getUserInfoAsync(123);
        CompletableFuture<OrderHistory> orderFuture = getOrderHistoryAsync(123);
        CompletableFuture<InventoryStatus> inventoryFuture = getInventoryStatusAsync(456);

        // 第二阶段：合并三方数据
        CompletableFuture<OrderPackage> mergedFuture = userFuture
                .thenCombine(orderFuture, (user, orders) -> new UserOrderData(user, orders))
                .thenCombine(inventoryFuture, (userOrder, inventory) -> {
                    // 验证库存和用户资格
                    if (inventory.stock() < 1) throw new IllegalStateException("Out of stock");
                    if (!userOrder.user().valid()) throw new IllegalArgumentException("Invalid user");
                    return new OrderPackage(userOrder, inventory);
                });

        // 第三阶段：拆分处理
        CompletableFuture<Void> paymentFuture = mergedFuture.thenComposeAsync(this::processPayment);
        CompletableFuture<Void> shippingFuture = mergedFuture.thenComposeAsync(this::prepareShipping);

        // 第四阶段：并行处理结果合并
        var finalResult = paymentFuture
                .thenCombine(shippingFuture, (p, s) -> new OrderResult("Combined", true))
                .thenApplyAsync(this::generateReport);

        // 验证结果
        OrderReport report = finalResult.get(5, TimeUnit.SECONDS);

        assertAll(
                () -> assertEquals(1, userServiceCalls.get()),
                () -> assertEquals(1, orderServiceCalls.get()),
                () -> assertEquals(1, inventoryServiceCalls.get()),
                () -> assertEquals(2, notificationCounter.get()), // 邮件+短信
                () -> assertTrue(report.success()),
                () -> assertEquals("Order123-Report", report.reportId())
        );
    }

    //------------------- 模拟服务方法 -------------------//

    private CompletableFuture<UserInfo> getUserInfoAsync(int userId) {
        return AsyncExecutor.supplyAsync(() -> {
            simulateWork(50);
            userServiceCalls.incrementAndGet();
            return new UserInfo(userId, "user" + userId + "@example.com", true);
        });
    }

    private CompletableFuture<OrderHistory> getOrderHistoryAsync(int userId) {
        return AsyncExecutor.supplyAsync(() -> {
            simulateWork(80);
            orderServiceCalls.incrementAndGet();
            return new OrderHistory(userId, List.of("Order001", "Order002"));
        });
    }

    private CompletableFuture<InventoryStatus> getInventoryStatusAsync(int itemId) {
        return AsyncExecutor.supplyAsync(() -> {
            simulateWork(100);
            inventoryServiceCalls.incrementAndGet();
            return new InventoryStatus(itemId, 10);
        });
    }

    private CompletableFuture<Void> processPayment(OrderPackage order) {
        return AsyncExecutor.runAsync(() -> {
            simulateWork(150);
            sendEmailNotification(order.userOrder().user(), "Payment Processed");
        });
    }

    private CompletableFuture<Void> prepareShipping(OrderPackage order) {
        return AsyncExecutor.runAsync(() -> {
            simulateWork(200);
            sendSmsNotification(order.userOrder().user(), "Shipping Prepared");
        });
    }

    private OrderReport generateReport(OrderResult result) {
        return new OrderReport(
                "Order123-Report",
                true,
                Instant.now(),
                "Combined processing completed"
        );
    }

    //------------------- 辅助方法 -------------------//

    private void sendEmailNotification(UserInfo user, String message) {
        simulateWork(30);
        notificationCounter.incrementAndGet();
    }

    private void sendSmsNotification(UserInfo user, String message) {
        simulateWork(20);
        notificationCounter.incrementAndGet();
    }

    private void simulateWork(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }

    //------------------- 记录定义 -------------------//

    record UserInfo(int userId, String email, boolean valid) {
    }

    record OrderHistory(int userId, List<String> orderIds) {
    }

    record InventoryStatus(int itemId, int stock) {
    }

    record UserOrderData(UserInfo user, OrderHistory orders) {
    }

    record OrderPackage(UserOrderData userOrder, InventoryStatus inventory) {
    }

    record OrderResult(String transactionId, boolean success) {
    }

    record OrderReport(String reportId, boolean success, Instant timestamp, String details) {
    }
}