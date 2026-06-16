import { useMutation } from "@tanstack/react-query";
import { useAuthStore } from "../store/useAuthStore";
import type { LoginRequest, RegisterRequest } from "../types/auth";
import { authApi } from "../api/auth.api";
import { useNavigate } from "react-router-dom";

export function useLogin(){
    const setAuth = useAuthStore((s) => s.setAuth);
    const navigate = useNavigate()
    

    return useMutation({
        mutationFn: (data: LoginRequest) => authApi.login(data),

        onSuccess: (res) => {
            const {accessToken, userResponse} = res.data
            if(!accessToken) return;
            setAuth(userResponse, accessToken)
            navigate('/')
        }
    })
}

export function useRegister(){
    const setAuth = useAuthStore((s) => s.setAuth);
    const navigate = useNavigate();

    return useMutation({
        mutationFn: (data: RegisterRequest) => authApi.register(data),
        onSuccess: (res) => {
            const {accessToken, userResponse} = res.data
            if (accessToken){
                setAuth(userResponse, accessToken)
                navigate('/')
            }else{
                navigate('/login', {
                    state: {
                        message: 'Tài khoản thủ thư đang chờ admin phê duyệt'
                    }
                })
            }
                
        }
    })
}

export function useLogout(){
    const navigate = useNavigate();
    const clearAuth = useAuthStore((s) => s.clearAuth)

    return useMutation({
        mutationFn: () => authApi.logout(),
        onSettled: () => {
            clearAuth()            
            navigate('/login')
        }
    })
}