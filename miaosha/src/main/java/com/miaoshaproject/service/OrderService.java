package com.miaoshaproject.service;

import com.miaoshaproject.error.BusinessExeption;
import com.miaoshaproject.service.model.OrderModel;

public interface OrderService {
    //1.通过前端url上传过来秒杀活动id，然后下单接口内校验是否属于对应商品且活动已开始

    //2.直接在下单接口内判断对应的商品是否存在秒杀活动，若存在进行中的则以秒杀价格下单

    OrderModel cerateOrder(Integer userId,Integer itemId,Integer promoId,Integer amount) throws BusinessExeption;


}
