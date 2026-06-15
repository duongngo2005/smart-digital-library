import axios from "axios";

const api = axios.create({
    baseURL: "http://localhost:8080/api/v1",
    withCredentials: true,
    headers: {
        "Content-Type": "application/json"
    }
})

api.interceptors.request.use((config) => {
    const token = localStorage.getItem('accessToken');

    if(token){
        config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
})

api.interceptors.response.use(
    (response) => response,
    async (error) => {
        const originalRequest = error.config
        if(error.response?.status === 401 && !originalRequest._retry){
            originalRequest._retry = true
            try{
                const res = await api.post('/auth/refresh');
                const newToken: string = res.data.accessToken

                localStorage.setItem('accessToken', newToken);
                originalRequest.headers.Authorization = `Bearer ${newToken}`

                return api(originalRequest);
            }catch{
                localStorage.removeItem('accessToken');
                window.location.href = '/login'
            }
        }
        return Promise.reject(error)
    }
)

export default api;