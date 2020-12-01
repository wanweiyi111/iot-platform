package com.hzyw.iot.feignServer.service;

import java.util.concurrent.FutureTask;

public interface IFeignService {

    public com.hzyw.iot.feignServer.util.TransmitDataVO getModelInfo(String jsonStrng);

    FutureTask doAction();
}
