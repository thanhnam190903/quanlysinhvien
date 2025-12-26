package com.example.QuanLySinhVien.dto;

public interface UserStatusStatistics {
    Long getActive();
    Long getActivePct();
    Long getInactive();
    Long getInactivePct();
    Long getNewUser();
    Long getNewUserPct();
}
