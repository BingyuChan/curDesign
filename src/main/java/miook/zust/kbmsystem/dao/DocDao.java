package miook.zust.kbmsystem.dao;

import miook.zust.kbmsystem.entity.TDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocDao extends JpaRepository<TDocument,Integer> {
    TDocument findByName(String name);
    TDocument findById(int id);
}
