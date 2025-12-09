package com.example.QuanLySinhVien.entity;

public interface UserStatusStatistics {
    Long getActive();
    Long getActivePct();
    Long getInactive();
    Long getInactivePct();
    Long getNewUser();
    Long getNewUserPct();
}
