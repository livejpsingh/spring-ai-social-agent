import { useState } from 'react';
import {
  TrendingUp, Users, Eye, Heart, ArrowUpRight, ArrowDownRight,
  PenTool, Calendar, MessageSquare, CheckCircle, Sparkles
} from 'lucide-react';
import { AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, PieChart, Pie, Cell } from 'recharts';
import { useAuth } from '../context/AuthContext';

const engagementData = [
  { name: 'Mon', value: 2400 }, { name: 'Tue', value: 1398 },
  { name: 'Wed', value: 4800 }, { name: 'Thu', value: 3908 },
  { name: 'Fri', value: 4800 }, { name: 'Sat', value: 3800 },
  { name: 'Sun', value: 4300 },
];

const platformData = [
  { name: 'Twitter', value: 35, color: '#1DA1F2' },
  { name: 'LinkedIn', value: 28, color: '#0A66C2' },
  { name: 'Instagram', value: 22, color: '#E4405F' },
  { name: 'Facebook', value: 15, color: '#1877F2' },
];

const recentPosts = [
  { id: 1, text: '🚀 Excited to announce our new AI-powered analytics...', platform: 'Twitter', status: 'published', engagement: 4.2, time: '2h ago' },
  { id: 2, text: 'The future of social media management is here...', platform: 'LinkedIn', status: 'scheduled', engagement: 0, time: 'Tomorrow 9 AM' },
  { id: 3, text: '5 tips for increasing your social media presence...', platform: 'Instagram', status: 'draft', engagement: 0, time: 'Draft' },
  { id: 4, text: 'Thank you for 10K followers! 🎉 Heres what we...', platform: 'Facebook', status: 'published', engagement: 6.8, time: '5h ago' },
];

const statsCards = [
  { label: 'Total Reach', value: '142.5K', change: '+12.5%', up: true, icon: Eye, color: 'from-blue-500 to-cyan-400' },
  { label: 'Engagement Rate', value: '4.8%', change: '+0.8%', up: true, icon: Heart, color: 'from-pink-500 to-rose-400' },
  { label: 'Followers', value: '28.3K', change: '+1.2K', up: true, icon: Users, color: 'from-violet-500 to-purple-400' },
  { label: 'Posts This Week', value: '24', change: '-3', up: false, icon: TrendingUp, color: 'from-amber-500 to-orange-400' },
];

const quickActions = [
  { icon: PenTool, label: 'Create Post', path: '/content', color: 'bg-primary-500/15 text-primary-400' },
  { icon: Calendar, label: 'Schedule', path: '/scheduler', color: 'bg-cyan-500/15 text-cyan-400' },
  { icon: MessageSquare, label: 'Respond', path: '/engagement', color: 'bg-pink-500/15 text-pink-400' },
  { icon: CheckCircle, label: 'Review', path: '/approvals', color: 'bg-green-500/15 text-green-400' },
];

export default function DashboardPage() {
  const { user } = useAuth();
  const [timeRange, setTimeRange] = useState('7d');

  return (
    <div className="space-y-6">
      {/* Welcome */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold">
            Welcome back, <span className="gradient-text">{user?.name?.split(' ')[0] || 'User'}</span>
          </h1>
          <p className="text-surface-200/50 mt-1">Here's what's happening across your channels</p>
        </div>
        <div className="flex gap-2">
          {['24h', '7d', '30d', '90d'].map((r) => (
            <button
              key={r}
              onClick={() => setTimeRange(r)}
              className={`px-4 py-2 rounded-lg text-xs font-medium transition-all ${
                timeRange === r
                  ? 'bg-primary-500/20 text-primary-400 ring-1 ring-primary-500/30'
                  : 'bg-surface-800/50 text-surface-200/50 hover:text-white'
              }`}
            >
              {r}
            </button>
          ))}
        </div>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-4 gap-4">
        {statsCards.map(({ label, value, change, up, icon: Icon, color }) => (
          <div key={label} className="glass rounded-2xl p-5 hover:glow-sm transition-all duration-300 group">
            <div className="flex items-start justify-between mb-4">
              <div className={`w-11 h-11 rounded-xl bg-gradient-to-br ${color} flex items-center justify-center shadow-lg group-hover:scale-110 transition-transform`}>
                <Icon className="w-5 h-5 text-white" />
              </div>
              <span className={`flex items-center gap-1 text-xs font-semibold ${up ? 'text-emerald-400' : 'text-red-400'}`}>
                {up ? <ArrowUpRight className="w-3 h-3" /> : <ArrowDownRight className="w-3 h-3" />}
                {change}
              </span>
            </div>
            <p className="text-2xl font-bold text-white">{value}</p>
            <p className="text-xs text-surface-200/50 mt-1">{label}</p>
          </div>
        ))}
      </div>

      {/* Charts Row */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Engagement Chart */}
        <div className="lg:col-span-2 glass rounded-2xl p-6">
          <h3 className="text-lg font-semibold mb-4">Engagement Overview</h3>
          <ResponsiveContainer width="100%" height={280}>
            <AreaChart data={engagementData}>
              <defs>
                <linearGradient id="engGrad" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="0%" stopColor="#818cf8" stopOpacity={0.3} />
                  <stop offset="100%" stopColor="#818cf8" stopOpacity={0} />
                </linearGradient>
              </defs>
              <CartesianGrid strokeDasharray="3 3" stroke="#334155" />
              <XAxis dataKey="name" stroke="#64748b" fontSize={12} />
              <YAxis stroke="#64748b" fontSize={12} />
              <Tooltip
                contentStyle={{ background: '#1e293b', border: '1px solid #334155', borderRadius: '12px', color: '#fff' }}
              />
              <Area type="monotone" dataKey="value" stroke="#818cf8" strokeWidth={2.5} fill="url(#engGrad)" />
            </AreaChart>
          </ResponsiveContainer>
        </div>

        {/* Platform Distribution */}
        <div className="glass rounded-2xl p-6">
          <h3 className="text-lg font-semibold mb-4">Platform Mix</h3>
          <ResponsiveContainer width="100%" height={200}>
            <PieChart>
              <Pie data={platformData} cx="50%" cy="50%" innerRadius={55} outerRadius={80} paddingAngle={4} dataKey="value">
                {platformData.map((entry, i) => (
                  <Cell key={i} fill={entry.color} />
                ))}
              </Pie>
              <Tooltip contentStyle={{ background: '#1e293b', border: '1px solid #334155', borderRadius: '12px', color: '#fff' }} />
            </PieChart>
          </ResponsiveContainer>
          <div className="space-y-2 mt-4">
            {platformData.map((p) => (
              <div key={p.name} className="flex items-center justify-between text-sm">
                <div className="flex items-center gap-2">
                  <div className="w-3 h-3 rounded-full" style={{ background: p.color }} />
                  <span className="text-surface-200/70">{p.name}</span>
                </div>
                <span className="font-semibold">{p.value}%</span>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Quick Actions & Recent Posts */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Quick Actions */}
        <div className="glass rounded-2xl p-6">
          <h3 className="text-lg font-semibold mb-4 flex items-center gap-2">
            <Sparkles className="w-5 h-5 text-primary-400" />
            Quick Actions
          </h3>
          <div className="grid grid-cols-2 gap-3">
            {quickActions.map(({ icon: Icon, label, color }) => (
              <button
                key={label}
                className="flex flex-col items-center gap-2 p-4 rounded-xl bg-surface-800/40 hover:bg-surface-800/70 border border-surface-700/30 hover:border-primary-500/20 transition-all group"
              >
                <div className={`w-10 h-10 rounded-xl ${color} flex items-center justify-center group-hover:scale-110 transition-transform`}>
                  <Icon className="w-5 h-5" />
                </div>
                <span className="text-xs font-medium text-surface-200/70 group-hover:text-white">{label}</span>
              </button>
            ))}
          </div>
        </div>

        {/* Recent Posts */}
        <div className="lg:col-span-2 glass rounded-2xl p-6">
          <h3 className="text-lg font-semibold mb-4">Recent Posts</h3>
          <div className="space-y-3">
            {recentPosts.map((post) => (
              <div key={post.id} className="flex items-center gap-4 p-3 rounded-xl bg-surface-800/30 hover:bg-surface-800/50 transition-colors">
                <div className={`w-2 h-2 rounded-full flex-shrink-0 ${
                  post.status === 'published' ? 'bg-emerald-400' :
                  post.status === 'scheduled' ? 'bg-amber-400' : 'bg-surface-200/40'
                }`} />
                <div className="flex-1 min-w-0">
                  <p className="text-sm text-white truncate">{post.text}</p>
                  <p className="text-xs text-surface-200/40 mt-0.5">{post.platform} · {post.time}</p>
                </div>
                {post.engagement > 0 && (
                  <span className="text-xs font-semibold text-emerald-400 bg-emerald-500/10 px-2.5 py-1 rounded-lg">
                    {post.engagement}% eng.
                  </span>
                )}
                <span className={`text-xs px-2.5 py-1 rounded-lg font-medium capitalize ${
                  post.status === 'published' ? 'bg-emerald-500/10 text-emerald-400' :
                  post.status === 'scheduled' ? 'bg-amber-500/10 text-amber-400' :
                  'bg-surface-700/50 text-surface-200/60'
                }`}>
                  {post.status}
                </span>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
