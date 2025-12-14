import React, { useState, useEffect } from 'react';
import SidebarLayout from "../components/SidebarLayout";
import { CheckCircle, XCircle, User } from 'lucide-react';
import axios from 'axios';


const UserManagement = () => {
  const [users, setUsers] = useState([])

  useEffect(() => {
    axios.get(`${import.meta.env.VITE_BACKEND_URL}/transaction/getAllPendings`, {
      headers: {
        Authorization: "Bearer " + localStorage.getItem("token")
      }
    }).then(response => {
      if (response.status === 200) {
        console.log(response.data)
        setUsers(response.data.transactions)
      }
    })
  }, []);

  const action = function(transactionId, isApprove) {
    if (isApprove === "approve") {
      axios.put(`${import.meta.env.VITE_BACKEND_URL}/transaction/approval/validate`,{
        transactionId:transactionId,
        approvalStatus: true
      }, {
        headers: {
          Authorization: "Bearer " + localStorage.getItem("token")
        }
      }).then(response => {
        if (response.status === 200) {
          console.log(response.data.transaction)
          setUsers((prevUsers) => {
            return prevUsers.filter((user) => user.transaction_id !== transactionId);
          });
        }
      }).catch(error => {
        console.log("error")
      })
      console.log("approve");
    } else if(isApprove === "reject") {
      console.log("reject");
      axios.put(`${import.meta.env.VITE_BACKEND_URL}/transaction/approval/validate`, {
        transactionId: transactionId,
        approvalStatus: false
      }, {
        headers: {
          Authorization: "Bearer " + localStorage.getItem("token")
        }
      }).then(response => {
        if (response.status === 200) {
          setUsers((prevUsers) => {
            return prevUsers.filter((user) => user.transaction_id !== transactionId);
          });
        }
      }).catch(error => {
        console.log("error")
      })
    }
  };

  return (
      <div className="flex h-screen bg-gray-50">
        <SidebarLayout />

        <div className="flex-1 p-6 overflow-auto">
          <div className="h-16 md:hidden" /> {/* Spacer for mobile view */}

          {/* Top header section */}
          <div className="flex flex-col md:flex-row md:items-center md:justify-between mb-8 gap-4">
            <div>
              <h2 className="text-2xl font-semibold text-gray-800">Borrowing Requests</h2>
              <p className="text-gray-500 text-sm mt-1">Manage tool borrowing requests</p>
            </div>

            <div className="relative">
              <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                <svg className="h-5 w-5 text-gray-400" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor">
                  <path fillRule="evenodd" d="M8 4a4 4 0 100 8 4 4 0 000-8zM2 8a6 6 0 1110.89 3.476l4.817 4.817a1 1 0 01-1.414 1.414l-4.816-4.816A6 6 0 012 8z" clipRule="evenodd" />
                </svg>
              </div>
              <input
                  type="text"
                  placeholder="Search users or tools"
                  className="border border-gray-200 pl-10 px-4 py-2 rounded-lg w-full md:w-64 text-sm focus:outline-none focus:ring-2 focus:ring-teal-500 focus:border-transparent shadow-sm"
              />
            </div>
          </div>

          {/* Card container */}
          <div className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
            {/* Header with subtle background */}
            <div className="bg-gray-50 px-6 py-4 border-b border-gray-100">
              <div className="grid grid-cols-3 text-sm font-medium text-gray-700">
                <div>User</div>
                <div>Tool Requested</div>
                <div className="text-right">Actions</div>
              </div>
            </div>

            {/* User rows*/}
            <div className="divide-y divide-gray-100">
              {users.map((user) => (
                  <div
                      key={user.transaction_id}
                      className="grid grid-cols-3 items-center px-6 py-4 hover:bg-teal-50 transition-colors duration-200"
                  >
                    <div className="flex items-center gap-3">
                      <div className="w-10 h-10 rounded-full bg-teal-100 flex items-center justify-center overflow-hidden shadow-sm border border-gray-200">
                        <User className="w-6 h-6 text-teal-600" />
                      </div>
                      <span className="text-gray-800 font-medium">{user.user_firstName + ' ' + user.user_lastName}</span>
                    </div>

                    <div className="text-gray-700 flex items-center">
                      <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-gray-100 text-gray-800">
                        {user.tool_name}
                      </span>
                    </div>

                    <div className="flex items-center justify-end gap-1">
                      <button className="cursor-pointer flex items-center justify-center rounded-md bg-green-50 hover:bg-green-100 text-green-600 px-3 py-1.5 text-xs font-medium transition-colors"
                              onClick={()=> action(user.transaction_id,"approve")}>
                        <CheckCircle className="w-4 h-4 mr-1" />
                        Approve
                      </button>
                      <button className="cursor-pointer flex items-center justify-center rounded-md bg-red-50 hover:bg-red-100 text-red-600 px-3 py-1.5 text-xs font-medium transition-colors"
                              onClick={()=> action(user.transaction_id,"reject")}>
                        <XCircle className="w-4 h-4 mr-1" />
                        Decline
                      </button>
                    </div>
                  </div>
              ))}
            </div>

            {/* Footer */}
            <div className="px-6 py-4 bg-gray-50 border-t border-gray-100">
            </div>
          </div>
        </div>
      </div>
  );
};

export default UserManagement;