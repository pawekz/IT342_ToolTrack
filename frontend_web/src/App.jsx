import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import LoginPage from "./pages/LoginPage";
import Register from "./pages/Register";
import Dashboard from "./pages/Dashboard";
import ToolManagement from "./pages/ToolManagement";
import UserManagement from "./pages/UserManagement";
import ChunkUploader from "./pages/ChunkUploaderTest";
import LandingPage from "./pages/LandingPage";
import SettingsPage from "./pages/SettingsPage";
import { AuthProvider } from "./components/AuthProvider";
import ProtectedRoute from "./components/ProtectedRoute";

function App() {
  return (
    <AuthProvider>
    <Router>
      <Routes>
        <Route path="/" element={<LandingPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<Register />} />

        {/*To access these endpoints the user must be authenticated*/}
        <Route path="/dashboard" element={<ProtectedRoute><Dashboard /></ProtectedRoute>} />
        <Route path="/toolmanagement" element={<ProtectedRoute><ToolManagement /></ProtectedRoute>} />
        <Route path="/user-management" element={<ProtectedRoute><UserManagement /></ProtectedRoute>} />
        <Route path="/settings" element={<ProtectedRoute><SettingsPage /></ProtectedRoute>} />
        <Route path="/ChunkUploader" element={<ProtectedRoute><ChunkUploader/></ProtectedRoute>}/>
      </Routes>
    </Router>
    </AuthProvider>
  );
}

export default App;
