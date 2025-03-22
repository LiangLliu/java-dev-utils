package com.LiangLliu.utils.async;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public final class AsyncExecutor implements AutoCloseable {
    // 默认使用虚拟线程池（Java 21+），通常无需关闭
    private static final ExecutorService DEFAULT_EXECUTOR = Executors.newVirtualThreadPerTaskExecutor();

    // 允许自定义 Executor（例如测试时替换为固定线程池）
    private static volatile ExecutorService asyncExecutor = DEFAULT_EXECUTOR;

    private AsyncExecutor() {
    } // 防止实例化

    //------------------- 核心方法 -------------------//

    /**
     * 提交有返回值的任务（推荐使用 CompletableFuture 链式操作）
     */
    public static <T> CompletableFuture<T> supplyAsync(Supplier<T> supplier) {
        return CompletableFuture.supplyAsync(supplier, asyncExecutor);
    }

    /**
     * 提交无返回值的任务
     */
    public static CompletableFuture<Void> runAsync(Runnable runnable) {
        return CompletableFuture.runAsync(runnable, asyncExecutor);
    }

    /**
     * 同步获取 Future 结果，并保留原始异常信息
     */
    public static <T> T fetch(Future<T> future) {
        try {
            return future.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // 恢复中断状态
            throw new AsyncExecutionException("Task interrupted", e);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            throw new AsyncExecutionException("Task execution failed", cause);
        }
    }

    @SafeVarargs
    public static <T> List<T> mergeFutureLists(CompletableFuture<List<T>>... completableFutureList) {
        if (Objects.isNull(completableFutureList)) {
            return Collections.emptyList();
        }
        return CompletableFuture.allOf(completableFutureList)
                .thenApply(v -> Arrays.stream(completableFutureList)
                        .map(CompletableFuture::join)
                        .flatMap(List::stream)
                        .toList()
                )
                .join();
    }


    /**
     * 设置自定义 Executor（非必须，默认虚拟线程已足够）
     * 如果之前使用了自定义 Executor，则在替换前会关闭旧的 Executor以释放资源
     */
    public static synchronized void setCustomExecutor(ExecutorService executor) {
        Objects.requireNonNull(executor, "Executor cannot be null");
        if (asyncExecutor != DEFAULT_EXECUTOR && asyncExecutor != executor) {
            shutdownExecutor(asyncExecutor);
        }
        asyncExecutor = executor;
    }

    //------------------- 资源管理 -------------------//

    /**
     * 实现 AutoCloseable，支持 try-with-resources 优雅关闭 Executor
     */
    @Override
    public void close() {
        shutdown();
    }

    /**
     * 关闭 Executor
     * 如果当前 Executor 不是默认虚拟线程池，则优雅关闭后恢复为默认 Executor
     */
    public static void shutdown() {
        if (asyncExecutor != DEFAULT_EXECUTOR) {
            shutdownExecutor(asyncExecutor);
            asyncExecutor = DEFAULT_EXECUTOR;
        }
    }

    /**
     * 关闭指定 Executor，等待任务执行完毕，否则强制关闭
     */
    private static void shutdownExecutor(ExecutorService executor) {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    //------------------- 自定义异常 -------------------//
    public static class AsyncExecutionException extends RuntimeException {
        public AsyncExecutionException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}