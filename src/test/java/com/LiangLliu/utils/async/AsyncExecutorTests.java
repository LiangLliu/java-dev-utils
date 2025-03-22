package com.LiangLliu.utils.async;

import com.LiangLliu.utils.async.AsyncExecutor.AsyncExecutionException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;


import java.lang.reflect.Field;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AsyncExecutorTests {

    @Test
    public void testRunAsync() {
        AsyncExecutor.runAsync(() -> System.out.println(2222));
    }

    /**
     * 多任务并行执行测试
     * 提交多个任务并通过 CompletableFuture.allOf() 等待所有任务完成，
     * 最后汇总各任务返回的结果。
     */
    @Test
    public void testParallelTasksExecution() throws Exception {
        CompletableFuture<Integer> task1 = AsyncExecutor.supplyAsync(() -> 10);
        CompletableFuture<Integer> task2 = AsyncExecutor.supplyAsync(() -> 20);
        CompletableFuture<Integer> task3 = AsyncExecutor.supplyAsync(() -> 30);

        // 等待所有任务完成
        CompletableFuture<Void> allTasks = CompletableFuture.allOf(task1, task2, task3);
        allTasks.get(5, TimeUnit.SECONDS);

        int sum = task1.get() + task2.get() + task3.get();
        // 10 + 20 + 30 = 60
        assertEquals(60, sum);
    }

    /**
     * 多任务链式调用测试
     * 任务之间通过 thenCompose/thenApply 链接，每个任务的结果传递给下一个任务进行处理。
     */
    @Test
    public void testChainedTasksExecution() throws Exception {
        CompletableFuture<Integer> chainFuture = AsyncExecutor.supplyAsync(() -> 5)
                // 第一个任务的结果乘以2
                .thenCompose(result -> AsyncExecutor.supplyAsync(() -> result * 2))
                // 再减去3
                .thenCompose(result -> AsyncExecutor.supplyAsync(() -> result - 3))
                // 最后乘以4
                .thenApply(result -> result * 4);

        // 计算过程：((5 * 2) - 3) * 4 = (10 - 3) * 4 = 7 * 4 = 28
        int finalResult = chainFuture.get(5, TimeUnit.SECONDS);
        assertEquals(28, finalResult);
    }

    /**
     * 并行与链式组合测试
     * 首先并行执行两个任务，然后将结果合并后再进行链式处理。
     */
    @Test
    public void testParallelAndChainCombined() throws Exception {
        // 并行执行两个任务
        CompletableFuture<Integer> taskA = AsyncExecutor.supplyAsync(() -> 7);
        CompletableFuture<Integer> taskB = AsyncExecutor.supplyAsync(() -> 13);

        // 合并两个任务的结果
        CompletableFuture<Integer> combined = taskA.thenCombine(taskB, Integer::sum)
                // 对合并后的结果进行链式处理：先乘以3，再减去5
                .thenCompose(sum -> AsyncExecutor.supplyAsync(() -> sum * 3))
                .thenApply(result -> result - 5);

        // 计算过程：7 + 13 = 20; 20 * 3 = 60; 60 - 5 = 55
        int finalResult = combined.get(5, TimeUnit.SECONDS);
        assertEquals(55, finalResult);
    }

    /**
     * 每个测试结束后重置 AsyncExecutor 使用的 Executor，
     * 保证后续测试不会受到 setCustomExecutor 的影响。
     */
    @AfterEach
    public void tearDown() throws Exception {
        Field asyncExecutorField = AsyncExecutor.class.getDeclaredField("asyncExecutor");
        asyncExecutorField.setAccessible(true);
        Field defaultExecutorField = AsyncExecutor.class.getDeclaredField("DEFAULT_EXECUTOR");
        defaultExecutorField.setAccessible(true);
        asyncExecutorField.set(null, defaultExecutorField.get(null));
    }

    /**
     * 测试 supplyAsync 方法能正确返回结果。
     */
    @Test
    public void testSupplyAsyncSuccess() throws Exception {
        CompletableFuture<String> future = AsyncExecutor.supplyAsync(() -> "Hello, World!");
        assertEquals("Hello, World!", future.get());
    }

    /**
     * 测试 runAsync 方法能正确执行 Runnable（通过副作用验证）。
     */
    @Test
    public void testRunAsyncSuccess() throws Exception {
        AtomicBoolean flag = new AtomicBoolean(false);
        CompletableFuture<Void> future = AsyncExecutor.runAsync(() -> flag.set(true));
        future.get(5, TimeUnit.SECONDS);
        assertTrue(flag.get());
    }

    /**
     * 测试 fetch 方法在任务正常完成时能正确获取结果。
     */
    @Test
    public void testFetchSuccess() {
        CompletableFuture<Integer> future = AsyncExecutor.supplyAsync(() -> 42);
        int result = AsyncExecutor.fetch(future);
        assertEquals(42, result);
    }

    /**
     * 测试 fetch 方法在任务抛出异常时，能够包装并透传原始异常。
     */
    @Test
    public void testFetchExecutionException() {
        CompletableFuture<Integer> future = AsyncExecutor.supplyAsync(() -> {
            throw new IllegalArgumentException("Invalid argument");
        });
        AsyncExecutionException ex = assertThrows(
                AsyncExecutionException.class,
                () -> AsyncExecutor.fetch(future)
        );
        assertNotNull(ex.getCause());
        assertInstanceOf(IllegalArgumentException.class, ex.getCause());
    }

    /**
     * 测试自定义 Executor 的使用，并验证 shutdown 后该 Executor 已关闭。
     */
    @Test
    public void testCustomExecutorUsageAndShutdown() throws Exception {
        ExecutorService customExecutor = Executors.newFixedThreadPool(2);
        AsyncExecutor.setCustomExecutor(customExecutor);
        CompletableFuture<Integer> future = AsyncExecutor.supplyAsync(() -> 100);
        assertEquals(100, future.get().intValue());
        AsyncExecutor.shutdown();
        assertTrue(customExecutor.isShutdown());
    }

    /**
     * 测试当使用默认 Executor 时，调用 shutdown 不会影响后续任务执行。
     */
    @Test
    public void testDefaultExecutorNotShutdown() throws Exception {
        // 未设置自定义 Executor，则默认使用虚拟线程池，调用 shutdown 后仍可正常提交任务
        AsyncExecutor.shutdown();
        CompletableFuture<String> future = AsyncExecutor.supplyAsync(() -> "default executor works");
        assertEquals("default executor works", future.get());
    }

    /**
     * 测试同时提交多个任务，验证并发执行结果正确性。
     */
    @Test
    public void testMultipleConcurrentTasks() throws Exception {
        int taskCount = 50;
        @SuppressWarnings("unchecked")
        CompletableFuture<Integer>[] futures = new CompletableFuture[taskCount];
        for (int i = 0; i < taskCount; i++) {
            final int index = i;
            futures[i] = AsyncExecutor.supplyAsync(() -> index);
        }
        int sum = 0;
        for (int i = 0; i < taskCount; i++) {
            sum += futures[i].get();
        }
        // 0+1+2+...+49 = 1225
        assertEquals(1225, sum);
    }

    /**
     * 测试当 Future.get() 被中断时，fetch 方法能捕获 InterruptedException，
     * 恢复中断状态并抛出 AsyncExecutionException。
     */
    @Test
    public void testFetchInterruptedException() throws Exception {
        CompletableFuture<String> future = AsyncExecutor.supplyAsync(() -> {
            try {
                Thread.sleep(2000); // 模拟耗时任务
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return "Delayed";
        });

        AtomicReference<Throwable> thrown = new AtomicReference<>();
        Thread testThread = new Thread(() -> {
            try {
                AsyncExecutor.fetch(future);
            } catch (Throwable t) {
                thrown.set(t);
            }
        });
        testThread.start();
        // 等待任务进入阻塞状态后中断该线程
        Thread.sleep(500);
        testThread.interrupt();
        testThread.join();

        Throwable t = thrown.get();
        assertNotNull(t, "Expected an exception from fetch");
        assertInstanceOf(AsyncExecutionException.class, t, "Expected AsyncExecutionException");
        // 验证异常原因为 InterruptedException
        assertInstanceOf(InterruptedException.class, t.getCause(), "Cause should be InterruptedException");
    }
}