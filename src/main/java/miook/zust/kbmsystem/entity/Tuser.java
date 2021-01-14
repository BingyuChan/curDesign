package miook.zust.kbmsystem.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table
public class Tuser implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "login_name")
    private String loginName;
    @Column
    private String password;

//    @ManyToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
//    @JoinTable(name = "tuser_doc_likes",
//        joinColumns =
//            @JoinColumn(name = "user_id",referencedColumnName = "id"),
//        inverseJoinColumns =
//            @JoinColumn(name = "doc_id",referencedColumnName = "id"))
//    private Set<TDocument> tDocuments;
}
