package com.bokecc.livemodule.utils;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 线程检测类
 * Created by dds on 2019/1/14.
 */
public class ThreadUtils {
    public ThreadUtils() {
    }

    /**
     * 检查是否在主线程
     */
    public static void checkIsOnMainThread() {
        if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
            throw new IllegalStateException("Not on main thread!");
        }
    }

    public static void executeUninterruptibly(ThreadUtils.BlockingOperation operation) {
        boolean wasInterrupted = false;

        while (true) {
            try {
                operation.run();
                break;
            } catch (InterruptedException var3) {
                wasInterrupted = true;
            }
        }

        if (wasInterrupted) {
            Thread.currentThread().interrupt();
        }

    }

    public static boolean joinUninterruptibly(Thread thread, long timeoutMs) {
        long startTimeMs = SystemClock.elapsedRealtime();
        long timeRemainingMs = timeoutMs;
        boolean wasInterrupted = false;

        while (timeRemainingMs > 0L) {
            try {
                thread.join(timeRemainingMs);
                break;
            } catch (InterruptedException var11) {
                wasInterrupted = true;
                long elapsedTimeMs = SystemClock.elapsedRealtime() - startTimeMs;
                timeRemainingMs = timeoutMs - elapsedTimeMs;
            }
        }

        if (wasInterrupted) {
            Thread.currentThread().interrupt();
        }

        return !thread.isAlive();
    }

    public static void joinUninterruptibly(final Thread thread) {
        executeUninterruptibly(new  ThreadUtils.BlockingOperation() {
            public void run() throws InterruptedException {
                thread.join();
            }
        });
    }

    public static void awaitUninterruptibly(final CountDownLatch latch) {
        executeUninterruptibly(new ThreadUtils.BlockingOperation() {
            public void run() throws InterruptedException {
                latch.await();
            }
        });
    }

    public static boolean awaitUninterruptibly(CountDownLatch barrier, long timeoutMs) {
        long startTimeMs = SystemClock.elapsedRealtime();
        long timeRemainingMs = timeoutMs;
        boolean wasInterrupted = false;
        boolean result = false;

        while (true) {
            try {
                result = barrier.await(timeRemainingMs, TimeUnit.MILLISECONDS);
                break;
            } catch (InterruptedException var12) {
                wasInterrupted = true;
                long elapsedTimeMs = SystemClock.elapsedRealtime() - startTimeMs;
                timeRemainingMs = timeoutMs - elapsedTimeMs;
                if (timeRemainingMs <= 0L) {
                    break;
                }
            }
        }

        if (wasInterrupted) {
            Thread.currentThread().interrupt();
        }

        return result;
    }

    public static void waitUninterruptibly(final Object object) {
        executeUninterruptibly(new  ThreadUtils.BlockingOperation() {
            public void run() throws InterruptedException {
                object.wait();
            }
        });
    }

    public static <V> V invokeAtFrontUninterruptibly(Handler handler, final Callable<V> callable) {
        if (handler.getLooper().getThread() == Thread.currentThread()) {
            try {
                return callable.call();
            } catch (Exception var6) {
                throw new RuntimeException(var6);
            }
        } else {
            class Result {
                public V value;

                Result() {
                }
            }

            final Result result = new Result();
            class CaughtException {
                Exception e;

                CaughtException() {
                }
            }

            final CaughtException caughtException = new CaughtException();
            final CountDownLatch barrier = new CountDownLatch(1);
            handler.post(new Runnable() {
                public void run() {
                    try {
                        result.value = callable.call();
                    } catch (Exception var2) {
                        caughtException.e = var2;
                    }

                    barrier.countDown();
                }
            });
            awaitUninterruptibly(barrier);
            if (caughtException.e != null) {
                RuntimeException runtimeException = new RuntimeException(caughtException.e);
                runtimeException.setStackTrace(concatStackTraces(caughtException.e.getStackTrace(), runtimeException.getStackTrace()));
                throw runtimeException;
            } else {
                return result.value;
            }
        }
    }

    public static void invokeAtFrontUninterruptibly(Handler handler, final Runnable runner) {
        invokeAtFrontUninterruptibly(handler, new Callable<Void>() {
            public Void call() {
                runner.run();
                return null;
            }
        });
    }

    static StackTraceElement[] concatStackTraces(StackTraceElement[] inner, StackTraceElement[] outer) {
        StackTraceElement[] combined = new StackTraceElement[inner.length + outer.length];
        System.arraycopy(inner, 0, combined, 0, inner.length);
        System.arraycopy(outer, 0, combined, inner.length, outer.length);
        return combined;
    }

    public interface BlockingOperation {
        void run() throws InterruptedException;
    }

    /**
     * 线程检测类
     */
    public static class ThreadChecker {
        private Thread thread = Thread.currentThread();

        public ThreadChecker() {
        }

        /**
         * 检查线程是否在当前线程
         */
        public void checkIsOnValidThread() {
            if (this.thread == null) {
                this.thread = Thread.currentThread();
            }

            if (Thread.currentThread() != this.thread) {
                throw new IllegalStateException("Wrong thread");
            }
        }

        public void detachThread() {
            this.thread = null;
        }
    }
}
