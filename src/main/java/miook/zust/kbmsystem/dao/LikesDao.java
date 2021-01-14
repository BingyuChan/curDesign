package miook.zust.kbmsystem.dao;

import miook.zust.kbmsystem.entity.Tlikes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikesDao extends JpaRepository<Tlikes,Integer> {
    List<Tlikes> findAllByUserId(int userId);
}
