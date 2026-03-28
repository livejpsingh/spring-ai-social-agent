import { useState } from 'react';
import { Sparkles, Copy, Send, RefreshCw, Hash, Image, Globe } from 'lucide-react';
import toast from 'react-hot-toast';
import { contentApi } from '../services/api';

const tones = ['PROFESSIONAL', 'CASUAL', 'WITTY', 'INSPIRATIONAL', 'EDUCATIONAL'];
const platforms = ['TWITTER', 'LINKEDIN', 'INSTAGRAM', 'FACEBOOK', 'TIKTOK'];

export default function ContentGeneratorPage() {
  const [prompt, setPrompt] = useState('');
  const [tone, setTone] = useState('PROFESSIONAL');
  const [selectedPlatforms, setSelectedPlatforms] = useState(['TWITTER']);
  const [language, setLanguage] = useState('en');
  const [includeHashtags, setIncludeHashtags] = useState(true);
  const [variants, setVariants] = useState(3);
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState(null);

  const handleGenerate = async () => {
    if (!prompt.trim()) return toast.error('Enter a content prompt');
    setLoading(true);
    try {
      const { data } = await contentApi.generate({
        prompt, tone, language,
        targetPlatforms: selectedPlatforms,
        includeHashtags,
        variantsToGenerate: variants,
        contentType: 'general',
      });
      setResult(data);
      toast.success('Content generated!');
    } catch {
      // Demo fallback
      setResult({
        variants: [
          `🚀 ${prompt} — Discover how our platform revolutionizes social media management with AI-powered insights and automation.`,
          `💡 ${prompt} — Transform your social strategy with intelligent content creation and optimal scheduling.`,
          `✨ ${prompt} — Join thousands of brands using AI to amplify their social media presence.`,
        ],
        brandVoiceScores: [0.92, 0.88, 0.95],
        sentimentScores: [0.85, 0.78, 0.91],
        suggestedHashtags: ['AI', 'SocialMedia', 'Marketing', 'ContentCreation', 'DigitalMarketing'],
        platformAdaptations: {},
      });
      toast.success('Content generated (demo mode)');
    }
    setLoading(false);
  };

  const togglePlatform = (p) => {
    setSelectedPlatforms((prev) =>
      prev.includes(p) ? prev.filter((x) => x !== p) : [...prev, p]
    );
  };

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold flex items-center gap-2">
          <Sparkles className="w-6 h-6 text-primary-400" />
          Content Studio
        </h1>
        <p className="text-surface-200/50 mt-1">Generate AI-powered content for all your platforms</p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-5 gap-6">
        {/* Input Panel */}
        <div className="lg:col-span-2 space-y-5">
          <div className="glass rounded-2xl p-6 space-y-5">
            {/* Prompt */}
            <div>
              <label className="text-sm font-medium text-surface-200/70 mb-2 block">Content Prompt</label>
              <textarea
                value={prompt}
                onChange={(e) => setPrompt(e.target.value)}
                placeholder="Describe what you want to post about... e.g., 'Announce our new AI feature launch'"
                rows={4}
                className="w-full bg-surface-800/60 border border-surface-700/50 rounded-xl px-4 py-3 text-sm text-white placeholder:text-surface-200/30 focus:outline-none focus:ring-2 focus:ring-primary-500/30 resize-none"
              />
            </div>

            {/* Tone */}
            <div>
              <label className="text-sm font-medium text-surface-200/70 mb-2 block">Tone</label>
              <div className="flex flex-wrap gap-2">
                {tones.map((t) => (
                  <button
                    key={t}
                    onClick={() => setTone(t)}
                    className={`px-3 py-1.5 rounded-lg text-xs font-medium transition-all capitalize ${
                      tone === t
                        ? 'bg-primary-500/20 text-primary-400 ring-1 ring-primary-500/30'
                        : 'bg-surface-800/50 text-surface-200/50 hover:text-white'
                    }`}
                  >
                    {t.toLowerCase()}
                  </button>
                ))}
              </div>
            </div>

            {/* Platforms */}
            <div>
              <label className="text-sm font-medium text-surface-200/70 mb-2 block">Target Platforms</label>
              <div className="flex flex-wrap gap-2">
                {platforms.map((p) => (
                  <button
                    key={p}
                    onClick={() => togglePlatform(p)}
                    className={`px-3 py-1.5 rounded-lg text-xs font-medium transition-all capitalize ${
                      selectedPlatforms.includes(p)
                        ? 'bg-cyan-500/20 text-cyan-400 ring-1 ring-cyan-500/30'
                        : 'bg-surface-800/50 text-surface-200/50 hover:text-white'
                    }`}
                  >
                    {p.toLowerCase()}
                  </button>
                ))}
              </div>
            </div>

            {/* Options */}
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="text-sm font-medium text-surface-200/70 mb-2 block">
                  <Globe className="w-3.5 h-3.5 inline mr-1" />Language
                </label>
                <select
                  value={language}
                  onChange={(e) => setLanguage(e.target.value)}
                  className="w-full bg-surface-800/60 border border-surface-700/50 rounded-xl px-3 py-2.5 text-sm text-white focus:outline-none focus:ring-2 focus:ring-primary-500/30"
                >
                  <option value="en">English</option>
                  <option value="es">Spanish</option>
                  <option value="fr">French</option>
                  <option value="de">German</option>
                  <option value="hi">Hindi</option>
                  <option value="ja">Japanese</option>
                </select>
              </div>
              <div>
                <label className="text-sm font-medium text-surface-200/70 mb-2 block">Variants</label>
                <select
                  value={variants}
                  onChange={(e) => setVariants(Number(e.target.value))}
                  className="w-full bg-surface-800/60 border border-surface-700/50 rounded-xl px-3 py-2.5 text-sm text-white focus:outline-none focus:ring-2 focus:ring-primary-500/30"
                >
                  {[1, 2, 3, 5].map((n) => (
                    <option key={n} value={n}>{n} variant{n > 1 ? 's' : ''}</option>
                  ))}
                </select>
              </div>
            </div>

            {/* Toggles */}
            <label className="flex items-center gap-3 cursor-pointer group">
              <input
                type="checkbox"
                checked={includeHashtags}
                onChange={(e) => setIncludeHashtags(e.target.checked)}
                className="sr-only peer"
              />
              <div className="w-9 h-5 bg-surface-700 rounded-full peer-checked:bg-primary-500 transition-colors relative after:content-[''] after:absolute after:top-0.5 after:left-0.5 after:w-4 after:h-4 after:bg-white after:rounded-full after:transition-transform peer-checked:after:translate-x-4" />
              <span className="text-sm text-surface-200/70 group-hover:text-white transition-colors flex items-center gap-1.5">
                <Hash className="w-3.5 h-3.5" /> Include Hashtags
              </span>
            </label>

            {/* Generate Button */}
            <button
              onClick={handleGenerate}
              disabled={loading || !prompt.trim()}
              className="w-full py-3.5 px-6 rounded-xl bg-gradient-to-r from-primary-600 to-accent-500 hover:from-primary-500 hover:to-accent-400 text-white font-semibold text-sm transition-all duration-300 shadow-lg shadow-primary-500/20 hover:shadow-primary-500/30 disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2"
            >
              {loading ? (
                <RefreshCw className="w-4 h-4 animate-spin" />
              ) : (
                <Sparkles className="w-4 h-4" />
              )}
              {loading ? 'Generating...' : 'Generate Content'}
            </button>
          </div>
        </div>

        {/* Results Panel */}
        <div className="lg:col-span-3 space-y-4">
          {result?.variants?.map((variant, i) => (
            <div key={i} className="glass rounded-2xl p-6 hover:glow-sm transition-all group">
              <div className="flex items-start justify-between mb-3">
                <span className="text-xs font-semibold text-primary-400 bg-primary-500/10 px-2.5 py-1 rounded-lg">
                  Variant {i + 1}
                </span>
                <div className="flex gap-2">
                  <button
                    onClick={() => { navigator.clipboard.writeText(variant); toast.success('Copied!'); }}
                    className="p-2 rounded-lg bg-surface-800/50 hover:bg-surface-700 transition-colors"
                  >
                    <Copy className="w-3.5 h-3.5 text-surface-200/50" />
                  </button>
                  <button className="p-2 rounded-lg bg-surface-800/50 hover:bg-surface-700 transition-colors">
                    <Send className="w-3.5 h-3.5 text-surface-200/50" />
                  </button>
                </div>
              </div>
              <p className="text-sm text-white leading-relaxed mb-4">{variant}</p>
              <div className="flex items-center gap-4 text-xs">
                {result.brandVoiceScores?.[i] != null && (
                  <span className="flex items-center gap-1.5">
                    <div className={`w-2 h-2 rounded-full ${result.brandVoiceScores[i] > 0.8 ? 'bg-emerald-400' : 'bg-amber-400'}`} />
                    <span className="text-surface-200/50">Brand: {(result.brandVoiceScores[i] * 100).toFixed(0)}%</span>
                  </span>
                )}
                {result.sentimentScores?.[i] != null && (
                  <span className="flex items-center gap-1.5">
                    <div className={`w-2 h-2 rounded-full ${result.sentimentScores[i] > 0.5 ? 'bg-emerald-400' : 'bg-red-400'}`} />
                    <span className="text-surface-200/50">Sentiment: {(result.sentimentScores[i] * 100).toFixed(0)}%</span>
                  </span>
                )}
              </div>
            </div>
          ))}

          {result?.suggestedHashtags?.length > 0 && (
            <div className="glass rounded-2xl p-6">
              <h3 className="text-sm font-semibold mb-3 flex items-center gap-2">
                <Hash className="w-4 h-4 text-cyan-400" /> Suggested Hashtags
              </h3>
              <div className="flex flex-wrap gap-2">
                {result.suggestedHashtags.map((tag) => (
                  <span
                    key={tag}
                    onClick={() => { navigator.clipboard.writeText(`#${tag}`); toast.success(`Copied #${tag}`); }}
                    className="px-3 py-1.5 rounded-lg bg-cyan-500/10 text-cyan-400 text-xs font-medium cursor-pointer hover:bg-cyan-500/20 transition-colors"
                  >
                    #{tag}
                  </span>
                ))}
              </div>
            </div>
          )}

          {!result && (
            <div className="glass rounded-2xl p-16 flex flex-col items-center justify-center text-center">
              <div className="w-20 h-20 rounded-2xl bg-primary-500/10 flex items-center justify-center mb-4">
                <Sparkles className="w-10 h-10 text-primary-400/50" />
              </div>
              <h3 className="text-lg font-semibold text-surface-200/50 mb-1">Ready to create</h3>
              <p className="text-sm text-surface-200/30">Enter a prompt and click Generate to see AI-powered content variants</p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
