import React from 'react';
import { Icon } from '@iconify/react';
import iPhone from "../assets/handphone.png";
import Navbar from "../components/Navbar";
import { useNavigate } from 'react-router-dom';

const LandingPage = () => {
    const navigate = useNavigate();
  return (
    <div className="w-full">
        <Navbar/>
      {/* Hero Section */}
      <section className="relative overflow-hidden bg-gray-50 py-24 md:py-32">
        {/* Background Elements */}
        <div className="absolute inset-0 z-0">
          <div className="absolute -right-16 -top-16 h-64 w-64 rounded-full bg-teal-500 opacity-20"></div>
          <div className="absolute -left-20 bottom-10 h-80 w-80 rounded-full bg-teal-400 opacity-10"></div>
          <div className="absolute right-1/4 top-1/2 h-40 w-40 rounded-full bg-teal-300 opacity-10"></div>
        </div>
        
        <div className="container relative z-1 mx-auto px-4 md:px-8">
          <div className="flex flex-col items-center md:flex-row md:space-x-12">
            <div className="w-full md:w-1/2 mb-16 md:mb-0">
              <div className="mb-4 inline-block rounded-full bg-teal-100 px-4 py-1 text-sm font-medium text-teal-600">
                Tool Management Simplified
              </div>
              <h1 className="mb-6 text-4xl font-bold leading-tight tracking-tight text-gray-900 md:text-6xl">
                ToolTrack - <span className="text-teal-600">Smart Borrowing</span> Easy Tracking
              </h1>
              <p className="mb-8 text-lg text-gray-600 md:text-xl">
                ToolTrack simplifies equipment management for schools, workplaces, and communities with intuitive tracking and seamless borrowing.
              </p>
              <div className="flex flex-col sm:flex-row gap-4">
                <button onClick={() => navigate("/login")} className="flex items-center justify-center rounded-lg bg-teal-600 px-6 py-3 font-medium text-white shadow-lg shadow-teal-500/20 transition-all hover:bg-teal-700 hover:shadow-xl hover:shadow-teal-500/30 cursor-pointer">
                  Get Started <Icon icon="material-symbols:arrow-right-alt" className="ml-2 h-5 w-5" />
                </button>
                <button className="rounded-lg border-2 border-teal-600 bg-transparent px-6 py-3 font-medium text-teal-600 transition-colors hover:bg-teal-50 cursor-pointer">
                  See How It Works
                </button>
              </div>
              
              {/* char char ra ning trusted badges 
              <div className="mt-12">
                <p className="mb-3 text-sm font-medium text-gray-500">TRUSTED BY</p>
                <div className="flex flex-wrap items-center gap-6">
                  <div className="h-8 w-auto opacity-60">
                    <Icon icon="mdi:school" className="h-8 text-gray-500" />
                  </div>
                  <div className="h-8 w-auto opacity-60">
                    <Icon icon="mdi:domain" className="h-8 text-gray-500" />
                  </div>
                  <div className="h-8 w-auto opacity-60">
                    <Icon icon="mdi:tools" className="h-8 text-gray-500" />
                  </div>
                </div>
              </div>*/}
            </div>
            
            <div className="w-full md:w-1/2">
              <div className="relative">
                {/* Phone mockup */}
                <div className="relative z-10 overflow-hidden rounded-3xl p-2">

                  <div className="overflow-hidden">
                  <img src={iPhone} alt="iPhone Mockup" className="w-2/3 h-auto mx-auto" />
                  </div>
                  {/* Floating elements to indicate interactive app
                  <div className="absolute -right-7 top-1/4 flex h-16 w-32 items-center gap-2 rounded-lg bg-white p-2 shadow-lg">
                    <div className="rounded-full bg-teal-100 p-2">
                      <Icon icon="mdi:check" className="h-6 w-6 text-teal-600" />
                    </div>
                    <span className="text-sm font-medium">Request<br/>Approved</span>
                  </div>*/}
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* How It Works Section - Improved */}
      <section className="py-20 bg-white">
        <div className="container mx-auto px-4 md:px-8">
          <div className="text-center mb-16">
            <div className="inline-block mb-4 rounded-full bg-teal-100 px-4 py-1 text-sm font-medium text-teal-600">
              Simple Process
            </div>
            <h2 className="text-3xl font-bold mb-4">How ToolTrack Works</h2>
            <p className="text-gray-600 max-w-2xl mx-auto">Experience a seamless borrowing process from start to finish</p>
          </div>
          
          <div className="relative">
            {/* Connection line */}
            <div className="hidden md:block absolute top-1/2 left-0 w-full h-0.5 bg-teal-100 -translate-y-1/2 z-0"></div>
            
            <div className="grid grid-cols-1 md:grid-cols-3 gap-8 relative z-10">
              <div className="bg-white rounded-xl shadow-lg p-8 transform transition-all duration-300 hover:-translate-y-2 hover:shadow-xl border border-gray-100">
                <div className="p-4 rounded-full w-16 h-16 flex items-center justify-center mx-auto mb-6 bg-teal-50">
                  <Icon icon="mdi:magnify" className="h-8 w-8 text-teal-600" />
                </div>
                <h3 className="font-bold text-xl mb-4 text-center">Browse Available Tools</h3>
                <p className="text-gray-600 text-center">Search through our inventory or scan QR codes to find the perfect tool for your project.</p>
                <div className="mt-6 bg-teal-50 rounded-full w-8 h-8 flex items-center justify-center mx-auto">
                  <span className="font-bold text-teal-600">1</span>
                </div>
              </div>
              
              <div className="bg-white rounded-xl shadow-lg p-8 transform transition-all duration-300 hover:-translate-y-2 hover:shadow-xl border border-gray-100">
                <div className="p-4 rounded-full w-16 h-16 flex items-center justify-center mx-auto mb-6 bg-teal-50">
                  <Icon icon="mdi:qrcode-scan" className="h-8 w-8 text-teal-600" />
                </div>
                <h3 className="font-bold text-xl mb-4 text-center">Scan & Request</h3>
                <p className="text-gray-600 text-center">Scan the QR code on any tool to quickly send a borrowing request with just a few taps.</p>
                <div className="mt-6 bg-teal-50 rounded-full w-8 h-8 flex items-center justify-center mx-auto">
                  <span className="font-bold text-teal-600">2</span>
                </div>
              </div>
              
              <div className="bg-white rounded-xl shadow-lg p-8 transform transition-all duration-300 hover:-translate-y-2 hover:shadow-xl border border-gray-100">
                <div className="p-4 rounded-full w-16 h-16 flex items-center justify-center mx-auto mb-6 bg-teal-50">
                  <Icon icon="mdi:clock-outline" className="h-8 w-8 text-teal-600" />
                </div>
                <h3 className="font-bold text-xl mb-4 text-center">Track & Return</h3>
                <p className="text-gray-600 text-center">Receive notifications about due dates and return items seamlessly through the app.</p>
                <div className="mt-6 bg-teal-50 rounded-full w-8 h-8 flex items-center justify-center mx-auto">
                  <span className="font-bold text-teal-600">3</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Features Section - Improved */}
      <section className="py-20 bg-gray-50">
        <div className="container mx-auto px-4 md:px-8">
          <div className="text-center mb-16">
            <div className="inline-block mb-4 rounded-full bg-teal-100 px-4 py-1 text-sm font-medium text-teal-600">
              Further Capabilities
            </div>
            <h2 className="text-3xl font-bold mb-4">Key Features</h2>
            <p className="text-gray-600 max-w-2xl mx-auto">Designed to make tool management effortless for everyone</p>
          </div>
          
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8">
            <div className="bg-white p-6 rounded-xl shadow-md transition-all duration-300 hover:shadow-lg border border-gray-100">
              <div className="mb-4">
                <div className="p-3 rounded-full inline-block bg-teal-50">
                  <Icon icon="mdi:qrcode" className="h-6 w-6 text-teal-600" />
                </div>
              </div>
              <h3 className="font-bold text-xl mb-3">QR Code Scanning</h3>
              <p className="text-gray-600">Instantly identify tools and equipment with quick QR code scanning.</p>
            </div>
            
            <div className="bg-white p-6 rounded-xl shadow-md transition-all duration-300 hover:shadow-lg border border-gray-100">
              <div className="mb-4">
                <div className="p-3 rounded-full inline-block bg-teal-50">
                  <Icon icon="mdi:cellphone" className="h-6 w-6 text-teal-600" />
                </div>
              </div>
              <h3 className="font-bold text-xl mb-3">Mobile-First Design</h3>
              <p className="text-gray-600">Access all features from your mobile device with our intuitive interface.</p>
            </div>
            
            <div className="bg-white p-6 rounded-xl shadow-md transition-all duration-300 hover:shadow-lg border border-gray-100">
              <div className="mb-4">
                <div className="p-3 rounded-full inline-block bg-teal-50">
                  <Icon icon="mdi:desktop-mac" className="h-6 w-6 text-teal-600" />
                </div>
              </div>
              <h3 className="font-bold text-xl mb-3">Admin Dashboard</h3>
              <p className="text-gray-600">Comprehensive management tools for administrators to oversee all activities.</p>
            </div>
            
            <div className="bg-white p-6 rounded-xl shadow-md transition-all duration-300 hover:shadow-lg border border-gray-100">
              <div className="mb-4">
                <div className="p-3 rounded-full inline-block bg-teal-50">
                  <Icon icon="mdi:check-circle-outline" className="h-6 w-6 text-teal-600" />
                </div>
              </div>
              <h3 className="font-bold text-xl mb-3">Real-time Updates</h3>
              <p className="text-gray-600">Receive instant notifications about request status and due dates.</p>
            </div>
            
            <div className="bg-white p-6 rounded-xl shadow-md transition-all duration-300 hover:shadow-lg border border-gray-100">
              <div className="mb-4">
                <div className="p-3 rounded-full inline-block bg-teal-50">
                  <Icon icon="mdi:chart-line" className="h-6 w-6 text-teal-600" />
                </div>
              </div>
              <h3 className="font-bold text-xl mb-3">Usage Analytics</h3>
              <p className="text-gray-600">Track borrowing patterns and tool utilization with detailed reports.</p>
            </div>
            
            <div className="bg-white p-6 rounded-xl shadow-md transition-all duration-300 hover:shadow-lg border border-gray-100">
              <div className="mb-4">
                <div className="p-3 rounded-full inline-block bg-teal-50">
                  <Icon icon="mdi:calendar-clock" className="h-6 w-6 text-teal-600" />
                </div>
              </div>
              <h3 className="font-bold text-xl mb-3">Scheduling System</h3>
              <p className="text-gray-600">Plan ahead by reserving tools for specific dates and timeframes.</p>
            </div>
            
            <div className="bg-white p-6 rounded-xl shadow-md transition-all duration-300 hover:shadow-lg border border-gray-100">
              <div className="mb-4">
                <div className="p-3 rounded-full inline-block bg-teal-50">
                  <Icon icon="mdi:account-group" className="h-6 w-6 text-teal-600" />
                </div>
              </div>
              <h3 className="font-bold text-xl mb-3">User Management</h3>
              <p className="text-gray-600">Assign different access levels and permissions to various user groups.</p>
            </div>
            
            <div className="bg-white p-6 rounded-xl shadow-md transition-all duration-300 hover:shadow-lg border border-gray-100">
              <div className="mb-4">
                <div className="p-3 rounded-full inline-block bg-teal-50">
                  <Icon icon="mdi:bell-ring-outline" className="h-6 w-6 text-teal-600" />
                </div>
              </div>
              <h3 className="font-bold text-xl mb-3">Reminder System</h3>
              <p className="text-gray-600">Automatic notifications to remind users about upcoming return dates.</p>
            </div>
          </div>
        </div>
      </section>

      {/* CTA Section - Improved (with adjustments) */}
      <section className="relative py-20 overflow-hidden">
        <div className="absolute inset-0 bg-teal-600"></div>
        <div className="absolute inset-0 bg-gradient-to-r from-teal-800 to-teal-500 opacity-90"></div>
        <div className="absolute inset-x-0 top-0 h-40 bg-gradient-to-b from-white opacity-10"></div>
        <div className="absolute inset-0">
          <div className="absolute -right-16 -top-16 h-64 w-64 rounded-full bg-teal-500 opacity-20"></div>
          <div className="absolute -left-20 bottom-10 h-80 w-80 rounded-full bg-teal-800 opacity-20"></div>
        </div>
        
        <div className="container mx-auto px-4 md:px-8 text-center relative z-10">
          <h2 className="text-3xl md:text-4xl font-bold mb-4 text-white">Ready to simplify tool management?</h2>
          <p className="text-xl mb-8 max-w-2xl mx-auto text-teal-50">ToolTrack helps schools and organizations streamline their borrowing processes.</p>
          <div className="flex justify-center">
            <button className="cursor-pointer bg-transparent border-2 border-white py-3 px-8 rounded-lg font-semibold hover:bg-white hover:text-teal-600 transition-colors text-white flex items-center shadow-lg hover:shadow-xl">
              <Icon icon="mdi:presentation" className="mr-2 h-5 w-5" />
              Request Demo
            </button>
          </div>
        </div>
      </section>

      {/* Developer Section - Improved */}
      <section className="py-20 bg-white">
        <div className="container mx-auto px-4 md:px-8">
          <div className="text-center mb-16">
            <div className="inline-block mb-4 rounded-full bg-teal-100 px-4 py-1 text-sm font-medium text-teal-600">
              Our Team
            </div>
            <h2 className="text-3xl font-bold mb-4">Meet The Creators</h2>
            <p className="text-gray-600 max-w-2xl mx-auto">The developers behind ToolTrack</p>
          </div>
          
          <div className="grid grid-cols-1 md:grid-cols-3 gap-10">
            <div className="group">
              <div className="relative mb-6 overflow-hidden rounded-2xl shadow-lg">
                <div className="aspect-w-3 aspect-h-4 bg-gray-200">
                  <div className="flex items-center justify-center h-full">
                    <Icon icon="mdi:account" className="h-24 w-24 text-gray-400" />
                  </div>
                </div>
                <div className="absolute inset-0 bg-gradient-to-t from-teal-900 via-transparent to-transparent opacity-0 group-hover:opacity-70 transition-opacity duration-300"></div>
                <div className="absolute bottom-0 left-0 right-0 p-4 transform translate-y-full group-hover:translate-y-0 transition-transform duration-300">
                  <div className="flex justify-center space-x-3">
                    <a href="#" className="p-2 rounded-full bg-white text-teal-600 hover:bg-teal-50">
                      <Icon icon="mdi:github" className="h-5 w-5" />
                    </a>
                    <a href="#" className="p-2 rounded-full bg-white text-teal-600 hover:bg-teal-50">
                      <Icon icon="mdi:linkedin" className="h-5 w-5" />
                    </a>
                    <a href="#" className="p-2 rounded-full bg-white text-teal-600 hover:bg-teal-50">
                      <Icon icon="mdi:email" className="h-5 w-5" />
                    </a>
                  </div>
                </div>
              </div>
              <h3 className="font-bold text-xl mb-1 text-center">Paulo Y. Carabuena</h3>
              <p className="text-teal-600 mb-3 text-center font-medium">Mobile Developer & Group Leader</p>
              <p className="text-gray-600 text-center">Leading the development of our mobile application and coordinating the team's efforts.</p>
            </div>
            
            <div className="group">
              <div className="relative mb-6 overflow-hidden rounded-2xl shadow-lg">
                <div className="aspect-w-3 aspect-h-4 bg-gray-200">
                  <div className="flex items-center justify-center h-full">
                    <Icon icon="mdi:account" className="h-24 w-24 text-gray-400" />
                  </div>
                </div>
                <div className="absolute inset-0 bg-gradient-to-t from-teal-900 via-transparent to-transparent opacity-0 group-hover:opacity-70 transition-opacity duration-300"></div>
                <div className="absolute bottom-0 left-0 right-0 p-4 transform translate-y-full group-hover:translate-y-0 transition-transform duration-300">
                  <div className="flex justify-center space-x-3">
                    <a href="#" className="p-2 rounded-full bg-white text-teal-600 hover:bg-teal-50">
                      <Icon icon="mdi:github" className="h-5 w-5" />
                    </a>
                    <a href="#" className="p-2 rounded-full bg-white text-teal-600 hover:bg-teal-50">
                      <Icon icon="mdi:linkedin" className="h-5 w-5" />
                    </a>
                    <a href="#" className="p-2 rounded-full bg-white text-teal-600 hover:bg-teal-50">
                      <Icon icon="mdi:email" className="h-5 w-5" />
                    </a>
                  </div>
                </div>
              </div>
              <h3 className="font-bold text-xl mb-1 text-center">Aeron Cylde N. Espina</h3>
              <p className="text-teal-600 mb-3 text-center font-medium">Backend Developer</p>
              <p className="text-gray-600 text-center">Building the robust server infrastructure and APIs that power ToolTrack.</p>
            </div>
            
            <div className="group">
              <div className="relative mb-6 overflow-hidden rounded-2xl shadow-lg">
                <div className="aspect-w-3 aspect-h-4 bg-gray-200">
                  <div className="flex items-center justify-center h-full">
                    <Icon icon="mdi:account" className="h-24 w-24 text-gray-400" />
                  </div>
                </div>
                <div className="absolute inset-0 bg-gradient-to-t from-teal-900 via-transparent to-transparent opacity-0 group-hover:opacity-70 transition-opacity duration-300"></div>
                <div className="absolute bottom-0 left-0 right-0 p-4 transform translate-y-full group-hover:translate-y-0 transition-transform duration-300">
                  <div className="flex justify-center space-x-3">
                    <a href="#" className="p-2 rounded-full bg-white text-teal-600 hover:bg-teal-50">
                      <Icon icon="mdi:github" className="h-5 w-5" />
                    </a>
                    <a href="#" className="p-2 rounded-full bg-white text-teal-600 hover:bg-teal-50">
                      <Icon icon="mdi:linkedin" className="h-5 w-5" />
                    </a>
                    <a href="#" className="p-2 rounded-full bg-white text-teal-600 hover:bg-teal-50">
                      <Icon icon="mdi:email" className="h-5 w-5" />
                    </a>
                  </div>
                </div>
              </div>
              <h3 className="font-bold text-xl mb-1 text-center">Nathaniel Salvoro</h3>
              <p className="text-teal-600 mb-3 text-center font-medium">Frontend Web Developer & UI/UX Designer</p>
              <p className="text-gray-600 text-center">Creating the intuitive web interface and designing the user experience.</p>
            </div>
          </div>
        </div>
      </section>

      {/* Original Footer (kept as requested) */}
      <footer className="bg-gray-800 text-white py-8">
        <div className="container mx-auto px-4 md:px-8">
          <div className="flex flex-col md:flex-row justify-between items-center">
            <div className="mb-6 md:mb-0">
              <h2 className="text-2xl font-bold mb-2">ToolTrack</h2>
              <p className="text-gray-400">Simplifying tool borrowing and tracking</p>
            </div>
            
            <div className="text-center mb-6 md:mb-0">
              <p className="text-gray-400">A school project by Team ToolTrack</p>
              <p className="text-gray-400">© {new Date().getFullYear()} ToolTrack. All rights reserved.</p>
            </div>
            
            <div className="flex gap-4">
              <a href="#" className="text-gray-400 hover:text-white">
                <Icon icon="mdi:github" className="h-6 w-6" />
              </a>
              <a href="#" className="text-gray-400 hover:text-white">
                <Icon icon="mdi:email" className="h-6 w-6" />
              </a>
              <a href="#" className="text-gray-400 hover:text-white">
                <Icon icon="mdi:school" className="h-6 w-6" />
              </a>
            </div>
          </div>
        </div>
      </footer>
    </div>
  );
};

export default LandingPage;