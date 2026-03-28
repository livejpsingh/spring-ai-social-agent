import { useState } from 'react';
import { CheckCircle, XCircle, RotateCcw, Shield, AlertTriangle, Clock } from 'lucide-react';
import toast from 'react-hot-toast';

const pendingApprovals = [
  {
    id: 1, postText: '🚀 Exciting news! Our Q2 results are in and we\'ve exceeded all targets. Here\'s a breakdown of our incredible growth...',
    author: 'Sarah Marketing', platform: 'LinkedIn', riskScore: 0.25,
    brandScore: 0.94, sentimentScore: 0.87, submittedAt: '2h ago', dueDate: 'Tomorrow',
  },
  {
    id: 2, postText: 'We hear your concerns about the recent outage. Here\'s our official statement and the steps we\'re taking to prevent this...',
    author: 'PR Team', platform: 'Twitter', riskScore: 0.72,
    brandScore: 0.81, sentimentScore: -0.15, submittedAt: '4h ago', dueDate: 'Today',
  },
  {
    id: 3, postText: 'Flash sale! 50% off all premium plans for the next 48 hours. Use code SOCIAL50 at checkout 🎉',
    author: 'Growth Team', platform: 'Facebook', riskScore: 0.35,
    brandScore: 0.88, sentimentScore: 0.75, submittedAt: '6h ago', dueDate: 'Tomorrow',
  },
  {
    id: 4, postText: 'Our CEO responds to the latest industry controversy with a bold take on AI regulation...',
    author: 'Content Lead', platform: 'LinkedIn', riskScore: 0.85,
    brandScore: 0.72, sentimentScore: 0.1, submittedAt: '8h ago', dueDate: 'Today',
  },
];

export default function ApprovalPage() {
  const [filter, setFilter] = useState('all');
  const [revisionFeedback, setRevisionFeedback] = useState({});
  const [showRevision, setShowRevision] = useState({});
  const [items, setItems] = useState(pendingApprovals);

  const handleApprove = (id) => {
    setItems((prev) => prev.filter((item) => item.id !== id));
    toast.success('Content approved and queued for publishing!');
  };

  const handleReject = (id) => {
    setItems((prev) => prev.filter((item) => item.id !== id));
    toast.error('Content rejected.');
  };

  const handleRevision = (id) => {
    if (!revisionFeedback[id]?.trim()) return toast.error('Enter revision feedback');
    setItems((prev) => prev.filter((item) => item.id !== id));
    toast.success('Revision requested.');
    setShowRevision((prev) => ({ ...prev, [id]: false }));
  };

  const getRiskBadge = (score) => {
    if (score > 0.7) return { label: 'High Risk', color: 'bg-red-500/15 text-red-400', icon: AlertTriangle };
    if (score > 0.4) return { label: 'Medium Risk', color: 'bg-amber-500/15 text-amber-400', icon: Shield };
    return { label: 'Low Risk', color: 'bg-emerald-500/15 text-emerald-400', icon: Shield };
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold flex items-center gap-2">
            <CheckCircle className="w-6 h-6 text-emerald-400" />
            Approval Queue
          </h1>
          <p className="text-surface-200/50 mt-1">Review and approve content before publishing</p>
        </div>
        <div className="flex items-center gap-3">
          <div className="glass rounded-xl px-4 py-2 flex items-center gap-2">
            <Clock className="w-4 h-4 text-amber-400" />
            <span className="text-sm font-medium text-amber-400">{items.length} pending</span>
          </div>
        </div>
      </div>

      {/* Approval Cards */}
      <div className="space-y-4">
        {items.map((item) => {
          const risk = getRiskBadge(item.riskScore);
          const RiskIcon = risk.icon;

          return (
            <div key={item.id} className="glass rounded-2xl p-6 hover:glow-sm transition-all">
              <div className="flex items-start justify-between mb-4">
                <div className="flex items-center gap-3">
                  <span className="text-xs font-medium text-surface-200/40">{item.author}</span>
                  <span className="text-xs text-surface-200/30">·</span>
                  <span className="text-xs text-surface-200/30">{item.platform}</span>
                  <span className="text-xs text-surface-200/30">·</span>
                  <span className="text-xs text-surface-200/30">{item.submittedAt}</span>
                </div>
                <span className={`flex items-center gap-1 text-xs px-2.5 py-1 rounded-lg font-medium ${risk.color}`}>
                  <RiskIcon className="w-3 h-3" /> {risk.label}
                </span>
              </div>

              {/* Content Preview */}
              <div className="bg-surface-800/40 rounded-xl p-4 mb-4 border border-surface-700/30">
                <p className="text-sm text-white leading-relaxed">{item.postText}</p>
              </div>

              {/* Scores */}
              <div className="grid grid-cols-3 gap-4 mb-4">
                <div className="text-center">
                  <div className="relative inline-flex">
                    <svg className="w-16 h-16 -rotate-90">
                      <circle cx="32" cy="32" r="26" fill="none" stroke="#334155" strokeWidth="4" />
                      <circle
                        cx="32" cy="32" r="26" fill="none"
                        stroke={item.riskScore > 0.7 ? '#f87171' : item.riskScore > 0.4 ? '#fbbf24' : '#34d399'}
                        strokeWidth="4" strokeDasharray={`${item.riskScore * 163.36} 163.36`}
                        strokeLinecap="round"
                      />
                    </svg>
                    <span className="absolute inset-0 flex items-center justify-center text-xs font-bold">
                      {(item.riskScore * 100).toFixed(0)}%
                    </span>
                  </div>
                  <p className="text-xs text-surface-200/40 mt-1">Risk</p>
                </div>
                <div className="text-center">
                  <div className="relative inline-flex">
                    <svg className="w-16 h-16 -rotate-90">
                      <circle cx="32" cy="32" r="26" fill="none" stroke="#334155" strokeWidth="4" />
                      <circle
                        cx="32" cy="32" r="26" fill="none" stroke="#818cf8"
                        strokeWidth="4" strokeDasharray={`${item.brandScore * 163.36} 163.36`}
                        strokeLinecap="round"
                      />
                    </svg>
                    <span className="absolute inset-0 flex items-center justify-center text-xs font-bold">
                      {(item.brandScore * 100).toFixed(0)}%
                    </span>
                  </div>
                  <p className="text-xs text-surface-200/40 mt-1">Brand</p>
                </div>
                <div className="text-center">
                  <div className="relative inline-flex">
                    <svg className="w-16 h-16 -rotate-90">
                      <circle cx="32" cy="32" r="26" fill="none" stroke="#334155" strokeWidth="4" />
                      <circle
                        cx="32" cy="32" r="26" fill="none"
                        stroke={item.sentimentScore > 0 ? '#34d399' : '#f87171'}
                        strokeWidth="4"
                        strokeDasharray={`${((item.sentimentScore + 1) / 2) * 163.36} 163.36`}
                        strokeLinecap="round"
                      />
                    </svg>
                    <span className="absolute inset-0 flex items-center justify-center text-xs font-bold">
                      {(item.sentimentScore * 100).toFixed(0)}%
                    </span>
                  </div>
                  <p className="text-xs text-surface-200/40 mt-1">Sentiment</p>
                </div>
              </div>

              {/* Revision Feedback */}
              {showRevision[item.id] && (
                <div className="mb-4">
                  <textarea
                    value={revisionFeedback[item.id] || ''}
                    onChange={(e) => setRevisionFeedback((p) => ({ ...p, [item.id]: e.target.value }))}
                    placeholder="Enter revision feedback..."
                    rows={3}
                    className="w-full bg-surface-800/60 border border-surface-700/50 rounded-xl px-4 py-3 text-sm text-white placeholder:text-surface-200/30 focus:outline-none focus:ring-2 focus:ring-primary-500/30 resize-none mb-2"
                  />
                  <div className="flex gap-2">
                    <button
                      onClick={() => handleRevision(item.id)}
                      className="px-4 py-2 rounded-xl bg-amber-500 hover:bg-amber-400 text-white text-sm font-medium transition-colors"
                    >
                      Send Revision
                    </button>
                    <button
                      onClick={() => setShowRevision((p) => ({ ...p, [item.id]: false }))}
                      className="px-4 py-2 rounded-xl bg-surface-700 hover:bg-surface-600 text-white text-sm font-medium transition-colors"
                    >
                      Cancel
                    </button>
                  </div>
                </div>
              )}

              {/* Actions */}
              {!showRevision[item.id] && (
                <div className="flex items-center gap-3">
                  <button
                    onClick={() => handleApprove(item.id)}
                    className="flex items-center gap-2 px-5 py-2.5 rounded-xl bg-emerald-500 hover:bg-emerald-400 text-white text-sm font-medium transition-all shadow-lg shadow-emerald-500/20 hover:shadow-emerald-500/30"
                  >
                    <CheckCircle className="w-4 h-4" /> Approve
                  </button>
                  <button
                    onClick={() => setShowRevision((p) => ({ ...p, [item.id]: true }))}
                    className="flex items-center gap-2 px-5 py-2.5 rounded-xl bg-amber-500/15 text-amber-400 hover:bg-amber-500/25 text-sm font-medium transition-colors"
                  >
                    <RotateCcw className="w-4 h-4" /> Revision
                  </button>
                  <button
                    onClick={() => handleReject(item.id)}
                    className="flex items-center gap-2 px-5 py-2.5 rounded-xl bg-red-500/15 text-red-400 hover:bg-red-500/25 text-sm font-medium transition-colors"
                  >
                    <XCircle className="w-4 h-4" /> Reject
                  </button>
                  <span className="text-xs text-surface-200/30 ml-auto">Due: {item.dueDate}</span>
                </div>
              )}
            </div>
          );
        })}

        {items.length === 0 && (
          <div className="glass rounded-2xl p-16 flex flex-col items-center justify-center text-center">
            <CheckCircle className="w-16 h-16 text-emerald-400/30 mb-4" />
            <h3 className="text-lg font-semibold text-surface-200/50">All caught up!</h3>
            <p className="text-sm text-surface-200/30">No pending content to review</p>
          </div>
        )}
      </div>
    </div>
  );
}
