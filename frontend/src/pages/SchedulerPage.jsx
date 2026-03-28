import { useState } from 'react';
import { Calendar, Clock, Zap, Plus, ChevronLeft, ChevronRight } from 'lucide-react';

const daysOfWeek = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];
const hours = Array.from({ length: 24 }, (_, i) => `${String(i).padStart(2, '0')}:00`);

const scheduledPosts = [
  { id: 1, day: 1, hour: 9, text: 'Product update announcement', platform: 'Twitter', color: 'bg-blue-500' },
  { id: 2, day: 2, hour: 14, text: 'Behind the scenes content', platform: 'Instagram', color: 'bg-pink-500' },
  { id: 3, day: 3, hour: 10, text: 'Industry insights article', platform: 'LinkedIn', color: 'bg-sky-600' },
  { id: 4, day: 4, hour: 16, text: 'Weekend flash sale promo', platform: 'Facebook', color: 'bg-blue-600' },
  { id: 5, day: 5, hour: 11, text: 'Customer success story', platform: 'Twitter', color: 'bg-blue-500' },
  { id: 6, day: 0, hour: 15, text: 'Weekly recap thread', platform: 'Twitter', color: 'bg-blue-500' },
];

const optimalHours = [9, 10, 11, 14, 15, 16];

export default function SchedulerPage() {
  const [weekOffset, setWeekOffset] = useState(0);

  const getWeekDates = () => {
    const now = new Date();
    const start = new Date(now);
    start.setDate(now.getDate() - now.getDay() + weekOffset * 7);
    return Array.from({ length: 7 }, (_, i) => {
      const d = new Date(start);
      d.setDate(start.getDate() + i);
      return d;
    });
  };

  const dates = getWeekDates();

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold flex items-center gap-2">
            <Calendar className="w-6 h-6 text-cyan-400" />
            Content Scheduler
          </h1>
          <p className="text-surface-200/50 mt-1">Schedule and manage your content calendar</p>
        </div>
        <div className="flex items-center gap-3">
          <button className="px-4 py-2.5 rounded-xl bg-gradient-to-r from-primary-600 to-primary-500 text-white text-sm font-medium flex items-center gap-2 hover:shadow-lg hover:shadow-primary-500/20 transition-all">
            <Plus className="w-4 h-4" /> Schedule Post
          </button>
        </div>
      </div>

      {/* Week Navigation */}
      <div className="glass rounded-2xl p-4 flex items-center justify-between">
        <button onClick={() => setWeekOffset(weekOffset - 1)} className="p-2 rounded-xl bg-surface-800/60 hover:bg-surface-700 transition-colors">
          <ChevronLeft className="w-5 h-5" />
        </button>
        <h2 className="text-lg font-semibold">
          {dates[0].toLocaleDateString('en-US', { month: 'short', day: 'numeric' })} —{' '}
          {dates[6].toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' })}
        </h2>
        <div className="flex items-center gap-2">
          <button onClick={() => setWeekOffset(0)} className="px-3 py-1.5 rounded-lg bg-surface-800/50 text-xs text-surface-200/50 hover:text-white transition-colors">
            Today
          </button>
          <button onClick={() => setWeekOffset(weekOffset + 1)} className="p-2 rounded-xl bg-surface-800/60 hover:bg-surface-700 transition-colors">
            <ChevronRight className="w-5 h-5" />
          </button>
        </div>
      </div>

      {/* Calendar Grid */}
      <div className="glass rounded-2xl overflow-hidden">
        {/* Day Headers */}
        <div className="grid grid-cols-8 border-b border-surface-700/50">
          <div className="p-3 text-xs text-surface-200/30 font-medium flex items-center justify-center">
            <Clock className="w-3.5 h-3.5" />
          </div>
          {dates.map((date, i) => {
            const isToday = date.toDateString() === new Date().toDateString();
            return (
              <div key={i} className={`p-3 text-center border-l border-surface-700/30 ${isToday ? 'bg-primary-500/5' : ''}`}>
                <p className="text-xs text-surface-200/40">{daysOfWeek[i]}</p>
                <p className={`text-lg font-bold mt-0.5 ${isToday ? 'text-primary-400' : 'text-white'}`}>
                  {date.getDate()}
                </p>
              </div>
            );
          })}
        </div>

        {/* Time Slots */}
        <div className="max-h-[500px] overflow-auto">
          {hours.filter((_, i) => i >= 6 && i <= 22).map((hour, idx) => {
            const hourNum = idx + 6;
            const isOptimal = optimalHours.includes(hourNum);
            return (
              <div key={hour} className="grid grid-cols-8 border-b border-surface-700/20 group">
                <div className="p-2 text-xs text-surface-200/30 text-center flex items-center justify-center">
                  {hour}
                  {isOptimal && <Zap className="w-3 h-3 text-amber-400 ml-1" />}
                </div>
                {Array.from({ length: 7 }, (_, dayIdx) => {
                  const post = scheduledPosts.find(
                    (p) => p.day === dayIdx && p.hour === hourNum
                  );
                  return (
                    <div
                      key={dayIdx}
                      className={`p-1 border-l border-surface-700/20 min-h-[48px] ${
                        isOptimal ? 'bg-amber-500/3' : ''
                      } hover:bg-surface-800/30 transition-colors cursor-pointer`}
                    >
                      {post && (
                        <div className={`${post.color} rounded-lg p-2 text-xs text-white shadow-md hover:scale-105 transition-transform cursor-pointer`}>
                          <p className="font-medium truncate">{post.text}</p>
                          <p className="opacity-70 text-[10px] mt-0.5">{post.platform}</p>
                        </div>
                      )}
                    </div>
                  );
                })}
              </div>
            );
          })}
        </div>
      </div>

      {/* Legend */}
      <div className="flex items-center gap-6 text-xs text-surface-200/40">
        <span className="flex items-center gap-1.5">
          <Zap className="w-3 h-3 text-amber-400" /> AI-suggested optimal time
        </span>
        <span className="flex items-center gap-1.5">
          <div className="w-3 h-3 rounded bg-blue-500" /> Twitter
        </span>
        <span className="flex items-center gap-1.5">
          <div className="w-3 h-3 rounded bg-pink-500" /> Instagram
        </span>
        <span className="flex items-center gap-1.5">
          <div className="w-3 h-3 rounded bg-sky-600" /> LinkedIn
        </span>
        <span className="flex items-center gap-1.5">
          <div className="w-3 h-3 rounded bg-blue-600" /> Facebook
        </span>
      </div>
    </div>
  );
}
