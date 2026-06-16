import { AxiosError } from "axios";

export function getServerError(error: unknown, fallback = "Đã có lỗi xảy ra"): string{
    if(error instanceof AxiosError){
        return error.response?.data?.message ?? fallback;
    }
    return fallback;
}