import type { ApiResponse } from "../types/common";
import type { ChangePasswordRequest, UpdateProfileRequest, UserResponse } from "../types/user"
import api from "./axios"

export const userApi = {
    getMe: () => api.get<ApiResponse<UserResponse>>('/users/me'),


    updateProfile: (request: UpdateProfileRequest) => 
        api.put<ApiResponse<UserResponse>>('/users/me/profile', request),

    updateAvatar: (file: File) => {
        const formData = new FormData();
        formData.append('file', file);
        return api.put<ApiResponse<UserResponse>>('/users/me/avatar', formData, {
            headers: {
                "Content-Type": "multipart/form-data"
            }
        })
    },

    changePassword: (request: ChangePasswordRequest) => 
        api.put<ApiResponse<void>>('/users/me/password', request)
}