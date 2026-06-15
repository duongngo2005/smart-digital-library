export interface PageResponse<T>{
    content: T[];
    totalElements: number;
    totalPages: number;
    number: number;
    size: number;
    first: boolean;
    last: boolean;
}

export interface ApiError{
    timestamp: string;
    status: number;
    message: string;
    errors?: Record<string, string>
}