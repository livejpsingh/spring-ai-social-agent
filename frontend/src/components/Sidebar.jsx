import { NavLink } from 'react-router-dom';
import {
  LayoutDashboard, PenTool, Calendar, MessageSquare,
  BarChart3, CheckCircle, Settings, Sparkles, LogOut
} from 'lucide-react';
import { useAuth } from '../context/AuthContext';

const navItems = [
  { to: '/', icon: LayoutDashboard, label: 'Dashboard', end: true },
  { to: '/content', icon: PenTool, label: 'Content Studio' },
  { to: '/scheduler', icon: Calendar, label: 'Scheduler' },
  { to: '/engagement', icon: MessageSquare, label: 'Engagement' },
  { to: '/analytics', icon: BarChart3, label: 'Analytics' },
  { to: '/approvals', icon: CheckCircle, label: 'Approvals' },
  { to: '/settings', icon: Settings, label: 'Settings' },
];

export default function Sidebar() {
  const { logout, user } = useAuth();

  return (
    <aside className="fixed left-0 top-0 h-screen w-64 bg-surface-900/80 backdrop-blur-xl border-r border-surface-700/50 flex flex-col z-50">
      {/* Logo */}
      <div className="p-5 border-b border-surface-700/50">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 rounded-xl bg-gradient-to-br from-primary-500 to-accent-500 flex items-center justify-center shadow-lg">
            <Sparkles className="w-5 h-5 text-white" />
          </div>
          <div>
            <h1 className="font-bold text-lg gradient-text">SocialAI</h1>
            <p className="text-xs text-surface-200/60">Media Agent</p>
          </div>
        </div>
      </div>

      {/* Navigation */}
      <nav className="flex-1 py-4 px-3 space-y-1 overflow-auto">
        {navItems.map(({ to, icon: Icon, label, end }) => (
          <NavLink
            key={to}
            to={to}
            end={end}
            className={({ isActive }) =>
              `flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-medium transition-all duration-200 group
              ${isActive
                ? 'bg-primary-500/15 text-primary-400 shadow-lg shadow-primary-500/5'
                : 'text-surface-200/70 hover:bg-surface-800/60 hover:text-white'}`
            }
          >
            <Icon className="w-5 h-5 transition-transform group-hover:scale-110" />
            <span>{label}</span>
          </NavLink>
        ))}
      </nav>

      {/* User & Logout */}
      <div className="p-4 border-t border-surface-700/50">
        <div className="flex items-center gap-3 mb-3 px-2">
          {user?.picture ? (
            <img src={user.picture} alt="" className="w-9 h-9 rounded-full ring-2 ring-primary-500/30" />
          ) : (
            <div className="w-9 h-9 rounded-full bg-primary-500/20 flex items-center justify-center text-primary-400 font-semibold text-sm">
              {user?.name?.[0] || 'U'}
            </div>
          )}
          <div className="flex-1 min-w-0">
            <p className="text-sm font-medium truncate">{user?.name || 'User'}</p>
            <p className="text-xs text-surface-200/50 truncate">{user?.email || ''}</p>
          </div>
        </div>
        <button
          onClick={logout}
          className="flex items-center gap-2 w-full px-4 py-2.5 rounded-xl text-sm text-red-400 hover:bg-red-500/10 transition-colors"
        >
          <LogOut className="w-4 h-4" />
          <span>Sign Out</span>
        </button>
      </div>
    </aside>
  );
}
