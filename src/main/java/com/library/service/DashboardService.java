package com.library.service;

import com.library.dto.AdminDashboardStats;
import com.library.dto.UserDashboardStats;

public interface DashboardService {
    AdminDashboardStats getAdminStats();

    UserDashboardStats getUserStats(Long memberId);
}
