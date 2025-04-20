import { useState } from "react";
import { Icon } from "@iconify/react";
import { Camera, Loader2 } from "lucide-react";
import SidebarLayout from "../components/SidebarLayout";

const SettingsPage = () => {
  // Static user data (would be fetched from backend in real implementation)
  const [userData, setUserData] = useState({
    firstName: "Aeron",
    lastName: "Carabuena",
    email: "aeron.carabuena@example.com",
    role: "Admin",
    isGoogle: false,
    image_url: null, // Null for now, will use initials
    is_active: true,
    created_at: "2023-10-15T09:30:00Z", 
    updated_at: "2024-04-01T14:45:00Z"
  });

  // For password change
  const [passwordData, setPasswordData] = useState({
    currentPassword: "",
    newPassword: "",
    confirmPassword: ""
  });

  // UI states
  const [isEditing, setIsEditing] = useState(false);
  const [isChangingPassword, setIsChangingPassword] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [notification, setNotification] = useState({ show: false, message: "", type: "" });

  // Brand color to match sidebar
  const brandColor = "#2EA69E";

  // Handle input changes
  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setUserData(prev => ({ ...prev, [name]: value }));
  };

  // Handle password changes
  const handlePasswordChange = (e) => {
    const { name, value } = e.target;
    setPasswordData(prev => ({ ...prev, [name]: value }));
  };

  // Handle profile update
  const handleUpdateProfile = (e) => {
    e.preventDefault();
    setIsLoading(true);
    
    // Simulate API call
    setTimeout(() => {
      setIsLoading(false);
      setIsEditing(false);
      setNotification({
        show: true,
        message: "Profile updated successfully!",
        type: "success"
      });
      
      // Hide notification after 3 seconds
      setTimeout(() => {
        setNotification({ show: false, message: "", type: "" });
      }, 3000);
    }, 1000);
    
    // In real implementation:
    // const response = await updateUserProfile(userData);
    // if (response.ok) { ... }
  };

  // Handle password update
  const handleUpdatePassword = (e) => {
    e.preventDefault();
    
    // Validate passwords
    if (passwordData.newPassword !== passwordData.confirmPassword) {
      setNotification({
        show: true,
        message: "New passwords don't match!",
        type: "error"
      });
      setTimeout(() => setNotification({ show: false, message: "", type: "" }), 3000);
      return;
    }
    
    setIsLoading(true);
    
    // Simulate API call
    setTimeout(() => {
      setIsLoading(false);
      setIsChangingPassword(false);
      setPasswordData({
        currentPassword: "",
        newPassword: "",
        confirmPassword: ""
      });
      setNotification({
        show: true,
        message: "Password updated successfully!",
        type: "success"
      });
      
      setTimeout(() => {
        setNotification({ show: false, message: "", type: "" });
      }, 3000);
    }, 1000);
    
    // In real implementation:
    // const response = await updateUserPassword(passwordData);
    // if (response.ok) { ... }
  };

  // Handle avatar upload
  const handleAvatarUpload = (e) => {
    const file = e.target.files[0];
    if (!file) return;
    
    // Create URL for preview
    const reader = new FileReader();
    reader.onload = (e) => {
      setUserData(prev => ({ ...prev, image_url: e.target.result }));
    };
    reader.readAsDataURL(file);
    
    // In real implementation:
    // const formData = new FormData();
    // formData.append('avatar', file);
    // const response = await uploadUserAvatar(formData);
    // if (response.ok) { setUserData(prev => ({ ...prev, image_url: response.image_url })); }
  };
  
  // Get user initials for avatar fallback
  const getInitials = () => {
    return `${userData.firstName.charAt(0)}${userData.lastName.charAt(0)}`;
  };

  const content = (
    <div className="w-full p-4 md:p-6 h-screen overflow-y-auto">
      {/* Header */}
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-gray-800">Settings</h1>
        <p className="text-gray-600">Manage your account settings and preferences</p>
      </div>
      
      {/* Notification */}
      {notification.show && (
        <div 
          className={`mb-6 p-4 rounded-lg ${
            notification.type === "success" ? "bg-green-50 text-green-700" : "bg-red-50 text-red-700"
          }`}
        >
          {notification.message}
        </div>
      )}

      {/* Profile Section */}
      <div className="bg-white rounded-xl shadow-sm border border-gray-200 mb-6 overflow-hidden">
        <div className="p-4 md:p-6 border-b border-gray-200 flex justify-between items-center">
          <h2 className="text-lg font-medium text-gray-800">Profile Information</h2>
          {!isEditing ? (
            <button 
              onClick={() => setIsEditing(true)} 
              className="px-4 py-2 text-sm rounded-lg cursor-pointer"
              style={{ color: brandColor }}
            >
              Edit Profile
            </button>
          ) : (
            <button 
              onClick={() => setIsEditing(false)} 
              className="px-4 py-2 text-sm text-gray-600 rounded-lg cursor-pointer"
            >
              Cancel
            </button>
          )}
        </div>
        
        <div className="p-4 md:p-6">
          {/* Avatar Section */}
          <div className="flex flex-col sm:flex-row items-center gap-6 mb-6">
            <div className="relative">
              {userData.image_url ? (
                <img 
                  src={userData.image_url} 
                  alt="User avatar" 
                  className="w-24 h-24 rounded-full object-cover"
                />
              ) : (
                <div 
                  className="w-24 h-24 rounded-full flex items-center justify-center text-white text-xl font-bold"
                  style={{ background: brandColor }}
                >
                  {getInitials()}
                </div>
              )}
              
              {isEditing && (
                <div className="absolute -bottom-2 -right-2">
                  <label 
                    htmlFor="avatar-upload" 
                    className="w-8 h-8 rounded-full bg-white shadow-md flex items-center justify-center cursor-pointer border border-gray-200"
                    style={{ color: brandColor }}
                  >
                    <Camera size={16} />
                    <input 
                      id="avatar-upload" 
                      type="file" 
                      accept="image/*" 
                      className="hidden" 
                      onChange={handleAvatarUpload}
                    />
                  </label>
                </div>
              )}
            </div>
            
            <div>
              <h3 className="text-lg font-medium text-gray-800">
                {userData.firstName} {userData.lastName}
              </h3>
              <div className="flex items-center gap-2 mt-1">
                <span 
                  className="text-xs px-2 py-0.5 rounded-full" 
                  style={{ backgroundColor: `${brandColor}15`, color: brandColor }}
                >
                  {userData.role}
                </span>
                <span className="text-sm text-gray-500">•</span>
                <span className="text-sm text-gray-500">
                  {userData.is_active ? "Active" : "Inactive"}
                </span>
              </div>
              <p className="text-sm text-gray-500 mt-1">{userData.email}</p>
            </div>
          </div>
          
          {/* Profile Form */}
          <form onSubmit={handleUpdateProfile}>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <label htmlFor="firstName" className="block text-sm font-medium text-gray-700 mb-1">
                  First Name
                </label>
                <input
                  type="text"
                  id="firstName"
                  name="firstName"
                  value={userData.firstName}
                  onChange={handleInputChange}
                  disabled={!isEditing}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:border-transparent transition-colors disabled:bg-gray-50 disabled:text-gray-500"
                  style={{ "--tw-ring-color": `${brandColor}40` }}
                />
              </div>
              
              <div>
                <label htmlFor="lastName" className="block text-sm font-medium text-gray-700 mb-1">
                  Last Name
                </label>
                <input
                  type="text"
                  id="lastName"
                  name="lastName"
                  value={userData.lastName}
                  onChange={handleInputChange}
                  disabled={!isEditing}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:border-transparent transition-colors disabled:bg-gray-50 disabled:text-gray-500"
                  style={{ "--tw-ring-color": `${brandColor}40` }}
                />
              </div>
              
              <div>
                <label htmlFor="email" className="block text-sm font-medium text-gray-700 mb-1">
                  Email Address
                </label>
                <input
                  type="email"
                  id="email"
                  name="email"
                  value={userData.email}
                  onChange={handleInputChange}
                  disabled={!isEditing || userData.isGoogle}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:border-transparent transition-colors disabled:bg-gray-50 disabled:text-gray-500"
                  style={{ "--tw-ring-color": `${brandColor}40` }}
                />
                {userData.isGoogle && (
                  <p className="mt-1 text-xs text-gray-500 flex items-center gap-1">
                    <Icon icon="mdi:google" className="text-sm" />
                    Managed by Google Account
                  </p>
                )}
              </div>
              
              <div>
                <label htmlFor="role" className="block text-sm font-medium text-gray-700 mb-1">
                  Role
                </label>
                <input
                  type="text"
                  id="role"
                  name="role"
                  value={userData.role}
                  disabled
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg bg-gray-50 text-gray-500"
                />
                <p className="mt-1 text-xs text-gray-500">Role changes require admin permission</p>
              </div>
            </div>
            
            {isEditing && (
              <div className="mt-6 flex justify-end">
                <button
                  type="submit"
                  className="px-4 py-2 rounded-lg text-white flex items-center gap-2 cursor-pointer"
                  style={{ backgroundColor: brandColor }}
                  disabled={isLoading}
                >
                  {isLoading ? <Loader2 size={16} className="animate-spin" /> : <Icon icon="mdi:check" />}
                  Save Changes
                </button>
              </div>
            )}
          </form>
        </div>
      </div>
      
      {/* Password Section */}
      <div className="bg-white rounded-xl shadow-sm border border-gray-200 mb-6 overflow-hidden">
        <div className="p-4 md:p-6 border-b border-gray-200 flex justify-between items-center">
          <h2 className="text-lg font-medium text-gray-800">Password</h2>
          {!isChangingPassword ? (
            <button 
              onClick={() => setIsChangingPassword(true)} 
              className="px-4 py-2 text-sm rounded-lg cursor-pointer"
              style={{ color: brandColor }}
              disabled={userData.isGoogle}
            >
              Change Password
            </button>
          ) : (
            <button 
              onClick={() => setIsChangingPassword(false)} 
              className="px-4 py-2 text-sm text-gray-600 rounded-lg cursor-pointer"
            >
              Cancel
            </button>
          )}
        </div>
        
        <div className="p-4 md:p-6">
          {userData.isGoogle ? (
            <div className="flex items-center gap-2 text-gray-600">
              <Icon icon="mdi:google" className="text-lg" />
              <p>Password is managed by your Google account</p>
            </div>
          ) : isChangingPassword ? (
            <form onSubmit={handleUpdatePassword}>
              <div className="space-y-4">
                <div>
                  <label htmlFor="currentPassword" className="block text-sm font-medium text-gray-700 mb-1">
                    Current Password
                  </label>
                  <input
                    type="password"
                    id="currentPassword"
                    name="currentPassword"
                    value={passwordData.currentPassword}
                    onChange={handlePasswordChange}
                    required
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:border-transparent transition-colors"
                    style={{ "--tw-ring-color": `${brandColor}40` }}
                  />
                </div>
                
                <div>
                  <label htmlFor="newPassword" className="block text-sm font-medium text-gray-700 mb-1">
                    New Password
                  </label>
                  <input
                    type="password"
                    id="newPassword"
                    name="newPassword"
                    value={passwordData.newPassword}
                    onChange={handlePasswordChange}
                    required
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:border-transparent transition-colors"
                    style={{ "--tw-ring-color": `${brandColor}40` }}
                  />
                </div>
                
                <div>
                  <label htmlFor="confirmPassword" className="block text-sm font-medium text-gray-700 mb-1">
                    Confirm New Password
                  </label>
                  <input
                    type="password"
                    id="confirmPassword"
                    name="confirmPassword"
                    value={passwordData.confirmPassword}
                    onChange={handlePasswordChange}
                    required
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:border-transparent transition-colors"
                    style={{ "--tw-ring-color": `${brandColor}40` }}
                  />
                </div>
              </div>
              
              <div className="mt-6 flex justify-end">
                <button
                  type="submit"
                  className="px-4 py-2 rounded-lg text-white flex items-center gap-2 cursor-pointer"
                  style={{ backgroundColor: brandColor }}
                  disabled={isLoading}
                >
                  {isLoading ? <Loader2 size={16} className="animate-spin" /> : <Icon icon="mdi:lock" />}
                  Update Password
                </button>
              </div>
            </form>
          ) : (
            <p className="text-gray-600">
              Your password was last updated on {new Date(userData.updated_at).toLocaleDateString()}.
            </p>
          )}
        </div>
      </div>
      
      {/* Account Info Section */}
      <div className="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
        <div className="p-4 md:p-6 border-b border-gray-200">
          <h2 className="text-lg font-medium text-gray-800">Account Information</h2>
        </div>
        
        <div className="p-4 md:p-6">
          <dl className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <dt className="text-sm font-medium text-gray-500">Account Created</dt>
              <dd className="mt-1 text-gray-800">
                {new Date(userData.created_at).toLocaleDateString()} at {new Date(userData.created_at).toLocaleTimeString()}
              </dd>
            </div>
            
            <div>
              <dt className="text-sm font-medium text-gray-500">Last Updated</dt>
              <dd className="mt-1 text-gray-800">
                {new Date(userData.updated_at).toLocaleDateString()} at {new Date(userData.updated_at).toLocaleTimeString()}
              </dd>
            </div>
            
            <div>
              <dt className="text-sm font-medium text-gray-500">Account Status</dt>
              <dd className="mt-1 flex items-center gap-2">
                <span className={`inline-block w-2 h-2 rounded-full ${userData.is_active ? "bg-green-500" : "bg-red-500"}`}></span>
                <span className="text-gray-800">{userData.is_active ? "Active" : "Inactive"}</span>
              </dd>
            </div>
            
            <div>
              <dt className="text-sm font-medium text-gray-500">Authentication Method</dt>
              <dd className="mt-1 flex items-center gap-2 text-gray-800">
                {userData.isGoogle ? (
                  <>
                    <Icon icon="mdi:google" className="text-lg" />
                    Google Account
                  </>
                ) : (
                  <>
                    <Icon icon="mdi:email" className="text-lg" />
                    Email and Password
                  </>
                )}
              </dd>
            </div>
          </dl>
        </div>
      </div>
    </div>
  );

  return <SidebarLayout>{content}</SidebarLayout>;
};

export default SettingsPage;