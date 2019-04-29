package com.miaoshaproject.error;

//包装其业务异常类实现
public class BusinessExeption extends Exception implements CommonError{

    private CommonError commonError;

    //直接接受EmBusinessError的传参用于构造业务异常
    public BusinessExeption(CommonError commonError){
        super();
        this.commonError = commonError;
    }

    //接受自定义errMsg的方式构造业务异常
    public BusinessExeption(CommonError commonError,String errMsg){
        super();
        this.commonError = commonError;
        this.commonError.setErrMsg(errMsg);
    }

    @Override
    public int getErrCode() {
        return this.commonError.getErrCode();
    }

    @Override
    public String getErrMsg() {
        return this.commonError.getErrMsg();
    }

    @Override
    public CommonError setErrMsg(String errMsg) {
        this.commonError.setErrMsg(errMsg);
        return this;
    }
}
