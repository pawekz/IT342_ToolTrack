import { useEffect, useRef } from 'react';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';



const WebsocketUi = () => {
    const stompClientRef = useRef(null);

    useEffect(() => {
        if (!stompClientRef.current) {
            const socket = new SockJS('http://localhost:8080/ws/tooltrack?userId=admin@email.com');
            const stompClient = new Client({
                webSocketFactory: () => socket,
                reconnectDelay: 5000,
                onConnect: () => {
                    console.log('WebSocket connected');
                    stompClient.subscribe('/user/notifications', (message) => {
                        console.log("yawa")
                        console.log('Received Notification:', JSON.parse(message.body));
                    });
                },
                onStompError: (frame) => {
                    console.error('STOMP error:', frame.headers['message']);
                },
            });

            stompClient.activate();
            stompClientRef.current = stompClient;
        }

        // Cleanup logic to avoid stale WebSocket connections
        return () => {
            if (stompClientRef.current) {
                console.log('Deactivating WebSocket connection...');
                stompClientRef.current.deactivate();
                stompClientRef.current = null;
            }
        };
    }, []); // Empty dependency array ensures this runs only once

    return <div>WebSocket Component</div>;
};

export default WebsocketUi;
