package com.wt2024.points.repository.account.vo;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @ClassName PointsAccountDetail
 * @Description: TODO
 * @Author shade.yang
 * @Date 2021/5/26
 * @Version V1.0
 **/
public class PointsAccountDetails {

    private BigDecimal pointsAmount;

    private Date expirationTime;

    public PointsAccountDetails(BigDecimal pointsAmount, Date expirationTime) {
        this.pointsAmount = pointsAmount;
        this.expirationTime = expirationTime;
    }

    public PointsAccountDetails() {
        super();
    }

    public BigDecimal getPointsAmount() {
        return pointsAmount;
    }

    public void setPointsAmount(BigDecimal pointsAmount) {
        this.pointsAmount = pointsAmount;
    }

    public Date getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Date expirationTime) {
        this.expirationTime = expirationTime;
    }
}
