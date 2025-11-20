import React, { useState, useEffect } from 'react';
import { Filter, ChevronDown, Search, Calendar, Check, X, ArrowDown, ArrowUp } from 'lucide-react';
import SidebarLayout from "../components/SidebarLayout";
import axios from 'axios'

const ActivityLog = () => {
    const [transactions, setTransactions] = useState([]);
    const [loading, setLoading] = useState(true);
    const [filter, setFilter] = useState('all');
    const [searchQuery, setSearchQuery] = useState('');
    const [sortField, setSortField] = useState('created_at');
    const [sortDirection, setSortDirection] = useState('desc');

    // Mock data based on the ToolTransaction entity
    const mockTransactions = [
        {
            transaction_id: 1,
            user_id: { name: "Alex Johnson" },
            tool_id: { name: "Electric Drill", tool_id: "1" },
            transaction_type: "borrow",
            condition_before: "good",
            status: "approved",
            borrow_date: "2025-05-01",
            due_date: "2025-05-08",
            return_date: null,

        },
        {
            transaction_id: 2,
            user_id: { name: "Jamie Smith"},
            tool_id: { name: "Circular Saw", tool_id: "2" },
            transaction_type: "borrow",
            condition_before: "good",
            status: "rejected",
            borrow_date: null,
            due_date: null,
            return_date: null,

        },
        {
            transaction_id: 3,
            user_id: { name: "Taylor Wilson" },
            tool_id: { name: "Hammer", tool_id: "3" },
            transaction_type: "borrow",
            condition_before: "fair",
            status: "approved",
            borrow_date: "2025-05-02",
            due_date: "2025-05-09",
            return_date: null,

        },
        {
            transaction_id: 4,
            user_id: { name: "Morgan Lee"},
            tool_id: { name: "Electric Drill", tool_id: "4" },
            transaction_type: "returned",
            condition_after: "good",
            status: "approved",
            borrow_date: "2025-04-25",
            due_date: "2025-05-02",
            return_date: "2025-05-02",

        },
        {
            transaction_id: 5,
            user_id: { name: "Casey Brown"},
            tool_id: { name: "Wrench Set", tool_id: "5" },
            transaction_type: "borrow",
            condition_before: "good",
            status: "rejected",
            borrow_date: null,
            due_date: null,
            return_date: null,

        }
    ];

    useEffect(() => {
        // Simulating API fetch
        axios.get('https://backend-tooltrack-pe3u8.ondigitalocean.app/transaction/getAllProcessed', {
                headers: {
                    Authorization: 'Bearer ' + localStorage.getItem('token')
                }
            }
        ).then(res => {
            if (res.status === 200) {
                console.log(res.data.transactions)
                setTransactions(res.data.transactions);
            }
        }).catch(err => {
            console.error('Error fetching transactions:', err);
        });

        setTimeout(() => {
            setLoading(false);
        }, 800);
    }, []);

    const getStatusColor = (status) => {
        switch (status) {
            case 'approved': return 'bg-green-100 text-green-800';
            case 'rejected': return 'bg-red-100 text-red-800';
            default: return 'bg-gray-100 text-gray-800';
        }
    };

    const getStatusIcon = (status) => {
        switch (status) {
            case 'approved': return <Check size={16} className="text-green-800" />;
            case 'rejected': return <X size={16} className="text-red-800" />;
            default: return null;
        }
    };

    const filteredTransactions = transactions.filter(transaction => {
        // Only show approved and rejected
        if (!['approved', 'rejected'].includes(transaction.status)) {
            return false;
        }

        // Apply status filter
        if (filter !== 'all' && transaction.status !== filter) {
            return false;
        }

        // Apply search filter
        const searchLower = searchQuery.toLowerCase();
        console.log(transaction.user_firstName)
        return (
            transaction.user_firstName.toLowerCase().includes(searchLower) ||
            transaction.user_lastName.toLowerCase().includes(searchLower) ||
            transaction.tool_name.toLowerCase().includes(searchLower) ||
            transaction.reason?.toLowerCase().includes(searchLower) ||
            transaction.tool_id.toLowerCase().includes(searchLower)
        );
    });

    const sortedTransactions = [...filteredTransactions].sort((a, b) => {
        let comparison = 0;
        if (a[sortField] < b[sortField]) {
            comparison = -1;
        } else if (a[sortField] > b[sortField]) {
            comparison = 1;
        }
        return sortDirection === 'asc' ? comparison : -comparison;
    });

    const handleSort = (field) => {
        if (field === sortField) {
            setSortDirection(sortDirection === 'asc' ? 'desc' : 'asc');
        } else {
            setSortField(field);
            setSortDirection('asc');
        }
    };

    const renderSortIcon = (field) => {
        if (sortField !== field) return null;
        return sortDirection === 'asc' ? <ArrowUp size={14} /> : <ArrowDown size={14} />;
    };

    return (
        <div className="min-h-screen flex bg-gray-50">
            <SidebarLayout />

            <div className="flex-1 p-6 h-screen overflow-y-auto">
                <div className="h-16 md:hidden" /> {/* Spacer for mobile view */}

                <div className="flex justify-between items-center mb-8">
                    <h1 className="text-2xl font-semibold text-gray-800">Activity Log</h1>

                    <div className="relative">
                        <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                            <Search size={16} className="text-gray-400" />
                        </div>
                        <input
                            type="text"
                            className="block w-64 pl-10 pr-3 py-2 border border-gray-300 rounded-lg text-sm"
                            placeholder="Search transactions..."
                            value={searchQuery}
                            onChange={(e) => setSearchQuery(e.target.value)}
                        />
                    </div>
                </div>

                {/* Filter bar */}
                <div className="flex justify-between mb-6">
                    <div className="flex items-center gap-2">
                        <span className="text-sm font-medium text-gray-700">Filter:</span>
                        <button
                            onClick={() => setFilter('all')}
                            className={`px-3 py-1 text-sm rounded-md ${
                                filter === 'all' ? 'bg-[#2EA69E] text-white' : 'bg-white border border-gray-300 text-gray-700'
                            }`}
                        >
                            All
                        </button>
                        <button
                            onClick={() => setFilter('approved')}
                            className={`px-3 py-1 text-sm rounded-md ${
                                filter === 'approved' ? 'bg-[#2EA69E] text-white' : 'bg-white border border-gray-300 text-gray-700'
                            }`}
                        >
                            Approved
                        </button>
                        <button
                            onClick={() => setFilter('rejected')}
                            className={`px-3 py-1 text-sm rounded-md ${
                                filter === 'rejected' ? 'bg-[#2EA69E] text-white' : 'bg-white border border-gray-300 text-gray-700'
                            }`}
                        >
                            Rejected
                        </button>
                    </div>
                </div>

                {/* Transaction Table */}
                <div className="bg-white rounded-lg shadow overflow-hidden">
                    <div className="overflow-x-auto">
                        <table className="min-w-full divide-y divide-gray-200">
                            <thead className="bg-gray-50">
                            <tr>
                                <th
                                    scope="col"
                                    className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider cursor-pointer"
                                    onClick={() => handleSort('transaction_id')}
                                >
                                    <div className="flex items-center">
                                        ID
                                        {renderSortIcon('transaction_id')}
                                    </div>
                                </th>
                                <th
                                    scope="col"
                                    className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider cursor-pointer"
                                    onClick={() => handleSort('user_id.name')}
                                >
                                    <div className="flex items-center">
                                        User
                                        {renderSortIcon('user_firstName')}
                                    </div>
                                </th>
                                <th
                                    scope="col"
                                    className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider cursor-pointer"
                                    onClick={() => handleSort('tool_id.name')}
                                >
                                    <div className="flex items-center">
                                        Tool
                                        {renderSortIcon('tool_id.name')}
                                    </div>
                                </th>
                                <th
                                    scope="col"
                                    className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider cursor-pointer"
                                    onClick={() => handleSort('transaction_type')}
                                >
                                    <div className="flex items-center">
                                        Type
                                        {renderSortIcon('transaction_type')}
                                    </div>
                                </th>
                                <th
                                    scope="col"
                                    className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider cursor-pointer"
                                    onClick={() => handleSort('status')}
                                >
                                    <div className="flex items-center">
                                        Status
                                        {renderSortIcon('status')}
                                    </div>
                                </th>
                                <th
                                    scope="col"
                                    className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider cursor-pointer"
                                    onClick={() => handleSort('borrow_date')}
                                >
                                    <div className="flex items-center">
                                        Borrow Date
                                        {renderSortIcon('borrow_date')}
                                    </div>
                                </th>
                                <th
                                    scope="col"
                                    className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider cursor-pointer"
                                    onClick={() => handleSort('due_date')}
                                >
                                    <div className="flex items-center">
                                        Due Date
                                        {renderSortIcon('due_date')}
                                    </div>
                                </th>
                                <th
                                    scope="col"
                                    className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider cursor-pointer"
                                    onClick={() => handleSort('return_date')}
                                >
                                    <div className="flex items-center">
                                        Return Date
                                        {renderSortIcon('return_date')}
                                    </div>
                                </th>
                                <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                    Actions
                                </th>
                            </tr>
                            </thead>
                            <tbody className="bg-white divide-y divide-gray-200">
                            {loading ? (
                                <tr>
                                    <td colSpan={9} className="px-6 py-4 text-center text-sm text-gray-500">
                                        Loading transactions...
                                    </td>
                                </tr>
                            ) : sortedTransactions.length === 0 ? (
                                <tr>
                                    <td colSpan={9} className="px-6 py-4 text-center text-sm text-gray-500">
                                        No transactions found
                                    </td>
                                </tr>
                            ) : (
                                sortedTransactions.map((transaction) => (
                                    <tr key={transaction.transaction_id} className="hover:bg-gray-50">
                                        <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                                            #{transaction.transaction_id}
                                        </td>
                                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                            <div>{transaction.user_firstName + ' '+ transaction.user_lastName}</div>

                                        </td>
                                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                            <div>{transaction.tool_name}</div>
                                            <div className="text-xs text-gray-400">{transaction.tool_id.tool_id}</div>
                                        </td>
                                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                            <span className="capitalize">{transaction.transaction_type}</span>
                                        </td>
                                        <td className="px-6 py-4 whitespace-nowrap">
                        <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getStatusColor(transaction.status)}`}>
                          <span className="mr-1">{getStatusIcon(transaction.status)}</span>
                          <span className="capitalize">{transaction.status}</span>
                        </span>
                                        </td>
                                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                            {transaction.borrow_date || '-'}
                                        </td>
                                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                            {transaction.due_date || '-'}
                                        </td>
                                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                            {transaction.return_date || '-'}
                                        </td>
                                        <td className="px-6 py-4 whitespace-nowrap text-sm">
                                            <button
                                                className="text-[#2EA69E] hover:text-[#25857E] text-sm font-medium"
                                                onClick={() => {}}
                                            >
                                                View
                                            </button>
                                        </td>
                                    </tr>
                                ))
                            )}
                            </tbody>
                        </table>
                    </div>
                </div>

                {/* Pagination */}
                {/*<div className="flex justify-between items-center mt-6">*/}
                {/*    <div>*/}
                {/*        <p className="text-sm text-gray-700">*/}
                {/*            Showing <span className="font-medium">1</span> to <span className="font-medium">{sortedTransactions.length}</span> of{" "}*/}
                {/*            <span className="font-medium">{sortedTransactions.length}</span> results*/}
                {/*        </p>*/}
                {/*    </div>*/}
                {/*    <div className="flex items-center space-x-2">*/}
                {/*        <button className="px-3 py-1 border border-gray-300 rounded-md text-sm text-gray-500 hover:bg-gray-50">*/}
                {/*            Previous*/}
                {/*        </button>*/}
                {/*        <button className="px-3 py-1 bg-[#2EA69E] text-white rounded-md text-sm">*/}
                {/*            1*/}
                {/*        </button>*/}
                {/*        <button className="px-3 py-1 border border-gray-300 rounded-md text-sm text-gray-700 hover:bg-gray-50">*/}
                {/*            2*/}
                {/*        </button>*/}
                {/*        <button className="px-3 py-1 border border-gray-300 rounded-md text-sm text-gray-500 hover:bg-gray-50">*/}
                {/*            Next*/}
                {/*        </button>*/}
                {/*    </div>*/}
                {/*</div>*/}
            </div>
        </div>
    );
};

export default ActivityLog;