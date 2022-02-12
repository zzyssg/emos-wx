package com.example.emoswx.db.pojo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @ClassName MessageRefEntity
 * @Date 2022/2/10 15:25
 * @Author Admin
 * @Description
 */
@Data
@Document
public class MessageRefEntity {

    @Id
    private String _id;

    @Indexed(unique = true)
    private String messageId;

    @Indexed
    private Integer receiverId;

    @Indexed
    private Boolean readFlag;

    @Indexed
    private Boolean lastFlag;

}
