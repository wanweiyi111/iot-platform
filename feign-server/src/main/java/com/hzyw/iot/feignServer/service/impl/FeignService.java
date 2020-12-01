package com.hzyw.iot.feignServer.service.impl;

import com.hzyw.iot.feignServer.service.IFeignService;
import com.hzyw.iot.feignServer.service.IOnSuccess;
import com.hzyw.iot.feignServer.util.JsonFormatUtil;
import com.hzyw.iot.feignServer.util.TransmitDataVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.concurrent.*;

@Service
public class FeignService implements IFeignService {

    public HashMap<String,FutureTask<String>> taskCache=new HashMap<>();

    @Autowired
    private IOnSuccess onSuccess;

    @Override
    public TransmitDataVO getModelInfo(String jsonStrng) {
        return JsonFormatUtil.format(jsonStrng);
    }

    @Override
    public FutureTask doAction() {
        System.out.println("doAction...");
        String res = "NULL";
        FutureTask<String> task = new FutureTask<String>(new Callable() {
            long s=0L;
            @Override
            public String call() throws Exception {
                System.out.println("doing....");
                for(int i=0;i<1000000000;i++){
                    s = i+334334;
                }
                System.out.println("sleep notified....");
                onSuccess.successArrived("3000ms");
                return s+"";
            }
        });
        new Thread(task).start();
        System.out.println("action task doing....");
        return task;
    }

    public  String peekResult(FutureTask task) throws ExecutionException, InterruptedException {
        String s = null;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    if(task.isDone()){
                        break;
                    }
                }

            }
        }).start();

        return  null;
    }
}
