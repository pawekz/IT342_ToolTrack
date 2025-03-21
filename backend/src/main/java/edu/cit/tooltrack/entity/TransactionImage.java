package edu.cit.tooltrack.entity;

import jakarta.persistence.*;

import javax.tools.Tool;
import java.math.BigInteger;
import java.sql.Timestamp;

@Entity
@Table(name = "transaction_images")
public class TransactionImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int image_id;
//
//    //Foreign Key
//    @JoinColumn(name="transaction_images_ibfk_1")
//    private ToolTransaction transaction_id;
//
//    @Column(nullable = false)
//    private ImageType imageType;
//    @Column(nullable = false)
//    private String image_url;
//    @Column(nullable = false)
//    private String s3_bucket_name;
//    @Column(nullable = false)
//    private String s3_key;
//    @Column(nullable = false)
//
//    @JoinColumn(name = "transaction_images_ibfk_2")
//    private User uploaded_by;
//    private String notes;
//
//    private Timestamp created_at = null;
//    private Timestamp updated_at;  // Comment: null on update CURRENT_TIMESTAMP, what do you mean?
//
//    private enum ImageType {
//        pre_borrow, post_return
//    }
}
