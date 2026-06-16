import {z} from "zod";
import { useRegister } from "../../hooks/useAuth";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { getServerError } from "../../utils/getServerError";
import styles from "./RegisterPage.module.css"
import { Link } from "react-router-dom";

const registerSchema = z.object({
    email: z
        .string()
        .min(1, "Email không được để trống")
        .email("Email sai định dạng"),
    password: z
        .string()
        .min(8, "Password phải tối thiểu 8 ký tự"),
    confirmPassword: z.string().min(1, "Vui lòng nhập xác nhận mật khẩu"),
    fullName: z.string().min(1, "Tên đầy đủ không được để trống"),
    role: z.enum(['USER', 'LIBRARIAN']),
}).refine((data) => data.password === data.confirmPassword, {
    message: "Mật khẩu xác nhận không khớp",
    path: ['confirmPassword'],
})

type RegisterFormData = z.infer<typeof registerSchema>

export default function RegisterPage(){
    const {mutate: register, isPending, error} = useRegister();

    const {
        register: formRegister,
        handleSubmit,
        watch,
        setValue,
        formState: {errors}
    } = useForm<RegisterFormData>({
        resolver: zodResolver(registerSchema),
        defaultValues: {role: 'USER'}
    })

    const selectedRole = watch('role')

    const onSubmit = (data: RegisterFormData) => {
        const {confirmPassword, ...payload} = data
        register(payload)
    }

    const serverError = error ? getServerError(error) : null;

    return (
        <div className={styles.container}>
            <div className={styles.card}>
                <div className={styles.header}>
                    <h1 className={styles.title}>Đăng ký tài khoản</h1>
                    <p className={styles.subtitle}>Tham gia thư viện số ngay</p>
                </div>

                {serverError && (
                        <div className={styles.errorBanner}>{serverError}</div>
                    )}

                <form onSubmit={handleSubmit(onSubmit)} className={styles.form} noValidate>
                    <div className={styles.roleSelector}>
                        <button
                            type="button"
                            className={`${styles.roleBtn} ${selectedRole === 'USER' ? styles.roleBtnActive : ''}`}
                            onClick={() => setValue('role', 'USER')}
                        >
                            Độc giả
                        </button>

                        <button
                            type="button"
                            className={`${styles.roleBtn} ${selectedRole === 'LIBRARIAN' ? styles.roleBtnActive : ''}`}
                            onClick={() => setValue('role', 'LIBRARIAN')}
                        >
                            Thủ thư
                        </button>
                    </div>

                    {selectedRole === 'LIBRARIAN' && (
                        <div className={styles.infoBanner}>
                            Tài khoản thủ thư cần được phê duyệt trước khi sử dụng
                        </div>
                    )}

                    <div className={styles.field}>
                        <label htmlFor="fullName" className={styles.label}>Họ và tên</label>
                        <input type="text" 
                            id="fullName"
                            className={`${styles.input} ${errors.fullName ? styles.inputError : ''}`}
                            placeholder="Nguyen Van A"
                            {...formRegister('fullName')}
                        />
                        {errors.fullName && (
                            <span className={styles.errorText}>{errors.fullName.message}</span>
                        )}
                    </div>

                    <div className={styles.field}>
                        <label htmlFor="email" className={styles.label}>Email</label>
                        <input type="email" 
                            id="email"
                            className={`${styles.input} ${errors.email ? styles.inputError : ''}`}
                            {...formRegister('email')}
                            placeholder="example@gmail.com"
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
                            placeholder="Ít nhất 8 ký tự"
                            {...formRegister('password')}
                        />
                        {errors.password && (
                            <span className={styles.errorText}>{errors.password.message}</span>
                        )}
                    </div>

                    <div className={styles.field}>
                        <label htmlFor="confirmPassword" className={styles.label}>Xác nhận mật khẩu</label>
                        <input
                            id="confirmPassword"
                            type="password"
                            className={`${styles.input} ${errors.confirmPassword ? styles.inputError : ''}`}
                            placeholder="Nhập lại mật khẩu"
                            {...formRegister('confirmPassword')}
                        />
                        {errors.confirmPassword && (
                            <span className={styles.errorText}>{errors.confirmPassword.message}</span>
                        )}
                    </div>

                    <button 
                        type="submit"
                        className={styles.submitButton}
                        disabled={isPending}
                    >
                        {isPending ? "Đang đăng ký..." : "Đăng ký"}
                    </button>

                    <p className={styles.switchText}>
                        Đã có tài khoản? {' '}
                        <Link to="/login" className={styles.switchLink}>Đăng nhập</Link>
                    </p>
                </form>
            </div>
        </div>
    )
}