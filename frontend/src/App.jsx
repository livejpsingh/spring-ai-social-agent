import { Routes, Route, Navigate } from 'react-router-dom';
import { useAuth } from './context/AuthContext';
import Layout from './components/Layout';
import LoginPage from './pages/LoginPage';
import DashboardPage from './pages/DashboardPage';
import ContentGeneratorPage from './pages/ContentGeneratorPage';
import SchedulerPage from './pages/SchedulerPage';
import EngagementPage from './pages/EngagementPage';
import AnalyticsPage from './pages/AnalyticsPage';
import ApprovalPage from './pages/ApprovalPage';
import SettingsPage from './pages/SettingsPage';

function ProtectedRoute({ children }) {
  const { isAuthenticated, loading } = useAuth();
  if (loading) return (
    <div className="min-h-screen bg-surface-950 flex items-center justify-center">
      <div className="w-10 h-10 border-4 border-primary-500 border-t-transparent rounded-full animate-spin" />
    </div>
  );
  return isAuthenticated ? children : <Navigate to="/login" />;
}

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/" element={<ProtectedRoute><Layout /></ProtectedRoute>}>
        <Route index element={<DashboardPage />} />
        <Route path="content" element={<ContentGeneratorPage />} />
        <Route path="scheduler" element={<SchedulerPage />} />
        <Route path="engagement" element={<EngagementPage />} />
        <Route path="analytics" element={<AnalyticsPage />} />
        <Route path="approvals" element={<ApprovalPage />} />
        <Route path="settings" element={<SettingsPage />} />
      </Route>
      <Route path="*" element={<Navigate to="/" />} />
    </Routes>
  );
}
