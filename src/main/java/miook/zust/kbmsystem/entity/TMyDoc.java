package miook.zust.kbmsystem.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tuser_doc_mydoc")
public class TMyDoc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name="user_id")
    private int userId;
    @Column(name = "doc_id")
    private int docId;
}
