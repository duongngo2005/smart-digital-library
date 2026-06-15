import type { AuthResponse, LoginRequest, RegisterRequest } from "../types/auth";
import api from "./axios";

export const authApi = {
    login: (data: LoginRequest) => api.post<AuthResponse>('/auth/login', data),

    register: (data: RegisterRequest) => api.post<AuthResponse>('/auth/register', data),

    refresh: () => api.post<AuthResponse>('/auth/refresh'),

    logout: () => api.post('/auth/logout')
}