import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import { LoginPage } from "./pages/LoginPage";
import { RegisterPage } from "./pages/RegisterPage";
import { ActivationPage } from "./pages/ActivationPage";
import { HomePage } from "./pages/HomePage";
import { palette } from "./styles/palette";

function App() {
  return (
    <>
      {/* Google Font - Outfit */}
      <link
        href="https://fonts.googleapis.com/css2?family=Outfit:wght@400;500;600;700&display=swap"
        rel="stylesheet"
      />

      <Router>
        <div
          style={{
            minHeight: "100vh",
            display: "flex",
            flexDirection: "column",
            background: palette.bg,
            fontFamily: "'Outfit', sans-serif",
          }}
        >
          <Routes>
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />
            <Route path="/activation/:userId" element={<ActivationPage />} />
            <Route path="/home" element={<HomePage />} />
            <Route path="/" element={<Navigate to="/login" replace />} />
          </Routes>
        </div>
      </Router>
    </>
  );
}

export default App;
