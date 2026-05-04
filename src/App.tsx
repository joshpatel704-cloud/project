import React, { useState, useEffect } from 'react';
import { 
  AreaChart, 
  Area, 
  XAxis, 
  YAxis, 
  CartesianGrid, 
  Tooltip, 
  ResponsiveContainer 
} from 'recharts';
import { Home, ArrowRightLeft, Globe, HelpCircle } from 'lucide-react';
import { clsx, type ClassValue } from 'clsx';
import { twMerge } from 'tailwind-merge';

function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}

// --- Components ---

const GlassCard = ({ children, className }: { children: React.ReactNode; className?: string }) => (
  <div className={cn(
    "bg-white/[0.03] backdrop-blur-[12px] border border-white/10 rounded-[24px] overflow-hidden shadow-[0_8px_32px_0_rgba(0,0,0,0.37)]",
    className
  )}>
    {children}
  </div>
);

// --- Screens ---

const HomeScreen = () => {
  const [amount, setAmount] = useState<string>('1000');
  const [from, setFrom] = useState('USD');
  const [to, setTo] = useState('EUR');
  const [result, setResult] = useState<number | null>(null);
  const [conversionRate, setConversionRate] = useState<number | null>(null);
  const [loading, setLoading] = useState(false);
  const [historyLoading, setHistoryLoading] = useState(false);
  const [timeFilter, setTimeFilter] = useState('1M');
  const [chartData, setChartData] = useState<number[]>([]);
  const [historyData, setHistoryData] = useState<{date: string, value: number}[]>([]);
  const [hoverIndex, setHoverIndex] = useState<number | null>(null);

  const handleConvert = async () => {
    if (!amount || isNaN(parseFloat(amount))) return;
    setLoading(true);
    try {
      const response = await fetch('/api/v1/currency/convert', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ from, to, amount: parseFloat(amount) })
      });
      const data = await response.json();
      setResult(data.result);
      setConversionRate(data.rate);
    } catch (error) {
      console.error("Conversion failed", error);
    } finally {
      setLoading(false);
    }
  };

  const fetchHistory = async () => {
    setHistoryLoading(true);
    try {
      const days = timeFilter === '1W' ? 7 : timeFilter === '1M' ? 30 : timeFilter === '1Y' ? 365 : 1825;
      const res = await fetch(`/api/v1/currency/historical-rates?base=${from}&symbol=${to}&days=${days}`);
      if (!res.ok) throw new Error("API responded with error");
      const data = await res.json();
      
      // Sort keys chronologically
      const sortedKeys = Object.keys(data).sort();
      console.log(`[CHART] Raw data keys: ${sortedKeys.length}`);
      
      const points = sortedKeys
        .map(key => ({
          date: key,
          value: data[key][to]
        }))
        .filter(p => typeof p.value === 'number' && !isNaN(p.value));
      
      console.log(`[CHART] Filtered points: ${points.length}`);
      
      setChartData(points.map(p => p.value));
      setHistoryData(points); // New state for Recharts
    } catch (e) {
      console.error("[CHART] History fetch failed", e);
      setChartData([]);
      setHistoryData([]);
    } finally {
      setHistoryLoading(false);
    }
  };

  useEffect(() => {
    handleConvert();
  }, []);

  useEffect(() => {
    fetchHistory();
  }, [timeFilter, from, to]);

  const generatePath = () => {
    if (chartData.length === 0) return "";
    const min = Math.min(...chartData);
    const max = Math.max(...chartData);
    const range = max - min || 1;
    const width = 400;
    const height = 150;
    const padding = 20;
    const chartHeight = height - padding * 2;
    const step = chartData.length > 1 ? width / (chartData.length - 1) : 0;
    
    const points = chartData.map((val, i) => {
      const x = i * step;
      const y = height - ((val - min) / range * chartHeight + padding);
      return { x, y };
    });

    if (points.length < 2) return "";

    // Cubic Bezier smoothing
    let d = `M ${points[0].x} ${points[0].y}`;
    for (let i = 0; i < points.length - 1; i++) {
      const curr = points[i];
      const next = points[i + 1];
      const cp1x = curr.x + (next.x - curr.x) / 2;
      const cp1y = curr.y;
      const cp2x = curr.x + (next.x - curr.x) / 2;
      const cp2y = next.y;
      d += ` C ${cp1x} ${cp1y}, ${cp2x} ${cp2y}, ${next.x} ${next.y}`;
    }
    return d;
  };

  const calculateChange = () => {
    if (chartData.length < 2) return null;
    const first = chartData[0];
    const last = chartData[chartData.length - 1];
    const diff = ((last - first) / first) * 100;
    return diff;
  };

  const change = calculateChange();

  return (
    <div className="space-y-6 pb-32 pt-8">
      {/* Header */}
      <div className="flex justify-between items-start mb-8">
        <div className="flex flex-col gap-1">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-xl bg-gradient-to-br from-[#3b82f6] to-[#2563eb] flex items-center justify-center text-white font-bold text-2xl shadow-[0_0_20px_rgba(59,130,246,0.3)]">
              R
            </div>
            <h1 className="text-2xl font-bold tracking-tight text-white">RateXpro</h1>
          </div>
          <p className="text-[10px] uppercase tracking-[2px] text-white/40 font-bold ml-[52px]">Real-time currency intelligence</p>
        </div>
        <button 
          onClick={handleConvert}
          className="flex items-center gap-2 px-5 py-2.5 bg-white/5 border border-white/10 rounded-xl text-white/60 hover:text-white hover:bg-white/10 transition-all text-xs font-bold uppercase tracking-wider"
        >
          <ArrowRightLeft size={14} />
          Sync Rates
        </button>
      </div>

      <div className="grid grid-cols-1 gap-6">
        {/* Main Converter Card */}
        <GlassCard className="p-8">
          <div className="grid grid-cols-1 md:grid-cols-12 gap-8 items-start">
            <div className="md:col-span-5 space-y-6">
              <div>
                <label className="text-[10px] uppercase tracking-widest text-white/40 font-bold mb-2 block">Amount Configuration</label>
                <input 
                  type="number" 
                  value={amount}
                  onChange={(e) => setAmount(e.target.value)}
                  className="w-full bg-black/30 border border-white/10 rounded-2xl p-5 text-3xl font-bold text-white focus:outline-none focus:border-blue-500/50 transition-colors"
                  placeholder="0.00"
                />
              </div>

              <div className="flex flex-col items-center gap-3">
                <div className="w-full">
                  <label className="text-[10px] uppercase tracking-widest text-white/40 font-bold mb-2 block">From</label>
                  <select 
                    value={from}
                    onChange={(e) => setFrom(e.target.value)}
                    className="w-full bg-black/30 border border-white/10 rounded-2xl p-4 text-white font-medium focus:outline-none appearance-none cursor-pointer hover:bg-white/[0.05] transition-colors"
                  >
                    {[
                      { code: 'USD', name: 'US Dollar' },
                      { code: 'EUR', name: 'Euro' },
                      { code: 'INR', name: 'Indian Rupee' },
                      { code: 'GBP', name: 'British Pound' },
                      { code: 'JPY', name: 'Japanese Yen' },
                      { code: 'AUD', name: 'Australian Dollar' },
                      { code: 'CAD', name: 'Canadian Dollar' },
                      { code: 'CNY', name: 'Chinese Yuan' },
                      { code: 'CHF', name: 'Swiss Franc' },
                      { code: 'AED', name: 'UAE Dirham' },
                      { code: 'ZAR', name: 'South African Rand' },
                      { code: 'SGD', name: 'Singapore Dollar' },
                      { code: 'NZD', name: 'New Zealand Dollar' },
                      { code: 'RUB', name: 'Russian Ruble' },
                      { code: 'BRL', name: 'Brazilian Real' },
                      { code: 'HKD', name: 'Hong Kong Dollar' },
                      { code: 'KRW', name: 'South Korean Won' },
                    ].map(c => (
                      <option key={c.code} value={c.code}>{c.code} - {c.name}</option>
                    ))}
                  </select>
                </div>

                <div className="relative z-10 flex items-center justify-center">
                  <div 
                    onClick={() => {
                      const temp = from;
                      setFrom(to);
                      setTo(temp);
                    }}
                    className="w-11 h-11 bg-gradient-to-br from-[#3b82f6] to-[#2563eb] rounded-full flex items-center justify-center text-white shadow-[0_4px_15px_rgba(59,130,246,0.5)] cursor-pointer hover:scale-110 active:scale-95 transition-all z-20"
                  >
                    <ArrowRightLeft size={20} className="rotate-90" />
                  </div>
                </div>

                <div className="w-full">
                  <label className="text-[10px] uppercase tracking-widest text-white/40 font-bold mb-2 block">To</label>
                  <select 
                    value={to}
                    onChange={(e) => setTo(e.target.value)}
                    className="w-full bg-black/30 border border-white/10 rounded-2xl p-4 text-white font-medium focus:outline-none appearance-none cursor-pointer hover:bg-white/[0.05] transition-colors"
                  >
                    {[
                      { code: 'USD', name: 'US Dollar' },
                      { code: 'EUR', name: 'Euro' },
                      { code: 'INR', name: 'Indian Rupee' },
                      { code: 'GBP', name: 'British Pound' },
                      { code: 'JPY', name: 'Japanese Yen' },
                      { code: 'AUD', name: 'Australian Dollar' },
                      { code: 'CAD', name: 'Canadian Dollar' },
                      { code: 'CNY', name: 'Chinese Yuan' },
                      { code: 'CHF', name: 'Swiss Franc' },
                      { code: 'AED', name: 'UAE Dirham' },
                      { code: 'ZAR', name: 'South African Rand' },
                      { code: 'SGD', name: 'Singapore Dollar' },
                      { code: 'NZD', name: 'New Zealand Dollar' },
                      { code: 'RUB', name: 'Russian Ruble' },
                      { code: 'BRL', name: 'Brazilian Real' },
                      { code: 'HKD', name: 'Hong Kong Dollar' },
                      { code: 'KRW', name: 'South Korean Won' },
                    ].map(c => (
                      <option key={c.code} value={c.code}>{c.code} - {c.name}</option>
                    ))}
                  </select>
                </div>
              </div>
            </div>

            <div className="md:col-span-7 flex flex-col justify-center h-full gap-8 border-l border-white/5 pl-0 md:pl-8">
              <div className="text-center md:text-left">
                {loading ? (
                  <div className="space-y-2">
                    <div className="h-4 w-24 bg-white/5 animate-pulse rounded" />
                    <div className="h-12 w-64 bg-white/5 animate-pulse rounded-xl" />
                  </div>
                ) : (
                  <>
                    <p className="text-[10px] uppercase tracking-widest text-white/40 mb-2 font-bold">Lumina Market Estimate</p>
                    <div className="flex items-baseline gap-3">
                      <span className="text-5xl font-bold text-white tracking-tight">{result?.toLocaleString(undefined, { minimumFractionDigits: 2 })}</span>
                      <span className="text-2xl font-bold text-blue-500 uppercase">{to}</span>
                    </div>
                    {conversionRate && (
                      <p className="text-xs text-white/30 mt-2 font-mono italic">
                        1 {from} = {conversionRate.toFixed(4)} {to}
                      </p>
                    )}
                  </>
                )}
              </div>
              
              <button 
                onClick={handleConvert}
                disabled={loading}
                className="w-full px-12 py-5 bg-gradient-to-r from-[#3b82f6] to-[#2563eb] text-white font-bold rounded-2xl hover:brightness-110 active:scale-[0.98] disabled:opacity-50 transition-all shadow-[0_0_30px_rgba(59,130,246,0.4)] tracking-[2px] text-sm uppercase"
              >
                {loading ? 'Processing Transaction...' : 'COMPUTE EXCHANGE'}
              </button>
            </div>
          </div>
        </GlassCard>

        {/* Chart Section */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <GlassCard className="lg:col-span-2 p-8">
            <div className="flex flex-col gap-6 mb-8">
              <div className="flex justify-between items-start">
                <div className="space-y-1">
                  <div className="flex items-center gap-3">
                    <h3 className="text-xl font-bold text-white leading-none">{from} to {to} Chart</h3>
                    {change !== null && (
                      <span className={cn(
                        "px-2 py-1 rounded-full text-[10px] font-bold",
                        change >= 0 ? "bg-green-500/20 text-green-400" : "bg-red-500/20 text-red-400"
                      )}>
                        {change >= 0 ? '+' : ''}{change.toFixed(2)}%
                      </span>
                    )}
                  </div>
                  <p className="text-[10px] font-bold text-white/30 tracking-widest uppercase">Real-time market analysis for {from} / {to}</p>
                </div>
                <div className="flex gap-1 bg-black/20 p-1 rounded-xl border border-white/5">
                  {['1W', '1M', '1Y', '5Y'].map((t) => (
                    <button 
                      key={t}
                      onClick={() => setTimeFilter(t)}
                      className={cn(
                        "px-3 py-1.5 rounded-lg text-[10px] font-bold transition-all",
                        timeFilter === t ? "bg-blue-500 text-white shadow-lg" : "text-white/40 hover:text-white/60"
                      )}
                    >
                      {t}
                    </button>
                  ))}
                </div>
              </div>
            </div>
            
            <div className="h-64 w-full relative group">
              {historyLoading && (
                <div className="absolute inset-0 z-30 flex items-center justify-center bg-[#0b0e14]/70 rounded-xl backdrop-blur-sm">
                  <div className="flex flex-col items-center gap-4 text-center">
                    <div className="w-8 h-8 border-2 border-blue-500 border-t-transparent rounded-full animate-spin" />
                    <p className="text-[10px] font-bold text-white/40 uppercase tracking-[2px]">Synthesizing Historical Vectors...</p>
                  </div>
                </div>
              )}

              {!historyLoading && historyData.length === 0 && (
                <div className="absolute inset-0 z-20 flex items-center justify-center bg-[#05070a]/80 rounded-2xl border-2 border-white/5 border-dashed shadow-2xl backdrop-blur-md">
                  <div className="text-center px-6">
                    <div className="w-16 h-16 bg-white/5 rounded-full flex items-center justify-center mx-auto mb-4 border border-white/10">
                      <Globe size={32} className="text-white/40" />
                    </div>
                    <p className="text-[11px] font-medium uppercase tracking-[3px] text-white/50 leading-relaxed max-w-[200px] mx-auto">
                      No Historical Data Available For This Pair
                    </p>
                  </div>
                </div>
              )}
              
              <ResponsiveContainer width="100%" height="100%">
                <AreaChart data={historyData}>
                  <defs>
                    <linearGradient id="colorVal" x1="0" y1="0" x2="0" y2="1">
                      <stop offset="5%" stopColor="#3b82f6" stopOpacity={0.3}/>
                      <stop offset="95%" stopColor="#3b82f6" stopOpacity={0}/>
                    </linearGradient>
                  </defs>
                  <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="rgba(255,255,255,0.05)" />
                  <XAxis 
                    dataKey="date" 
                    hide 
                  />
                  <YAxis 
                    hide 
                    domain={['auto', 'auto']}
                  />
                  <Tooltip 
                    content={({ active, payload }) => {
                      if (active && payload && payload.length) {
                        return (
                          <div className="bg-[#1a1f3c] border border-white/20 rounded-lg p-2 shadow-2xl backdrop-blur-md min-w-[120px]">
                            <p className="text-[8px] font-black text-white/40 uppercase tracking-[1.5px] mb-0.5">{payload[0].payload.date}</p>
                            <p className="text-[13px] font-bold text-white tabular-nums">
                              {Number(payload[0].value).toFixed(6)} <span className="text-[10px] text-blue-400">{to}</span>
                            </p>
                          </div>
                        );
                      }
                      return null;
                    }}
                  />
                  <Area 
                    type="monotone" 
                    dataKey="value" 
                    stroke="#3b82f6" 
                    strokeWidth={3}
                    fillOpacity={1} 
                    fill="url(#colorVal)" 
                    animationDuration={1000}
                  />
                </AreaChart>
              </ResponsiveContainer>
            </div>
          </GlassCard>

          {/* Live Rates Grid */}
          <GlassCard className="p-8">
            <h3 className="text-sm font-bold uppercase tracking-wider text-white/70 mb-6">Live Quotes</h3>
            <div className="space-y-4">
              {[
                { p: 'GBP/USD', v: '1.2452', c: '+0.12%', t: 'up' },
                { p: 'EUR/USD', v: '1.0612', c: '-0.05%', t: 'down' },
                { p: 'USD/JPY', v: '148.23', c: '+0.45%', t: 'up' },
                { p: 'BTC/USD', v: '64,231', c: '+1.24%', t: 'up' },
                { p: 'ETH/USD', v: '3,452.1', c: '+0.82%', t: 'up' },
              ].map((r, i) => (
                <div key={i} className="flex justify-between items-center p-3 rounded-lg bg-black/20 border border-white/5 hover:border-white/10 transition-colors">
                  <span className="text-xs font-medium text-white/80">{r.p}</span>
                  <div className="text-right">
                    <p className="text-xs font-mono text-white">{r.v}</p>
                    <p className={cn("text-[10px] font-bold", r.t === 'up' ? 'text-green-400' : 'text-red-400')}>{r.c}</p>
                  </div>
                </div>
              ))}
            </div>
          </GlassCard>
        </div>
      </div>
    </div>
  );
};

// --- Main App ---

export default function App() {
  return (
    <div className="min-h-screen bg-[#0b0e14] font-sans selection:bg-[#3b82f6] selection:text-white flex flex-col justify-center py-20">
      {/* Background Decor */}
      <div className="fixed inset-0 overflow-hidden pointer-events-none">
        <div className="absolute inset-0 bg-radial-[circle_at_20%_30%] from-[#1a1f3c] to-[#0b0e14]" />
        <div className="absolute inset-0 bg-[url('https://grainy-gradients.vercel.app/noise.svg')] opacity-[0.15] mix-blend-overlay" />
      </div>

      <main className="relative max-w-5xl mx-auto px-6 w-full">
        <HomeScreen />
      </main>
    </div>
  );
}

