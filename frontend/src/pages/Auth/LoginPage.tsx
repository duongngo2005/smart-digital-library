import {z} from "zod";
import { useLogin } from "../../hooks/useAuth";
import { Link, useLocation } from "react-router-dom";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import styles from './LoginPage.module.css'
import { getServerError } from "../../utils/getServerError";

const loginSchema = z.object({
    email: z
    .string()
    .min(1, "Email không được để trống")
    .email("Email không đúng định dạng"),
    password: z
    .string()
    .min(1, "Password không được để trống"),
})

type LoginFormData = z.infer<typeof loginSchema>

export default function LoginPage(){
    const {mutate: login, isPending, error} = useLogin()

    const location = useLocation()
    const successMessage = location.state?.message

    const {
        register,
        handleSubmit,
        formState: {errors}
    } = useForm<LoginFormData>({
        resolver: zodResolver(loginSchema)
    })

    const onSubmit = (data: LoginFormData) => {
        login(data)
    }

    const serverError = error ? getServerError(error) : null;

    return (
        <div className={styles.container}>
            <div className={styles.card}>
                <div className={styles.header}>
                    <h1 className={styles.title}>Đăng nhập</h1>
                    <p className={styles.subtitle}>Chào mừng trở lại</p>
                </div>

                {successMessage && (
                    <div className={styles.successBanner}>{successMessage}</div>
                )}

                {serverError && (
                    <div className={styles.errorBanner}>{serverError}</div>
                )}

                <form onSubmit={handleSubmit(onSubmit)} noValidate className={styles.form}>
                    <div className={styles.field}>
                        <label htmlFor="email" className={styles.label}>Email</label>
                        <input
                            id="email"
                            type="email"
                            className={`${styles.input} ${errors.email ? styles.inputError : ''}`}
                            placeholder="example@email.com"
                            {...register('email')}
                        />
                        {errors.email && (
                            <span className={styles.errorText}>{errors.email.message}</span>
                        )}
                    </div>

                    <div className={styles.field}>
                        <label htmlFor="password" className={styles.label}>Mật khẩu</label>
                        <input
                            id="password"
                            type="password"
                            className={`${styles.input} ${errors.password ? styles.inputError : ''}`}
                            placeholder="••••••••"
                            {...register('password')}
                        />
                        {errors.password && (
                            <span className={styles.errorText}>{errors.password.message}</span>
                        )}
                    </div>

                    <button
                        type="submit"
                        className={styles.submitButton}
                        disabled={isPending}
                    >
                        {isPending ? 'Đang đăng nhập...' : 'Đăng nhập'}
                    </button>
                </form>
                <p className={styles.switchText}>
                    Chưa có tài khoản?{' '}
                    <Link to="/register" className={styles.switchLink}>Đăng ký ngay</Link>
                </p>
            </div>

            
        </div>
    )
}