import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import LoginPage from "./pages/LoginPage";
import Register from "./pages/Register";
import Dashboard from "./pages/Dashboard";
import OAuthSuccess from "./pages/OAuthSuccess";
import ToolManagement from "./pages/ToolManagement";
import UserManagement from "./pages/UserManagement";
import ChunkUploader from "./pages/ChunkUploaderTest";
import LandingPage from "./pages/LandingPage";

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<LandingPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<Register />} />
        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="/toolmanagement" element={<ToolManagement />} />
        <Route path="/user-management" element={<UserManagement />} />
        <Route path="/oauth-success" element={<OAuthSuccess />} />
        <Route path="/ChunkUploader" element={<ChunkUploader/>}/>
      </Routes>
    </Router>
  );
}

export default App;
