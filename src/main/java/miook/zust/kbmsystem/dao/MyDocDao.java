package miook.zust.kbmsystem.dao;

import miook.zust.kbmsystem.entity.TMyDoc;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MyDocDao extends JpaRepository<TMyDoc,Integer>{
    List<TMyDoc> findAllByUserId(int userId);
}