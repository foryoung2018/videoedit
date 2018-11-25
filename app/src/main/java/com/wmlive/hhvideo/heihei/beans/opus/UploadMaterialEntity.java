package com.wmlive.hhvideo.heihei.beans.opus;

import com.wmlive.hhvideo.common.base.BaseModel;
import com.wmlive.hhvideo.heihei.beans.login.UserInfo;

/**
 * Created by lsq on 9/12/2017.
 */

public class UploadMaterialEntity extends BaseModel {
    /**
     * "ori_id": 0,
     * "material_cover": "",
     * "material_length": 13000,
     * "music_id": 0,
     * "video_sign": "8aeb8fdcfb08a8804a67326385fd5fbc",
     * "material_video": "http://s1.wmlives.com/data/dongci/opus_material/201708242010017XKFavHIqkR",
     * "material_cover_small": "",
     * "material_dynamic_cover": "",
     * "id": 10003,
     * "owner_id": 10017
     * <p>
     * "video_sign": "9a0c3222e3d3e60fd0be3c369164a778"
     */

    public long id;
    public long ori_id;
    public String video_path;
    public String material_cover;
    public long material_length;
    public long music_id;
    public String video_sign;
    public String material_video;
    public String material_video_high;
    public String material_cover_small;
    public String material_dynamic_cover;
    public long owner_id;
    public int is_visible;
    public int is_delete;
    public int material_index;
    public UserInfo user;
    public int downloadId;

    public String fileDownloadPath;//下载完成后的地址
    public int downloadState;//对应视频文件的下载状态 0, 1 2
    public int index;//对应view的下标
    @Override
    public String toString() {
        return "UploadMaterialEntity{" +
                "id=" + id +
                ", ori_id=" + ori_id +
                ", material_cover='" + material_cover + '\'' +
                ", material_length=" + material_length +
                ", music_id=" + music_id +
                ", video_sign='" + video_sign + '\'' +
                ", material_video='" + material_video + '\'' +
                ", video_path='" + video_path + '\'' +
                ", material_video_high='" + material_video_high + '\'' +
                ", material_cover_small='" + material_cover_small + '\'' +
                ", material_dynamic_cover='" + material_dynamic_cover + '\'' +
                ", owner_id=" + owner_id +
                ", is_visible=" + is_visible +
                ", is_delete=" + is_delete +
                ", material_index=" + material_index +
                ", user=" + user +
                ", downloadId=" + downloadId +
                '}';
    }
}
