package miook.zust.kbmsystem.service;

import miook.zust.kbmsystem.dto.UserDto;

public interface UserServiceI {
    UserDto getUser(String loginName, String password);
    boolean login(UserDto dto);
    UserDto addUser(UserDto userDto);
}
