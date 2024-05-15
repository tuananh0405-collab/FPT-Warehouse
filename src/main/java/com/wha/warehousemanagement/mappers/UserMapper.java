package com.wha.warehousemanagement.mappers;

import com.wha.warehousemanagement.dtos.UserDTO;
import com.wha.warehousemanagement.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper extends EntityMapper<UserDTO, User>{
}
