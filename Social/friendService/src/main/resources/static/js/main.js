'use strict';

// Kết nối WebSocket với STOMP
const socket = new SockJS('/post/ws');
const stompClient = Stomp.over(socket);

// Token authentication (giả định lấy từ localStorage hoặc hard-coded)
const token = localStorage.getItem('id') || 'your-jwt-token-here'; // Thay bằng token thực tế

// Danh sách postId từ HTML
const postIds = [
    '1b294e40-44f6-42e6-b235-5fe7758b3c9c',
    'c936a89c-079b-44d5-9b04-1fc101d48f66'
];

// Kết nối STOMP với header Authorization
stompClient.connect(
    { 'Authorization': 'Bearer ' + token }, // Header authentication
    function(frame) {
        console.log('Connected: ' + frame);

        // Subscribe vào topic của từng post
        postIds.forEach(postId => {
            const topic = '/topic/post' + postId;
            stompClient.subscribe(topic, function(message) {
                console.log('Received message for ' + topic + ': ' + message.body);
                showMessage(postId, message.body);
            });
        });
    },
    function(error) {
        console.error('Connection error: ' + error);
    }
);

// Hiển thị bình luận lên giao diện
function showMessage(postId, message) {
    let messageArea;
    if (postId === '1b294e40-44f6-42e6-b235-5fe7758b3c9c') {
        messageArea = document.getElementById('messageArea');
    } else if (postId === 'c936a89c-079b-44d5-9b04-1fc101d48f66') {
        messageArea = document.getElementById('messageArea2');
    }

    if (messageArea) {
        const messageElement = document.createElement('li');
        const comment = JSON.parse(message);
        messageElement.innerHTML = `<strong>${comment.user.username}</strong>: ${comment.content}`;
        messageArea.appendChild(messageElement);
    }
}

// Xử lý gửi bình luận từ form
function setupForm(postId, formId) {
    const form = document.getElementById(formId);
    const input = document.getElementById(postId);

    form.addEventListener('submit', function(event) {
        event.preventDefault();
        const content = input.value.trim();
        if (content) {
            const commentRequest = {
                postId: postId,
                userId: token,
                content: content
            };
            // Gửi message với header Authorization
            stompClient.send('/app/chat.sendComment',{}, JSON.stringify(commentRequest));
            input.value = '';
        }
    });
}

// Gắn sự kiện cho từng form
setupForm('1b294e40-44f6-42e6-b235-5fe7758b3c9c', 'messageForm');
setupForm('c936a89c-079b-44d5-9b04-1fc101d48f66', 'messageForm2');