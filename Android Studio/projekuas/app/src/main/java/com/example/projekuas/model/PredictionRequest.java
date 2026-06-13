package com.example.projekuas.model;

public class PredictionRequest {
    private double cgpa;
    private int college_tier;
    private double coding_score;
    private String company_type;
    private String job_role;

    public PredictionRequest(double cgpa, int college_tier, double coding_score, String company_type, String job_role) {
        this.cgpa = cgpa;
        this.college_tier = college_tier;
        this.coding_score = coding_score;
        this.company_type = company_type;
        this.job_role = job_role;
    }
}