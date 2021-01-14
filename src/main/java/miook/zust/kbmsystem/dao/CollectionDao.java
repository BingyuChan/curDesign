package miook.zust.kbmsystem.dao;

import miook.zust.kbmsystem.entity.TCollection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CollectionDao extends JpaRepository<TCollection,Integer> {
    List<TCollection> findAllByUserId(int userId);
}
