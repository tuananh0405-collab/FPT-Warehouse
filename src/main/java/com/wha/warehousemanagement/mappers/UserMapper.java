package com.wha.warehousemanagement.mappers;

import com.wha.warehousemanagement.dtos.UserDTO;
import com.wha.warehousemanagement.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper extends EntityMapper<UserDTO, User>{
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
}
