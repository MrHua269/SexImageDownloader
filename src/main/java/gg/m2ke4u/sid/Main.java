package gg.m2ke4u.sid;

import org.apache.logging.log4j.LogManager;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        final AtomicInteger currentDownloading = new AtomicInteger();
        final AtomicInteger currentAPITasks = new AtomicInteger();
        for (; ; ) {
            if (currentDownloading.get() > 50) {
                LogManager.getLogger().info("Waiting for downloading tasks...");
                Thread.sleep(1000);
            }
            if(currentAPITasks.get() > 16){
                LogManager.getLogger().info("Waiting for api tasks...");
                Thread.sleep(1000);
            }
            CompletableFuture<SexImageResponseImpl> response;
            if (args.length > 0){
                response = SexImageResponseImpl.getNewAsync(true,args);
            }else{
                response = SexImageResponseImpl.getNewAsync(true);
            }
            currentAPITasks.getAndIncrement();
            response.thenAcceptAsync(responseImpl -> {
                currentAPITasks.getAndDecrement();
                SexImageResponseImpl.Data[] dataArray = responseImpl.getData();
                for (SexImageResponseImpl.Data data : dataArray) {
                    CompletableFuture<?> downLoadTask = data.urls.saveAsync();
                    currentDownloading.getAndIncrement();
                    downLoadTask.thenAcceptAsync(object -> currentDownloading.getAndDecrement());
                }
            });
            Thread.sleep(1000);
        }
    }
}