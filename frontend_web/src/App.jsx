import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import LoginPage from "./pages/LoginPage";
import Register from "./pages/Register";
import Dashboard from "./pages/Dashboard";
import OAuthSuccess from "./pages/OAuthSuccess";

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<LoginPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<Register />} />
        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="/oauth-success" element={<OAuthSuccess />} />
      </Routes>
    </Router>
  );
}

export default App;
