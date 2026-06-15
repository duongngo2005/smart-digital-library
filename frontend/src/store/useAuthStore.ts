import { create } from 'zustand'
import type { UserResponse } from '../types/user'

interface AuthState{
    user: UserResponse | null;
    accessToken: string | null;

    setAuth: (user: UserResponse, accessToken: string) => void;
    clearAuth: () => void;
    isAuthenticated: () => boolean;
    isAdmin: () => boolean;
    isLibrarian: () => boolean;
    isStaff: () => boolean;
}

export const useAuthStore = create<AuthState>((set, get) => ({
    user: null,
    accessToken: localStorage.getItem('accessToken'),

    setAuth: (user, token) => {
        localStorage.setItem('accessToken', token)
        set({user: user, accessToken: token})
    },
    clearAuth: () => {
        localStorage.removeItem('accessToken')
    },
    isAuthenticated: () => !!get().accessToken,
    isAdmin: () => get().user?.role === 'ADMIN',
    isLibrarian: () => get().user?.role === 'LIBRARIAN',
    isStaff: () => get().user?.role === 'ADMIN' || get().user?.role === 'LIBRARIAN'
}))