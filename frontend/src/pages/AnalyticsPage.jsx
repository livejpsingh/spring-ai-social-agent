import { useState } from 'react';
import {
  BarChart3, TrendingUp, Users, Eye, Heart, Share2, MessageSquare
} from 'lucide-react';
import {
  AreaChart, Area, BarChart, Bar, LineChart, Line, XAxis, YAxis,
  CartesianGrid, Tooltip, ResponsiveContainer, RadarChart,
  PolarGrid, PolarAngleAxis, Radar
} from 'recharts';

const reachData = [
  { name: 'Jan', twitter: 4000, linkedin: 2400, instagram: 3200 },
  { name: 'Feb', twitter: 3000, linkedin: 1398, instagram: 2800 },
  { name: 'Mar', twitter: 5000, linkedin: 4800, instagram: 4200 },
  { name: 'Apr', twitter: 2780, linkedin: 3908, instagram: 3500 },
  { name: 'May', twitter: 6890, linkedin: 4800, instagram: 5200 },
  { name: 'Jun', twitter: 5390, linkedin: 3800, instagram: 4800 },
  { name: 'Jul', twitter: 7490, linkedin: 4300, instagram: 6100 },
];

const engagementByType = [
  { name: 'Likes', value: 8400 },
  { name: 'Comments', value: 3200 },
  { name: 'Shares', value: 1800 },
  { name: 'Saves', value: 2100 },
  { name: 'Clicks', value: 4600 },
];

const radarData = [
  { subject: 'Reach', A: 85 },
  { subject: 'Engagement', A: 72 },
  { subject: 'Growth', A: 68 },
  { subject: 'Consistency', A: 90 },
  { subject: 'Brand Voice', A: 88 },
  { subject: 'Response Time', A: 76 },
];

const topPosts = [
  { id: 1, text: '🚀 Just launched our AI analytics...', platform: 'Twitter', reach: '45.2K', engagement: '6.8%', sentiment: 0.94 },
  { id: 2, text: '5 tips for social media success...', platform: 'LinkedIn', reach: '32.1K', engagement: '5.2%', sentiment: 0.88 },
  { id: 3, text: 'Behind the scenes of our team...', platform: 'Instagram', reach: '28.7K', engagement: '7.1%', sentiment: 0.91 },
];

const metrics = [
  { label: 'Impressions', value: '1.2M', change: '+15%', icon: Eye, color: 'from-blue-500 to-cyan-400' },
  { label: 'Engagements', value: '48.2K', change: '+22%', icon: Heart, color: 'from-pink-500 to-rose-400' },
  { label: 'Shares', value: '5.8K', change: '+8%', icon: Share2, color: 'from-violet-500 to-purple-400' },
  { label: 'Comments', value: '3.2K', change: '+18%', icon: MessageSquare, color: 'from-amber-500 to-orange-400' },
  { label: 'Followers', value: '28.3K', change: '+1.2K', icon: Users, color: 'from-emerald-500 to-teal-400' },
  { label: 'Growth Rate', value: '4.2%', change: '+0.5%', icon: TrendingUp, color: 'from-indigo-500 to-blue-400' },
];

const tooltipStyle = { background: '#1e293b', border: '1px solid #334155', borderRadius: '12px', color: '#fff', fontSize: '12px' };

export default function AnalyticsPage() {
  const [period, setPeriod] = useState('7d');

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold flex items-center gap-2">
            <BarChart3 className="w-6 h-6 text-violet-400" />
            Analytics
          </h1>
          <p className="text-surface-200/50 mt-1">Track performance across all your social channels</p>
        </div>
        <div className="flex gap-2">
          {['24h', '7d', '30d', '90d'].map((p) => (
            <button
              key={p}
              onClick={() => setPeriod(p)}
              className={`px-4 py-2 rounded-lg text-xs font-medium transition-all ${
                period === p
                  ? 'bg-primary-500/20 text-primary-400 ring-1 ring-primary-500/30'
                  : 'bg-surface-800/50 text-surface-200/50 hover:text-white'
              }`}
            >
              {p}
            </button>
          ))}
        </div>
      </div>

      {/* Metric Cards */}
      <div className="grid grid-cols-2 md:grid-cols-3 xl:grid-cols-6 gap-3">
        {metrics.map(({ label, value, change, icon: Icon, color }) => (
          <div key={label} className="glass rounded-xl p-4 hover:glow-sm transition-all group">
            <div className={`w-9 h-9 rounded-lg bg-gradient-to-br ${color} flex items-center justify-center mb-3 group-hover:scale-110 transition-transform`}>
              <Icon className="w-4 h-4 text-white" />
            </div>
            <p className="text-lg font-bold">{value}</p>
            <div className="flex items-center justify-between mt-1">
              <p className="text-[11px] text-surface-200/40">{label}</p>
              <span className="text-[11px] text-emerald-400 font-semibold">{change}</span>
            </div>
          </div>
        ))}
      </div>

      {/* Charts Row 1 */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Reach by Platform */}
        <div className="lg:col-span-2 glass rounded-2xl p-6">
          <h3 className="text-lg font-semibold mb-4">Reach by Platform</h3>
          <ResponsiveContainer width="100%" height={300}>
            <AreaChart data={reachData}>
              <defs>
                <linearGradient id="tw" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="0%" stopColor="#1DA1F2" stopOpacity={0.3} />
                  <stop offset="100%" stopColor="#1DA1F2" stopOpacity={0} />
                </linearGradient>
                <linearGradient id="li" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="0%" stopColor="#0A66C2" stopOpacity={0.3} />
                  <stop offset="100%" stopColor="#0A66C2" stopOpacity={0} />
                </linearGradient>
                <linearGradient id="ig" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="0%" stopColor="#E4405F" stopOpacity={0.3} />
                  <stop offset="100%" stopColor="#E4405F" stopOpacity={0} />
                </linearGradient>
              </defs>
              <CartesianGrid strokeDasharray="3 3" stroke="#334155" />
              <XAxis dataKey="name" stroke="#64748b" fontSize={12} />
              <YAxis stroke="#64748b" fontSize={12} />
              <Tooltip contentStyle={tooltipStyle} />
              <Area type="monotone" dataKey="twitter" stroke="#1DA1F2" fill="url(#tw)" strokeWidth={2} />
              <Area type="monotone" dataKey="linkedin" stroke="#0A66C2" fill="url(#li)" strokeWidth={2} />
              <Area type="monotone" dataKey="instagram" stroke="#E4405F" fill="url(#ig)" strokeWidth={2} />
            </AreaChart>
          </ResponsiveContainer>
        </div>

        {/* Performance Radar */}
        <div className="glass rounded-2xl p-6">
          <h3 className="text-lg font-semibold mb-4">Performance Score</h3>
          <ResponsiveContainer width="100%" height={300}>
            <RadarChart data={radarData} cx="50%" cy="50%" outerRadius="70%">
              <PolarGrid stroke="#334155" />
              <PolarAngleAxis dataKey="subject" tick={{ fill: '#94a3b8', fontSize: 11 }} />
              <Radar name="Score" dataKey="A" stroke="#818cf8" fill="#818cf8" fillOpacity={0.2} strokeWidth={2} />
            </RadarChart>
          </ResponsiveContainer>
        </div>
      </div>

      {/* Charts Row 2 */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Engagement by Type */}
        <div className="glass rounded-2xl p-6">
          <h3 className="text-lg font-semibold mb-4">Engagement Breakdown</h3>
          <ResponsiveContainer width="100%" height={250}>
            <BarChart data={engagementByType}>
              <CartesianGrid strokeDasharray="3 3" stroke="#334155" />
              <XAxis dataKey="name" stroke="#64748b" fontSize={12} />
              <YAxis stroke="#64748b" fontSize={12} />
              <Tooltip contentStyle={tooltipStyle} />
              <Bar dataKey="value" fill="#818cf8" radius={[6, 6, 0, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </div>

        {/* Top Posts */}
        <div className="glass rounded-2xl p-6">
          <h3 className="text-lg font-semibold mb-4">Top Performing Posts</h3>
          <div className="space-y-3">
            {topPosts.map((post, i) => (
              <div key={post.id} className="flex items-center gap-4 p-3 rounded-xl bg-surface-800/30 hover:bg-surface-800/50 transition-colors">
                <span className="w-8 h-8 rounded-lg bg-primary-500/15 flex items-center justify-center text-primary-400 font-bold text-sm">
                  #{i + 1}
                </span>
                <div className="flex-1 min-w-0">
                  <p className="text-sm text-white truncate">{post.text}</p>
                  <p className="text-xs text-surface-200/40">{post.platform}</p>
                </div>
                <div className="text-right">
                  <p className="text-sm font-semibold text-white">{post.reach}</p>
                  <p className="text-xs text-emerald-400">{post.engagement} eng.</p>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
