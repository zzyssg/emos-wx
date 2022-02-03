package com.example.emoswx.service;

import java.util.HashMap;

/**
 * @ClassName CheckinService
 * @Date 2022/1/23 17:34
 * @Author Admin
 * @Description
 */
public interface CheckinService {
    public String validCanCheckin(int userId,String date);

    public void checkin(HashMap checkinMap);

    public void createFaceModel(int userId, String faceModel);
}
