package com.hzyw.iot.netty.processor.Impl;

import com.hzyw.iot.netty.processor.Impl.IDataProcessor;
import com.hzyw.iot.vo.dc.enums.ERTUChannelFlag;

import io.netty.buffer.ByteBuf;

/**
 * 数据解码器 抽象类
 *
 * @author TheEmbers Guo
 * @version 1.0
 * createTime 2018-10-19 15:52
 */
public abstract class ProcessorAbstract implements IDataProcessor {
    private IDataProcessor nextProcessor;
    private ERTUChannelFlag flag;

    @Override
    public IDataProcessor getNextProcessor() { 
        return nextProcessor;
    }

    @Override
    public void setNextProcessor(IDataProcessor nextProcessor) {
        this.nextProcessor = nextProcessor;
    }
    
     

    public ProcessorAbstract(ERTUChannelFlag flag) {
        this.flag = flag;
    }

    /**
     * 判断进入哪个处理proccess
     *
     * @param source
     * @return
     */
    public boolean checkAndToProcess(int type) {  
        if (flag.getType()!= type) {
            return false;
        }
        return true;
    }
     

    public String buildDataKey(String littleKey) {
        return this.flag + "-" + littleKey;
    }
}
