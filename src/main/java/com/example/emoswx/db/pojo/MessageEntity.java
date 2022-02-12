package com.example.emoswx.db.pojo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * @ClassName MessageENtity
 * @Date 2022/2/10 15:19
 * @Author Admin
 * @Description
 */
@Data
@Document
public class MessageEntity {


    @Id
    private String _id;

    @Indexed(unique = true)
    private String uuid;

    @Indexed
    private Integer senderId;

    private String senderPhoto = "https://gimg2.baidu.com/image_search/src=h" +
            "ttp%3A%2F%2Fup.enterdesk.com%2Fedpic%2Fe8%2F34%2F02%2Fe834028fa4b313a1607872736437ef2d.jpg";

    private String senderName;

    private String msg;

    @Indexed
    private Date sendTime;

}
