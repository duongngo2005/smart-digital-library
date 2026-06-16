import { create } from 'zustand'
import type { UserResponse } from '../types/user'
import { userApi } from '../api/user.api';

interface AuthState{
    user: UserResponse | null;
    accessToken: string | null;
    isInitialized: boolean;

    setAuth: (user: UserResponse, accessToken: string) => void;
    initialize: () => Promise<void>
    clearAuth: () => void;
    isAuthenticated: () => boolean;
    isAdmin: () => boolean;
    isLibrarian: () => boolean;
    isStaff: () => boolean;
}

export const useAuthStore = create<AuthState>((set, get) => ({
    user: null,
    accessToken: localStorage.getItem('accessToken'),
    isInitialized: false,

    setAuth: (user, token) => {
        localStorage.setItem('accessToken', token)
        set({user: user, accessToken: token})
    },
    initialize: async () => {
        const token = get().accessToken

        if(!token){
            set({isInitialized: true})
            return;
        }

        try{
            const res = await userApi.getMe()
            set({user: res.data.data, isInitialized: true})
        }catch{
            localStorage.removeItem('accessToken');
            set({user: null, accessToken: null, isInitialized: true})
        }
    },
    clearAuth: () => {
        localStorage.removeItem('accessToken')
        set({user: null, accessToken: null})
    },
    isAuthenticated: () => !!get().accessToken,
    isAdmin: () => get().user?.role === 'ADMIN',
    isLibrarian: () => get().user?.role === 'LIBRARIAN',
    isStaff: () => get().user?.role === 'ADMIN' || get().user?.role === 'LIBRARIAN'
}))