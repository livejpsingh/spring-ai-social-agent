import { useState } from 'react';
import { MessageSquare, AlertTriangle, ThumbsUp, HelpCircle, Send, Filter } from 'lucide-react';
import toast from 'react-hot-toast';

const comments = [
  { id: 1, user: '@sarah_jones', avatar: 'S', text: 'Love the new features you guys just released! 🔥', platform: 'Twitter', sentiment: 0.92, emotion: 'joy', intent: 'praise', status: 'auto_replied', time: '15m ago', response: 'Thank you so much, Sarah! We\'re thrilled you love the updates! 💜' },
  { id: 2, user: '@mike_dev', avatar: 'M', text: 'The API keeps timing out. This is really frustrating.', platform: 'Twitter', sentiment: -0.75, emotion: 'anger', intent: 'complaint', status: 'escalated', time: '32m ago', response: null },
  { id: 3, user: 'Tech Explorer', avatar: 'T', text: 'How does the AI scheduling algorithm work?', platform: 'LinkedIn', sentiment: 0.3, emotion: 'neutral', intent: 'question', status: 'pending', time: '1h ago', response: null },
  { id: 4, user: '@brand_fan', avatar: 'B', text: 'Just recommended your tool to my entire team!', platform: 'Instagram', sentiment: 0.95, emotion: 'joy', intent: 'praise', status: 'auto_replied', time: '2h ago', response: 'That means the world to us! Thank you for spreading the word! 🙏' },
  { id: 5, user: 'Startup Founder', avatar: 'S', text: 'Can you add support for TikTok scheduling?', platform: 'LinkedIn', sentiment: 0.2, emotion: 'neutral', intent: 'question', status: 'pending', time: '3h ago', response: null },
  { id: 6, user: '@angry_user', avatar: 'A', text: 'This is the worst service I have ever used. Want a refund NOW.', platform: 'Facebook', sentiment: -0.95, emotion: 'anger', intent: 'complaint', status: 'escalated', time: '4h ago', response: null },
];

const statusConfig = {
  auto_replied: { label: 'Auto-Replied', color: 'bg-emerald-500/10 text-emerald-400' },
  escalated: { label: 'Escalated', color: 'bg-red-500/10 text-red-400' },
  pending: { label: 'Pending', color: 'bg-amber-500/10 text-amber-400' },
  human_reviewed: { label: 'Reviewed', color: 'bg-blue-500/10 text-blue-400' },
};

const intentIcons = {
  praise: ThumbsUp,
  complaint: AlertTriangle,
  question: HelpCircle,
  spam: AlertTriangle,
};

export default function EngagementPage() {
  const [filter, setFilter] = useState('all');
  const [replyText, setReplyText] = useState({});

  const filtered = filter === 'all' ? comments : comments.filter((c) => c.status === filter);

  const handleReply = (commentId) => {
    if (!replyText[commentId]?.trim()) return;
    toast.success('Reply sent!');
    setReplyText((prev) => ({ ...prev, [commentId]: '' }));
  };

  const getSentimentBar = (score) => {
    const percent = ((score + 1) / 2) * 100;
    const color = score > 0.5 ? 'bg-emerald-400' : score > 0 ? 'bg-amber-400' : score > -0.5 ? 'bg-orange-400' : 'bg-red-400';
    return (
      <div className="w-20 h-1.5 bg-surface-700 rounded-full overflow-hidden">
        <div className={`h-full ${color} rounded-full transition-all`} style={{ width: `${percent}%` }} />
      </div>
    );
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold flex items-center gap-2">
            <MessageSquare className="w-6 h-6 text-pink-400" />
            Engagement Hub
          </h1>
          <p className="text-surface-200/50 mt-1">Manage comments, DMs, and community interactions</p>
        </div>
        <div className="flex items-center gap-3">
          <div className="flex items-center gap-1 text-xs">
            <Filter className="w-3.5 h-3.5 text-surface-200/40" />
            {['all', 'pending', 'escalated', 'auto_replied'].map((f) => (
              <button
                key={f}
                onClick={() => setFilter(f)}
                className={`px-3 py-1.5 rounded-lg font-medium transition-all capitalize ${
                  filter === f
                    ? 'bg-primary-500/20 text-primary-400 ring-1 ring-primary-500/30'
                    : 'bg-surface-800/50 text-surface-200/50 hover:text-white'
                }`}
              >
                {f === 'auto_replied' ? 'Auto-Replied' : f}
              </button>
            ))}
          </div>
        </div>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-4 gap-4">
        {[
          { label: 'Total Comments', value: comments.length, color: 'text-white' },
          { label: 'Pending', value: comments.filter((c) => c.status === 'pending').length, color: 'text-amber-400' },
          { label: 'Escalated', value: comments.filter((c) => c.status === 'escalated').length, color: 'text-red-400' },
          { label: 'Auto-Replied', value: comments.filter((c) => c.status === 'auto_replied').length, color: 'text-emerald-400' },
        ].map(({ label, value, color }) => (
          <div key={label} className="glass rounded-xl p-4 text-center">
            <p className={`text-2xl font-bold ${color}`}>{value}</p>
            <p className="text-xs text-surface-200/40 mt-1">{label}</p>
          </div>
        ))}
      </div>

      {/* Comments List */}
      <div className="space-y-3">
        {filtered.map((comment) => {
          const IntentIcon = intentIcons[comment.intent] || HelpCircle;
          const cfg = statusConfig[comment.status] || statusConfig.pending;

          return (
            <div key={comment.id} className="glass rounded-2xl p-5 hover:glow-sm transition-all">
              <div className="flex items-start gap-4">
                {/* Avatar */}
                <div className="w-10 h-10 rounded-full bg-gradient-to-br from-primary-500 to-accent-500 flex items-center justify-center text-white font-semibold text-sm flex-shrink-0">
                  {comment.avatar}
                </div>

                <div className="flex-1 min-w-0">
                  {/* Header */}
                  <div className="flex items-center gap-3 mb-2 flex-wrap">
                    <span className="font-semibold text-sm text-white">{comment.user}</span>
                    <span className="text-xs text-surface-200/30">{comment.platform}</span>
                    <span className="text-xs text-surface-200/30">{comment.time}</span>
                    <span className={`text-xs px-2 py-0.5 rounded-full font-medium ${cfg.color}`}>
                      {cfg.label}
                    </span>
                  </div>

                  {/* Comment Text */}
                  <p className="text-sm text-surface-200/80 mb-3">{comment.text}</p>

                  {/* Metadata */}
                  <div className="flex items-center gap-5 text-xs mb-3">
                    <span className="flex items-center gap-1.5 text-surface-200/40">
                      Sentiment: {getSentimentBar(comment.sentiment)}
                      <span className={comment.sentiment > 0 ? 'text-emerald-400' : 'text-red-400'}>
                        {comment.sentiment.toFixed(2)}
                      </span>
                    </span>
                    <span className="flex items-center gap-1 text-surface-200/40 capitalize">
                      <IntentIcon className="w-3 h-3" /> {comment.intent}
                    </span>
                    <span className="text-surface-200/40 capitalize">
                      😊 {comment.emotion}
                    </span>
                  </div>

                  {/* Auto Response */}
                  {comment.response && (
                    <div className="bg-primary-500/5 border border-primary-500/15 rounded-xl p-3 mb-3">
                      <p className="text-xs text-surface-200/40 mb-1">Auto-Reply:</p>
                      <p className="text-sm text-primary-300">{comment.response}</p>
                    </div>
                  )}

                  {/* Reply Input for pending/escalated */}
                  {(comment.status === 'pending' || comment.status === 'escalated') && (
                    <div className="flex gap-2">
                      <input
                        type="text"
                        placeholder="Type a reply..."
                        value={replyText[comment.id] || ''}
                        onChange={(e) => setReplyText((prev) => ({ ...prev, [comment.id]: e.target.value }))}
                        className="flex-1 bg-surface-800/60 border border-surface-700/50 rounded-xl px-4 py-2 text-sm text-white placeholder:text-surface-200/30 focus:outline-none focus:ring-2 focus:ring-primary-500/30"
                      />
                      <button
                        onClick={() => handleReply(comment.id)}
                        className="px-4 py-2 rounded-xl bg-primary-500 hover:bg-primary-400 text-white text-sm font-medium transition-colors flex items-center gap-1.5"
                      >
                        <Send className="w-3.5 h-3.5" /> Reply
                      </button>
                    </div>
                  )}
                </div>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}
