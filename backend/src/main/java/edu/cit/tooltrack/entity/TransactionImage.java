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

    @ManyToOne
    @JoinColumn(name = "fk_transaction_images_transaction")
    private ToolTransaction transaction_id;



    private ImageType imageType;
    private String image_url;
    private String s3_bucket_name;
    private String s3_key;
    private String notes;

    private Timestamp created_at = null;
    private Timestamp updated_at;  // Comment: null on update CURRENT_TIMESTAMP, what do you mean?

    private enum ImageType {
        pre_borrow, post_return
    }
}
