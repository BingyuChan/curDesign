package miook.zust.kbmsystem.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Table(name = "tuser_doc_collection")
@Entity
public class TCollection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name="user_id")
    private int userId;
    @Column(name = "doc_id")
    private int docId;
}
