package com.LiangLliu.utils.async;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AsyncExecutorAdvancedTests {
    private final AtomicInteger counter = new AtomicInteger();

    //------------------- 并行任务场景 -------------------//

    @Test
    void testParallelTasksWithSharedResource() throws Exception {
        // 模拟100个线程并发修改共享计数器
        CompletableFuture<?>[] futures = new CompletableFuture[1_0000];
        for (int i = 0; i < 1_0000; i++) {
            futures[i] = AsyncExecutor.runAsync(() -> {
                counter.incrementAndGet();
                simulateWork(10); // 模拟工作耗时
            });
        }

        CompletableFuture.allOf(futures).get(2, TimeUnit.SECONDS);
        assertEquals(10000, counter.get());
    }

    @Test
    void testParallelTasksWithResultCombination() throws Exception {
        // 并行计算三个独立任务后合并结果
        CompletableFuture<Integer> task1 = AsyncExecutor.supplyAsync(() -> 10);
        CompletableFuture<Integer> task2 = AsyncExecutor.supplyAsync(() -> 20);
        CompletableFuture<Integer> task3 = AsyncExecutor.supplyAsync(() -> 30);

        CompletableFuture<Integer> combined = task1.thenCombine(task2, Integer::sum)
                .thenCombine(task3, Integer::sum);

        assertEquals(60, AsyncExecutor.fetch(combined));
    }

    //------------------- 链式调用场景 -------------------//

    @Test
    void testChainedTasksWithDependency() throws Exception {
        // 顺序执行：获取用户 -> 获取订单 -> 发送通知
        var chain = AsyncExecutor.supplyAsync(this::mockFetchUser)
                .thenApplyAsync(this::mockFetchOrder)
                .thenAcceptAsync(this::mockSendNotification)
                .thenRunAsync(() -> System.out.println("All done"));

        assertNull(AsyncExecutor.fetch(chain)); // Void结果
    }

    @Test
    void testChainWithExceptionHandling() {
        // 模拟链式调用中的异常传递
        var faultyChain = AsyncExecutor.supplyAsync(() -> "start")
                .thenApplyAsync(s -> s + "-step1")
                .thenApplyAsync(s -> {
                    throw new IllegalStateException("Broken here");
                })
                .thenApplyAsync(s -> s + "-step2")
                .exceptionally(e -> "Recovered: " + e.getCause().getMessage());

        assertEquals("Recovered: Broken here", AsyncExecutor.fetch(faultyChain));
    }

    //------------------- 混合场景 -------------------//

    @Test
    void testParallelAndChainedTasks() throws Exception {
        // 并行执行两个任务，完成后触发第三个任务
        CompletableFuture<Integer> taskA = AsyncExecutor.supplyAsync(() -> 10);
        CompletableFuture<Integer> taskB = AsyncExecutor.supplyAsync(() -> 30);

        var finalTask = taskA.thenCombineAsync(taskB, (a, b) -> a * b)
                .thenApplyAsync(sum -> sum + 2)
                .thenApplyAsync(sum -> sum * 3)
                .thenApplyAsync(this::mockSaveToDatabase);

        assertEquals(906, AsyncExecutor.fetch(finalTask));
    }

    @Test
    void testTimeoutInChain() {
        var timeoutChain = AsyncExecutor.supplyAsync(() -> {
                    simulateWork(500);
                    return "Initial Data";
                })
                .thenApplyAsync(data -> {
                    simulateWork(200);
                    return data + " Processed";
                })
                .orTimeout(300, TimeUnit.MILLISECONDS)
                .exceptionally(e -> {
                    // 安全获取异常信息
                    Throwable cause = e instanceof CompletionException ? e.getCause() : e;
                    String message = (cause != null) ? cause.getMessage() : "Unknown error";
                    return "Timeout occurred: " + message;
                });

        String result = AsyncExecutor.fetch(timeoutChain);
        assertTrue(result.startsWith("Timeout occurred"));
    }

    //------------------- 模拟方法 -------------------//

    private String mockFetchUser() {
        simulateWork(50);
        return "User123";
    }

    private String mockFetchOrder(String user) {
        simulateWork(100);
        return user + "-Order456";
    }

    private void mockSendNotification(String order) {
        simulateWork(80);
    }

    private Integer mockSaveToDatabase(Integer data) {
        simulateWork(150);
        return data;
    }

    private void simulateWork(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }
}