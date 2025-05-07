// import React, { useEffect, useState, useRef } from 'react';
// import SockJS from 'sockjs-client';
// import { Client } from '@stomp/stompjs';
//
// const NotificationListener = () => {
//     const [notifications, setNotifications] = useState([]);
//     const clientRef = useRef(null); // Hold reference to STOMP client
//
//     useEffect(() => {
//         const socket = new SockJS('http://localhost:8080/ws/tooltrack'); // Update to your correct endpoint
//         const stompClient = new Client({
//             webSocketFactory: () => socket,
//             reconnectDelay: 5000, // Retry every 5s if disconnected
//             onConnect: () => {
//                 console.log('Connected to WebSocket');
//                 stompClient.subscribe('/topic/notifications', (message) => {
//                     try {
//                         const payload = JSON.parse(message.body);
//                         setNotifications(prev => [
//                             ...prev,
//                             payload.message || payload.content || '[Empty Notification]'
//                         ]);
//                     } catch (e) {
//                         console.error('Error parsing message', e);
//                     }
//                 });
//             },
//             onStompError: (frame) => {
//                 console.error('STOMP error: ' + frame.headers['message']);
//                 console.error(frame.body);
//             },
//         });
//
//         stompClient.activate();
//         clientRef.current = stompClient;
//
//         return () => {
//             if (clientRef.current && clientRef.current.active) {
//                 clientRef.current.deactivate();
//             }
//         };
//     }, []);
//
//     return (
//         <div style={{ padding: '20px' }}>
//             <h3>🔔 Notifications</h3>
//             <ul>
//                 {notifications.map((notif, index) => (
//                     <li key={index}>{notif}</li>
//                 ))}
//             </ul>
//         </div>
//     );
// };
//
// export default NotificationListener;
