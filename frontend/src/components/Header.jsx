import { Bell, Search } from 'lucide-react';
import { useAuth } from '../context/AuthContext';

export default function Header() {
  const { user } = useAuth();

  return (
    <header className="h-16 border-b border-surface-700/50 bg-surface-900/50 backdrop-blur-lg flex items-center justify-between px-6 sticky top-0 z-40">
      {/* Search */}
      <div className="relative w-80">
        <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-surface-200/40" />
        <input
          type="text"
          placeholder="Search content, comments, analytics..."
          className="w-full bg-surface-800/60 border border-surface-700/50 rounded-xl pl-10 pr-4 py-2.5 text-sm text-white placeholder:text-surface-200/40 focus:outline-none focus:ring-2 focus:ring-primary-500/30 focus:border-primary-500/30 transition-all"
        />
      </div>

      {/* Right section */}
      <div className="flex items-center gap-4">
        {/* Notifications */}
        <button className="relative p-2.5 rounded-xl bg-surface-800/60 border border-surface-700/50 hover:bg-surface-700/60 transition-colors group">
          <Bell className="w-5 h-5 text-surface-200/60 group-hover:text-white transition-colors" />
          <span className="absolute -top-1 -right-1 w-4 h-4 bg-accent-500 rounded-full text-[10px] font-bold flex items-center justify-center animate-pulse">
            3
          </span>
        </button>

        {/* Date */}
        <div className="text-sm text-surface-200/50 hidden lg:block">
          {new Date().toLocaleDateString('en-US', { weekday: 'long', month: 'short', day: 'numeric' })}
        </div>
      </div>
    </header>
  );
}
