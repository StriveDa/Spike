package com.miaoshaproject.service.impl;

import com.miaoshaproject.dao.UserDOMapper;
import com.miaoshaproject.dao.UserpasswordDOMapper;
import com.miaoshaproject.dataobject.UserDO;
import com.miaoshaproject.dataobject.UserPasswordDO;
import com.miaoshaproject.error.BusinessExeption;
import com.miaoshaproject.error.EmBusinessError;
import com.miaoshaproject.service.UserService;
import com.miaoshaproject.service.model.UserModel;
import com.miaoshaproject.validator.ValidationResult;
import com.miaoshaproject.validator.ValidatorImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDOMapper userDOMapper;

    @Autowired
    private UserpasswordDOMapper userPasswordDOMapper;

    @Autowired
    private ValidatorImpl validato;


    @Override
    public UserModel getUserById(Integer id) {
        //调用userdomapper获取到对应的用户dataobject
        UserDO userDO = userDOMapper.selectByPrimaryKey(id);

        if(userDO == null){
            return null;
        }
        //通过用户id获取对应的用户加密密码信息
        UserPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(userDO.getId());

        return convertFromDataObject(userDO,userPasswordDO);
    }

    @Override
    @Transactional
    public void register(UserModel userModel) throws BusinessExeption {
        if (userModel == null) {
            throw new BusinessExeption(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
//        if(StringUtils.isEmpty(userModel.getName())
//        ||userModel.getGender() == null
//        ||userModel.getAge() == null
//        ||StringUtils.isNotEmpty(userModel.getTalphone())){
//            throw new BusinessExeption(EmBusinessError.PARAMETER_VALIDATION_ERROR);
//        }
        ValidationResult result = validato.validate(userModel);
        if (result.isHasErrors()) {
            throw new BusinessExeption(EmBusinessError.PARAMETER_VALIDATION_ERROR, result.getErrMsg());
        }

        //UserDO userDO = new UserDO();

        //实现model-->dataobject方法
        UserDO userDo = convertFromModel(userModel);
        try{
            //userDo使用的是UserDO userDo = convertFromModel(userModel)的userDo，不是new UserDO的对象userDo
            userDOMapper.insertSelective(userDo);
        }catch (DuplicateKeyException ex){
            throw new BusinessExeption(EmBusinessError.PARAMETER_VALIDATION_ERROR,"手机号已经被注册");
        }
        //userDo使用的是UserDO userDo = convertFromModel(userModel)的userDo，不是new UserDO的对象userDo
        userModel.setId(userDo.getId());
        UserPasswordDO userPasswordDO = converPasswordFromModel(userModel);
        userPasswordDOMapper.insertSelective(userPasswordDO);
        return;
    }



    private UserPasswordDO converPasswordFromModel(UserModel userModel){
        if(userModel == null){
            return null;
        }
        UserPasswordDO userPasswordDO = new UserPasswordDO();
        userPasswordDO.setEncrptPassword(userModel.getEncrptPassword());
        userPasswordDO.setUserId(userModel.getId());
        return userPasswordDO;
    }
    private UserDO convertFromModel(UserModel userModel){
        if(userModel == null){
            return null;
        }
        UserDO userDO = new UserDO();
        BeanUtils.copyProperties(userModel,userDO);
        return userDO;
    }

    private UserModel convertFromDataObject(UserDO userDO, UserPasswordDO userPasswordDO){
        if(userDO == null){
            return null;
        }
        UserModel userModel = new UserModel();
        BeanUtils.copyProperties(userDO,userModel);

        if(userPasswordDO != null){
            userModel.setEncrptPassword(userPasswordDO.getEncrptPassword());
        }
        return userModel;
    }




    @Override
    public UserModel validateLogin(String talphone, String encrptPassword) throws BusinessExeption {
        //通过用户的手机获取用户信息
        UserDO userDO = userDOMapper.selectByTalphone(talphone);
        if(userDO == null){
            throw new BusinessExeption(EmBusinessError.USER_LOGIN_FAIL);
        }
        UserPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(userDO.getId());
        UserModel userModel = convertFromDataObject(userDO,userPasswordDO);

        //比对用户信息内加密的密码是否和传输进来的密码相匹配
        if(!StringUtils.equals(encrptPassword,userModel.getEncrptPassword())){
            throw new BusinessExeption(EmBusinessError.USER_LOGIN_FAIL);
        }
        return userModel;
    }

}
