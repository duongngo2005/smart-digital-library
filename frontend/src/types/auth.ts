import type { UserResponse } from "./user";

export interface AuthResponse{
    accessToken: string;
    tokenType: string;
    userResponse: UserResponse;
}

export interface LoginRequest{
    email: string;
    password: string;
}

export interface RegisterRequest{
    fullName: string;
    role?: "USER" | "LIBRARIAN";
    email: string;
    password: string;
}