package com.wmlive.hhvideo.heihei.beans.personal;

import com.wmlive.hhvideo.common.base.BaseModel;
import com.wmlive.networklib.entity.BaseResponse;

import java.util.List;

/**
 * Created by lsq on 1/12/2018.6:46 PM
 *
 * @author lsq
 * @describe 添加描述
 */

public class DecibelListResponse extends BaseResponse {
    public List<DecibelEntity> data;
    public StatisticEntity statistic;

    public static class StatisticEntity extends BaseModel {
        public int total_gift_point;
        public int total_point;
        public int total_prize_point;
        public int total_count;
    }
}
