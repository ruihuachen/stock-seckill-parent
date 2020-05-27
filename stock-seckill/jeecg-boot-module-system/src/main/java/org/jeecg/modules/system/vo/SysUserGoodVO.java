package org.jeecg.modules.system.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SysUserGoodVO implements Serializable {
    private static final long serialVersionUID = 1L;

    // 票商品id
    private String goodId;

    // 对应的用户id集合
    private List<String> userIdList;

    public SysUserGoodVO() {
        super();
    }

    public SysUserGoodVO(String goodId, List<String> userIdList) {
        super();
        this.goodId = goodId;
        this.userIdList = userIdList;
    }
}
