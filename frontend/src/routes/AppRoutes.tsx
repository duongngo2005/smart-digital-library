import { BrowserRouter, Route, Routes } from "react-router-dom"
import LoginPage from "../pages/Auth/LoginPage"
import RegisterPage from "../pages/Auth/RegisterPage"
import MainLayout from "../components/layout/MainLayout"
import HomePage from "../pages/Home/HomePage"
import DocumentListPage from "../pages/Documents/DocumentListPage"
import DocumentDetailPage from "../pages/Documents/DocumentDetailPage"
import PricingPage from "../pages/Subscription/PricingPage"
import ProtectedRoutes from "./ProtectedRoutes"
import ReadingHistoryPage from "../pages/Profile/ReadingHistoryPage"
import FavoritesPage from "../pages/Profile/FavouritePage"
import ProfilePage from "../pages/Profile/ProfilePage"
import RoleRoutes from "./RoleRoutes"
import LibrarianDashboard from "../pages/Librarian/LibrarianDashboard"
import MyDocumentsPage from "../pages/Librarian/MyDocumentsPage"
import DocumentFormPage from "../pages/Librarian/DocumentFormPage"
import AdminDashboard from "../pages/Admin/AdminDashboard"
import UserManagementPage from "../pages/Admin/UserManagementPage"
import CategoryManagementPage from "../pages/Admin/CategoryManagementPage"

export default function AppRoutes(){
    return(
        <BrowserRouter>
            <Routes>
                <Route path="/login" element={<LoginPage/>}/>
                <Route path="/register" element={<RegisterPage/>}/>

                <Route element={<MainLayout/>}>
                    <Route path="/" element={<HomePage/>}/>
                    <Route path="/documents" element={<DocumentListPage/>}/>
                    <Route path="/documents/:id" element={<DocumentDetailPage/>}/>
                    <Route path="/pricing" element={<PricingPage/>}/>
                </Route>

                <Route element={<ProtectedRoutes/>}>
                    <Route path="/profile" element={<ProfilePage />} />
                    <Route path="/profile/history" element={<ReadingHistoryPage />} />
                    <Route path="/profile/favorites" element={<FavoritesPage />} />

                    <Route element={<RoleRoutes allowedRoles={['LIBRARIAN', 'ADMIN']} />}>
                        <Route path="/librarian" element={<LibrarianDashboard />} />
                        <Route path="/librarian/documents" element={<MyDocumentsPage />} />
                        <Route path="/librarian/documents/new" element={<DocumentFormPage />} />
                        <Route path="/librarian/documents/:id/edit" element={<DocumentFormPage />} />

                    </Route>

                    <Route element={<RoleRoutes allowedRoles={['ADMIN']} />}>
                        <Route path="/admin" element={<AdminDashboard />} />
                        <Route path="/admin/users" element={<UserManagementPage />} />
                        <Route path="/admin/categories" element={<CategoryManagementPage />} />
                    </Route>
                </Route>

            </Routes>
        </BrowserRouter>
    )
}