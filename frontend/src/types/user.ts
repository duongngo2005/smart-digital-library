export type Role = "ADMIN" | "USER" | "LIBRARIAN";
export type UserStatus = "PENDING" | "ACTIVE" | "SUSPENDED";
export type SubscriptionTier = "MEMBER" | "PRO" | "PLUS";

export interface UserResponse {
    id: number;
    fullName: string;
    role: Role;
    userStatus: UserStatus;
    subscriptionTier: SubscriptionTier;
    email: string;
    avatarUrl: string | null;
    downloadedThisMonth: number;
    subscriptionUntil: string;
}

export interface UpdateProfileRequest{
    fullName: string;
}

export interface ChangePasswordRequest{
    oldPassword: string;
    newPassword: string;
    confirmPassword: string;
}