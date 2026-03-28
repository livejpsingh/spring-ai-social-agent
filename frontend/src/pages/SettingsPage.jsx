import { useState } from 'react';
import { Settings, User, Bell, Shield, Palette, Globe, Key, Save } from 'lucide-react';
import toast from 'react-hot-toast';
import { useAuth } from '../context/AuthContext';

const platformConnections = [
  { name: 'Twitter / X', connected: true, handle: '@your_brand', color: '#1DA1F2', icon: '𝕏' },
  { name: 'LinkedIn', connected: true, handle: 'Your Company Page', color: '#0A66C2', icon: 'in' },
  { name: 'Instagram', connected: false, handle: null, color: '#E4405F', icon: '📷' },
  { name: 'Facebook', connected: false, handle: null, color: '#1877F2', icon: 'f' },
  { name: 'TikTok', connected: false, handle: null, color: '#000000', icon: '♪' },
];

export default function SettingsPage() {
  const { user } = useAuth();
  const [activeTab, setActiveTab] = useState('profile');

  const tabs = [
    { id: 'profile', label: 'Profile', icon: User },
    { id: 'connections', label: 'Platform Connections', icon: Globe },
    { id: 'notifications', label: 'Notifications', icon: Bell },
    { id: 'brand', label: 'Brand Guidelines', icon: Palette },
    { id: 'security', label: 'Security', icon: Shield },
    { id: 'api', label: 'API Keys', icon: Key },
  ];

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold flex items-center gap-2">
          <Settings className="w-6 h-6 text-surface-200/60" />
          Settings
        </h1>
        <p className="text-surface-200/50 mt-1">Manage your account and preferences</p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-4 gap-6">
        {/* Tabs */}
        <div className="glass rounded-2xl p-4 h-fit">
          <nav className="space-y-1">
            {tabs.map(({ id, label, icon: Icon }) => (
              <button
                key={id}
                onClick={() => setActiveTab(id)}
                className={`flex items-center gap-3 w-full px-4 py-3 rounded-xl text-sm font-medium transition-all ${
                  activeTab === id
                    ? 'bg-primary-500/15 text-primary-400'
                    : 'text-surface-200/50 hover:bg-surface-800/50 hover:text-white'
                }`}
              >
                <Icon className="w-4 h-4" />
                {label}
              </button>
            ))}
          </nav>
        </div>

        {/* Tab Content */}
        <div className="lg:col-span-3">
          {activeTab === 'profile' && (
            <div className="glass rounded-2xl p-6 space-y-6">
              <h2 className="text-lg font-semibold">Profile Settings</h2>
              <div className="flex items-center gap-6">
                {user?.picture ? (
                  <img src={user.picture} alt="" className="w-20 h-20 rounded-2xl ring-2 ring-primary-500/30" />
                ) : (
                  <div className="w-20 h-20 rounded-2xl bg-primary-500/20 flex items-center justify-center text-primary-400 font-bold text-2xl">
                    {user?.name?.[0] || 'U'}
                  </div>
                )}
                <div>
                  <button className="px-4 py-2 rounded-xl bg-primary-500/15 text-primary-400 text-sm font-medium hover:bg-primary-500/25 transition-colors">
                    Change Photo
                  </button>
                </div>
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="text-sm font-medium text-surface-200/70 mb-2 block">Full Name</label>
                  <input defaultValue={user?.name || ''} className="w-full bg-surface-800/60 border border-surface-700/50 rounded-xl px-4 py-3 text-sm text-white focus:outline-none focus:ring-2 focus:ring-primary-500/30" />
                </div>
                <div>
                  <label className="text-sm font-medium text-surface-200/70 mb-2 block">Email</label>
                  <input defaultValue={user?.email || ''} className="w-full bg-surface-800/60 border border-surface-700/50 rounded-xl px-4 py-3 text-sm text-white focus:outline-none focus:ring-2 focus:ring-primary-500/30" disabled />
                </div>
                <div>
                  <label className="text-sm font-medium text-surface-200/70 mb-2 block">Company</label>
                  <input placeholder="Your company name" className="w-full bg-surface-800/60 border border-surface-700/50 rounded-xl px-4 py-3 text-sm text-white placeholder:text-surface-200/30 focus:outline-none focus:ring-2 focus:ring-primary-500/30" />
                </div>
                <div>
                  <label className="text-sm font-medium text-surface-200/70 mb-2 block">Time Zone</label>
                  <select className="w-full bg-surface-800/60 border border-surface-700/50 rounded-xl px-4 py-3 text-sm text-white focus:outline-none focus:ring-2 focus:ring-primary-500/30">
                    <option>UTC+05:30 (IST)</option>
                    <option>UTC-08:00 (PST)</option>
                    <option>UTC-05:00 (EST)</option>
                    <option>UTC+00:00 (GMT)</option>
                  </select>
                </div>
              </div>
              <button onClick={() => toast.success('Profile saved!')} className="px-5 py-2.5 rounded-xl bg-primary-500 hover:bg-primary-400 text-white text-sm font-medium transition-colors flex items-center gap-2">
                <Save className="w-4 h-4" /> Save Changes
              </button>
            </div>
          )}

          {activeTab === 'connections' && (
            <div className="glass rounded-2xl p-6 space-y-4">
              <h2 className="text-lg font-semibold mb-2">Platform Connections</h2>
              {platformConnections.map((p) => (
                <div key={p.name} className="flex items-center justify-between p-4 rounded-xl bg-surface-800/30 border border-surface-700/30 hover:border-surface-700/50 transition-colors">
                  <div className="flex items-center gap-4">
                    <div className="w-10 h-10 rounded-xl flex items-center justify-center text-white font-bold text-sm" style={{ background: p.color }}>
                      {p.icon}
                    </div>
                    <div>
                      <p className="text-sm font-semibold text-white">{p.name}</p>
                      {p.connected ? (
                        <p className="text-xs text-emerald-400">{p.handle}</p>
                      ) : (
                        <p className="text-xs text-surface-200/40">Not connected</p>
                      )}
                    </div>
                  </div>
                  <button
                    className={`px-4 py-2 rounded-xl text-sm font-medium transition-colors ${
                      p.connected
                        ? 'bg-red-500/10 text-red-400 hover:bg-red-500/20'
                        : 'bg-primary-500/15 text-primary-400 hover:bg-primary-500/25'
                    }`}
                    onClick={() => toast.success(p.connected ? `${p.name} disconnected` : `Connecting to ${p.name}...`)}
                  >
                    {p.connected ? 'Disconnect' : 'Connect'}
                  </button>
                </div>
              ))}
            </div>
          )}

          {activeTab === 'notifications' && (
            <div className="glass rounded-2xl p-6 space-y-4">
              <h2 className="text-lg font-semibold mb-2">Notification Preferences</h2>
              {[
                { label: 'Post published successfully', desc: 'Get notified when scheduled posts are published' },
                { label: 'New comments requiring review', desc: 'Alert for escalated or pending comments' },
                { label: 'Approval requests', desc: 'Notify when content needs your approval' },
                { label: 'Weekly analytics report', desc: 'Receive weekly performance summary' },
                { label: 'AI generation complete', desc: 'Alert when AI content is ready' },
              ].map(({ label, desc }, i) => (
                <div key={label} className="flex items-center justify-between p-4 rounded-xl bg-surface-800/30 border border-surface-700/30">
                  <div>
                    <p className="text-sm font-medium text-white">{label}</p>
                    <p className="text-xs text-surface-200/40">{desc}</p>
                  </div>
                  <label className="cursor-pointer">
                    <input type="checkbox" defaultChecked={i < 3} className="sr-only peer" />
                    <div className="w-10 h-5.5 bg-surface-700 rounded-full peer-checked:bg-primary-500 transition-colors relative after:content-[''] after:absolute after:top-0.5 after:left-0.5 after:w-4.5 after:h-4.5 after:bg-white after:rounded-full after:transition-transform peer-checked:after:translate-x-[18px]" />
                  </label>
                </div>
              ))}
              <button onClick={() => toast.success('Notifications updated!')} className="px-5 py-2.5 rounded-xl bg-primary-500 hover:bg-primary-400 text-white text-sm font-medium transition-colors flex items-center gap-2">
                <Save className="w-4 h-4" /> Save Preferences
              </button>
            </div>
          )}

          {activeTab === 'brand' && (
            <div className="glass rounded-2xl p-6 space-y-6">
              <h2 className="text-lg font-semibold">Brand Guidelines</h2>
              <div>
                <label className="text-sm font-medium text-surface-200/70 mb-2 block">Brand Voice Description</label>
                <textarea
                  rows={4}
                  placeholder="Describe your brand's tone and personality... e.g., 'Professional and approachable, using simple language...'"
                  className="w-full bg-surface-800/60 border border-surface-700/50 rounded-xl px-4 py-3 text-sm text-white placeholder:text-surface-200/30 focus:outline-none focus:ring-2 focus:ring-primary-500/30 resize-none"
                />
              </div>
              <div>
                <label className="text-sm font-medium text-surface-200/70 mb-2 block">Preferred Vocabulary (comma-separated)</label>
                <input placeholder="innovative, empower, transform, seamless" className="w-full bg-surface-800/60 border border-surface-700/50 rounded-xl px-4 py-3 text-sm text-white placeholder:text-surface-200/30 focus:outline-none focus:ring-2 focus:ring-primary-500/30" />
              </div>
              <div>
                <label className="text-sm font-medium text-surface-200/70 mb-2 block">Prohibited Terms (comma-separated)</label>
                <input placeholder="cheap, basic, competitor names..." className="w-full bg-surface-800/60 border border-surface-700/50 rounded-xl px-4 py-3 text-sm text-white placeholder:text-surface-200/30 focus:outline-none focus:ring-2 focus:ring-primary-500/30" />
              </div>
              <button onClick={() => toast.success('Brand guidelines saved!')} className="px-5 py-2.5 rounded-xl bg-primary-500 hover:bg-primary-400 text-white text-sm font-medium transition-colors flex items-center gap-2">
                <Save className="w-4 h-4" /> Save Guidelines
              </button>
            </div>
          )}

          {activeTab === 'security' && (
            <div className="glass rounded-2xl p-6 space-y-4">
              <h2 className="text-lg font-semibold">Security Settings</h2>
              <div className="p-4 rounded-xl bg-surface-800/30 border border-surface-700/30">
                <p className="text-sm font-medium text-white mb-1">Two-Factor Authentication</p>
                <p className="text-xs text-surface-200/40 mb-3">Add an extra layer of security to your account</p>
                <button className="px-4 py-2 rounded-xl bg-emerald-500/15 text-emerald-400 text-sm font-medium hover:bg-emerald-500/25 transition-colors">
                  Enable 2FA
                </button>
              </div>
              <div className="p-4 rounded-xl bg-surface-800/30 border border-surface-700/30">
                <p className="text-sm font-medium text-white mb-1">Active Sessions</p>
                <p className="text-xs text-surface-200/40 mb-3">You're currently logged in on 1 device</p>
                <button className="px-4 py-2 rounded-xl bg-red-500/10 text-red-400 text-sm font-medium hover:bg-red-500/20 transition-colors">
                  Sign Out All Devices
                </button>
              </div>
            </div>
          )}

          {activeTab === 'api' && (
            <div className="glass rounded-2xl p-6 space-y-4">
              <h2 className="text-lg font-semibold">API Keys</h2>
              <div className="p-4 rounded-xl bg-surface-800/30 border border-surface-700/30">
                <div className="flex items-center justify-between mb-2">
                  <p className="text-sm font-medium text-white">OpenAI API Key</p>
                  <span className="text-xs text-emerald-400 bg-emerald-500/10 px-2 py-0.5 rounded">Configured</span>
                </div>
                <p className="text-xs text-surface-200/30 font-mono">sk-****...****3def</p>
              </div>
              <div className="p-4 rounded-xl bg-surface-800/30 border border-surface-700/30">
                <div className="flex items-center justify-between mb-2">
                  <p className="text-sm font-medium text-white">Webhook URL</p>
                  <span className="text-xs text-surface-200/40 bg-surface-700/50 px-2 py-0.5 rounded">Not Set</span>
                </div>
                <input placeholder="https://your-domain.com/webhook" className="w-full bg-surface-800/60 border border-surface-700/50 rounded-xl px-4 py-2.5 text-sm text-white placeholder:text-surface-200/30 focus:outline-none focus:ring-2 focus:ring-primary-500/30 mt-2" />
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
