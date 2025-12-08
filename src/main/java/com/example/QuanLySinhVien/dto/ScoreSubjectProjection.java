package com.example.QuanLySinhVien.dto;

public interface ScoreSubjectProjection {
    String getStudentId();
    String getStudentName();
    Integer getSubjectId();
    String getSubjectName();
    String getSubjectStartDate();
    String getSubjectEndDate();

    Double getScoreProcess();
    Double getScoreFinal();
    Double getTotalScore();

    Integer getScoreSubjectId();
}
