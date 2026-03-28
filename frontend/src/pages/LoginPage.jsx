import { useNavigate } from 'react-router-dom';
import { GoogleLogin } from '@react-oauth/google';
import { jwtDecode } from 'jwt-decode';
import { useAuth } from '../context/AuthContext';
import { Sparkles, Zap, Shield, BarChart3, MessageSquare, Calendar } from 'lucide-react';
import toast from 'react-hot-toast';
import { authApi } from '../services/api';

const features = [
  { icon: Zap, title: 'AI Content Generation', desc: 'Create engaging posts with GPT-4' },
  { icon: Calendar, title: 'Smart Scheduling', desc: 'AI-optimized posting times' },
  { icon: MessageSquare, title: 'Auto Engagement', desc: 'Context-aware auto-replies' },
  { icon: BarChart3, title: 'Deep Analytics', desc: 'Cross-platform insights' },
  { icon: Shield, title: 'Brand Safety', desc: 'Approval workflows & compliance' },
];

export default function LoginPage() {
  const { login, isAuthenticated } = useAuth();
  const navigate = useNavigate();

  if (isAuthenticated) {
    navigate('/');
    return null;
  }

  const handleGoogleSuccess = async (credentialResponse) => {
    try {
      const { data } = await authApi.googleLogin(credentialResponse.credential);
      login({
        ...data.user,
        token: data.token,
      });
      toast.success(`Welcome, ${data.user.name}!`);
      navigate('/');
    } catch (err) {
      console.error(err);
      toast.error('Login failed. Please try again.');
    }
  };

  const handleDemoLogin = () => {
    login({
      id: 'demo-1',
      name: 'Demo User',
      email: 'demo@socialmediaagent.ai',
      picture: null,
      token: 'demo-token',
    });
    toast.success('Welcome to the demo!');
    navigate('/');
  };

  return (
    <div className="min-h-screen bg-surface-950 flex overflow-hidden">
      {/* Left side - Hero */}
      <div className="hidden lg:flex lg:w-1/2 relative flex-col justify-center px-16">
        {/* Background glow effects */}
        <div className="absolute top-1/4 left-1/4 w-96 h-96 bg-primary-500/10 rounded-full blur-[120px]" />
        <div className="absolute bottom-1/4 right-1/4 w-80 h-80 bg-accent-500/10 rounded-full blur-[100px]" />

        <div className="relative z-10">
          <div className="flex items-center gap-3 mb-8">
            <div className="w-14 h-14 rounded-2xl bg-gradient-to-br from-primary-500 to-accent-500 flex items-center justify-center shadow-2xl shadow-primary-500/25">
              <Sparkles className="w-7 h-7 text-white" />
            </div>
            <div>
              <h1 className="text-3xl font-bold gradient-text">SocialAI</h1>
              <p className="text-surface-200/50 text-sm">Media Agent Platform</p>
            </div>
          </div>

          <h2 className="text-5xl font-bold leading-tight mb-6">
            AI-Powered Social
            <br />
            <span className="gradient-text">Media Management</span>
          </h2>
          <p className="text-lg text-surface-200/60 mb-12 max-w-md">
            Generate, schedule, and optimize content across all platforms with the power of artificial intelligence.
          </p>

          <div className="space-y-4">
            {features.map(({ icon: Icon, title, desc }) => (
              <div key={title} className="flex items-center gap-4 group">
                <div className="w-10 h-10 rounded-xl bg-primary-500/10 flex items-center justify-center group-hover:bg-primary-500/20 transition-colors">
                  <Icon className="w-5 h-5 text-primary-400" />
                </div>
                <div>
                  <h3 className="text-sm font-semibold text-white">{title}</h3>
                  <p className="text-xs text-surface-200/50">{desc}</p>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Right side - Login form */}
      <div className="flex-1 flex items-center justify-center px-8">
        <div className="w-full max-w-md">
          <div className="glass rounded-3xl p-10 glow">
            <div className="text-center mb-8">
              <div className="lg:hidden flex items-center justify-center gap-3 mb-6">
                <div className="w-12 h-12 rounded-2xl bg-gradient-to-br from-primary-500 to-accent-500 flex items-center justify-center">
                  <Sparkles className="w-6 h-6 text-white" />
                </div>
                <h1 className="text-2xl font-bold gradient-text">SocialAI</h1>
              </div>
              <h2 className="text-2xl font-bold text-white mb-2">Welcome Back</h2>
              <p className="text-surface-200/50 text-sm">Sign in to manage your social media empire</p>
            </div>

            {/* Google OAuth */}
            <div className="flex justify-center mb-6">
              <GoogleLogin
                onSuccess={handleGoogleSuccess}
                onError={() => toast.error('Google login failed')}
                theme="filled_black"
                shape="pill"
                size="large"
                text="signin_with"
                width="320"
              />
            </div>

            <div className="flex items-center gap-4 my-6">
              <div className="flex-1 h-px bg-surface-700/50" />
              <span className="text-xs text-surface-200/40 uppercase tracking-wider">or</span>
              <div className="flex-1 h-px bg-surface-700/50" />
            </div>

            {/* Demo Login */}
            <button
              onClick={handleDemoLogin}
              className="w-full py-3 px-6 rounded-xl bg-gradient-to-r from-primary-600 to-primary-500 hover:from-primary-500 hover:to-primary-400 text-white font-semibold text-sm transition-all duration-300 shadow-lg shadow-primary-500/20 hover:shadow-primary-500/30 hover:scale-[1.02] active:scale-[0.98]"
            >
              Try Demo Account
            </button>

            <p className="text-center text-xs text-surface-200/30 mt-6">
              By signing in, you agree to our Terms of Service and Privacy Policy
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}
