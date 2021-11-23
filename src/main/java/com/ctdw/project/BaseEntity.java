package com.ctdw.project;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>实体超类</p>
 *
 * @author : yzh
 * @date : 2021-10-21 16:24
 **/
public class BaseEntity implements Serializable {

    /**
     * 创建时间
     */
    @DateTimeFormat(pattern = "YYYY-MM-DD hh:mm:ss")
    @JSONField(format = "YYYY/MM/DD HH:mm:ss")
    private Date createTime;

    /**
     * 更新时间
     */
    @DateTimeFormat(pattern = "YYYY-MM-DD hh:mm:ss")
    @JSONField(format = "YYYY/MM/DD HH:mm:ss")
    private Date updateTime;

    /**
     * 更新人
     */
    private Integer updateBy;

    /**
     * 创建人
     */
    private Integer createBy;

    /**
     * 是否删除 0否 1是
     */
    private Boolean delFlag;

    /**
     * 备注
     */
    private String remark;

    /**
     * 页数
     */
    private Integer page;

    /**
     * 限制
     */
    private Integer limit;

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(Integer updateBy) {
        this.updateBy = updateBy;
    }

    public Integer getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Integer createBy) {
        this.createBy = createBy;
    }

    public Boolean getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Boolean delFlag) {
        this.delFlag = delFlag;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }
}
