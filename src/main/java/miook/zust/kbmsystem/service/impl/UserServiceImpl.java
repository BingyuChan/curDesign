package miook.zust.kbmsystem.service.impl;

import miook.zust.kbmsystem.dao.UserDao;
import miook.zust.kbmsystem.dto.UserDto;
import miook.zust.kbmsystem.entity.Tuser;
import miook.zust.kbmsystem.service.UserServiceI;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserServiceI {
    @Autowired
    private UserDao userdao;
    private Tuser d2e(UserDto dto){
        Tuser tuser=new Tuser();
        BeanUtils.copyProperties(dto,tuser);
        return tuser;
    }
    private UserDto e2d(Tuser tuser){
        UserDto dto=new UserDto();
        BeanUtils.copyProperties(tuser,dto);
        return dto;
    }
    @Override
    public UserDto getUser(String loginName, String password){
        Tuser tuser=userdao.findByLoginNameAndPassword(loginName, password);
        return e2d(tuser);
    }

    @Override
    public boolean login(UserDto dto) {
        Tuser tuser = userdao.findByLoginNameAndPassword(dto.getLoginName(),dto.getPassword());
        if(tuser != null)
            return true;
        else
            return false;
    }

    @Override
    public UserDto addUser(UserDto userdto){
        Tuser user = userdao.findByLoginName(userdto.getLoginName());
        if(user==null){
            Tuser tuser =userdao.save(d2e(userdto));
            return e2d(tuser);
        }
        else
            return null;
    }
}
