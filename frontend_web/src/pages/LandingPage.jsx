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
        
        <div className="container relative z-10 mx-auto px-4 md:px-8">
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
                <button onClick={() => navigate("/login")} className="flex items-center justify-center rounded-lg bg-teal-600 px-6 py-3 font-medium text-white shadow-lg shadow-teal-500/20 transition-all hover:bg-teal-700 hover:shadow-xl hover:shadow-teal-500/30">
                  Get Started <Icon icon="material-symbols:arrow-right-alt" className="ml-2 h-5 w-5" />
                </button>
                <button className="rounded-lg border-2 border-teal-600 bg-transparent px-6 py-3 font-medium text-teal-600 transition-colors hover:bg-teal-50">
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

      {/* How It Works Section */}
      <section className="py-16 bg-gray-50">
        <div className="container mx-auto px-4 md:px-8">
          <div className="text-center mb-12">
            <h2 className="text-3xl font-bold mb-4">How ToolTrack Works</h2>
            <p className="text-gray-600 max-w-2xl mx-auto">Simple, intuitive, and efficient tool borrowing process</p>
          </div>
          
          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            <div className="bg-white p-6 rounded-lg shadow-md text-center">
              <div className="p-4 rounded-full w-16 h-16 flex items-center justify-center mx-auto mb-4" style={{ backgroundColor: 'rgba(46, 166, 158, 0.1)' }}>
                <Icon icon="mdi:magnify" className="h-8 w-8" style={{ color: '#2EA69E' }} />
              </div>
              <h3 className="font-bold text-xl mb-2">Browse Available Tools</h3>
              <p className="text-gray-600">Search through available tools or scan QR codes to find exactly what you need.</p>
            </div>
            
            <div className="bg-white p-6 rounded-lg shadow-md text-center">
              <div className="p-4 rounded-full w-16 h-16 flex items-center justify-center mx-auto mb-4" style={{ backgroundColor: 'rgba(46, 166, 158, 0.1)' }}>
                <Icon icon="mdi:qrcode-scan" className="h-8 w-8" style={{ color: '#2EA69E' }} />
              </div>
              <h3 className="font-bold text-xl mb-2">Scan & Request</h3>
              <p className="text-gray-600">Simply scan the QR code on any tool to quickly send a borrowing request.</p>
            </div>
            
            <div className="bg-white p-6 rounded-lg shadow-md text-center">
              <div className="p-4 rounded-full w-16 h-16 flex items-center justify-center mx-auto mb-4" style={{ backgroundColor: 'rgba(46, 166, 158, 0.1)' }}>
                <Icon icon="mdi:clock-outline" className="h-8 w-8" style={{ color: '#2EA69E' }} />
              </div>
              <h3 className="font-bold text-xl mb-2">Track & Return</h3>
              <p className="text-gray-600">Keep track of borrowed items and return dates through the app.</p>
            </div>
          </div>
        </div>
      </section>

      {/* Features Section */}
      <section className="py-16">
        <div className="container mx-auto px-4 md:px-8">
          <div className="text-center mb-12">
            <h2 className="text-3xl font-bold mb-4">Key Features</h2>
            <p className="text-gray-600 max-w-2xl mx-auto">Designed to make tool management simple for everyone</p>
          </div>
          
          <div className="grid grid-cols-1 md:grid-cols-2 gap-12">
            <div className="flex">
              <div className="mr-4">
                <div className="p-3 rounded-full" style={{ backgroundColor: 'rgba(46, 166, 158, 0.1)' }}>
                  <Icon icon="mdi:qrcode" className="h-6 w-6" style={{ color: '#2EA69E' }} />
                </div>
              </div>
              <div>
                <h3 className="font-bold text-xl mb-2">QR Code Scanning</h3>
                <p className="text-gray-600">Quickly identify and request tools by scanning their unique QR codes.</p>
              </div>
            </div>
            
            <div className="flex">
              <div className="mr-4">
                <div className="p-3 rounded-full" style={{ backgroundColor: 'rgba(46, 166, 158, 0.1)' }}>
                  <Icon icon="mdi:cellphone" className="h-6 w-6" style={{ color: '#2EA69E' }} />
                </div>
              </div>
              <div>
                <h3 className="font-bold text-xl mb-2">Mobile-First Experience</h3>
                <p className="text-gray-600">Full-featured mobile app for on-the-go tool requests and tracking.</p>
              </div>
            </div>
            
            <div className="flex">
              <div className="mr-4">
                <div className="p-3 rounded-full" style={{ backgroundColor: 'rgba(46, 166, 158, 0.1)' }}>
                  <Icon icon="mdi:desktop-mac" className="h-6 w-6" style={{ color: '#2EA69E' }} />
                </div>
              </div>
              <div>
                <h3 className="font-bold text-xl mb-2">Admin Dashboard</h3>
                <p className="text-gray-600">Comprehensive web interface for administrators to manage inventory and requests.</p>
              </div>
            </div>
            
            <div className="flex">
              <div className="mr-4">
                <div className="p-3 rounded-full" style={{ backgroundColor: 'rgba(46, 166, 158, 0.1)' }}>
                  <Icon icon="mdi:check-circle-outline" className="h-6 w-6" style={{ color: '#2EA69E' }} />
                </div>
              </div>
              <div>
                <h3 className="font-bold text-xl mb-2">Real-time Updates</h3>
                <p className="text-gray-600">Get notified about request approvals, due dates, and available tools.</p>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="text-white py-16" style={{ backgroundColor: '#2EA69E' }}>
        <div className="container mx-auto px-4 md:px-8 text-center">
          <h2 className="text-3xl font-bold mb-4">Ready to simplify tool management?</h2>
          <p className="text-xl mb-8 max-w-2xl mx-auto">Join schools and organizations that are already using ToolTrack to streamline their borrowing processes.</p>
          <div className="flex justify-center gap-4 flex-wrap">
            <button className="bg-white font-semibold py-3 px-6 rounded-lg hover:bg-teal-50 transition-colors" style={{ color: '#2EA69E' }}>
              Download Mobile App
            </button>
            <button className="bg-transparent border-2 border-white py-3 px-6 rounded-lg font-semibold hover:bg-white hover:text-teal-600 transition-colors" style={{ '--hover-color': '#2EA69E' }}>
              Request Demo
            </button>
          </div>
        </div>
      </section>

      {/* Develeoper Section */}
      <section className="py-16 bg-gray-50">
        <div className="container mx-auto px-4 md:px-8">
          <div className="text-center mb-12">
            <h2 className="text-3xl font-bold mb-4">Meet The Team</h2>
            <p className="text-gray-600 max-w-2xl mx-auto">The developers behind ToolTrack</p>
          </div>
          
          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            <div className="bg-white p-6 rounded-lg shadow-md text-center">
              <div className="w-24 h-24 rounded-full bg-gray-200 mx-auto mb-4 flex items-center justify-center">
                <Icon icon="mdi:account" className="h-12 w-12 text-gray-400" />
              </div>
              <h3 className="font-bold text-xl mb-1">Paulo Y. Carabuena</h3>
              <p className="text-gray-600 mb-3">Mobile Developer & Group Leader</p>
              <p className="text-gray-500 text-sm">Leading the development of our mobile application and coordinating the team's efforts.</p>
            </div>
            
            <div className="bg-white p-6 rounded-lg shadow-md text-center">
              <div className="w-24 h-24 rounded-full bg-gray-200 mx-auto mb-4 flex items-center justify-center">
                <Icon icon="mdi:account" className="h-12 w-12 text-gray-400" />
              </div>
              <h3 className="font-bold text-xl mb-1">Aeron Cylde N. Espina</h3>
              <p className="text-gray-600 mb-3">Backend Developer</p>
              <p className="text-gray-500 text-sm">Building the robust server infrastructure and APIs that power ToolTrack.</p>
            </div>
            
            <div className="bg-white p-6 rounded-lg shadow-md text-center">
              <div className="w-24 h-24 rounded-full bg-gray-200 mx-auto mb-4 flex items-center justify-center">
                <Icon icon="mdi:account" className="h-12 w-12 text-gray-400" />
              </div>
              <h3 className="font-bold text-xl mb-1">Nathaniel Salvoro</h3>
              <p className="text-gray-600 mb-3">Frontend Web Developer & UI/UX Designer</p>
              <p className="text-gray-500 text-sm">Creating the intuitive web interface and designing the user experience.</p>
            </div>
          </div>
        </div>
      </section>

      {/* Footer */}
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