package miook.zust.kbmsystem.dao;

import miook.zust.kbmsystem.entity.Tuser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDao extends JpaRepository<Tuser,Integer> {
    Tuser findByLoginName(String loginName);
    Tuser findByLoginNameAndPassword(String loginName, String password);
    public Tuser save(Tuser tuser);
}
