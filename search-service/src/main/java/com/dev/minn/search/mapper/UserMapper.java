package com.dev.minn.search.mapper;

import com.dev.minn.search.document.User;
import com.dev.minn.search.event.UserCreatedEvent;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(UserCreatedEvent event);
}
