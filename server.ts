import express from "express";
import { createServer as createViteServer } from "vite";
import path from "path";

async function startServer() {
  const app = express();
  const PORT = 3000;

  app.use(express.json());

  // Mock API routes to match the requested backend structure
  // In a real deployment, these would proxy to the Spring Boot service
  app.get("/api/v1/currency/latest-rates", (req, res) => {
    res.json({
      USD: 1.0,
      EUR: 0.9423,
      GBP: 0.8124,
      JPY: 148.2321,
      AUD: 1.5243,
      CAD: 1.3542,
      INR: 83.4521,
      CNY: 7.2341,
      CHF: 0.9123,
      AED: 3.6724,
      ZAR: 18.2341,
      SGD: 1.3542,
      NZD: 1.6542,
      RUB: 92.4321,
      BRL: 5.1234,
      HKD: 7.8234,
      KRW: 1354.21,
      BTC: 0.0000156
    });
  });

  app.post("/api/v1/currency/convert", (req, res) => {
    const { from, to, amount } = req.body;
    const rates: any = { 
      'USD_EUR': 0.9423, 
      'EUR_USD': 1.0612,
      'USD_GBP': 0.8124 ,
      'GBP_USD': 1.2312
    };
    const key = `${from}_${to}`;
    const rate = rates[key] || (0.8 + Math.random() * 0.4);
    
    res.json({
      from,
      to,
      amount,
      result: amount * rate,
      rate,
      timestamp: new Date().toISOString()
    });
  });

  app.get("/api/v1/currency/historical-rates", (req, res) => {
    const { base, symbol, days } = req.query;
    const history: any = {};
    const d = parseInt(days as string) || 30;
    for (let i = 0; i < d; i++) {
        const date = new Date();
        date.setDate(date.getDate() - (d - i));
        const dateStr = date.toISOString().split('T')[0];
        history[dateStr] = { [symbol as string]: 0.92 + Math.random() * 0.05 };
    }
    res.json(history);
  });

  // Vite middleware for development
  if (process.env.NODE_ENV !== "production") {
    const vite = await createViteServer({
      server: { middlewareMode: true },
      appType: "spa",
    });
    app.use(vite.middlewares);
  } else {
    const distPath = path.join(process.cwd(), 'dist');
    app.use(express.static(distPath));
    app.get('*', (req, res) => {
      res.sendFile(path.join(distPath, 'index.html'));
    });
  }

  app.listen(PORT, "0.0.0.0", () => {
    console.log(`Lumina Currency Server running on http://localhost:${PORT}`);
  });
}

startServer();
